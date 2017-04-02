package org.github.zymosi3.mg;

import org.github.zymosi3.mg.math.Vec3;

import java.util.Arrays;

public class Face {

    public final Vec3[] vertices;

    public final float intensity;

    public final Vec3 n;

    public final Vec3 center;

    public Face(Vec3... vertices) {
        this(
                1.0f,
                norm(vertices[0], vertices[1], vertices[2]),
                center(vertices[0], vertices[1], vertices[2]),
                vertices
        );
    }

    public Face(float intensity, Vec3 n, Vec3 center, Vec3... vertices) {
        assert vertices != null;
        assert vertices.length > 0;
        this.intensity = intensity;
        this.vertices = vertices;
        this.n = n;
        this.center = center;
    }

    public Vec3 norm() {
        return vertices[2].sub(vertices[0]).cross(vertices[1].sub(vertices[0])).normalize();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Face face = (Face) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(vertices, face.vertices);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(vertices);
    }

    @Override
    public String toString() {
        return "Face{" +
                "vertices=" + Arrays.toString(vertices) +
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
