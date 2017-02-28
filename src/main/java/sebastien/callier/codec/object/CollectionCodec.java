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
import sebastien.callier.codec.CodecCache;
import sebastien.callier.deserialization.InputStreamWrapper;
import sebastien.callier.exceptions.MissingCodecException;
import sebastien.callier.serialization.OutputStreamWrapper;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Sebastien Callier
 * @since 2017
 */
public final class CollectionCodec implements Codec<Collection> {
    private final byte start;
    private final byte end;

    private final CodecCache codecCache;

    /**
     * The codec of each of the objects contained in the lists will be required to be present
     * at runtime when read and/or write are called
     */
    public CollectionCodec(byte reservedByte,
                           CodecCache codecCache) {
        this.codecCache = codecCache;
        this.start = reservedByte;
        this.end = (byte) (reservedByte + 1);
    }

    @Override
    public Collection read(InputStreamWrapper wrapper) throws IOException {
        byte marker = wrapper.read1();
        if (marker == NULL) {
            return null;
        }
        if (marker == start) {
            Queue<Object> result = new LinkedList<>();
            while ((marker = wrapper.peek()) != end) {
                result.add(codecCache.get(marker).read(wrapper));
            }
            wrapper.read1();
            return result;
        }
        throw new IOException("Could not deserialize as a collection.");
    }

    @Override
    public void write(OutputStreamWrapper wrapper, Collection value) throws IOException {
        if (value == null) {
            wrapper.writeByte(NULL);
            return;
        }
        wrapper.writeByte(start);
        for (Object element : value) {
            codecCache.codecFor(element).write(wrapper, element);
        }
        wrapper.writeByte(end);
    }

    @Override
    public byte[] reservedBytes() {
        return new byte[]{start, end};
    }

    @Override
    public boolean writes(Class clazz) {
        return Collection.class.isAssignableFrom(clazz);
    }

    /**
     * The returned codec is compatible with the current instance of CollectionCodec
     * it should be somewhat faster than the non typed version when referenced
     * during read / write but should not be registered.
     *
     * @param tClass the generic class to deserialize
     * @return a typed version of the collection codec
     */
    public <T> Codec<Collection<T>> asTypedCollection(Class<T> tClass) throws MissingCodecException {
        return new CollectionCodec.Typed<>(codecCache.codecForClass(tClass));
    }

    private final class Typed<T> implements Codec<Collection<T>> {
        private final Codec<T> codec;

        private Typed(Codec<T> codec) {
            this.codec = codec;
        }

        @Override
        public Collection<T> read(InputStreamWrapper wrapper) throws IOException {
            byte marker = wrapper.read1();
            if (marker == NULL) {
                return null;
            }
            if (marker == start) {
                Queue<T> result = new LinkedList<>();
                while (wrapper.peek() != end) {
                    result.add(codec.read(wrapper));
                }
                wrapper.read1();
                return result;
            }
            throw new IOException("Could not deserialize as a collection.");
        }

        @Override
        public void write(OutputStreamWrapper wrapper, Collection<T> value) throws IOException {
            if (value == null) {
                wrapper.writeByte(NULL);
                return;
            }
            wrapper.writeByte(start);
            for (T element : value) {
                codec.write(wrapper, element);
            }
            wrapper.writeByte(end);
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
