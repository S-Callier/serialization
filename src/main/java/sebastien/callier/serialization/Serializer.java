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

package sebastien.callier.serialization;

import sebastien.callier.codec.Codec;
import sebastien.callier.codec.CodecCache;
import sebastien.callier.exceptions.MissingCodecException;
import sebastien.callier.stream.FastOutputStream;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author Sebastien Callier
 * @since 2017
 */
public class Serializer implements Closeable {
    private final OutputStreamWrapper wrapper;
    private final FastOutputStream out;

    private final CodecCache codecCache;

    public Serializer(CodecCache codecCache) throws IOException {
        this.codecCache = codecCache;
        this.out = new FastOutputStream();
        this.wrapper = new OutputStreamWrapper(out);
    }

    public Serializer(
            CodecCache codecCache,
            int initialSize) throws IOException {
        this.codecCache = codecCache;
        this.out = new FastOutputStream(initialSize);
        this.wrapper = new OutputStreamWrapper(out);
    }

    public <T> void append(T value) throws IOException {
        Codec<T> codec = codecCache.codecFor(value);
        if (codec == null) {
            throw new MissingCodecException(value.getClass());
        }
        codec.write(wrapper, value);
    }

    public <T> void append(T value, Codec<T> codec) throws IOException {
        codec.write(wrapper, value);
    }

    @Override
    public void close() throws IOException {
        wrapper.close();
    }

    public byte[] getByteArray() throws IOException {
        return out.getByteArray();
    }

    public byte[] asByteArray() throws IOException {
        return out.asByteArray();
    }

    /**
     * @return the current size of the serialized data.
     */
    public int currentSize() {
        return out.getSize();
    }
}
