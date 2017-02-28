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

package sebastien.callier.codec.object;

import sebastien.callier.codec.Codec;
import sebastien.callier.deserialization.InputStreamWrapper;
import sebastien.callier.serialization.OutputStreamWrapper;
import sebastien.callier.utils.CodecUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author Sebastien Callier
 * @since 2017
 */
public final class StringCodec implements Codec<String> {
    private final byte optimizedFrom;
    private final byte size1;

    public StringCodec(
            byte reservedByte,
            int optimizedCount) {
        super();
        this.optimizedFrom = reservedByte;
        this.size1 = (byte) (reservedByte + optimizedCount);
    }

    @Override
    public String read(InputStreamWrapper wrapper) throws IOException {
        Integer size;
        byte read = wrapper.peek();
        if (read >= optimizedFrom && read < size1) {
            size = read - optimizedFrom;
            //skip it
            wrapper.read1();
        } else {
            size = CodecUtils.readSize(wrapper, size1);
        }
        if (size == null) {
            return null;
        }
        if (size == 0) {
            return "";
        }
        return new String(wrapper.readBytes(size), StandardCharsets.UTF_8);
    }

    @Override
    public void write(
            OutputStreamWrapper wrapper,
            String value) throws IOException {
        if (value == null) {
            wrapper.writeByte(NULL);
            return;
        }
        byte[] data = value.getBytes(StandardCharsets.UTF_8);
        int length = data.length;
        if (length < size1 - optimizedFrom) {
            wrapper.writeByte((byte) ((optimizedFrom + length) & 0xFF));
        } else {
            CodecUtils.writeSize(wrapper, length, size1);
        }
        if (length != 0) {
            wrapper.writeBytes(data);
        }
    }

    @Override
    public byte[] reservedBytes() {
        int size = size1 - optimizedFrom + 4;
        byte[] reserved = new byte[size];
        for (int i = 0; i < size; i++) {
            reserved[i] = (byte) ((optimizedFrom + i) & 0xFF);
        }
        return reserved;
    }

    @Override
    public boolean writes(Class clazz) {
        return String.class.equals(clazz);
    }
}