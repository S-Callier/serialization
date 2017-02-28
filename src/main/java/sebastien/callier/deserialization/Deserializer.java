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

package sebastien.callier.deserialization;

import sebastien.callier.codec.Codec;
import sebastien.callier.codec.CodecCache;
import sebastien.callier.stream.FastInputStream;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author Sebastien Callier
 * @since 2017
 */
public class Deserializer implements Closeable {
    private final InputStreamWrapper wrapper;
    private final FastInputStream input;
    private final CodecCache codecCache;

    private Deserializer(CodecCache codecCache, FastInputStream input) throws IOException {
        this.codecCache = codecCache;
        this.input = input;
        this.wrapper = new InputStreamWrapper(input);
    }

    public static Deserializer newInstance(
            CodecCache codecCache,
            byte[] data,
            int offset,
            int length) throws IOException {
        return new Deserializer(codecCache, new FastInputStream(data, offset, length));
    }

    public static Deserializer newInstance(
            CodecCache codecCache,
            byte[] data) throws IOException {
        return new Deserializer(codecCache, new FastInputStream(data));
    }

    public Object read() throws IOException {
        return codecCache.get(wrapper.peek()).read(wrapper);
    }

    public <T> T read(Codec<T> codec) throws IOException {
        return codec.read(wrapper);
    }

    public int available() {
        return input.available();
    }

    @Override
    public void close() throws IOException {
        input.close();
    }
}
