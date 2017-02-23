package org.github.zymosi3.mg;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import static org.github.zymosi3.mg.Drawer.color;

public class App {

    private static final int WIDTH = 1024;
    private static final int HEIGHT = 768;

    private static final int SCALE = 10;

    private static final int SWIDTH = WIDTH / SCALE;
    private static final int SHEIGHT = HEIGHT / SCALE;

    private static final int FRAMERATE = 4;

    public static void main(String[] args) {
        JFrame appWindow = new JFrame("Computer Graphics rocks");
        JPanel panel = new JPanel(new BorderLayout());
        Drawer drawer = new Drawer(SWIDTH, SHEIGHT);
        AppCanvas canvas = new AppCanvas(drawer);
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

        new Thread(() -> draw(drawer), "draw").start();
    }

    private static void draw(Drawer drawer) {
        debugBresenham(drawer);


    }

    public static void debugBresenham(Drawer drawer) {
        drawer.line(14, 21, 18, 22, color(255, 255, 255));
        drawer.line(18, 22, 14, 21, color(255, 0, 0));

        drawer.line(12, 21, 8, 22, color(255, 255, 255));
        drawer.line(8, 22, 12, 21, color(255, 0, 0));

        drawer.line(12, 19, 8, 18, color(255, 255, 255));
        drawer.line(8, 18, 12, 19, color(255, 0, 0));

        drawer.line(14, 19, 18, 18, color(255, 255, 255));
        drawer.line(18, 18, 14, 19, color(255, 0, 0));

        // 1st octant
        drawer.line(34, 21, 38, 22, color(255, 255, 255));
        // 2nd octant
        drawer.line(32, 21, 28, 22, color(255, 255, 255));
        // 3rd octant
        drawer.line(32, 19, 28, 18, color(255, 255, 255));
        // 4th octant
        drawer.line(34, 19, 38, 18, color(255, 255, 255));

        drawer.line(34, 41, 35, 45, color(255, 255, 255));
        drawer.line(35, 45, 34, 41, color(255, 0, 0));

        drawer.line(32, 41, 31, 45, color(255, 255, 255));
        drawer.line(31, 45, 32, 41, color(255, 0, 0));

        drawer.line(32, 39, 31, 35, color(255, 255, 255));
        drawer.line(31, 35, 32, 39, color(255, 0, 0));

        drawer.line(34, 39, 35, 35, color(255, 255, 255));
        drawer.line(35, 35, 34, 39, color(255, 0, 0));

        // 1st octant
        drawer.line(54, 41, 55, 45, color(255, 255, 255));
        // 2nd octant
        drawer.line(52, 41, 51, 45, color(255, 255, 255));
        // 3rd octant
        drawer.line(54, 39, 55, 35, color(255, 255, 255));
        // 4th octant
        drawer.line(52, 39, 51, 35, color(255, 255, 255));
    }

    private static class AppCanvas extends Canvas {

        private final BufferedImage image;
        private final int[] pixels;
        private final Drawer drawer;

        AppCanvas(Drawer drawer) {
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

            drawer.draw(pixels);

            Graphics g = bs.getDrawGraphics();
            g.fillRect(0, 0, getWidth(), getHeight());
            g.drawImage(image, 0, 0, App.WIDTH, App.HEIGHT, null);
            g.dispose();
            bs.show();
        }
    }
}
