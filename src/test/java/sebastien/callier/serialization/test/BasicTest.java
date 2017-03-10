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

package sebastien.callier.serialization.test;

import org.junit.Test;
import sebastien.callier.serialization.codec.CodecCache;
import sebastien.callier.serialization.codec.array.*;
import sebastien.callier.serialization.codec.object.*;
import sebastien.callier.serialization.codec.primitive.*;
import sebastien.callier.serialization.deserializer.Deserializer;
import sebastien.callier.serialization.deserializer.DeserializerFactory;
import sebastien.callier.serialization.serializer.Serializer;
import sebastien.callier.serialization.serializer.SerializerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Sebastien Callier
 * @since 2017
 */
public class BasicTest {
    @Test
    public void largerCache() throws Exception {
        CodecCache cache = new CodecCache();

        cache.register(new BooleanCodec(cache.nextFreeMarker()));
        cache.register(new ByteCodec(cache.nextFreeMarker()));
        cache.register(new CharCodec(cache.nextFreeMarker()));
        cache.register(new DoubleCodec(cache.nextFreeMarker()));
        cache.register(new FloatCodec(cache.nextFreeMarker()));
        cache.register(new IntCodec(cache.nextFreeMarker()));
        cache.register(new LongCodec(cache.nextFreeMarker()));
        cache.register(new ShortCodec(cache.nextFreeMarker()));

        cache.register(new StringCodec(cache.nextFreeMarker(), 0));

        cache.register(new BooleanArrayCodec(cache.nextFreeMarker()));
        cache.register(new ByteArrayCodec(cache.nextFreeMarker()));
        cache.register(new CharArrayCodec(cache.nextFreeMarker()));
        cache.register(new DoubleArrayCodec(cache.nextFreeMarker()));
        cache.register(new FloatArrayCodec(cache.nextFreeMarker()));
        cache.register(new IntArrayCodec(cache.nextFreeMarker()));
        cache.register(new LongArrayCodec(cache.nextFreeMarker()));
        cache.register(new ShortArrayCodec(cache.nextFreeMarker()));

        cache.register(new MapCodec(cache.nextFreeMarker(), cache));
        cache.register(new QueueCodec(cache.nextFreeMarker(), cache));
        cache.register(new SetCodec(cache.nextFreeMarker(), cache));
        cache.register(new ListCodec(cache.nextFreeMarker(), cache));
        cache.register(new CollectionCodec(cache.nextFreeMarker(), cache));

        List<Object> mixedList = new LinkedList<>();
        mixedList.add(1L);
        mixedList.add(false);
        mixedList.add(new LinkedList<>());
        mixedList.add(new HashMap<>());
        mixedList.add("test");

        Serializer serializer = new SerializerFactory(cache).newSerializer();
        serializer.append(mixedList);
        serializer.close();

        try (Deserializer deserializer = new DeserializerFactory(cache).newDeserializer(
                serializer.getByteArray(),
                0,
                serializer.currentSize())) {
            assertThat(deserializer.read(), is(mixedList));
            assertThat(deserializer.available(), is(0));
        }
    }
}
