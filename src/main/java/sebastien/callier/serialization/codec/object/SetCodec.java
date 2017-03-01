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
import sebastien.callier.serialization.exceptions.MissingCodecException;
import sebastien.callier.serialization.serializer.OutputStreamWrapper;
import sebastien.callier.serialization.utils.CodecUtils;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Sebastien Callier
 * @since 2017
 */
public final class SetCodec implements Codec<Set> {
    private final byte size1;

    private final CodecCache codecCache;

    /**
     * The codec of each of the objects contained in the set will be required to be present
     * at runtime when read and/or write are called
     */
    public SetCodec(byte reservedByte,
                     CodecCache codecCache) {
        this.size1 = reservedByte;
        this.codecCache = codecCache;
    }

    @Override
    public Set read(InputStreamWrapper wrapper) throws IOException {
        Integer size = CodecUtils.readSize(wrapper, size1);
        if (size == null) {
            return null;
        }
        Set<Object> result = new LinkedHashSet<>(CodecUtils.capacityForKnownSize(size));
        for (int i = 0; i < size; i++) {
            result.add(codecCache.get(wrapper.peek()).read(wrapper));
        }
        return result;
    }

    @Override
    public void write(OutputStreamWrapper wrapper, Set value) throws IOException {
        if (value == null) {
            wrapper.writeByte(NULL);
            return;
        }
        CodecUtils.writeSize(wrapper, value.size(), size1);
        for (Object element : value) {
            codecCache.codecFor(element).write(wrapper, element);
        }
    }

    @Override
    public byte[] reservedBytes() {
        return new byte[]{size1, (byte) (size1 + 1), (byte) (size1 + 2), (byte) (size1 + 3)};
    }

    @Override
    public boolean writes(Class clazz) {
        return Set.class.isAssignableFrom(clazz);
    }

    /**
     * The returned codec is compatible with the current instance of SetCodec
     * it should be somewhat faster than the non typed version when referenced
     * during read / write but should not be registered.
     *
     * @param tClass the generic class to deserialize
     * @return a typed version of the set codec
     */
    public <T> Codec<Set<T>> asTypedSet(Class<T> tClass) throws MissingCodecException {
        return new SetCodec.Typed<>(codecCache.codecForClass(tClass));
    }

    private final class Typed<T> implements Codec<Set<T>> {
        private final Codec<T> codec;

        private Typed(Codec<T> codec) {
            this.codec = codec;
        }

        @Override
        public Set<T> read(InputStreamWrapper wrapper) throws IOException {
            Integer size = CodecUtils.readSize(wrapper, size1);
            if (size == null) {
                return null;
            }
            Set<T> result = new LinkedHashSet<>(CodecUtils.capacityForKnownSize(size));
            for (int i = 0; i < size; i++) {
                result.add(codec.read(wrapper));
            }
            return result;
        }

        @Override
        public void write(OutputStreamWrapper wrapper, Set<T> value) throws IOException {
            if (value == null) {
                wrapper.writeByte(NULL);
                return;
            }
            CodecUtils.writeSize(wrapper, value.size(), size1);
            for (T element : value) {
                codec.write(wrapper, element);
            }
        }

        @Override
        public byte[] reservedBytes() {
            return new byte[0];
        }

        @Override
        public boolean writes(Class clazz) {
            return false;
        }
    }
}