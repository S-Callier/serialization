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

package sebastien.callier.codec.extendable.object.field;

import sebastien.callier.codec.Codec;
import sebastien.callier.deserialization.InputStreamWrapper;
import sebastien.callier.exceptions.CodecGenerationException;
import sebastien.callier.serialization.OutputStreamWrapper;

import java.io.IOException;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author Sebastien Callier
 * @since 2017
 */
public final class AccessingFieldCodec implements FieldCodec {
    private final Function get;
    private final BiConsumer set;
    private final Codec codec;

    public AccessingFieldCodec(
            Method getter,
            Method setter,
            Codec codec) throws CodecGenerationException {
        get = getter(getter);
        set = setter(setter);
        this.codec = codec;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void write(OutputStreamWrapper wrapper, Object instance) throws IOException {
        codec.write(wrapper, get.apply(instance));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void read(InputStreamWrapper wrapper, Object instance) throws IOException {
        set.accept(instance, codec.read(wrapper));
    }

    private Function getter(Method method) throws CodecGenerationException {
        try {
            MethodHandles.Lookup caller = MethodHandles.lookup();
            MethodHandle handle = caller.unreflect(method);
            return (Function) LambdaMetafactory
                    .metafactory(
                            caller,
                            "apply",
                            MethodType.methodType(Function.class),
                            MethodType.methodType(Object.class, Object.class),
                            handle,
                            handle.type())
                    .getTarget()
                    .invoke();
        } catch (Throwable e) {
            throw new CodecGenerationException("Could not generate the function to access the getter " + method.getName(), e);
        }
    }

    private BiConsumer setter(Method method) throws CodecGenerationException {
        try {
            MethodHandles.Lookup caller = MethodHandles.lookup();
            MethodHandle handle = caller.unreflect(method);
            return (BiConsumer) LambdaMetafactory
                    .metafactory(
                            caller,
                            "accept",
                            MethodType.methodType(BiConsumer.class),
                            MethodType.methodType(Void.TYPE, Object.class, Object.class),
                            handle,
                            handle.type())
                    .getTarget()
                    .invoke();
        } catch (Throwable e) {
            throw new CodecGenerationException("Could not generate the function to access the setter " + method.getName(), e);
        }
    }
}
