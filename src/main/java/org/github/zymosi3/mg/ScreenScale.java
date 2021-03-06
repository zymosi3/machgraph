package org.github.zymosi3.mg;

import org.github.zymosi3.mg.math.Vec3;

import java.util.function.Function;

public class ScreenScale implements Function<Vec3, Vec3> {

    private final int width;
    private final int height;
    private final int scaleTo;

    public ScreenScale(int width, int height) {
        this.width = width;
        this.height = height;
        this.scaleTo = Math.min(width, height);
    }

    @Override
    public Vec3 apply(Vec3 v) {
        Vec3 res = new Vec3(v.x * scaleTo + width * 0.5f, v.y * scaleTo + height * 0.5f, v.z);

        if (Global.DEBUG)
            System.out.println("ScreenScale " + v + " -> " + res);

        return res;
    }

    public Vec3 inverse(Vec3 v) {
        return new Vec3 ((v.x - width * 0.5f) / scaleTo, (v.y - height * 0.5f) / scaleTo, v.z);
    }
}
