/*
 * Copyright 2017 Sebastien Callier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sebastien.callier.serialization.codec.extendable.object;

import sebastien.callier.serialization.codec.Codec;
import sebastien.callier.serialization.codec.CodecCache;
import sebastien.callier.serialization.codec.extendable.object.field.AccessingFieldCodec;
import sebastien.callier.serialization.codec.extendable.object.field.DirectFieldCodec;
import sebastien.callier.serialization.codec.extendable.object.field.FieldCodec;
import sebastien.callier.serialization.codec.extendable.object.field.LambdaMetaFactoryUtils;
import sebastien.callier.serialization.codec.extendable.object.field.primitives.*;
import sebastien.callier.serialization.deserializer.InputStreamWrapper;
import sebastien.callier.serialization.exceptions.CodecGenerationException;
import sebastien.callier.serialization.exceptions.MissingCodecException;
import sebastien.callier.serialization.serializer.OutputStreamWrapper;
import sebastien.callier.serialization.utils.CodecUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author Sebastien Callier
 * @since 2017
 */
public class ObjectCodec<T> implements Codec<T> {
    private final byte reservedByte;
    private final Class<T> tClass;
    private final FieldCodec[] fields;
    private final Creator creator;

    @FunctionalInterface
    public interface Creator {
        Object build();
    }

    /**
     * Will try to generate a codec for the provided class.
     * All the non transient, non static fields should be accessible for serialization:
     * <ul>
     * <li>If the field is public it will be used directly (not recommended).</li>
     * <li>If the field is not public, public setter and getter will be used</li>
     * </ul>
     * A getter is a method
     * <ul>
     * <li>With no argument</li>
     * <li>With the return type exactly the same as the declared field</li>
     * <li>Called "get"+field name ("is"+field name is also acceptable for boolean)</li>
     * </ul>
     * A getter is a method
     * <ul>
     * <li>With a single argument of exactly the same type as the declared field</li>
     * <li>With void return type</li>
     * <li>Called "set"+field name</li>
     * </ul>
     * We do not allow a class to contain two fields with the same name.
     * Make sure to have a public no argument constructor.
     *
     * @throws CodecGenerationException if the codec could not be generated for this class
     * @throws MissingCodecException    if one of the fields contains a class with no registered codec
     */
    public ObjectCodec(
            byte reservedByte,
            Class<T> tClass,
            CodecCache cache) throws CodecGenerationException, MissingCodecException {
        this.reservedByte = reservedByte;
        this.tClass = tClass;

        creator = generateConstructor(tClass);
        List<Field> toExtract = fieldsInOrder(tClass);

        for (Field field : toExtract) {
            if (!field.getType().equals(tClass) &&
                    cache.codecForClass(field.getType()) == null) {
                throw new MissingCodecException(field.getType());
            }
        }
        List<Method> possibleGetters = getterLikeMethods(tClass);
        List<Method> possibleSetters = setterLikeMethods(tClass);

        fields = new FieldCodec[toExtract.size()];

        for (int i = 0; i < toExtract.size(); i++) {
            Field field = toExtract.get(i);
            if (Modifier.isPublic(field.getModifiers())) {
                fields[i] = new DirectFieldCodec(field, codecForField(field, cache));
            } else {
                Method getter = findGetter(field, possibleGetters);
                Method setter = findSetter(field, possibleSetters);
                Codec codec = codecForField(field, cache);
                fields[i] = createCodec(field, getter, setter, codec);
            }
        }
    }

    private FieldCodec createCodec(
            Field field,
            Method getter,
            Method setter,
            Codec codec) throws CodecGenerationException {
        if (boolean.class.equals(field.getType())) {
            return new BooleanFieldCodec(getter, setter, codec);
        } else if (byte.class.equals(field.getType())) {
            return new ByteFieldCodec(getter, setter, codec);
        } else if (char.class.equals(field.getType())) {
            return new CharFieldCodec(getter, setter, codec);
        } else if (short.class.equals(field.getType())) {
            return new ShortFieldCodec(getter, setter, codec);
        } else if (int.class.equals(field.getType())) {
            return new IntFieldCodec(getter, setter, codec);
        } else if (long.class.equals(field.getType())) {
            return new LongFieldCodec(getter, setter, codec);
        } else if (float.class.equals(field.getType())) {
            return new FloatFieldCodec(getter, setter, codec);
        } else if (double.class.equals(field.getType())) {
            return new DoubleFieldCodec(getter, setter, codec);
        } else {
            return new AccessingFieldCodec(getter, setter, codec);
        }
    }

    private Creator generateConstructor(Class<T> tClass) throws CodecGenerationException {
        try {
            return LambdaMetaFactoryUtils.wrapNorArgsConstructor(Creator.class, tClass.getConstructor(), Object.class);
        } catch (NoSuchMethodException e) {
            throw new CodecGenerationException("Missing no arg constructor for " + tClass.getSimpleName(), e);
        }
    }

