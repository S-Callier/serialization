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

package sebastien.callier.benchmark;

import org.junit.Ignore;
import org.junit.Test;
import sebastien.callier.codec.Codec;
import sebastien.callier.codec.CodecCache;
import sebastien.callier.codec.object.MapCodec;
import sebastien.callier.codec.primitive.LongCodec;
import sebastien.callier.deserialization.Deserializer;
import sebastien.callier.deserialization.DeserializerFactory;
import sebastien.callier.serialization.Serializer;
import sebastien.callier.serialization.SerializerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sebastien Callier
 * @since 2017
 */
public class MapBenchmark {
    private volatile Map map;

    @Test
    @Ignore("Benchmark")
    public void automaticMaps() throws IOException {
        CodecCache cache = new CodecCache();
        LongCodec longCodec = new LongCodec(cache.nextFreeMarker());
        cache.register(longCodec);
        MapCodec mapCodec = new MapCodec(cache.nextFreeMarker(), cache);
        cache.register(mapCodec);
        Codec<Map<Long, Long>> longLongMapCodec = mapCodec.asTypedMap(Long.class, Long.class);

        long start, end;

        Map<Long, Long> map1 = new HashMap<>();
        Map<Long, Long> map2 = new HashMap<>();
        map2.put(123L, 0L);
        Map<Long, Long> map3 = new HashMap<>();
        map3.put(123L, 0L);
        map3.put(213515L, 1234515413L);
        Map<Long, Long> map4 = new HashMap<>();
        map4.put(123L, 0L);
        map4.put(213515L, 1234515413L);
        map4.put(213512415L, 123454315413L);

        SerializerFactory serializerFactory = new SerializerFactory(cache);
        DeserializerFactory deserializerFactory = new DeserializerFactory(cache);

        int loops = 4;
        int iterations = 2_000_000;
        while (loops-- > 0) {
            start = System.currentTimeMillis();
            for (int i = 0; i < iterations; i++) {
                Serializer serializer = serializerFactory.newSerializer();
                serializer.append(map1);
                serializer.append(map2);
                serializer.append(map3);
                serializer.append(map4);
                serializer.close();

                try (Deserializer deserializer = deserializerFactory.newDeserializer(
                        serializer.getByteArray(),
                        0,
                        serializer.currentSize())) {
                    map = (Map) deserializer.read();
                    map = (Map) deserializer.read();
                    map = (Map) deserializer.read();
                    map = (Map) deserializer.read();
                }
            }
            end = System.currentTimeMillis();
            System.out.println("detected map done in " + (end - start) + " ms.");

            start = System.currentTimeMillis();
            for (int i = 0; i < iterations; i++) {
                Serializer serializer = serializerFactory.newSerializer();
                serializer.append(map1, mapCodec);
                serializer.append(map2, mapCodec);
                serializer.append(map3, mapCodec);
                serializer.append(map4, mapCodec);
                serializer.close();

                try (Deserializer deserializer = deserializerFactory.newDeserializer(
                        serializer.getByteArray(),
                        0,
                        serializer.currentSize())) {
                    map = deserializer.read(mapCodec);
                    map = deserializer.read(mapCodec);
                    map = deserializer.read(mapCodec);
                    map = deserializer.read(mapCodec);
                }
            }
            end = System.currentTimeMillis();
            System.out.println("map done in " + (end - start) + " ms.");

            start = System.currentTimeMillis();
            for (int i = 0; i < iterations; i++) {
                Serializer serializer = serializerFactory.newSerializer();
                serializer.append(map1, longLongMapCodec);
                serializer.append(map2, longLongMapCodec);
                serializer.append(map3, longLongMapCodec);
                serializer.append(map4, longLongMapCodec);

                try (Deserializer deserializer = deserializerFactory.newDeserializer(
                        serializer.getByteArray(),
                        0,
                        serializer.currentSize())) {
                    map = deserializer.read(longLongMapCodec);
                    map = deserializer.read(longLongMapCodec);
                    map = deserializer.read(longLongMapCodec);
                    map = deserializer.read(longLongMapCodec);
                }
            }
            end = System.currentTimeMillis();
            System.out.println("kvmap done in " + (end - start) + " ms.");
        }
    }
}
