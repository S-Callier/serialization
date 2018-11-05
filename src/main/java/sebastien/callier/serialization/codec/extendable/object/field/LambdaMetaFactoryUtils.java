/*
 * Copyright 2018 Sebastien Callier
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

package sebastien.callier.serialization.codec.extendable.object.field;

import sebastien.callier.serialization.exceptions.CodecGenerationException;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author Sebastien Callier
 * @since 2018
 */
public final class LambdaMetaFactoryUtils {
    private LambdaMetaFactoryUtils() {
        super();
    }

    /**
     * Wrap the getter in a lambda (getter must be a getter with with the method name "get")
     *
     * @param getter     the getter interface to wrap
     * @param method     the real getter
     * @param returnType the return type to use
     * @return the getter instance
     */
    public static <T> T wrapGetter(
            Class<T> getter,
            Method method,
            Class returnType) throws CodecGenerationException {
        try {
            MethodHandles.Lookup caller = MethodHandles.lookup();
            MethodHandle handle = caller.unreflect(method);
            return getter.cast(LambdaMetafactory
                    .metafactory(
                            caller,
                            "get",
                            MethodType.methodType(getter),
                            MethodType.methodType(returnType, Object.class),
                            handle,
                            handle.type())
                    .getTarget()
                    .invoke());
        } catch (Throwable e) {
            throw new CodecGenerationException("Could not generate the function to access the getter " + method.getName(), e);
        }
    }

    /**
     * Wrap the setter in a lambda (setter must be a setter with with the method name "set")
     *
     * @param setter    the setter interface to wrap
     * @param method    the real getter
     * @param fieldType the type of field to set
     * @return the setter instance
     */
    public static <T> T wrapSetter(
            Class<T> setter,
            Method method,
            Class fieldType) throws CodecGenerationException {
        try {
            MethodHandles.Lookup caller = MethodHandles.lookup();
            MethodHandle handle = caller.unreflect(method);
            return setter.cast(LambdaMetafactory
                    .metafactory(
                            caller,
                            "set",
                            MethodType.methodType(setter),
                            MethodType.methodType(Void.TYPE, Object.class, fieldType),
                            handle,
                            handle.type())
                    .getTarget()
                    .invoke());
        } catch (Throwable e) {
            throw new CodecGenerationException("Could not generate the function to access the setter " + method.getName(), e);
        }
    }

    /**
     * Wrap the constructor in a lambda (creator must be a constructor with with the method name "build")
     *
     * @param creator     the setter creator to wrap
     * @param constructor the real constructor
     * @param declared    the type of object to build
     * @return the creator instance
     */
    public static <T> T wrapNorArgsConstructor(
            Class<T> creator,
            Constructor constructor,
            Class declared) throws CodecGenerationException {
        try {
            MethodHandles.Lookup caller = MethodHandles.lookup();
            MethodHandle handle = caller.unreflectConstructor(constructor);
            return creator.cast(LambdaMetafactory
                    .metafactory(
                            caller,
                            "build",
                            MethodType.methodType(creator),
                            MethodType.methodType(declared),
                            handle,
                            handle.type())
                    .getTarget()
                    .invoke());
        } catch (Throwable e) {
            throw new CodecGenerationException("Could not generate the function to access the no arg constructor " + constructor.getName(), e);
        }
    }
}
