package org.github.zymosi3.mg;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(2)
public class DrawerTest {

    private static int WIDTH = 1024;
    private static int HEIGHT = 768;
    private static int WHITE = Drawer.color(255, 255, 255);

    private static Drawer drawer;
    private static Random random;

    @Setup
    public static void init() {
        random = new Random();
        drawer = new Drawer(WIDTH, HEIGHT);
    }

    @Benchmark
    public static void floatTriangle()  {
        drawer.triangle(
                random.nextInt(WIDTH),
                random.nextInt(HEIGHT),
                random.nextInt(WIDTH),
                random.nextInt(HEIGHT),
                random.nextInt(WIDTH),
                random.nextInt(HEIGHT),
                WHITE
        );
    }

    @Benchmark
    public static void bresenhamTriangle()  {
        drawer.triangleBresenham(
                random.nextInt(WIDTH),
                random.nextInt(HEIGHT),
                random.nextInt(WIDTH),
                random.nextInt(HEIGHT),
                random.nextInt(WIDTH),
                random.nextInt(HEIGHT),
                WHITE
        );
    }

    @Benchmark
    public static void bresenhamLine()  {
        drawer.line(
                random.nextInt(WIDTH),
                random.nextInt(HEIGHT),
                random.nextInt(WIDTH),
                random.nextInt(HEIGHT),
                WHITE
        );
    }
}
