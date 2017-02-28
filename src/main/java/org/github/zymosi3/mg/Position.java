package org.github.zymosi3.mg;

import org.github.zymosi3.mg.math.Mat3;
import org.github.zymosi3.mg.math.Vec3;

import java.util.function.Function;

public class Position {

    public final Mat3 A;
    public final Mat3 A1;
    public final Vec3 X0;

    public final Function<Vec3, Vec3> screen2obj;
    public final Function<Vec3, Vec3> obj2screen;

    public Position(Mat3 A, Mat3 A1, Vec3 X0) {
        this.A = A;
        this.A1 = A1;
        this.X0 = X0;

        this.screen2obj = x -> A.multiply(x).add(X0);
        this.obj2screen = x -> A1.multiply(x.sub(X0));
    }
}
