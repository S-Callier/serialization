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
import sebastien.callier.serialization.OutputStreamWrapper;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * @author Sebastien Callier
 * @since 2017
 */
public final class DirectFieldCodec implements FieldCodec {
    private final Field field;
    private final Codec codec;

    public DirectFieldCodec(
            Field field,
            Codec codec) {
        this.field = field;
        this.codec = codec;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void write(OutputStreamWrapper wrapper, Object instance) throws IOException {
        try {
            codec.write(wrapper, field.get(instance));
        } catch (IllegalAccessException e) {
            throw new IOException("Unexpected exception.", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void read(InputStreamWrapper wrapper, Object instance) throws IOException {
        try {
            field.set(instance, codec.read(wrapper));
        } catch (IllegalAccessException e) {
            throw new IOException("Unexpected exception.", e);
        }
    }
}