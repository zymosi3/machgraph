package org.github.zymosi3.mg;

public class Zbuf {

    private final float[] buf;
    private final int width;
    private final int height;

    public Zbuf(int width, int height) {
        this.width = width;
        this.height = height;
        buf = new float[width * height];
        clear();
    }

    public float get(int x, int y) {
        return buf[y * width + x];
    }

    public void set(int x, int y, float z) {
        buf[y * width + x] = z;
    }

    public void clear() {
        for (int i = 0; i < buf.length; i++) {
            buf[i] = Float.MAX_VALUE;
        }
    }
}
