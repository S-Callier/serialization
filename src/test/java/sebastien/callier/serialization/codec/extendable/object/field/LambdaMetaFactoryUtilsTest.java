/*
 * Copyright 2018 Sebastien Callier
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

package sebastien.callier.serialization.codec.extendable.object.field;

import org.junit.Ignore;
import org.junit.Test;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author Sebastien Callier
 * @since 2018
 */
public class LambdaMetaFactoryUtilsTest {
    @SuppressWarnings("unused")
    private volatile String extracted = "default";
    @SuppressWarnings("unused")
    private volatile Benchmark instance;

    @FunctionalInterface
    public interface Getter {
        String get(Object source);
    }

    @FunctionalInterface
    public interface Setter {
        void set(Object source, String value);
    }

    @FunctionalInterface
    public interface Creator {
        Benchmark build();
    }

    @Test
    @Ignore("Benchmark")
    public void getterBenchmark() throws Throwable {
        Benchmark benchmark = new Benchmark();
        Field field = Benchmark.class.getField("value");
        Method method = Benchmark.class.getMethod("getValue");
        MethodHandle handle = MethodHandles.lookup().unreflect(method);
        MethodHandle getterHandle = MethodHandles.lookup().unreflectGetter(field);
        Getter lambda = LambdaMetaFactoryUtils.wrapGetter(Getter.class, method, String.class);

        long start;
        long end;

        int loops = 4;
        int iterations = 200_000_000;
        while (loops-- > 0) {
            start = System.currentTimeMillis();
            for (int i = 0; i < iterations; i++) {
                extracted = benchmark.getValue();
            }
            end = System.currentTimeMillis();
            System.out.println("Direct access done in " + (end - start) + " ms.");

            start = System.currentTimeMillis();
            for (int i = 0; i < iterations; i++) {
                extracted = (String) field.get(benchmark);
            }
            end = System.currentTimeMillis();
            System.out.println("Field access done in " + (end - start) + " ms.");

            start = System.currentTimeMillis();
            for (int i = 0; i < iterations; i++) {
                extracted = (String) method.invoke(benchmark);
            }
            end = System.currentTimeMillis();
            System.out.println("Method access done in " + (end - start) + " ms.");

            start = System.currentTimeMillis();
            for (int i = 0; i < iterations; i++) {
                extracted = (String) handle.invoke(benchmark);
            }
            end = System.currentTimeMillis();
            System.out.println("Handle access done in " + (end - start) + " ms.");

            start = System.currentTimeMillis();
            for (int i = 0; i < iterations; i++) {
                extracted = (String) getterHandle.invoke(benchmark);
            }
            end = System.currentTimeMillis();
            System.out.println("Getter handle access done in " + (end - start) + " ms.");

            start = System.currentTimeMillis();
            for (int i = 0; i < iterations; i++) {
                extracted = lambda.get(benchmark);
            }
            end = System.currentTimeMillis();
            System.out.println("Lambda access done in " + (end - start) + " ms.");
        }
    }

    @Test
    @Ignore("Benchmark")
    public void setterBenchmark() throws Throwable {
        Benchmark benchmark = new Benchmark();
        Field field = Benchmark.class.getField("value");
        Method method = Benchmark.class.getMethod("setValue", String.class);
        MethodHandle handle = MethodHandles.lookup().unreflect(method);
        MethodHandle setterHandle = MethodHandles.lookup().unreflectSetter(field);
        Setter lambda = LambdaMetaFactoryUtils.wrapSetter(Setter.class, method, String.class);

        long start;
        long end;

        int loops = 4;
        int iterations = 200_000_000;
        while (loops-- > 0) {
            start = System.currentTimeMillis();
            for (int i = 0; i < iterations; i++) {
                benchmark.setValue(extracted);
            }
            end = System.currentTimeMillis();
            System.out.println("Direct access done in " + (end - start) + " ms.");

            start = System.currentTimeMillis();
            for (int i = 0; i < iterations; i++) {
                field.set(benchmark, extracted);
            }
            end = System.currentTimeMillis();
            System.out.println("Field access done in " + (end - start) + " ms.");

            start = System.currentTimeMillis();
            for (int i = 0; i < iterations; i++) {
                method.invoke(benchmark, extracted);
            }
            end = System.currentTimeMillis();
            System.out.println("Method access done in " + (end - start) + " ms.");

            start = System.currentTimeMillis();
            for (int i = 0; i < iterations; i++) {
                handle.invoke(benchmark, extracted);
            }
            end = System.currentTimeMillis();
            System.out.println("Handle access done in " + (end - start) + " ms.");

            start = System.currentTimeMillis();
            for (int i = 0; i < iterations; i++) {
                setterHandle.invoke(benchmark, extracted);
            }
            end = System.currentTimeMillis();
            System.out.println("Setter handle access done in " + (end - start) + " ms.");

            start = System.currentTimeMillis();
            for (int i = 0; i < iterations; i++) {
                lambda.set(benchmark, extracted);
            }
            end = System.currentTimeMillis();
            System.out.println("Lambda access done in " + (end - start) + " ms.");
        }
    }

    @Test
    @Ignore("Benchmark")
    public void noArgConstructorBenchmark() throws Throwable {
        Constructor<Benchmark> constructor = Benchmark.class.getDeclaredConstructor();
        MethodHandle handle = MethodHandles.lookup().unreflectConstructor(constructor);
        Creator lambda = LambdaMetaFactoryUtils.wrapNorArgsConstructor(Creator.class, constructor, Benchmark.class);

        long start;
        long end;

        int loops = 4;
        int iterations = 200_000_000;
        while (loops-- > 0) {
            start = System.currentTimeMillis();
            for (int i = 0; i < iterations; i++) {
                instance = new Benchmark();
            }
            end = System.currentTimeMillis();
            System.out.println("Direct call done in " + (end - start) + " ms.");

            start = System.currentTimeMillis();
            for (int i = 0; i < iterations; i++) {
                instance = constructor.newInstance();
            }
            end = System.currentTimeMillis();
            System.out.println("Constructor done in " + (end - start) + " ms.");

            start = System.currentTimeMillis();
            for (int i = 0; i < iterations; i++) {
                instance = (Benchmark) handle.invoke();
            }
            end = System.currentTimeMillis();
            System.out.println("Handle access done in " + (end - start) + " ms.");

            start = System.currentTimeMillis();
            for (int i = 0; i < iterations; i++) {
                instance = lambda.build();
            }
            end = System.currentTimeMillis();
            System.out.println("Lambda done in " + (end - start) + " ms.");
        }
    }
}