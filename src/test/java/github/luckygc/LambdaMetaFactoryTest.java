package github.luckygc;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.lang.invoke.*;
import java.lang.reflect.Constructor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * LambdaMetaFactory JMH 基准测试
 * 测试使用 LambdaMetaFactory 创建高性能方法调用器的性能
 * Benchmark                                                 Mode  Cnt  Score   Error  Units
 * LambdaMetaFactoryTest.benchmarkLambdaConstructorCall      avgt    5  1.495 ± 0.082  ns/op
 * LambdaMetaFactoryTest.benchmarkReflectionConstructorCall  avgt    5  5.419 ± 0.069  ns/op
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

        CallSite constructorCallSite = LambdaMetafactory.metafactory(
            lookup,
            "apply",
            MethodType.methodType(Function.class),
            MethodType.methodType(Object.class, Object.class),
            constructorHandle,
            MethodType.methodType(TestClass.class, String.class)
        );

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

    /**
     * 运行基准测试的主方法
     */
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(LambdaMetaFactoryTest.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    /**
     * 测试用的类
     */
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
