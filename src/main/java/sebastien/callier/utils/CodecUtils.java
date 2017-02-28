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

package sebastien.callier.utils;

import sebastien.callier.codec.Codec;
import sebastien.callier.deserialization.InputStreamWrapper;
import sebastien.callier.serialization.OutputStreamWrapper;

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
        if (size < 65792) {
            if (size < 256) {
                wrapper.writeByteAnd1(value, (byte) ((size - 128) & 0xFF));
            } else {
                wrapper.writeByteAnd2((byte) (value + 1), size - 32896);
            }
        } else if (size < 16843008) {
            wrapper.writeByteAnd3((byte) (value + 2), size - 8421504);
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
                return (int) wrapper.read1() + 128;
            case 1:
                return (int) wrapper.read2() + 32896;
            case 2:
                return wrapper.read3() + 8421504;
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
