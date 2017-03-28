package org.github.zymosi3.mg;

import org.github.zymosi3.mg.math.Mat3;
import org.github.zymosi3.mg.math.Vec3;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.SystemColor;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Stream;

public class App {

    private static final int FRAMERATE = 60;

    public static void main(String[] args) {
        Context context = new Context();
        loadObjects(context);

        JFrame appWindow = new JFrame("Computer Graphics rocks");
        JPanel panel = new JPanel(new BorderLayout());
        DrawerAlt drawer = new DrawerAlt(context.scaledWidth, context.scaledHeight);
        AppCanvas canvas = new AppCanvas(drawer, context);
        panel.add(canvas, BorderLayout.CENTER);

        appWindow.setContentPane(panel);
        appWindow.pack();
        appWindow.setLocationRelativeTo(null);
        appWindow.setResizable(false);
        appWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        appWindow.setVisible(true);

        new Thread(() -> {
            int frames = 0;

            double unprocessedSeconds = 0;
            long lastTime = System.nanoTime();
            double secondsPerTick = 1.0 / FRAMERATE;
            int tickCount = 0;

            while (true) {
                long now = System.nanoTime();
                long passedTime = now - lastTime;
                lastTime = now;
                if (passedTime < 0) passedTime = 0;
                if (passedTime > 100000000) passedTime = 100000000;

                unprocessedSeconds += passedTime / 1000000000.0;

                boolean ticked = false;
                while (unprocessedSeconds > secondsPerTick) {
                    unprocessedSeconds -= secondsPerTick;
                    ticked = true;

                    tickCount++;
                    if (tickCount % FRAMERATE == 0) {
                        System.out.println(frames + " fps");
                        lastTime += 1000;
                        frames = 0;
                    }
                }

                if (ticked) {
                    canvas.render();
                    frames++;
                } else {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ignored) {}
                }
            }
        }, "render").start();

//        new Thread(() -> draw(drawer, context), "draw").start();
    }

    private static void loadObjects(Context context) {
        Vec3 X0 = new Vec3(0.0f, 0.0f, 2.0f);
        float mu = 1.0f;
        Mat3 A = new Mat3(mu, mu, -mu);
        Mat3 A1 = new Mat3(1.0f / mu,  1.0f / mu, 1.0f / -mu);
        Obj cube = new Obj(
                new Position(A, A1, X0),
                WavefrontStream.ofResource("cube-triangles").get(),
//                new Motion(new Vec3(45.0f, 120.0f, 0.5f), new Vec3(0.0f, 0.0f, 0.0f))
                new Motion(new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.0f, 0.0f, 0.0f))
        );
        Obj cubeBack = new Obj(
                new Position(A, A1, X0),
                WavefrontStream.ofResource("cube-back").get(),
//                new Motion(new Vec3(45.0f, 95.0f, 10.0f), new Vec3(0.0f, 0.0f, 0.0f))
                new Motion(new Vec3(0.3f, 0.4f, 0.6f), new Vec3(0.0f, 0.0f, 0.0f))
        );
        Obj triangle = new Obj(
                new Position(A, A1, X0),
                WavefrontStream.ofResource("triangle").get(),
                new Motion(new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.0f, 0.0f, 0.0f))
        );

        Obj debugZbuffer = new Obj(
                new Position(A, A1, X0),
                WavefrontStream.ofResource("debug-zbuffer").get(),
//                new Motion(new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.0f, 0.0f, 0.0f))
                new Motion(new Vec3(60.0f, 80.0f, 80.0f), new Vec3(-5.0f, -1.5f, 0.0f))
        );
        context.objects.addAll(Arrays.asList(cube, cubeBack));
