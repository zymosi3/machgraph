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

    public Obj(Position position, Stream<Face> faceStream) {
        this.position = position;
        this.motion = new Motion(new Vec3(0.0f, 0.0f, 0.0f), new Vec3(0.0f, 0.0f, 0.0f));
        this.faces = faceStream.collect(Collectors.toList());

        obj2Screen = v -> motion.movePosition.apply(position).obj2screen.apply(v);
    }

    public Stream<Face> stream() {
        return faces.stream();
    }

    public Obj motion(Motion motion) {
        this.motion = this.motion.compose(motion);
        return this;
    }
}
