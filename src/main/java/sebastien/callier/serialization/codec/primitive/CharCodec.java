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

package sebastien.callier.serialization.codec.primitive;

import sebastien.callier.serialization.codec.Codec;
import sebastien.callier.serialization.deserializer.InputStreamWrapper;
import sebastien.callier.serialization.serializer.OutputStreamWrapper;

import java.io.IOException;

/**
 * @author Sebastien Callier
 * @since 2017
 */
public final class CharCodec implements Codec<Character> {
    private final byte size1;
    private final byte size2;
    private final byte size3;

    public CharCodec(byte reservedByte) {
        super();
        size1 = reservedByte;
        size2 = (byte) (reservedByte + 1);
        size3 = (byte) (reservedByte + 2);
    }

    @Override
    public Character read(InputStreamWrapper wrapper) throws IOException {
        byte marker = wrapper.read1();
        if (marker == NULL) {
            return null;
        }
        switch (marker - size1) {
            case 0:
                return (char) wrapper.read1();
            case 1:
                return (char) wrapper.read2();
            case 2:
                return (char) wrapper.read3();
            default:
                throw new IOException("Could not deserialize as a short.");
        }
    }

    @Override
    public void write(OutputStreamWrapper wrapper, Character value) throws IOException {
        if (value == null) {
            wrapper.writeByte(NULL);
            return;
        }
        if (value < 32768) {
            if (value < 128) {
                wrapper.writeByteAnd1(size1, (byte) (value & 0xFF));
            } else {
                wrapper.writeByteAnd2(size2, value);
            }
        } else {
            wrapper.writeByteAnd3(size3, value);
        }
    }

    @Override
    public byte[] reservedBytes() {
        return new byte[]{size1, size2, size3};
    }

    @Override
    public boolean writes(Class clazz) {
        return Character.class.equals(clazz) || char.class.equals(clazz);
    }
}