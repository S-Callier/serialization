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

package sebastien.callier.serialization.codec.extendable.object;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import sebastien.callier.serialization.codec.Codec;
import sebastien.callier.serialization.codec.CodecCache;
import sebastien.callier.serialization.codec.array.*;
import sebastien.callier.serialization.codec.primitive.*;
import sebastien.callier.serialization.deserializer.Deserializer;
import sebastien.callier.serialization.deserializer.DeserializerFactory;
import sebastien.callier.serialization.exceptions.CodecGenerationException;
import sebastien.callier.serialization.exceptions.MissingCodecException;
import sebastien.callier.serialization.serializer.Serializer;
import sebastien.callier.serialization.serializer.SerializerFactory;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Sebastien Callier
 * @since 2017
 */
public class PrimitiveObjectCodecTest {
    private static SerializerFactory serializerFactory;
    private static DeserializerFactory deserializerFactory;
    private static CodecCache cache;

    @BeforeClass
    public static void prepare() {
        cache = new CodecCache();
        serializerFactory = new SerializerFactory(cache);
        deserializerFactory = new DeserializerFactory(cache);

        cache.register(new BooleanCodec(cache.nextFreeMarker()));
        cache.register(new ByteCodec(cache.nextFreeMarker()));
        cache.register(new CharCodec(cache.nextFreeMarker()));
        cache.register(new DoubleCodec(cache.nextFreeMarker()));
        cache.register(new FloatCodec(cache.nextFreeMarker()));
        cache.register(new IntCodec(cache.nextFreeMarker()));
        cache.register(new LongCodec(cache.nextFreeMarker()));
        cache.register(new ShortCodec(cache.nextFreeMarker()));

        cache.register(new BooleanArrayCodec(cache.nextFreeMarker()));
        cache.register(new ByteArrayCodec(cache.nextFreeMarker()));
        cache.register(new CharArrayCodec(cache.nextFreeMarker()));
        cache.register(new DoubleArrayCodec(cache.nextFreeMarker()));
        cache.register(new FloatArrayCodec(cache.nextFreeMarker()));
        cache.register(new IntArrayCodec(cache.nextFreeMarker()));
        cache.register(new LongArrayCodec(cache.nextFreeMarker()));
        cache.register(new ShortArrayCodec(cache.nextFreeMarker()));
    }

    @Test
    public void testGetters() throws Throwable {
        Codec<PrimitiveObject> codec = new ObjectCodec<>(
                (byte) 50,
                PrimitiveObject.class,
                cache);
        cache.register(codec);

        testObject(null, codec);
        testObject(new PrimitiveObject(), codec);

        PrimitiveObject object = new PrimitiveObject();
        object.setBool(true);
        object.setByt((byte) 123);
        object.setCha('c');
        object.setDoubl(5.6D);
        object.setFloa(1.5F);
        object.setIn(123);
        object.setLon(1234L);
        object.setShor((short) 12);
        testObject(object, codec);
    }

    @Test
    public void testArrayGetters() throws Throwable {
        Codec<PrimitiveArrayObject> codec = new ObjectCodec<>(
                (byte) 51,
                PrimitiveArrayObject.class,
                cache);
        cache.register(codec);

        testObject(null, codec);
        testObject(new PrimitiveArrayObject(), codec);

        PrimitiveArrayObject object = new PrimitiveArrayObject();
        object.setBool(new boolean[]{true});
        object.setByt(new byte[]{(byte) 123});
        object.setCha(new char[]{'c'});
        object.setDoubl(new double[]{5.6D});
        object.setFloa(new float[]{1.5F});
        object.setIn(new int[]{123});
        object.setLon(new long[]{1234L});
        object.setShor(new short[]{(short) 12});
        testObject(object, codec);
    }

    @Test
    public void testDirectAccess() throws Throwable {
        Codec<DirectPrimitiveObject> codec = new ObjectCodec<>(
                (byte) 52,
                DirectPrimitiveObject.class,
                cache);
        cache.register(codec);

        testObject(null, codec);
        testObject(new DirectPrimitiveObject(), codec);

        DirectPrimitiveObject object = new DirectPrimitiveObject();
        object.bool = true;
        object.byt = (byte) 123;
        object.cha = 'c';
        object.doubl = 5.6D;
        object.floa = 1.5F;
        object.in = 123;
        object.lon = 1234L;
        object.shor = (short) 12;
        testObject(object, codec);
    }

