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

package sebastien.callier.codec.primitive;

import sebastien.callier.codec.Codec;
import sebastien.callier.deserialization.InputStreamWrapper;
import sebastien.callier.serialization.OutputStreamWrapper;

import java.io.IOException;

/**
 * @author Sebastien Callier
 * @since 2017
 */
public final class FloatCodec implements Codec<Float> {
    private final byte size1;
    private final byte size2;
    private final byte size3;
    private final byte size4;

    public FloatCodec(byte reservedByte) {
        super();
        size1 = reservedByte;
        size2 = (byte) (reservedByte + 1);
        size3 = (byte) (reservedByte + 2);
        size4 = (byte) (reservedByte + 3);
    }

    @Override
    public Float read(InputStreamWrapper wrapper) throws IOException {
        byte marker = wrapper.read1();
        if (marker == NULL) {
            return null;
        }
        switch (marker - size1) {
            case 0:
                return Float.intBitsToFloat((int) wrapper.read1());
            case 1:
                return Float.intBitsToFloat((int) wrapper.read2());
            case 2:
                return Float.intBitsToFloat(wrapper.read3());
            case 3:
                return Float.intBitsToFloat(wrapper.read4());
            default:
                throw new IOException("Could not deserialize as a float.");
        }
    }

    @Override
    public void write(OutputStreamWrapper wrapper, Float fValue) throws IOException {
        if (fValue == null) {
            wrapper.writeByte(NULL);
            return;
        }
        int value = Float.floatToRawIntBits(fValue);
        if (value < -128) {
            if (value < -32768) {
                if (value < -8388608) {
                    wrapper.writeByteAnd4(size4, value);
                } else {
                    wrapper.writeByteAnd3(size3, value);
                }
            } else {
                wrapper.writeByteAnd2(size2, value);
            }
        } else if (value < 32768) {
            if (value < 128) {
                wrapper.writeByteAnd1(size1, (byte) (value & 0xFF));
            } else {
                wrapper.writeByteAnd2(size2, value);
            }
        } else if (value < 8388608) {
            wrapper.writeByteAnd3(size3, value);
        } else {
            wrapper.writeByteAnd4(size4, value);
        }
    }

    @Override
    public byte[] reservedBytes() {
        return new byte[]{size1, size2, size3, size4};
    }

    @Override
    public boolean writes(Class clazz) {
        return Float.class.equals(clazz) || float.class.equals(clazz);
    }
}