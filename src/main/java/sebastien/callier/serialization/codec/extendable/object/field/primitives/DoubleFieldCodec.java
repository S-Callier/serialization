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

package sebastien.callier.serialization.codec.extendable.object.field.primitives;

import sebastien.callier.serialization.codec.Codec;
import sebastien.callier.serialization.codec.extendable.object.field.FieldCodec;
import sebastien.callier.serialization.codec.extendable.object.field.LambdaMetaFactoryUtils;
import sebastien.callier.serialization.deserializer.InputStreamWrapper;
import sebastien.callier.serialization.exceptions.CodecGenerationException;
import sebastien.callier.serialization.serializer.OutputStreamWrapper;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author Sebastien Callier
 * @since 2018
 */
public class DoubleFieldCodec implements FieldCodec {
    private final Getter get;
    private final Setter set;
    private final Codec codec;

    public DoubleFieldCodec(
            Method getter,
            Method setter,
            Codec codec) throws CodecGenerationException {
        super();
        get = LambdaMetaFactoryUtils.wrapGetter(Getter.class, getter, double.class);
        set = LambdaMetaFactoryUtils.wrapSetter(Setter.class, setter, double.class);
        this.codec = codec;
    }

    @FunctionalInterface
    public interface Getter {
        double get(Object instance);
    }

    @FunctionalInterface
    public interface Setter {
        void set(Object instance, double value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void write(OutputStreamWrapper wrapper, Object instance) throws IOException {
        codec.write(wrapper, get.get(instance));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void read(InputStreamWrapper wrapper, Object instance) throws IOException {
        set.set(instance, (Double) codec.read(wrapper));
    }
}