package org.github.zymosi3.mg;

import org.github.zymosi3.mg.math.Vec3;

import java.util.Arrays;

public class Face {

    public final Vec3[] v;

    public final Vec3[] vt;

    public final float intensity;

    public final Vec3 n;

    public final Vec3 center;

    public Face(Vec3[] v, Vec3[] vt) {
        this(
                1.0f,
                norm(v[0], v[1], v[2]),
                center(v[0], v[1], v[2]),
                v,
                vt
        );
    }

    public Face(float intensity, Vec3 n, Vec3 center, Vec3[] v, Vec3[] vt) {
        assert v != null;
        assert v.length > 0;
        this.intensity = intensity;
        this.v = v;
        this.n = n;
        this.center = center;
        this.vt = vt;
    }

    public Vec3 norm() {
        return v[2].sub(v[0]).cross(v[1].sub(v[0])).normalize();
    }

    public Vec3 toTexture(Vec3 v) {
        if (vt[0] == null)
            return null;

        float x1 = this.v[0].x;
        float x2 = this.v[2].x;
        float tx1 = this.vt[0].x;
        float tx2 = this.vt[2].x;
        float y1 = this.v[0].y;
        float y2 = this.v[2].y;
        float ty1 = this.vt[0].y;
        float ty2 = this.vt[0].y;

        if (this.v[1].x < x1) {
            x1 = this.v[1].x;
            tx1 = this.vt[1].x;
        }

        if (this.v[2].x < x1) {
            x1 = this.v[2].x;
            tx1 = this.vt[2].x;
        }

        if (this.v[0].x > x2) {
            x2 = this.v[0].x;
            tx2 = this.vt[0].x;
        }

        if (this.v[1].x > x2) {
            x2 = this.v[1].x;
            tx2 = this.vt[1].x;
        }

        if (this.v[1].y < y1) {
            y1 = this.v[1].y;
            ty1 = this.vt[1].y;
        }

        if (this.v[2].y < y1) {
            y1 = this.v[2].y;
            ty1 = this.vt[2].y;
        }

        if (this.v[0].y > y2) {
            y2 = this.v[0].y;
            ty2 = this.vt[0].y;
        }

        if (this.v[1].y > y2) {
            y2 = this.v[1].y;
            ty2 = this.vt[1].y;
        }

        float xTx = tx2 - (tx2 - tx1) * (x2 - v.x) / (x2 - x1);
        float xTy = ty2 - (ty2 - ty1) * (y2 - v.y) / (y2 - y1);
        return new Vec3(xTx, xTy, v.z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Face face = (Face) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(v, face.v);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(v);
    }

    @Override
    public String toString() {
        return "Face{" +
                "v=" + Arrays.toString(v) +
                ", intensity=" + intensity +
                ", n=" + n +
                ", center=" + center +
                '}';
    }

    private static Vec3 norm(Vec3 v0, Vec3 v1, Vec3 v2) {
        return center(v0, v1, v2).add(v2.sub(v0).cross(v1.sub(v0)).normalize());
    }

    private static Vec3 center(Vec3 v0, Vec3 v1, Vec3 v2) {
        return new Vec3(
                (v0.x + v1.x + v2.x) / 3,
                (v0.y + v1.y + v2.y) / 3,
                (v0.z + v1.z + v2.z) / 3
        );
    }
}
