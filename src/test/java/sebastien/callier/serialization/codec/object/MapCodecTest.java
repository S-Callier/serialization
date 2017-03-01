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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import sebastien.callier.serialization.codec.Codec;
import sebastien.callier.serialization.codec.CodecCache;
import sebastien.callier.serialization.codec.primitive.LongCodec;
import sebastien.callier.serialization.deserializer.Deserializer;
import sebastien.callier.serialization.deserializer.DeserializerFactory;
import sebastien.callier.serialization.exceptions.MissingCodecException;
import sebastien.callier.serialization.serializer.Serializer;
import sebastien.callier.serialization.serializer.SerializerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Sebastien Callier
 * @since 2017
 */
public class MapCodecTest {
    private static final CodecCache cache = new CodecCache();
    private static final MapCodec codec = new MapCodec((byte) 0, cache);
    private static final MapCodec invalidCodec = new MapCodec((byte) 50, cache);
    private static Codec<Map<Long, Object>> typedKey;
    private static Codec<Map<Object, String>> typedValue;
    private static Codec<Map<Long, String>> typedMap;

    private static SerializerFactory serializerFactory;
    private static DeserializerFactory deserializerFactory;

    @BeforeClass
    public static void prepare() throws MissingCodecException {
        cache.register(codec);
        cache.register(new LongCodec((byte) -50));
        cache.register(new StringCodec((byte) -60, 0));
        typedKey = codec.withTypedKeys(Long.class);
        typedValue = codec.withTypedValues(String.class);
        typedMap = codec.asTypedMap(Long.class, String.class);

        serializerFactory = new SerializerFactory(cache);
        deserializerFactory = new DeserializerFactory(cache);
    }

    @Test
    public void testMapWriteRead() throws IOException {
        testMap(null);
        testMap(new HashMap<>());
        Map<Object, Object> map = new HashMap<>();
        for (long i = 0; i < 500; i++) {
            map.put(i, "value");
            testMap(map);
        }
    }

    @Test
    public void testInvalidData() throws IOException {
        Serializer serializer = serializerFactory.newSerializer();
        serializer.append(new HashMap<>(), invalidCodec);
        serializer.close();

        try (Deserializer deserializer = deserializerFactory.newDeserializer(
                serializer.getByteArray(),
                0,
                serializer.currentSize())) {
            deserializer.read(codec);
            Assert.fail("missing exception");
        } catch (IOException e) {
            //expected
        }
    }

    private void testMap(Map<Object, Object> value) throws IOException {
        Serializer serializer = serializerFactory.newSerializer();
        serializer.append(value, codec);
        serializer.append(value);
        serializer.close();

        try (Deserializer deserializer = deserializerFactory.newDeserializer(
                serializer.getByteArray(),
                0,
                serializer.currentSize())) {
            assertThat(deserializer.read(), is(value));
            assertThat(deserializer.read(codec), is(value));
            assertThat(deserializer.available(), is(0));
        }
    }


    @Test
    public void testMapTypedWriteRead() throws IOException {
        testTypedMap(null, typedKey);
        testTypedMap(null, typedValue);
        testTypedMap(null, typedMap);
        testTypedMap(new LinkedHashMap<>(), typedKey);
        testTypedMap(new LinkedHashMap<>(), typedValue);
        testTypedMap(new LinkedHashMap<>(), typedMap);
        Map<Long, String> map = new LinkedHashMap<>();
        for (long i = 0; i < 500; i++) {
            map.put(i, "value");
            testTypedMap(map, typedKey);
            testTypedMap(map, typedValue);
            testTypedMap(map, typedMap);
        }
    }

    @Test
    public void testInvalidTypedData() throws IOException {
        Serializer serializer = serializerFactory.newSerializer();
        serializer.append(new LinkedHashMap<>(), invalidCodec);
        serializer.close();

        try (Deserializer deserializer = deserializerFactory.newDeserializer(
                serializer.getByteArray(),
                0,
                serializer.currentSize())) {
            deserializer.read(typedKey);
            Assert.fail("missing exception");
        } catch (IOException e) {
            //expected
        }

        try (Deserializer deserializer = deserializerFactory.newDeserializer(
                serializer.getByteArray(),
                0,
                serializer.currentSize())) {
            deserializer.read(typedValue);
            Assert.fail("missing exception");
        } catch (IOException e) {
            //expected
        }

        try (Deserializer deserializer = deserializerFactory.newDeserializer(
                serializer.getByteArray(),
                0,
                serializer.currentSize())) {
            deserializer.read(typedMap);
            Assert.fail("missing exception");
        } catch (IOException e) {
            //expected
        }
    }

    @SuppressWarnings("unchecked")
    private void testTypedMap(
            Map value,
            Codec codec) throws IOException {
        Serializer serializer = serializerFactory.newSerializer();
        serializer.append(value, codec);
        serializer.append(value);
        serializer.close();

        try (Deserializer deserializer = deserializerFactory.newDeserializer(
                serializer.getByteArray(),
                0,
                serializer.currentSize())) {
            assertThat(deserializer.read(), is(value));
            assertThat(deserializer.read(typedKey), is(value));
            assertThat(deserializer.available(), is(0));
        }
    }
}
