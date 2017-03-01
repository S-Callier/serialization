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

package sebastien.callier.serialization.codec;

import sebastien.callier.serialization.codec.object.AnyCodec;
import sebastien.callier.serialization.codec.object.NullCodec;
import sebastien.callier.serialization.exceptions.MissingCodecException;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Sebastien Callier
 * @since 2017
 */
public class CodecCache {
    private static final Codec<Object> NULL_CODEC = new NullCodec();
    private final Codec<?> anyCodec;

    private final Codec[] codecArray = new Codec[256];
    private final Map<Class, Codec> codecMapping = new ConcurrentHashMap<>();
    private final List<Codec> codecList = new LinkedList<>();

    public CodecCache() {
        super();
        //always register null to its fixed position
        register(NULL_CODEC);
        this.anyCodec = new AnyCodec(this);
    }

    /**
     * @return the next marker available in the cache
     */
    public synchronized byte nextFreeMarker() {
        for (int i = 0; i < codecArray.length; i++) {
            Codec codec = codecArray[i];
            if (codec == null) {
                return (byte) (i - 128);
            }
        }
        throw new IllegalStateException("The codec cache is full.");
    }

    /**
     * The codec used for null values
     */
    public Codec<Object> getNullCodec() {
        return NULL_CODEC;
    }

    /**
     * @return The wildcard codec able to serialize everything the cache can handle
     */
    public Codec<?> getAnyCodec() {
        return anyCodec;
    }

    /**
     * Register a new codec.
     * The first codec registered will get the highest priority for automatic typing
     *
     * @param codec the codec to register
     * @throws IllegalArgumentException if a registered byte is already taken
     */
    public synchronized void register(Codec codec) {
        for (byte val : codec.reservedBytes()) {
            if (codecArray[val + 128] != null) {
                throw new IllegalArgumentException("Byte " + val + " already registered.");
            }
        }
        codecList.add(codec);
        for (byte val : codec.reservedBytes()) {
            codecArray[val + 128] = codec;
        }
    }

    public Codec get(Byte key) {
        return codecArray[key + 128];
    }

    /**
     * Get a codec able to handle an object
     *
     * @param object the object to read/write
     * @return the codec or null
     */
    @SuppressWarnings("unchecked")
    public <T> Codec<T> codecFor(T object) throws MissingCodecException {
        return object == null ? (Codec<T>) NULL_CODEC : codecForClass((Class<T>) object.getClass());
    }

    /**
     * Get a code able to handle a class
     *
     * @param clazz the class to read/write
     * @return the codec or null
     */
    @SuppressWarnings("unchecked")
    public <T> Codec<T> codecForClass(Class<T> clazz) throws MissingCodecException {
        Codec codec = codecMapping.get(clazz);
        if (codec != null) {
            return codec;
        }
        return searchCodecForClass(clazz);
    }

    @SuppressWarnings("unchecked")
    private synchronized <T> Codec<T> searchCodecForClass(Class<T> clazz) throws MissingCodecException {
        for (Codec registered : codecList) {
            if (registered.writes(clazz)) {
                codecMapping.put(clazz, registered);
                return registered;
            }
        }
        throw new MissingCodecException(clazz);
    }
}
