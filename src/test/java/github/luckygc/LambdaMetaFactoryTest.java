/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package github.luckygc;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * LambdaMetaFactory JMH 基准测试 测试使用 LambdaMetaFactory 创建高性能方法调用器的性能 Benchmark Mode Cnt Score Error Units
 * LambdaMetaFactoryTest.benchmarkLambdaConstructorCall avgt 5 1.495 ± 0.082 ns/op
 * LambdaMetaFactoryTest.benchmarkReflectionConstructorCall avgt 5 5.419 ± 0.069 ns/op
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms2G", "-Xmx2G"})
@Warmup(iterations = 3)
@Measurement(iterations = 5)
public class LambdaMetaFactoryTest {

    private Constructor<TestClass> reflectConstructor;
    private Function<String, TestClass> lambdaConstructor;

    @Setup
    public void setup() throws Throwable {
        // 设置反射构造函数
        reflectConstructor = TestClass.class.getConstructor(String.class);

        // 设置 Lambda 构造函数调用器
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle constructorHandle = lookup.findConstructor(TestClass.class,
                MethodType.methodType(void.class, String.class));

        CallSite constructorCallSite = LambdaMetafactory.metafactory(lookup, "apply",
                MethodType.methodType(Function.class), MethodType.methodType(Object.class, Object.class),
                constructorHandle, MethodType.methodType(TestClass.class, String.class));

        lambdaConstructor = (Function<String, TestClass>) constructorCallSite.getTarget().invokeExact();
    }

    @Benchmark
    public TestClass benchmarkLambdaConstructorCall() {
        return lambdaConstructor.apply("TestParam");
    }

    @Benchmark
    public TestClass benchmarkReflectionConstructorCall() throws Exception {
        return reflectConstructor.newInstance("TestParam");
    }

    /** 运行基准测试的主方法 */
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include(LambdaMetaFactoryTest.class.getSimpleName()).forks(1).build();

        new Runner(opt).run();
    }

    /** 测试用的类 */
    public static class TestClass {

        private String parameter;

        public TestClass(String parameter) {
            this.parameter = parameter;
        }

        public String getParameter() {
            return parameter;
        }
    }
}
