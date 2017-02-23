package org.github.zymosi3.mg;

public class Drawer {

    private final int[] pixels;
    public final int width;
    public final int height;

    public Drawer(int width, int height) {
        assert width > 0;
        assert height > 0;
        this.width = width;
        this.height = height;
        pixels = new int[width * height];
    }

    public void point(int x, int y, int color) {
        if (x < 0 || x >= width || y < 0 || y >= height) return;
        pixels[width * y + x] = color;
    }

    /**
     * Integer Bresenham’s algorithm
     * According to David F. Rodgers "Procedural Elements for Computer Graphics"
     */
    public void line(int x0, int y0, int x1, int y1, int color) {
        int x = x0;
        int y = y0;
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int s1 = Integer.signum(x1 - x0);
        int s2 = Integer.signum(y1 - y0);

        boolean swap = false;

        if (dy > dx) {
            int v = dx;
            dx = dy;
            dy = v;
            swap = true;
        }

        int error = 2 * dy - dx;

        // iterate from 0 to dx to include the both line ends
        for (int i = 0; i <= dx; i++) {
            point(x, y, color);
            // By Rodgers here should be
            // while (error >= 0) {
            // this modification done to make lanes, drawn from right to left and from left to right, match
            while ((s2 >= 0 && error >= 0) || (s2 < 0 && error > 0)) {
                if (swap)
                    x += s1;
                else
                    y += s2;
                error -= 2 * dx;
            }
            if (swap)
                y += s2;
            else
                x += s1;
            error += 2 * dy;
        }
    }

    public void draw(int[] to) {
        assert to.length == pixels.length;
        System.arraycopy(pixels, 0, to, 0, pixels.length);
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
}
