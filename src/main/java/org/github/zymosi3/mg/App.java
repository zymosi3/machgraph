package org.github.zymosi3.mg;

import org.github.zymosi3.mg.math.Mat3;
import org.github.zymosi3.mg.math.Vec3;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Stream;

public class App {

    private static final int FRAMERATE = 60;

    public static final Vec3 v001 = new Vec3(0.0f, 0.0f, 1.0f);

    public static void main(String[] args) {
        Context context = new Context();
        loadObjects(context);

        JFrame appWindow = new JFrame("Computer Graphics rocks");
        JPanel panel = new JPanel(new BorderLayout());
        DrawerZBuffered drawer = new DrawerZBuffered(context.scaledWidth, context.scaledHeight);
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
                WavefrontStream.ofResource("cube-textured").get(),
//                new Motion(new Vec3(0.0f, 90.0f, 0.0f), new Vec3(0.0f, 0.0f, 0.0f))
                new Motion(new Vec3(1.5f, 1.5f, 1.5f), new Vec3(0.0f, 0.0f, 0.0f)),
                Texture.fromJpegResource("skybox_texture.jpg"));
//        Obj cubeBack = new Obj(
//                new Position(A, A1, X0),
//                WavefrontStream.ofResource("cube-back").get(),
////                new Motion(new Vec3(45.0f, 95.0f, 10.0f), new Vec3(0.0f, 0.0f, 0.0f))
//                new Motion(new Vec3(0.3f, 0.4f, 0.6f), new Vec3(0.0f, 0.0f, 0.0f)),
//                Texture.fromJpegResource("skybox_texture.jpg"));
//        Obj triangle = new Obj(
//                new Position(A, A1, X0),
//                WavefrontStream.ofResource("triangle").get(),
////                new Motion(new Vec3(30.0f, 30.0f, 30.0f), new Vec3(0.0f, 0.0f, 0.0f))
//                new Motion(new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.0f, 0.0f, 0.0f)),
//                Texture.fromJpegResource("skybox_texture.jpg"));

//        Obj debugZbuffer = new Obj(
//                new Position(A, A1, X0),
//                WavefrontStream.ofResource("debug-zbuffer").get(),
////                new Motion(new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.0f, 0.0f, 0.0f))
//                new Motion(new Vec3(60.0f, 80.0f, 80.0f), new Vec3(-5.0f, -1.5f, 0.0f)),
//                Texture.fromJpegResource("skybox_texture.jpg"));
        context.objects.addAll(Arrays.asList(cube));
//        context.objects.addAll(Arrays.asList(cube, cubeBack));
//        context.objects.addAll(Arrays.asList(cubeBack));
//        context.objects.addAll(Arrays.asList(triangle));
//        context.objects.addAll(Arrays.asList(debugZbuffer));
    }

    private static void draw(DrawerZBuffered drawer, Context context) {
        long startTime = System.currentTimeMillis();

//        FileStreamer.ofResource("debug-bresenham").get().forEach(c -> c.accept(drawer));
//        FileStreamer.ofResource("4dim-cube").get().forEach(c -> c.accept(drawer));

//        drawer.line(57, 80, 4.5968213f, 58, 81, 4.60206f, -13882324);
//        drawer.line(74, 96, 4.685876f, 58, 81, 4.60206f, -13882324);
//        drawer.line(57, 80, 4.5968213f, 71, 93, 4.6701603f, -5263441);
//        drawer.line(74, 96, 4.685876f, 71, 93, 4.6701603f, -5263441);

        PerspectiveProjection projection = new PerspectiveProjection(0.83f);
        Function<Face, Face> faceProjection = face ->
                new Face(
                        face.intensity,
                        projection.apply(face.n),
                        projection.apply(face.center),
                        Stream.of(face.v).map(projection).toArray(Vec3[]::new),
                        face.vt
                );

        ScreenScale screenScale = new ScreenScale(context.width, context.height);
        Function<Face, Face> faceScreenScale = face ->
                new Face(
                        face.intensity,
                        screenScale.apply(face.n),
                        screenScale.apply(face.center),
                        Stream.of(face.v).map(screenScale).toArray(Vec3[]::new),
                        face.vt
                );

        Function<Face, Face> intensity = face -> {
//            System.out.println(face.norm().toString() + " " + context.light);
//            System.out.println(face.norm().mult(context.light));
            float i = face.norm().mult(context.light);
            if (i < 0) i = 0.0f;
            i = 0.9f * i + 0.05f;
            return new Face(i, face.n, face.center, face.v, face.vt);
        };

        BinaryOperator<Vec3> drawLine = new DrawLine(drawer);

//        objects.stream().
//                map(obj -> obj.move.apply(obj)).
//                forEach(obj -> obj.stream().forEach(face ->
//                        {
//                            Vec3[] v = face.stream().
//                                    map(obj.obj2Screen).
//                                    map(projection).
//                                    map(screenScale).
//                                    toArray(Vec3[]::new);
//                            drawer.triangleBresenham(
//                                    Math.round(v[0].x), Math.round(v[0].y),
//                                    Math.round(v[1].x), Math.round(v[1].y),
//                                    Math.round(v[2].x), Math.round(v[2].y),
//                                    Drawer.color(255, 255, 255)
//                            );
//                        }
//                ));

         context.objects.stream().
                map(obj -> obj.move.apply(obj)).
                forEach(obj -> obj.stream().
                        forEach(orig -> {
                            Face face = faceScreenScale.
                                    compose(faceProjection).
                                    compose(intensity).
                                    compose(obj.faceObj2Screen).
                                    apply(orig);

                            float a = -orig.norm().angle(v001);
                            Vec3 u = orig.norm().cross(v001).normalize();
                            Face origT = new Face(Stream.of(orig.v).map(v -> v.rotateAboutAxis(a, u)).toArray(Vec3[]::new), orig.vt);

                            ColorFunction color = (x, y, z) -> {
                                Vec3 v = screenScale.inverse(new Vec3(x, y, z));
                                v = projection.inverse(v);
                                v = obj.screen2obj.apply(v);
                                v = v.rotateAboutAxis(a, u);
                                v = origT.toTexture(v);
                                if (v == null)
                                    return color((int) (255 * face.intensity), (int) (255 * face.intensity), (int) (255 * face.intensity));
                                int c = obj.texture.color(v.x, v.y);
                                return color((int) (red(c) * face.intensity), (int) (green(c) * face.intensity), (int) (blue(c) * face.intensity));
                            };

                            drawer.triangleBresenham(
                                    Math.round(face.v[0].x), Math.round(face.v[0].y), face.v[0].z,
                                    Math.round(face.v[1].x), Math.round(face.v[1].y), face.v[1].z,
                                    Math.round(face.v[2].x), Math.round(face.v[2].y), face.v[2].z,
                                    color
//                                    (x, y, z) -> {
//                                        color.apply(x, y, z);
//                                        return Drawer.color((int) (255 * face.intensity), (int) (255 * face.intensity), (int) (255 * face.intensity));
//                                    }
                            );
//                            float m = 100 / (float) Math.sqrt(n.x * n.x + n.y * n.y);
                            drawer.line(
                                    Math.round(face.center.x), Math.round(face.center.y), face.center.z,
                                    Math.round(face.n.x), Math.round(face.n.y), face.n.z,
                                    (x, y, z) -> color(255, 0, 0)
                            );
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
//                            Vec3[] v =
//                                    face.stream().
//                                            map(v -> new Vec3(v.x, v.y + 0.5f, v.z)).
//                                            map(screenScale).
//                                            toArray(Vec3[]::new);
//                                    drawer.triangle(
//                                    Math.round(v[0].x), Math.round(v[0].y),
//                                    Math.round(v[1].x), Math.round(v[1].y),
//                                    Math.round(v[2].x), Math.round(v[2].y),
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
        private final DrawerZBuffered drawer;
        private final Context context;

        AppCanvas(DrawerZBuffered drawer, Context context) {
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

    @SuppressWarnings("NumericOverflow")
    public static int color(int r, int g, int b) {
        assert r >= 0 && r <= 255;
        assert g >= 0 && g <= 255;
        assert b >= 0 && b <= 255;
        return ((0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8)  |
                ((b & 0xFF));
    }

    public static int red(int rgb) {
        return (0xff000000 | rgb >> 16) & 0xFF;
    }

    public static int green(int rgb) {
        return (0xff000000 | rgb >> 8) & 0xFF;
    }

    public static int blue(int rgb) {
        return (0xff000000 | rgb >> 0) & 0xFF;
    }
}
