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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Sebastien Callier
 * @since 2017
 */
public final class MapCodec implements Codec<Map> {
    private final byte size1;

    private final CodecCache codecCache;

    /**
     * The codec of each of the objects contained in the map will be required to be present
     * at runtime when read and/or write are called
     */
    public MapCodec(byte reservedByte,
                    CodecCache codecCache) {
        this.size1 = reservedByte;
        this.codecCache = codecCache;
    }

    @Override
    public Map read(InputStreamWrapper wrapper) throws IOException {
        Integer size = CodecUtils.readSize(wrapper, size1);
        if (size == null) {
            return null;
        }
        Map<Object, Object> result = new LinkedHashMap<>(CodecUtils.capacityForKnownSize(size), 0.75F);
        for (int i = 0; i < size; i++) {
            result.put(codecCache.get(wrapper.peek()).read(wrapper),
                       codecCache.get(wrapper.peek()).read(wrapper));
        }
        return result;
    }

    @Override
    public void write(
            OutputStreamWrapper wrapper,
            Map value) throws IOException {
        if (value == null) {
            wrapper.writeByte(NULL);
            return;
        }

        CodecUtils.writeSize(wrapper, value.size(), size1);
        for (Map.Entry<Object, Object> entry : (Set<Map.Entry<Object, Object>>) value.entrySet()) {
            codecCache.codecFor(entry.getKey()).write(wrapper, entry.getKey());
            codecCache.codecFor(entry.getValue()).write(wrapper, entry.getValue());
        }
    }

    @Override
    public byte[] reservedBytes() {
        return new byte[]{size1, (byte) (size1 + 1), (byte) (size1 + 2), (byte) (size1 + 3)};
    }

    @Override
    public boolean writes(Class clazz) {
        return Map.class.isAssignableFrom(clazz);
    }

    /**
     * The returned codec is compatible with the current instance of MapCodec
     * it should be somewhat faster than the non typed version when referenced
     * during read / write but should not be registered.
     *
     * @param keyClass the generic class of the keys to deserialize
     * @return a typed version of the map codec
     */
    public <K> Codec<Map<K, Object>> withTypedKeys(Class<K> keyClass) throws MissingCodecException {
        return (Codec) new Typed<>(codecCache.codecForClass(keyClass), codecCache.getAnyCodec());
    }

    /**
     * The returned codec is compatible with the current instance of MapCodec
     * it should be somewhat faster than the non typed version when referenced
     * during read / write but should not be registered.
     *
     * @param valueClass the generic class of the values to deserialize
     * @return a typed version of the map codec
     */
    public <V> Codec<Map<Object, V>> withTypedValues(Class<V> valueClass) throws MissingCodecException {
        return (Codec) new Typed<>(codecCache.getAnyCodec(), codecCache.codecForClass(valueClass));
    }

    /**
     * The returned codec is compatible with the current instance of MapCodec
     * it should be somewhat faster than the non typed version when referenced
     * during read / write but should not be registered.
     *
     * @param keyClass   the generic class of the keys to deserialize
     * @param valueClass the generic class of the values to deserialize
     * @return a typed version of the map codec
     */
    public <K, V> Codec<Map<K, V>> asTypedMap(Class<K> keyClass, Class<V> valueClass) throws MissingCodecException {
        return (Codec) new Typed<>(codecCache.codecForClass(keyClass), codecCache.codecForClass(valueClass));
    }

    private final class Typed<K, V> implements Codec<Map<K, V>> {
        private final Codec<K> keyCodec;
        private final Codec<V> valueCodec;

        private Typed(
                Codec<K> keyCodec,
                Codec<V> valueCodec) {
            this.keyCodec = keyCodec;
            this.valueCodec = valueCodec;
        }

        @Override
        public Map<K, V> read(InputStreamWrapper wrapper) throws IOException {
            Integer size = CodecUtils.readSize(wrapper, size1);
            if (size == null) {
                return null;
            }
            Map<K, V> result = new LinkedHashMap<>(CodecUtils.capacityForKnownSize(size), 0.75F);
            for (int i = 0; i < size; i++) {
                result.put(keyCodec.read(wrapper), valueCodec.read(wrapper));
            }
            return result;
        }

        @Override
        public void write(OutputStreamWrapper wrapper, Map<K, V> value) throws IOException {
            if (value == null) {
                wrapper.writeByte(NULL);
                return;
            }

            CodecUtils.writeSize(wrapper, value.size(), size1);
            for (Map.Entry<K, V> entry : value.entrySet()) {
                keyCodec.write(wrapper, entry.getKey());
                valueCodec.write(wrapper, entry.getValue());
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