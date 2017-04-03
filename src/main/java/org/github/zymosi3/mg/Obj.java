package org.github.zymosi3.mg;

import org.github.zymosi3.mg.math.Vec3;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Obj {

    public final Position position;

    private Motion motion;

    private List<Face> faces;

    public final Function<Vec3, Vec3> obj2Screen;
    public final Function<Vec3, Vec3> screen2obj;
    public final Function<Face, Face> faceObj2Screen;

    public final Function<Obj, Obj> move;

    public final Texture texture;

    public Obj(Position position, Stream<Face> faceStream, Motion move, Texture texture) {
        this.position = position;
        this.texture = texture;
        this.motion = new Motion(new Vec3(0.0f, 0.0f, 0.0f), new Vec3(0.0f, 0.0f, 0.0f));
        this.faces = faceStream.collect(Collectors.toList());

        this.move = move;

        obj2Screen = v -> motion.movePosition.apply(position).obj2screen.apply(v);
        screen2obj = v -> motion.movePosition.apply(position).screen2obj.apply(v);
        faceObj2Screen = face ->
                new Face(
                        face.intensity,
                        obj2Screen.apply(face.n),
                        obj2Screen.apply(face.center),
                        Stream.of(face.v).map(obj2Screen).toArray(Vec3[]::new),
                        face.vt
                );
    }

    public Stream<Face> stream() {
        return faces.stream();
    }

    public Obj motion(Motion motion) {
        this.motion = this.motion.compose(motion);
        return this;
    }
}
