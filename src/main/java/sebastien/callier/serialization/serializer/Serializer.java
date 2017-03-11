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

package sebastien.callier.serialization.serializer;

import sebastien.callier.serialization.codec.Codec;
import sebastien.callier.serialization.codec.CodecCache;
import sebastien.callier.serialization.exceptions.MissingCodecException;
import sebastien.callier.serialization.stream.FastOutputStream;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;

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

    /**
     * Returns the backing byte array as is.
     * Its size will most likely be larger than the number of valid bytes in the buffer.
     * Please check {@link #currentSize()}
     * This method is copy free just like {@link #asByteBuffer()}.
     *
     * @return the data written so far
     */
    public byte[] getByteArray() throws IOException {
        return out.getByteArray();
    }

    /**
     * Returns a copy of the backing array with the minimum required size.
     * Consider using {@link #getByteArray()} or {@link #asByteBuffer()} when possible.
     *
     * @return the data written so far
     */
    public byte[] asByteArray() throws IOException {
        return out.asByteArray();
    }

    /**
     * Returns a ByteBuffer wrapping the backing array.
     * This method is copy free just like {@link #getByteArray()}.
     *
     * @return the data written so far
     */
    public ByteBuffer asByteBuffer() {
        return ByteBuffer.wrap(out.getByteArray(), 0, out.getSize());
    }

    /**
     * @return the current size of the serialized data.
     */
    public int currentSize() {
        return out.getSize();
    }
}
