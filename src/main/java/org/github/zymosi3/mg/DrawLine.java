package org.github.zymosi3.mg;

import org.github.zymosi3.mg.math.Vec3;

import java.util.function.BinaryOperator;

public class DrawLine implements BinaryOperator<Vec3> {

    private final DrawerZBuffered drawer;

    public DrawLine(DrawerZBuffered drawer) {

        this.drawer = drawer;
    }

    @Override
    public Vec3 apply(Vec3 v1, Vec3 v2) {
        drawer.line(
                Math.round(v1.x),
                Math.round(v1.y),
                v1.z,
                Math.round(v2.x),
                Math.round(v2.y),
                v2.z,
                Drawer.color(255, 255, 255)
        );
        return v2;
    }
}
