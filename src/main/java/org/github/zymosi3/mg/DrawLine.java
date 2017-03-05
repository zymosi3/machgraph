package org.github.zymosi3.mg;

import org.github.zymosi3.mg.math.Vec3;

import java.util.function.BinaryOperator;

public class DrawLine implements BinaryOperator<Vec3> {

    private final Drawer drawer;

    public DrawLine(Drawer drawer) {

        this.drawer = drawer;
    }

    @Override
    public Vec3 apply(Vec3 v1, Vec3 v2) {
        drawer.line(
                Math.round(v1.x),
                Math.round(v1.y),
                Math.round(v2.x),
                Math.round(v2.y),
                Drawer.color(255, 255, 255)
        );
        return v2;
    }
}
