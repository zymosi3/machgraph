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
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public class App {

    private static final int WIDTH = 1024;
    private static final int HEIGHT = 768;

    private static final float SCALE = .1f;

    private static final int SWIDTH = (int) (WIDTH * SCALE);
    private static final int SHEIGHT = (int) (HEIGHT * SCALE);

    private static final int FRAMERATE = 60;

    public static void main(String[] args) {
        List<Obj> objects = loadObjects();

        JFrame appWindow = new JFrame("Computer Graphics rocks");
        JPanel panel = new JPanel(new BorderLayout());
        Drawer drawer = new Drawer(SWIDTH, SHEIGHT);
        AppCanvas canvas = new AppCanvas(drawer, objects);
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

//        new Thread(() -> draw(drawer, objects), "draw").start();
    }

    private static List<Obj> loadObjects() {
        Vec3 X0 = new Vec3(0.0f, 0.0f, 2.0f);
        float mu = 1.0f;
        Mat3 A = new Mat3(mu, mu, -mu);
        Mat3 A1 = new Mat3(1.0f / mu,  1.0f / mu, 1.0f / -mu);
        Obj cube = new Obj(
                new Position(A, A1, X0),
                WavefrontStream.ofResource("cube").get(),
                new Motion(new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.0f, 0.0f, 0.0f))
        );
        Obj triangle = new Obj(
                new Position(A, A1, X0),
                WavefrontStream.ofResource("triangle").get(),
                new Motion(new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.0f, 0.0f, 0.0f))
        );
        return Collections.singletonList(triangle);
    }

    private static void draw(Drawer drawer, List<Obj> objects) {
        long startTime = System.currentTimeMillis();

//        FileStreamer.ofResource("debug-bresenham").get().forEach(c -> c.accept(drawer));
//        FileStreamer.ofResource("4dim-cube").get().forEach(c -> c.accept(drawer));

        int color = Drawer.color(255, 255, 255);
        drawer.triangleBresenham(10, 10, 15, 20, 20, 10, color);
        drawer.line(35, 20, 30, 10, color);
        drawer.line(35, 20, 40, 10, color);
        drawer.line(30, 10, 40, 10, color);

        drawer.triangleBresenham(50, 20, 60, 20, 55, 10, color);
        drawer.line(75, 10, 70, 20, color);
        drawer.line(75, 10, 80, 20, color);
        drawer.line(70, 20, 80, 20, color);

        drawer.triangleBresenham(10, 25, 20, 45, 25, 30, color);
        drawer.line(40, 25, 50, 45, color);
        drawer.line(50, 45, 55, 30, color);
        drawer.line(55, 30, 40, 25, color);

        drawer.triangleBresenham(10, 50, 20, 70, 18, 55, color);
        drawer.line(40, 50, 50, 70, color);
        drawer.line(50, 70, 48, 55, color);
        drawer.line(48, 55, 40, 50, color);


        Function<Vec3, Vec3> projection = new PerspectiveProjection(0.83f);
        Function<Vec3, Vec3> screenScale = new ScreenScale(WIDTH, HEIGHT);
//
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

//        objects.stream().
//                map(obj -> obj.move.apply(obj)).
//                forEach(obj -> obj.stream().forEach(face ->
//                        face.stream().
//                                map(obj.obj2Screen).
//                                map(projection).
//                                map(screenScale).
//                                reduce(drawLine)
//                ));

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
        private final Drawer drawer;
        private final List<Obj> objects;

        AppCanvas(Drawer drawer, List<Obj> objects) {
            this.objects = objects;
            Dimension size = new Dimension(App.WIDTH, App.HEIGHT);
            setSize(size);
            setPreferredSize(size);
            setMinimumSize(size);
            setMaximumSize(size);

            image = new BufferedImage(App.SWIDTH, App.SHEIGHT, BufferedImage.TYPE_INT_RGB);
            pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

            this.drawer = drawer;
        }

        public void render() {
            BufferStrategy bs = getBufferStrategy();
            if (bs == null) {
                createBufferStrategy(3);
                return;
            }

            draw(drawer, objects);
            drawer.flush(pixels);
            drawer.clear();

            Graphics g = bs.getDrawGraphics();
            g.fillRect(0, 0, getWidth(), getHeight());
            g.drawImage(image, 0, 0, App.WIDTH, App.HEIGHT, null);
            g.dispose();
            bs.show();
        }
    }
}
