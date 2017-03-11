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

package sebastien.callier.serialization.utils;

import sebastien.callier.serialization.codec.Codec;
import sebastien.callier.serialization.deserializer.InputStreamWrapper;
import sebastien.callier.serialization.serializer.OutputStreamWrapper;

import java.io.IOException;

/**
 * @author Sebastien Callier
 * @since 2017
 */
public final class CodecUtils {
    private static final int MAX_POWER_OF_TWO = 1 << 30;

    private CodecUtils() {
        super();
    }

    public static void writeSize(
            OutputStreamWrapper wrapper,
            int size,
            byte value) throws IOException {
        if (size < 65772) {
            if (size < 256) {
                wrapper.writeByteAnd1(value, (byte) ((size - 128) & 0xFF));
            } else {
                wrapper.writeByteAnd2((byte) (value + 1), size - 33024);
            }
        } else if (size < 16842988) {
            wrapper.writeByteAnd3((byte) (value + 2), size - 8454380);
        } else {
            wrapper.writeByteAnd4((byte) (value + 3), size);
        }
    }

    public static Integer readSize(
            InputStreamWrapper wrapper,
            byte value) throws IOException {
        byte read = wrapper.read1();
        if (read == Codec.NULL) {
            return null;
        }
        switch (read - value) {
            case 0:
                //value - Byte.MIN_VALUE
                //read + 128
                //max value 255
                return (int) wrapper.read1() + 128;
            case 1:
                //value - Short.MIN_VALUE + previous max value + 1
                //read + 32768 + 255 + 1
                //max value 65536 + 255 = 65771
                return (int) wrapper.read2() + 33024;
            case 2:
                //value - min value for 3 bytes + previous max value + 1
                //read + 8388608 + 65771 + 1
                //max value 16777216 + 65771 = 16842987
                return wrapper.read3() + 8454380;
            case 3:
                return wrapper.read4();
            default:
                throw new IOException("Could not deserialize the size.");
        }
    }

    public static int capacityForKnownSize(int size) {
        if (size < 3) {
            return size + 1;
        }
        if (size < MAX_POWER_OF_TWO) {
            return size + size / 3;
        }
        return Integer.MAX_VALUE;
    }
}
