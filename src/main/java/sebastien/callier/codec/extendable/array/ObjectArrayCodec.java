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

package sebastien.callier.codec.extendable.array;

import sebastien.callier.codec.Codec;
import sebastien.callier.codec.CodecCache;
import sebastien.callier.deserialization.InputStreamWrapper;
import sebastien.callier.exceptions.MissingCodecException;
import sebastien.callier.serialization.OutputStreamWrapper;

import java.io.IOException;
import java.lang.reflect.Array;

/**
 * @author Sebastien Callier
 * @since 2017
 */
public abstract class ObjectArrayCodec<T> implements Codec<T[]> {
    private final Codec<T> elementCodec;
    private final Class<T> tClass;
    private final byte reservedByte;

    public ObjectArrayCodec(
            byte reservedByte,
            Class<T> tClass,
            CodecCache codecCache) throws MissingCodecException {
        this(reservedByte, tClass, codecCache.codecForClass(tClass));
    }

    public ObjectArrayCodec(
            byte reservedByte,
            Class<T> tClass,
            Codec<T> elementCodec) {
        super();
        this.reservedByte = reservedByte;
        this.tClass = tClass;
        this.elementCodec = elementCodec;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T[] read(InputStreamWrapper wrapper) throws IOException {
        byte marker = wrapper.read1();
        if (marker == NULL) {
            return null;
        } else if (marker == reservedByte) {
            int size = wrapper.read4();
            T[] result = (T[]) Array.newInstance(tClass, size);
            for (int i = 0; i < size; i++) {
                result[i] = elementCodec.read(wrapper);
            }
            return result;
        }
        throw new IOException("Unable to deserialize.");
    }

    @Override
    public void write(OutputStreamWrapper wrapper, T[] array) throws IOException {
        if (array == null) {
            wrapper.writeByte(NULL);
            return;
        }
        wrapper.writeByteAnd4(reservedByte, array.length);
        for (T value : array) {
            elementCodec.write(wrapper, value);
        }
    }

    @Override
    public byte[] reservedBytes() {
        return new byte[]{reservedByte};
    }
}
