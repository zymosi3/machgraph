package org.github.zymosi3.mg;

import java.util.Arrays;

@SuppressWarnings("Duplicates")
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
        pixels[width * (height - 1 - y) + x] = color;
    }

    /**
     * Integer Bresenham’s algorithm
     * According to David F. Rodgers "Procedural Elements for Computer Graphics"
     */
    public void line(int x0, int y0, int x1, int y1, int color) {
        if (Global.DEBUG)
            System.out.println("Drawer.line(" + x0 + ", " + y0 + ", " + x1 + ", " + y1 + ", " + color + ")");

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
//             while (error >= 0) {
            // this modification done to make lanes, drawn from right to left and from left to right, match
            // s2 >= 0 means that we in 1st or 2nd octant
            // s2 < 0 means that we in 3rd or 4th octant
            while ((s2 > 0 && error >= 0) || (s2 <= 0 && error > 0)) {
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

    public void triangleBresenham(int x0, int y0, int x1, int y1, int x2, int y2, int color) {
        int x_0, y_0, x_1, y_1, x_2, y_2;
        if (y0 <= y1) {
            if (y0 <= y2) {
                x_0 = x0;
                y_0 = y0;
                if (y1 <= y2) { //012
                    x_1 = x1;
                    y_1 = y1;
                    x_2 = x2;
                    y_2 = y2;
                } else { //021
                    x_1 = x2;
                    y_1 = y2;
                    x_2 = x1;
                    y_2 = y1;
                }
            } else { //201
                x_0 = x2;
                y_0 = y2;
                x_1 = x0;
                y_1 = y0;
                x_2 = x1;
                y_2 = y1;
            }
        } else if (y1 <= y2) {
            x_0 = x1;
            y_0 = y1;
            if (y0 <= y2) { //102
                x_1 = x0;
                y_1 = y0;
                x_2 = x2;
                y_2 = y2;
            } else { // 120
                x_1 = x2;
                y_1 = y2;
                x_2 = x0;
                y_2 = y0;
            }
        } else { // 210
            x_0 = x2;
            y_0 = y2;
            x_1 = x1;
            y_1 = y1;
            x_2 = x0;
            y_2 = y0;
        }

        triangleBresenhamSorted(x_0, y_0, x_1, y_1, x_2, y_2, color);
    }

    private void triangleBresenhamSorted(int x0, int y0, int x1, int y1, int x2, int y2, int color) {
        // here we know that y0 <= y2 <= y3
        if (y1 == y2) { // check for trivial case of bottom-flat triangle
            fillOneSideFlatTriangle(x1, y1, x2, y2, x0, y0, color);
        } else if (y0 == y1) { // check for trivial case of top-flat triangle
            fillOneSideFlatTriangle(x0, y0, x1, y1, x2, y2, color);
        } else {
            // general case - split the triangle in a topflat and bottom-flat one
            int x3 = Math.round(x0 + ((float)(y1 - y0) / (float)(y2 - y0)) * (x2 - x0));
            fillOneSideFlatTriangle(x1, y1, x3, y1, x0, y0, color);
            fillOneSideFlatTriangle(x1, y1, x3, y1, x2, y2, color);
        }
    }

    private void fillOneSideFlatTriangle(int x0, int y0, int x1, int y1, int x2, int y2, int color) {
        int x_0 = x2; // x for the one side line
        int x_1 = x2; // x for the other side line
        int y_0 = y2;
        int y_1 = y2;
        int dx_0 = Math.abs(x0 - x2);
        int dx_1 = Math.abs(x1 - x2);
        int dy_0 = Math.abs(y0 - y2);
        int dy_1 = Math.abs(y1 - y2);
        int sx_0 = Integer.signum(x0 - x2);
        int sx_1 = Integer.signum(x1 - x2);
        int sy_0 = Integer.signum(y0 - y2);
        int sy_1 = Integer.signum(y1 - y2);

        boolean swap_0 = false;
        if (dy_0 > dx_0) {
            int v = dx_0;
            dx_0 = dy_0;
            dy_0 = v;
            swap_0 = true;
        }

        boolean swap_1 = false;

        if (dy_1 > dx_1) {
            int v = dx_1;
            dx_1 = dy_1;
            dy_1 = v;
            swap_1 = true;
        }

        int error_0 = 2 * dy_0 - dx_0;
        int error_1 = 2 * dy_1 - dx_1;

        boolean stop_0 = false;
        boolean stop_1 = false;
        for (int i_0 = 0, i_1 = 0; i_0 <= dx_0 || i_1 <= dx_1; ) {
            if (stop_0 && stop_1) {
                int xMin = Math.min(x_0, x_1);
                int xMax = Math.max(x_0, x_1);
                for (int x = xMin; x <= xMax; x++){
                    point(x, y_0, color);
                }
                stop_0 = false;
                stop_1 = false;
            }
            if (i_0 <= dx_0 && !stop_0) {
                point(x_0, y_0, color);
                while ((sy_0 > 0 && error_0 >= 0) || (sy_0 <= 0 && error_0 > 0)) {
                    if (swap_0)
                        x_0 += sx_0;
                    else {
                        y_0 += sy_0;
                        stop_0 = true;
                    }
                    error_0 -= 2 * dx_0;
                }

                if (swap_0) {
                    y_0 += sy_0;
                    stop_0 = true;
                } else
                    x_0 += sx_0;
                error_0 += 2 * dy_0;
                i_0++;
            }

            if (i_1 <= dx_1 && !stop_1) {
                point(x_1, y_1, color);
                while ((sy_1 > 0 && error_1 >= 0) || (sy_1 <= 0 && error_1 > 0)) {
                    if (swap_1)
                        x_1 += sx_1;
                    else {
                        y_1 += sy_1;
                        stop_1 = true;
                    }
                    error_1 -= 2 * dx_1;
                }
                if (swap_1) {
                    y_1 += sy_1;
                    stop_1 = true;
                } else
                    x_1 += sx_1;
                error_1 += 2 * dy_1;
                i_1++;
            }
        }
    }

    public void triangle(int x0, int y0, int x1, int y1, int x2, int y2, int color) {
        int xMin = Math.min(x0, Math.min(x1, x2));
        int yMin = Math.min(y0, Math.min(y1, y2));
        int xMax = Math.max(x0, Math.max(x1, x2));
        int yMax = Math.max(y0, Math.max(y1, y2));

        for (int y = yMin; y <= yMax; y++) {
            float xCross0 = xMax;
            float xCross1 = xMin;
            float xCross = xCross(xMin, y, xMax, y, x0, y0, x1, y1);
            int nanCount = 0;
            if (Float.isNaN(xCross)) nanCount++;
            if (!Float.isNaN(xCross)) {
                if (xCross < xCross0) xCross0 = xCross;
                if (xCross > xCross1) xCross1 = xCross;
            }
            xCross = xCross(xMin, y, xMax, y, x1, y1, x2, y2);
            if (Float.isNaN(xCross)) nanCount++;
            if (!Float.isNaN(xCross)) {
                if (xCross < xCross0) xCross0 = xCross;
                if (xCross > xCross1) xCross1 = xCross;
            }
            xCross = xCross(xMin, y, xMax, y, x2, y2, x0, y0);
            if (Float.isNaN(xCross)) nanCount++;
            if (!Float.isNaN(xCross)) {
                if (xCross < xCross0) xCross0 = xCross;
                if (xCross > xCross1) xCross1 = xCross;
            }
            int xCross0int = Math.round(xCross0);
            int xCross1int = -Math.round(-xCross1);
            for (int x = xCross0int; x <= xCross1int; x++) {
                point(x, y, color);
            }
        }
    }

    private float xCross(float x00, float y00, float x01, float y01,
                         float x10, float y10, float x11, float y11
    ) {
        float xMin = Math.min(x10, x11);
        float yMin = Math.min(y10, y11);
        float xMax = Math.max(x10, x11);
        float yMax = Math.max(y10, y11);
        float a0 = y01 - y00;
        float b0 = x00 - x01;
        float c0 = x00 * (y00 - y01) + y00 * (x01 - x00);
        float a1 = y11 - y10;
        float b1 = x10 - x11;
        float c1 = x10 * (y10 - y11) + y10 * (x11 - x10);

        float D = a0 * b1 - a1 * b0;
        float Dx = -c0 * b1 + c1 * b0;
        float Dy = -a0 * c1 + a1 * c0;

        if (D != 0) {
            float x = Dx / D;
            float y = Dy / D;
            float e = -0.001f;
            if (x - xMin > e && xMax - x > e && y - yMin > e && yMax - y > e) {
                return x;
            }
        }

        return Float.NaN;
    }

    public void flush(int[] to) {
        assert to.length == pixels.length;
        System.arraycopy(pixels, 0, to, 0, pixels.length);
    }

    public void clear() {
        Arrays.setAll(pixels, i -> 0);
    }
}
