package org.github.zymosi3.mg;

import org.github.zymosi3.mg.math.Vec3;

import java.util.Arrays;
import java.util.stream.Stream;

public class Face {

    public final Vec3[] vertices;

    public Face(Vec3... vertices) {
        assert vertices != null;
        assert vertices.length > 0;
        this.vertices = vertices;
    }

    public Stream<Vec3> stream() {
        return Stream.of(Stream.of(vertices), Stream.of(vertices[0])).flatMap(s -> s);
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
                '}';
    }
}
