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

package sebastien.callier.serialization.codec.array;

import sebastien.callier.serialization.codec.Codec;
import sebastien.callier.serialization.deserializer.InputStreamWrapper;
import sebastien.callier.serialization.serializer.OutputStreamWrapper;
import sebastien.callier.serialization.utils.CodecUtils;

import java.io.IOException;

/**
 * @author Sebastien Callier
 * @since 2017
 */
public final class BooleanArrayCodec implements Codec<boolean[]> {
    private final byte size1;

    public BooleanArrayCodec(byte reservedByte) {
        super();
        size1 = reservedByte;
    }

    @Override
    public boolean[] read(InputStreamWrapper wrapper) throws IOException {
        Integer size = CodecUtils.readSize(wrapper, size1);
        if (size == null) {
            return null;
        }
        boolean[] array = new boolean[size];
        int i = 0;
        byte value = 0;
        int read = 8;
        while (i < size) {
            if (read == 8) {
                value = wrapper.read1();
                read = 0;
            }
            array[i++] = (value & 0b10000000) != 0;
            value <<= 1;
            read++;
        }
        return array;
    }

    @Override
    public void write(
            OutputStreamWrapper wrapper,
            boolean[] array) throws IOException {
        if (array == null) {
            wrapper.writeByte(NULL);
            return;
        }
        CodecUtils.writeSize(wrapper, array.length, size1);
        int currentCount = 0;
        byte value = 0;
        for (boolean element : array) {
            value = (byte) (value << 1 | (element ? 1 : 0));
            if (currentCount == 7) {
                currentCount = 0;
                wrapper.writeByte(value);
                value = 0;
            } else {
                currentCount++;
            }
        }
        if (currentCount > 0) {
            wrapper.writeByte((byte) (value << (8 - currentCount)));
        }
    }

    @Override
    public byte[] reservedBytes() {
        return new byte[]{size1, (byte) (size1 + 1), (byte) (size1 + 2), (byte) (size1 + 3)};
    }

    @Override
    public boolean writes(Class clazz) {
        return boolean[].class.equals(clazz);
    }
}
