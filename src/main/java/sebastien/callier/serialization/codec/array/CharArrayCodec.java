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
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author Sebastien Callier
 * @since 2017
 */
public final class CharArrayCodec implements Codec<char[]> {
    private final byte size1;

    public CharArrayCodec(byte reservedByte) {
        super();
        size1 = reservedByte;
    }

    @Override
    public char[] read(InputStreamWrapper wrapper) throws IOException {
        Integer size = CodecUtils.readSize(wrapper, size1);
        if (size == null) {
            return null;
        }
        if (size == 0) {
            return new char[0];
        }
        CharBuffer buffer = StandardCharsets.UTF_8.decode(ByteBuffer.wrap(wrapper.readBytes(size)));
        return Arrays.copyOfRange(buffer.array(), 0, buffer.limit());
    }

    @Override
    public void write(
            OutputStreamWrapper wrapper,
            char[] array) throws IOException {
        if (array == null) {
            wrapper.writeByte(NULL);
            return;
        }
        //Not very fast, but not using String at least?
        ByteBuffer buffer = StandardCharsets.UTF_8.encode(CharBuffer.wrap(array));
        CodecUtils.writeSize(wrapper, buffer.limit(), size1);
        wrapper.writeBytes(buffer.array(), buffer.limit());
    }

    @Override
    public byte[] reservedBytes() {
        return new byte[]{size1, (byte) (size1 + 1), (byte) (size1 + 2), (byte) (size1 + 3)};
    }

    @Override
    public boolean writes(Class clazz) {
        return char[].class.equals(clazz);
    }
}