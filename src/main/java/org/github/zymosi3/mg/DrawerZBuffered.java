package org.github.zymosi3.mg;

import java.util.Arrays;

@SuppressWarnings("Duplicates")
public class DrawerZBuffered {

    private final int[] pixels;
    private final Zbuf zbuf;
    public final int width;
    public final int height;

    public DrawerZBuffered(int width, int height) {
        assert width > 0;
        assert height > 0;
        this.width = width;
        this.height = height;
        pixels = new int[width * height];
        zbuf = new Zbuf(width, height);
    }

    public void point(int x, int y, float z, int color) {
        if (x < 0 || x >= width || y < 0 || y >= height || z < 0) return;
        if (z < zbuf.get(x, y)) {
            pixels[width * (height - 1 - y) + x] = color;
            zbuf.set(x, y, z);
        }
    }

    /**
     * Integer Bresenham’s algorithm
     * According to David F. Rodgers "Procedural Elements for Computer Graphics"
     */
    public void line(int x0, int y0, float z0, int x1, int y1, float z1, int color) {
        if (Global.DEBUG)
            System.out.println("Drawer.line(" + x0 + ", " + y0 + ", " + x1 + ", " + y1 + ", " + color + ")");

        int x = x0;
        int y = y0;
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        float dz = z1 - z0;
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
            int sx = swap ? s2 : s1;
            float z = z0 + (swap ? (y - y0) : (x - x0)) * dz / dx * sx;
            point(x, y, z, color);
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

    private float clamp(float value) {
        return Math.max(0.0f, Math.min(value, 1.0f));
    }

    private float interpolate1(float min, float max, float gradient) {
        return min + (max - min) * clamp(gradient);
    }

    private void processScanLine(
            int y,
            int pax, int pay, float paz,
            int pbx, int pby, float pbz,
            int pcx, int pcy, float pcz,
            int pdx, int pdy, float pdz,
            int color
    ) {
        float gradient1 = pay != pby ? ((float) (y - pay)) / (pby - pay) : 1;
        float gradient2 = pcy != pdy ? ((float) (y - pcy)) / (pdy - pcy) : 1;
        int sx = (int) interpolate1(pax, pbx, gradient1);
        int ex = (int) interpolate1(pcx, pdx, gradient2);
        float z1 = interpolate1(paz, pbz, gradient1);
        float z2 = interpolate1(pcz, pdz, gradient2);
        for (int x = sx; x < ex; x++) {
            float gradient = ((float) (x - sx)) / (ex - sx);
            float z = interpolate1(z1, z2, gradient);
            point(x, y, z, color);
        }
    }

    public void drawTriangle(
            int p1x, int p1y, float p1z,
            int p2x, int p2y, float p2z,
            int p3x, int p3y, float p3z,
            int color
    ) {
        // Sorting the points in order to always have this order on screen p1, p2 & p3
        // with p1 always up (thus having the Y the lowest possible to be near the top screen)
        // then p2 between p1 & p3
        if (p1y > p2y) {
            int x = p2x;
            int y = p2y;
            float z = p2z;
            p2x = p1x;
            p2y = p1y;
            p2z = p1z;
            p1x = x;
            p1y = y;
            p1z = z;
        }

        if (p2y > p3y) {
            int x = p2x;
            int y = p2y;
            float z = p2z;
            p2x = p3x;
            p2y = p3y;
            p2z = p3z;
            p3x = x;
            p3y = y;
            p3z = z;
        }

        if (p1y > p2y)
        {
            int x = p2x;
            int y = p2y;
            float z = p2z;
            p2x = p1x;
            p2y = p1y;
            p2z = p1z;
            p1x = x;
            p1y = y;
            p1z = z;
        }

        // inverse slopes
        float dP1P2, dP1P3;

        // http://en.wikipedia.org/wiki/Slope
        // Computing inverse slopes
        if (p2y - p1y > 0)
            dP1P2 = ((float) (p2x - p1x)) / (p2y - p1y);
        else
            dP1P2 = 0;

        if (p3y - p1y > 0)
            dP1P3 = ((float) (p3x - p1x)) / (p3y - p1y);
        else
            dP1P3 = 0;

        // First case where triangles are like that:
        // P1
        // -
        // --
        // - -
        // -  -
        // -   - P2
        // -  -
        // - -
        // -
        // P3
        if (dP1P2 > dP1P3) {
            for (int y = p1y; y <= p3y; y++) {
                if (y < p2y) {
                    processScanLine(
                            y,
                            p1x, p1y, p1z,
                            p3x, p3y, p3z,
                            p1x, p1y, p1z,
                            p2x, p2y, p2z,
                            color
                    );
                }
                else {
                    processScanLine(
                            y,
                            p1x, p1y, p1z,
                            p3x, p3y, p3z,
                            p2x, p2y, p2z,
                            p3x, p3y, p3z,
                            color
                    );
                }
            }
        }
        // First case where triangles are like that:
        //       P1
        //        -
        //       --
        //      - -
        //     -  -
        // P2 -   -
        //     -  -
        //      - -
        //        -
        //       P3
        else
        {
            for (int y = p1y; y <= p3y; y++) {
                if (y < p2y) {
                    processScanLine(
                            y,
                            p1x, p1y, p1z,
                            p2x, p2y, p2z,
                            p1x, p1y, p1z,
                            p3x, p3y, p3z,
                            color
                    );
                }
                else {
                    processScanLine(
                            y,
                            p2x, p2y, p2z,
                            p3x, p3y, p3z,
                            p1x, p1y, p1z,
                            p3x, p3y, p3z,
                            color
                    );
                }
            }
        }
    }

    public void triangleBresenham(
            int x0, int y0, float z0,
            int x1, int y1, float z1,
            int x2, int y2, float z2,
            int color
    ) {
        if (Global.DEBUG) {
            System.out.println("triangle " +
                    x0 + " " + y0 + " " + z0 + " " +
                    x1 + " " + y1 + " " + z1 + " " +
                    x2 + " " + y2 + " " + z2 + " " +
                    color);
        }
        int x_0, y_0, x_1, y_1, x_2, y_2;
        float z_0, z_1, z_2;
        if (y0 <= y1) {
            if (y0 <= y2) {
                x_0 = x0;
                y_0 = y0;
                z_0 = z0;
                if (y1 <= y2) { //012
                    x_1 = x1;
                    y_1 = y1;
                    z_1 = z1;
                    x_2 = x2;
                    y_2 = y2;
                    z_2 = z2;
                } else { //021
                    x_1 = x2;
                    y_1 = y2;
                    z_1 = z2;
                    x_2 = x1;
                    y_2 = y1;
                    z_2 = z1;
                }
            } else { //201
                x_0 = x2;
                y_0 = y2;
                z_0 = z2;
                x_1 = x0;
                y_1 = y0;
                z_1 = z0;
                x_2 = x1;
                y_2 = y1;
                z_2 = z1;
            }
        } else if (y1 <= y2) {
            x_0 = x1;
            y_0 = y1;
            z_0 = z1;
            if (y0 <= y2) { //102
                x_1 = x0;
                y_1 = y0;
                z_1 = z0;
                x_2 = x2;
                y_2 = y2;
                z_2 = z2;
            } else { // 120
                x_1 = x2;
                y_1 = y2;
                z_1 = z2;
                x_2 = x0;
                y_2 = y0;
                z_2 = z0;
            }
        } else { // 210
            x_0 = x2;
            y_0 = y2;
            z_0 = z2;
            x_1 = x1;
            y_1 = y1;
            z_1 = z1;
            x_2 = x0;
            y_2 = y0;
            z_2 = z0;
        }

        altTriangleSorted(x_0, y_0, z_0, x_1, y_1, z_1, x_2, y_2, z_2, color);
    }

    private void altTriangleSorted(
            int x0, int y0, float z0,
            int x1, int y1, float z1,
            int x2, int y2, float z2,
            int color
    ) {
        if (y1 == y2 && y0 == y1) {
            altTriangle(x0, y0, z0, x1, y1, z1, x2, y2, z2, color);
        } else {
            // general case - split the triangle in a topflat and bottom-flat one
            int x3 = Math.round(x0 + ((float)(y1 - y0) / (float)(y2 - y0)) * (x2 - x0));
            int dx = Math.abs(x0 - x2);
            int dy = Math.abs(y0 - y2);
            float z3;
            if (dy > dx) {
                z3 = z0 + (y1 - y0) * (z2 - z0) / (y2 - y0);
            } else {
                z3 = z0 + (x3 - x0) * (z2 - z0) / (x2 - x0);
            }
            altTriangle(x0, y0, z0, x1, y1, z1, x3, y1, z3, color);
            altTriangle(x1, y1, z1, x3, y1, z3, x2, y2, z2, color);
        }
    }

    private float interpolate(float start, float end, float gradient) {
        return start + Math.abs(end - start) * gradient;
    }

    private void altTriangle(
            int x0, int y0, float z0,
            int x1, int y1, float z1,
            int x2, int y2, float z2,
            int color
    ) {
        for (int y = y0; y <= y2; y++) {
            float gradient = y2 != y0 ? ((float) (y - y0)) / (y2 - y0) : 1.0f;
            int x_0, x_1;
            float x_0f, x_1f;
            float z_0, z_1;
            if (y0 == y1) {
                x_0f = interpolate(x0, x2, gradient * Math.signum(x2 - x0));
                x_1f = interpolate(x1, x2, gradient * Math.signum(x2 - x1));
                z_0 = interpolate(z0, z2, gradient * Math.signum(z2 - z0));
                z_1 = interpolate(z1, z2, gradient * Math.signum(z2 - z1));
            } else {
                x_0f = interpolate(x0, x1, gradient * Math.signum(x1 - x0));
                x_1f = interpolate(x0, x2, gradient * Math.signum(x2 - x0));
                z_0 = interpolate(z0, z1, gradient * Math.signum(z1 - z0));
                z_1 = interpolate(z0, z2, gradient * Math.signum(z2 - z0));
            }
            if (x_0f > x_1f) {
                float x = x_0f;
                x_0f = x_1f;
                x_1f = x;
                float z = z_0;
                z_0 = z_1;
                z_1 = z;
            }
            x_0 = (int) x_0f;
            x_1 = (int) x_1f;
            for (int x = x_0; x <= x_1; x++) {
                float gradientZ = x_1f != x_0f ? (x - x_0f) / (x_1f - x_0f) : 1.0f;
                float z = interpolate(z_0, z_1, gradientZ * Math.signum(z_1- z_0));
                point(x, y, z, color);
            }
        }
    }

//    public void triangle(int x0, int y0, int x1, int y1, int x2, int y2, int color) {
//        int xMin = Math.min(x0, Math.min(x1, x2));
//        int yMin = Math.min(y0, Math.min(y1, y2));
//        int xMax = Math.max(x0, Math.max(x1, x2));
//        int yMax = Math.max(y0, Math.max(y1, y2));
//
//        for (int y = yMin; y <= yMax; y++) {
//            float xCross0 = xMax;
//            float xCross1 = xMin;
//            float xCross = xCross(xMin, y, xMax, y, x0, y0, x1, y1);
//            if (!Float.isNaN(xCross)) {
//                if (xCross < xCross0) xCross0 = xCross;
//                if (xCross > xCross1) xCross1 = xCross;
//            }
//            xCross = xCross(xMin, y, xMax, y, x1, y1, x2, y2);
//            if (!Float.isNaN(xCross)) {
//                if (xCross < xCross0) xCross0 = xCross;
//                if (xCross > xCross1) xCross1 = xCross;
//            }
//            xCross = xCross(xMin, y, xMax, y, x2, y2, x0, y0);
//            if (!Float.isNaN(xCross)) {
//                if (xCross < xCross0) xCross0 = xCross;
//                if (xCross > xCross1) xCross1 = xCross;
//            }
//            int xCross0int = Math.round(xCross0);
//            int xCross1int = -Math.round(-xCross1);
//            for (int x = xCross0int; x <= xCross1int; x++) {
//                point(x, y, color);
//            }
//        }
//    }

//    private float xCross(float x00, float y00, float x01, float y01,
//                         float x10, float y10, float x11, float y11
//    ) {
//        float xMin = Math.min(x10, x11);
//        float yMin = Math.min(y10, y11);
//        float xMax = Math.max(x10, x11);
//        float yMax = Math.max(y10, y11);
//        float a0 = y01 - y00;
//        float b0 = x00 - x01;
//        float c0 = x00 * (y00 - y01) + y00 * (x01 - x00);
//        float a1 = y11 - y10;
//        float b1 = x10 - x11;
//        float c1 = x10 * (y10 - y11) + y10 * (x11 - x10);
//
//        float D = a0 * b1 - a1 * b0;
//        float Dx = -c0 * b1 + c1 * b0;
//        float Dy = -a0 * c1 + a1 * c0;
//
//        if (D != 0) {
//            float x = Dx / D;
//            float y = Dy / D;
//            float e = -0.001f;
//            if (x - xMin > e && xMax - x > e && y - yMin > e && yMax - y > e) {
//                return x;
//            }
//        }
//
//        return Float.NaN;
//    }

    public void flush(int[] to) {
        assert to.length == pixels.length;
        System.arraycopy(pixels, 0, to, 0, pixels.length);
    }

    public void clear() {
        Arrays.setAll(pixels, i -> 0);
        zbuf.clear();
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
