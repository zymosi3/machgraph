package org.github.zymosi3.mg.math;

public class Vec3 {

    public final float x;
    public final float y;
    public final float z;

    public Vec3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3 add(Vec3 v) {
        return new Vec3(x + v.x, y + v.y, z + v.z);
    }

    public Vec3 sub(Vec3 v) {
        return new Vec3(x - v.x, y - v.y, z - v.z);
    }

    public float mult(Vec3 v) {
        return x * v.x + y * v.y + z * v.z;
    }

    public Vec3 cross(Vec3 v) {
        return new Vec3(y * v.z - v.y * z, - (x * v.z - v.x * z),  x * v.y - v.x * y);
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public Vec3 normalize() {
        float length = length();
        return new Vec3(x/length, y/length, z/length);
    }

    public Vec3 rotateAboutAxis(float a, Vec3 u) {
        if (Float.isNaN(u.x) || Float.isNaN(u.y) || Float.isNaN(u.z)) {
            return this;
        }
        float cos = (float) Math.cos(a);
        float sin = (float) Math.sin(a);
        Mat3 m = new Mat3(
                cos + u.x * u.x * (1 - cos), u.x * u.y * (1 - cos) - u.z * sin, u.x * u.z * (1 - cos) + u.y * sin,
                u.x * u.y * (1 - cos) + u.z * sin, cos + u.y * u.y * (1 - cos), u.y * u.z * (1 - cos) - u.x * sin,
                u.x * u.z * (1 - cos) - u.y * sin, u.y * u.z * (1 - cos) + u.x * sin, cos + u.z * u.z * (1 - cos)
        );
        return m.multiply(this);
    }

    public float angle(Vec3 v) {
        return (float) Math.acos(this.mult(v)/(this.length() * v.length()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vec3 vec3 = (Vec3) o;

        return Float.compare(vec3.x, x) == 0 && Float.compare(vec3.y, y) == 0 && Float.compare(vec3.z, z) == 0;
    }

    @Override
    public int hashCode() {
        int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
        result = 31 * result + (z != +0.0f ? Float.floatToIntBits(z) : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Vec3{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