//        context.objects.addAll(Arrays.asList(cubeBack));
//        context.objects.addAll(Arrays.asList(debugZbuffer));
    }

    private static void draw(DrawerAlt drawer, Context context) {
        long startTime = System.currentTimeMillis();

//        FileStreamer.ofResource("debug-bresenham").get().forEach(c -> c.accept(drawer));
//        FileStreamer.ofResource("4dim-cube").get().forEach(c -> c.accept(drawer));

//        drawer.line(57, 80, 4.5968213f, 58, 81, 4.60206f, -13882324);
//        drawer.line(74, 96, 4.685876f, 58, 81, 4.60206f, -13882324);
//        drawer.line(57, 80, 4.5968213f, 71, 93, 4.6701603f, -5263441);
//        drawer.line(74, 96, 4.685876f, 71, 93, 4.6701603f, -5263441);

        Function<Vec3, Vec3> projection = new PerspectiveProjection(0.83f);
        Function<Face, Face> faceProjection = face ->
                new Face(face.intensity, Stream.of(face.vertices).map(projection).toArray(Vec3[]::new));

        Function<Vec3, Vec3> screenScale = new ScreenScale(context.width, context.height);
        Function<Face, Face> faceScreenScale = face ->
                new Face(face.intensity, Stream.of(face.vertices).map(screenScale).toArray(Vec3[]::new));

        Function<Face, Face> intensity = face -> {
//            System.out.println(face.norm().toString() + " " + context.light);
//            System.out.println(face.norm().mult(context.light));
            float i = face.norm().mult(context.light);
            if (i < 0) i = 0.0f;
            return new Face(i, face.vertices);
        };

        BinaryOperator<Vec3> drawLine = new DrawLine(drawer);

//        objects.stream().
//                map(obj -> obj.move.apply(obj)).
//                forEach(obj -> obj.stream().forEach(face ->
//                        {
//                            Vec3[] vertices = face.stream().
//                                    map(obj.obj2Screen).
//                                    map(projection).
//                                    map(screenScale).
//                                    toArray(Vec3[]::new);
//                            drawer.triangleBresenham(
//                                    Math.round(vertices[0].x), Math.round(vertices[0].y),
//                                    Math.round(vertices[1].x), Math.round(vertices[1].y),
//                                    Math.round(vertices[2].x), Math.round(vertices[2].y),
//                                    Drawer.color(255, 255, 255)
//                            );
//                        }
//                ));

         context.objects.stream().
                map(obj -> obj.move.apply(obj)).
                forEach(obj -> obj.stream().
                        map(obj.faceObj2Screen).
                        map(intensity).
//                        filter(face -> face.intensity > 0).
                        map(faceProjection).
                        map(faceScreenScale).
                        forEach(face -> {
                            drawer.drawTriangle(
                                    Math.round(face.vertices[0].x), Math.round(face.vertices[0].y), face.vertices[0].z,
                                    Math.round(face.vertices[1].x), Math.round(face.vertices[1].y), face.vertices[1].z,
                                    Math.round(face.vertices[2].x), Math.round(face.vertices[2].y), face.vertices[2].z,
                                    Drawer.color((int) (255 * face.intensity), (int) (255 * face.intensity), (int) (255 * face.intensity))
                            );
//                            Vec3 n = face.norm();
//                            float m = 100 / (float) Math.sqrt(n.x * n.x + n.y * n.y);
//                            Vec3 o = new Vec3(
//                                    (face.vertices[0].x + face.vertices[1].x + face.vertices[2].x) / 3,
//                                    (face.vertices[0].y + face.vertices[1].y + face.vertices[2].y) / 3,
//                                    (face.vertices[0].z + face.vertices[1].z + face.vertices[2].z) / 3
//                            );
//                            drawer.line(
//                                    Math.round(o.x), Math.round(o.y), 0.0f,
//                                    Math.round(o.x + n.x * m), Math.round(o.y + n.y * m), 0.0f,
//                                    Drawer.color(255, 0, 0)
//                            );
//                        face.stream().
//                                map(obj.obj2Screen).
//                                map(projection).
//                                map(screenScale).
//                                reduce(drawLine);
                }));

//        AtomicInteger i = new AtomicInteger();
//        Random r = new Random();
//        WavefrontStream.ofResource("african_head").get().
//                peek(f -> {
//                    if (Global.DEBUG)
//                        System.out.println("" + i.incrementAndGet() + " " + f);
////                    try {
////                        Thread.sleep(10);
////                    } catch (InterruptedException e) {
////                        e.printStackTrace();
////                    }
//                }).
//                forEach(face -> {
//                            Vec3[] vertices =
//                                    face.stream().
//                                            map(v -> new Vec3(v.x, v.y + 0.5f, v.z)).
//                                            map(screenScale).
//                                            toArray(Vec3[]::new);
//                                    drawer.triangle(
//                                    Math.round(vertices[0].x), Math.round(vertices[0].y),
//                                    Math.round(vertices[1].x), Math.round(vertices[1].y),
//                                    Math.round(vertices[2].x), Math.round(vertices[2].y),
//                                    Drawer.color(r.nextInt(255), r.nextInt(255), r.nextInt(255))
//                            );
//                        }
//                );

        if (Global.DEBUG)
            System.out.println("Draw time " + (System.currentTimeMillis() - startTime) + " ms");
    }

    private static class AppCanvas extends Canvas {

        private final BufferedImage image;
        private final int[] pixels;
        private final DrawerAlt drawer;
        private final Context context;

        AppCanvas(DrawerAlt drawer, Context context) {
            this.context = context;
            Dimension size = new Dimension(context.width, context.height);
            setSize(size);
            setPreferredSize(size);
            setMinimumSize(size);
            setMaximumSize(size);

            image = new BufferedImage(context.scaledWidth, context.scaledHeight, BufferedImage.TYPE_INT_RGB);
            pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

            this.drawer = drawer;
        }

        public void render() {
            BufferStrategy bs = getBufferStrategy();
            if (bs == null) {
                createBufferStrategy(3);
                return;
            }

            draw(drawer, context);
            drawer.flush(pixels);
            drawer.clear();

            Graphics g = bs.getDrawGraphics();
            g.fillRect(0, 0, getWidth(), getHeight());
            g.drawImage(image, 0, 0, context.width, context.height, null);
            g.dispose();
            bs.show();
        }
    }
}
