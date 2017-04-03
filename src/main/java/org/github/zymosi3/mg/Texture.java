package org.github.zymosi3.mg;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;

public class Texture {

    public static Texture fromJpegResource(String resource) {
        URL url = FileStreamer.class.getClassLoader().getResource(resource);
        assert url != null;
        try {
            BufferedImage image = ImageIO.read(url);
            int width = image.getWidth();
            int height = image.getHeight();
            int[] pixels = image.getRGB(0, 0, width, height, new int[width * height], 0, width);
            return new Texture(pixels, width, height);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private final int[] pixels;

    public final int width;
    public final int height;

    private final float scaleX;
    private final float scaleY;

    private Texture(int[] pixels, int width, int height) {
        this.pixels = pixels;
        this.width = width;
        this.height = height;
        this.scaleX = 1.0f / width;
        this.scaleY = 1.0f / height;
    }

    public int color(float x, float y) {
        return color(Math.round(x / scaleX), Math.round(y / scaleY));
    }

    public int color(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) return 0;
        return pixels[width * (height - 1 - y) + x];
    }
}
