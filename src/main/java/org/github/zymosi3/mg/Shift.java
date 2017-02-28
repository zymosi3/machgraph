package org.github.zymosi3.mg;

import org.github.zymosi3.mg.math.Vec3;

import java.util.function.Function;

public class Shift implements Function<Vec3, Function<Vec3, Vec3>> {

    private final Vec3 X0;

    public Shift(Vec3 X0) {
        this.X0 = X0;
    }

    @Override
    public Function<Vec3, Vec3> apply(Vec3 dX) {
        return v -> new Vec3(v.x + X0.x - dX.x, v.y + X0.y - dX.y, v.z + X0.z - dX.z);
    }
}
