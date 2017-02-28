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
public final class DoubleCodec implements Codec<Double> {
    private final byte size1;
    private final byte size2;
    private final byte size3;
    private final byte size4;
    private final byte size5;
    private final byte size6;
    private final byte size7;
    private final byte size8;

    public DoubleCodec(byte reservedByte) {
        super();
        size1 = reservedByte;
        size2 = (byte) (reservedByte + 1);
        size3 = (byte) (reservedByte + 2);
        size4 = (byte) (reservedByte + 3);
        size5 = (byte) (reservedByte + 4);
        size6 = (byte) (reservedByte + 5);
        size7 = (byte) (reservedByte + 6);
        size8 = (byte) (reservedByte + 7);
    }

    @Override
    public Double read(InputStreamWrapper wrapper) throws IOException {
        byte marker = wrapper.read1();
        if (marker == NULL) {
            return null;
        }
        switch (marker - size1) {
            case 0:
                return Double.longBitsToDouble((long) wrapper.read1());
            case 1:
                return Double.longBitsToDouble((long) wrapper.read2());
            case 2:
                return Double.longBitsToDouble(wrapper.read3());
            case 3:
                return Double.longBitsToDouble((long) wrapper.read4());
            case 4:
                return Double.longBitsToDouble(wrapper.read5());
            case 5:
                return Double.longBitsToDouble(wrapper.read6());
            case 6:
                return Double.longBitsToDouble(wrapper.read7());
            case 7:
                return Double.longBitsToDouble(wrapper.read8());
            default:
                throw new IOException("Could not deserialize as a double.");
        }
    }

    @Override
    public void write(OutputStreamWrapper wrapper, Double dValue) throws IOException {
        if (dValue == null) {
            wrapper.writeByte(NULL);
            return;
        }
        long value = Double.doubleToRawLongBits(dValue);
        if (value < -2147483648) {
            if (value < -549755813888L) {
                if (value < -140737488355328L) {
                    if (value < -36028797018963968L) {
                        wrapper.writeByteAnd8(size8, value);
                    } else {
                        wrapper.writeByteAnd7(size7, value);
                    }
                } else {
                    wrapper.writeByteAnd6(size6, value);
                }
            } else {
                wrapper.writeByteAnd5(size5, value);
            }
        } else if (value < 128) {
            if (value < -128) {
                if (value < -32768) {
                    if (value < -8388608) {
                        wrapper.writeByteAnd4(size4, (int) value);
                    } else {
                        wrapper.writeByteAnd3(size3, (int) value);
                    }
                } else {
                    wrapper.writeByteAnd2(size2, (int) value);
                }
            } else {
                wrapper.writeByteAnd1(size1, (byte) (value & 0xFF));
            }
        } else if (value < 2147483648L) {
            if (value < 8388608) {
                if (value < 32768) {
                    wrapper.writeByteAnd2(size2, (int) value);
                } else {
                    wrapper.writeByteAnd3(size3, (int) value);
                }
            } else {
                wrapper.writeByteAnd4(size4, (int) value);
            }
        } else if (value < 140737488355328L) {
            if (value < 549755813888L) {
                wrapper.writeByteAnd5(size5, value);
            } else {
                wrapper.writeByteAnd6(size6, value);
            }
        } else if (value < 36028797018963968L) {
            wrapper.writeByteAnd7(size7, value);
        } else {
            wrapper.writeByteAnd8(size8, value);
        }
    }

    @Override
    public byte[] reservedBytes() {
        return new byte[]{size1, size2, size3, size4, size5, size6, size7, size8};
    }

    @Override
    public boolean writes(Class clazz) {
        return Double.class.equals(clazz) || double.class.equals(clazz);
    }
}