    private Codec codecForField(Field field, CodecCache cache) throws MissingCodecException {
        return field.getType().equals(tClass) ? this : cache.codecForClass(field.getType());
    }

    private Method findSetter(Field field, List<Method> possibleSetters) throws CodecGenerationException {
        for (Method method : possibleSetters) {
            if (method.getParameterTypes()[0].equals(field.getType())) {
                String lcField = field.getName().toLowerCase(Locale.ENGLISH);
                String lcMethod = method.getName().toLowerCase(Locale.ENGLISH);
                if (lcMethod.startsWith("set") &&
                        lcMethod.length() == lcField.length() + 3 &&
                        lcMethod.endsWith(lcField)) {
                    return method;
                }
            }
        }
        throw new CodecGenerationException("No suitable setter for " + field.getName(), null);
    }

    private List<Method> setterLikeMethods(Class<T> tClass) {
        List<Method> possibleSetters = new LinkedList<>();
        Class clazz = tClass;
        while (!clazz.equals(Object.class)) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (Modifier.isPublic(method.getModifiers()) &&
                        !Modifier.isAbstract(method.getModifiers()) &&
                        method.getReturnType().equals(void.class) &&
                        method.getParameterCount() == 1) {
                    possibleSetters.add(method);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return possibleSetters;
    }

    private Method findGetter(Field field, List<Method> possibleGetters) throws CodecGenerationException {
        for (Method method : possibleGetters) {
            if (method.getReturnType().equals(field.getType())) {
                String lcField = field.getName().toLowerCase(Locale.ENGLISH);
                String lcMethod = method.getName().toLowerCase(Locale.ENGLISH);
                if (lcMethod.startsWith("get") &&
                        lcMethod.length() == lcField.length() + 3 &&
                        lcMethod.endsWith(lcField)) {
                    return method;
                }
                if (field.getType().equals(boolean.class) &&
                        lcMethod.startsWith("is") &&
                        lcMethod.length() == lcField.length() + 2 &&
                        lcMethod.endsWith(lcField)) {
                    return method;
                }
            }
        }
        throw new CodecGenerationException("No suitable getter for " + field.getName(), null);
    }

    private List<Field> fieldsInOrder(Class<T> tClass) throws CodecGenerationException {
        List<Field> toExtract = new LinkedList<>();
        Class clazz = tClass;
        while (!clazz.equals(Object.class)) {
            for (Field field : clazz.getDeclaredFields()) {
                if (!Modifier.isTransient(field.getModifiers()) &&
                        !Modifier.isStatic(field.getModifiers())) {
                    if (Modifier.isFinal(field.getModifiers())) {
                        throw new CodecGenerationException("Would not be able to set the value of the final field " + field.getName(), null);
                    }
                    toExtract.add(field);
                }
            }
            clazz = clazz.getSuperclass();
        }
        checkForDuplicates(toExtract);
        toExtract.sort((Field field1, Field field2) -> (field1.getName().compareTo(field2.getName())));
        return toExtract;
    }

    /**
     * @param toExtract the fields to extract form the class
     * @throws CodecGenerationException if at least two fields have the same name
     */
    private void checkForDuplicates(List<Field> toExtract) throws CodecGenerationException {
        Set<String> fields = new HashSet<>(CodecUtils.capacityForKnownSize(toExtract.size()));
        for (Field field : toExtract) {
            if (!fields.add(field.getName())) {
                throw new CodecGenerationException("Found several fields called " + field.getName(), null);
            }
        }
    }

    private List<Method> getterLikeMethods(Class<T> tClass) {
        List<Method> possibleGetters = new LinkedList<>();
        Class clazz = tClass;
        while (!clazz.equals(Object.class)) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (Modifier.isPublic(method.getModifiers()) &&
                        !Modifier.isAbstract(method.getModifiers()) &&
                        !method.getReturnType().equals(void.class) &&
                        method.getParameterCount() == 0) {
                    possibleGetters.add(method);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return possibleGetters;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final T read(InputStreamWrapper wrapper) throws IOException {
        Byte marker;
        if ((marker = wrapper.read1()) != reservedByte) {
            if (marker == NULL) {
                return null;
            }
            throw new IOException("Could not deserialize using " + this.getClass().getSimpleName());
        }
        T instance;
        try {
            instance = (T) creator.build();
        } catch (Exception e) {
            //Really should not be possible
            throw new IOException("Could not create a new instance of " + tClass.getSimpleName(), e);
        }
        for (FieldCodec writer : fields) {
            writer.read(wrapper, instance);
        }
        return instance;
    }

    @Override
    public final void write(OutputStreamWrapper wrapper, T value) throws IOException {
        if (value == null) {
            wrapper.writeByte(NULL);
            return;
        }
        wrapper.writeByte(reservedByte);
        for (FieldCodec writer : fields) {
            writer.write(wrapper, value);
        }
    }

    @Override
    public byte[] reservedBytes() {
        return new byte[]{reservedByte};
    }

    @Override
    public final boolean writes(Class clazz) {
        return tClass.equals(clazz);
    }
}

