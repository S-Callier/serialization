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
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Sebastien Callier
 * @since 2017
 */
public class CollectionCodecTest {
    private static final CodecCache cache = new CodecCache();
    private static final CollectionCodec codec = new CollectionCodec((byte) 0, cache);
    private static final CollectionCodec invalidCodec = new CollectionCodec((byte) 50, cache);
    private static Codec<Collection<Long>> typed;

    private static SerializerFactory serializerFactory;
    private static DeserializerFactory deserializerFactory;

    @BeforeClass
    public static void prepare() throws MissingCodecException {
        cache.register(codec);
        cache.register(new LongCodec((byte) -50));
        typed = codec.asTypedCollection(Long.class);
        serializerFactory = new SerializerFactory(cache);
        deserializerFactory = new DeserializerFactory(cache);
    }

    @Test
    public void testCollectionWriteRead() throws IOException {
        testCollection(null);
        testCollection(new ConcurrentLinkedQueue<>());
        Collection<Object> collection = new ConcurrentLinkedQueue<>();
        for (long i = 0; i < 500; i++) {
            collection.add(i);
            testCollection(collection);
        }
    }

    @Test
    public void testInvalidData() throws IOException {
        Serializer serializer = serializerFactory.newSerializer();
        serializer.append(new ArrayList<>(), invalidCodec);
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

    private void testCollection(Collection<Object> value) throws IOException {
        Serializer serializer = serializerFactory.newSerializer();
        serializer.append(value, codec);
        serializer.append(value);
        serializer.close();

        try (Deserializer deserializer = deserializerFactory.newDeserializer(
                serializer.getByteArray(),
                0,
                serializer.currentSize())) {
            if (value != null) {
                assertThat(deserializer.read(), is(new LinkedList<>(value)));
                assertThat(deserializer.read(codec), is(new LinkedList<>(value)));
            } else {
                assertThat(deserializer.read(), nullValue());
                assertThat(deserializer.read(cache.getNullCodec()), nullValue());
            }
            assertThat(deserializer.available(), is(0));
        }
    }

    @Test
    public void testCollectionTypedWriteRead() throws IOException {
        testTypedCollection(null);
        testTypedCollection(new LinkedList<>());
        List<Long> collection = new LinkedList<>();
        for (long i = 0; i < 500; i++) {
            collection.add(i);
            testTypedCollection(collection);
        }
    }

    @Test
    public void testInvalidTypedData() throws IOException {
        Serializer serializer = serializerFactory.newSerializer();
        serializer.append(new ArrayList<>(), invalidCodec);
        serializer.close();

        try (Deserializer deserializer = deserializerFactory.newDeserializer(
                serializer.getByteArray(),
                0,
                serializer.currentSize())) {
            deserializer.read(typed);
            Assert.fail("missing exception");
        } catch (IOException e) {
            //expected
        }
    }

    private void testTypedCollection(Collection<Long> value) throws IOException {
        Serializer serializer = serializerFactory.newSerializer();
        serializer.append(value, typed);
        serializer.append(value);
        serializer.close();

        try (Deserializer deserializer = deserializerFactory.newDeserializer(
                serializer.getByteArray(),
                0,
                serializer.currentSize())) {
            assertThat(deserializer.read(), is(value));
            assertThat(deserializer.read(typed), is(value));
            assertThat(deserializer.available(), is(0));
        }
    }
}