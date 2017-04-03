package org.github.zymosi3.mg;


import org.github.zymosi3.mg.math.Vec3;

import java.util.function.Function;

public class PerspectiveProjection implements Function<Vec3, Vec3> {

    private final float focus;

    public PerspectiveProjection(float focus) {
        this.focus = focus;
    }

    @Override
    public Vec3 apply(Vec3 v) {
        float zeta = 1.0f + v.z / focus;
        Vec3 res = new Vec3(v.x / zeta, v.y / zeta, v.z);

        if (Global.DEBUG)
            System.out.println("PerspectiveProjection " + v + " -> " + res);

        return res;
    }

    public Vec3 inverse(Vec3 v) {
        float zeta = 1.0f + v.z / focus;
        return new Vec3(v.x * zeta, v.y * zeta, v.z);
    }
}
