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

package sebastien.callier.serialization.codec.object;

import sebastien.callier.serialization.codec.Codec;
import sebastien.callier.serialization.codec.CodecCache;
import sebastien.callier.serialization.deserializer.InputStreamWrapper;
import sebastien.callier.serialization.serializer.OutputStreamWrapper;

import java.io.IOException;

/**
 * @author Sebastien Callier
 * @since 2017
 */
public final class AnyCodec implements Codec<Object> {
    private final CodecCache codecCache;

    public AnyCodec(CodecCache codecCache) {
        super();
        this.codecCache = codecCache;
    }

    @Override
    public Object read(InputStreamWrapper wrapper) throws IOException {
        return codecCache.get(wrapper.peek()).read(wrapper);
    }

    @Override
    public void write(OutputStreamWrapper wrapper, Object value) throws IOException {
        codecCache.codecFor(value).write(wrapper, value);
    }

    @Override
    public byte[] reservedBytes() {
        return new byte[]{};
    }

    @Override
    public boolean writes(Class clazz) {
        return false;
    }
}