    @Test
    public void testSneakyObject() throws Throwable {
        //For object referencing themselves
        Codec<SneakyObject> codec = new ObjectCodec<>(
                (byte) 53,
                SneakyObject.class,
                cache);
        cache.register(codec);

        testObject(null, codec);
        testObject(new SneakyObject(), codec);
    }

    @Test
    public void getsSuperFields() throws IOException, CodecGenerationException {
        Codec<ObjectExtendingPrimitive> codec = new ObjectCodec<>(
                (byte) 121,
                ObjectExtendingPrimitive.class,
                cache);
        cache.register(codec);

        ObjectExtendingPrimitive o = new ObjectExtendingPrimitive();
        o.setCha2('c');
        o.setCha('d');
        testObject(o, codec);
    }

    @Test
    public void transientAreSkipped() throws IOException, CodecGenerationException {
        Codec<TransientObject> codec = new ObjectCodec<>(
                (byte) 122,
                TransientObject.class,
                cache);
        cache.register(codec);

        TransientObject object = new TransientObject();
        object.value = "test";
        Serializer serializer = serializerFactory.newSerializer();
        serializer.append(object, codec);
        serializer.close();

        try (Deserializer deserializer = deserializerFactory.newDeserializer(
                serializer.getByteArray(),
                0,
                serializer.currentSize())) {
            assertThat(deserializer.read(codec).value, not("test"));
        }
    }

    @Test
    public void staticAreSkipped() throws IOException, CodecGenerationException {
        Codec<StaticObject> codec = new ObjectCodec<>(
                (byte) 123,
                StaticObject.class,
                cache);
        cache.register(codec);

        StaticObject object = new StaticObject();
        StaticObject.value = "test";
        Serializer serializer = serializerFactory.newSerializer();
        serializer.append(object, codec);
        serializer.close();

        StaticObject.value = "changed";

        try (Deserializer deserializer = deserializerFactory.newDeserializer(
                serializer.getByteArray(),
                0,
                serializer.currentSize())) {
            deserializer.read(codec);
            assertThat(StaticObject.value, is("changed"));
        }
    }

    private <T> void testObject(
            T value,
            Codec<T> codec) throws IOException {
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
    public void testInvalidData() throws IOException, CodecGenerationException {
        Codec<DirectPrimitiveObject> codec = new ObjectCodec<>(
                (byte) 120,
                DirectPrimitiveObject.class,
                cache);

        Serializer serializer = serializerFactory.newSerializer();
        serializer.append(1L);
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

    @Test(expected = CodecGenerationException.class)
    public void rejectFieldWithIdenticalName() throws MissingCodecException, CodecGenerationException {
        new ObjectCodec<>((byte) 122, DirtyPractice2.class, cache);
    }

    @Test(expected = CodecGenerationException.class)
    public void missingGetter() throws MissingCodecException, CodecGenerationException {
        new ObjectCodec<>((byte) 122, MissingGetter.class, cache);
    }

    @Test(expected = CodecGenerationException.class)
    public void missingSetter() throws MissingCodecException, CodecGenerationException {
        new ObjectCodec<>((byte) 122, MissingSetter.class, cache);
    }

    @Test(expected = CodecGenerationException.class)
    public void finalFieldAreFailing() throws MissingCodecException, CodecGenerationException {
        new ObjectCodec<>((byte) 122, FinalField.class, cache);
    }

    @Test(expected = MissingCodecException.class)
    public void missingCodec() throws MissingCodecException, CodecGenerationException {
        new ObjectCodec<>((byte) 122, MissingCodec.class, cache);
    }

    @Test(expected = CodecGenerationException.class)
    public void missingNoArgConstructor() throws MissingCodecException, CodecGenerationException {
        new ObjectCodec<>((byte) 122, NoConstructor.class, cache);
    }
}