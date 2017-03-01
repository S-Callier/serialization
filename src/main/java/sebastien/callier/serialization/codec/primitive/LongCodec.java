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
public final class LongCodec implements Codec<Long> {
    private final byte size1;
    private final byte size2;
    private final byte size3;
    private final byte size4;
    private final byte size5;
    private final byte size6;
    private final byte size7;
    private final byte size8;

    public LongCodec(byte reservedByte) {
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
    public Long read(InputStreamWrapper wrapper) throws IOException {
        byte marker = wrapper.read1();
        if (marker == NULL) {
            return null;
        }
        switch (marker - size1) {
            case 0:
                return (long) wrapper.read1();
            case 1:
                return (long) wrapper.read2();
            case 2:
                return (long) wrapper.read3();
            case 3:
                return (long) wrapper.read4();
            case 4:
                return wrapper.read5();
            case 5:
                return wrapper.read6();
            case 6:
                return wrapper.read7();
            case 7:
                return wrapper.read8();
            default:
                throw new IOException("Could not deserialize as a long.");
        }
    }

    @Override
    public void write(OutputStreamWrapper wrapper, Long value) throws IOException {
        if (value == null) {
            wrapper.writeByte(NULL);
            return;
        }
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
                        wrapper.writeByteAnd4(size4, value.intValue());
                    } else {
                        wrapper.writeByteAnd3(size3, value.intValue());
                    }
                } else {
                    wrapper.writeByteAnd2(size2, value.intValue());
                }
            } else {
                wrapper.writeByteAnd1(size1, (byte) (value & 0xFF));
            }
        } else if (value < 2147483648L) {
            if (value < 8388608) {
                if (value < 32768) {
                    wrapper.writeByteAnd2(size2, value.intValue());
                } else {
                    wrapper.writeByteAnd3(size3, value.intValue());
                }
            } else {
                wrapper.writeByteAnd4(size4, value.intValue());
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
        return Long.class.equals(clazz) || long.class.equals(clazz);
    }
}