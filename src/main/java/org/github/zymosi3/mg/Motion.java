package org.github.zymosi3.mg;

import org.github.zymosi3.mg.math.Mat3;
import org.github.zymosi3.mg.math.Vec3;

import java.util.function.Function;

/**
 *
 */
public class Motion implements Function<Obj, Obj> {

    public final Mat3 Q;
    public final Mat3 QT;
    public final Vec3 angles;
    public final Vec3 dX;

    public final Function<Position, Position> movePosition;

    public Motion(Vec3 angles, Vec3 dX) {
        float xAng = (float) Math.toRadians(angles.x);
        float xCos = (float) Math.cos(xAng);
        float xSin = (float) Math.sin(xAng);
        Mat3 Qx = new Mat3(
                1.0f, 0.0f, 0.0f,
                0.0f, xCos, -xSin,
                0.0f, xSin, xCos
        );

        float yAng = (float) Math.toRadians(angles.y);
        float yCos = (float) Math.cos(yAng);
        float ySin = (float) Math.sin(yAng);
        Mat3 Qy = new Mat3(
                yCos, 0.0f, -ySin,
                0.0f, 1.0f, 0.0f,
                ySin, 0.0f, yCos
        );

        float zAng = (float) Math.toRadians(angles.z);
        float zCos = (float) Math.cos(zAng);
        float zSin = (float) Math.sin(zAng);
        Mat3 Qz = new Mat3(
                zCos, -zSin, 0.0f,
                zSin, zCos,  0.0f,
                0.0f, 0.0f,  1.0f
        );

        Q = Qx.multiply(Qy).multiply(Qz);
        QT = Q.transpose();

        this.angles = angles;
        this.dX = dX;

        movePosition = position -> {
            Function<Vec3, Vec3> shiftX0 = new Shift(position.X0).apply(dX);
            Vec3 X0dash = QT.multiply(shiftX0.apply(position.X0));
            Mat3 Adash = QT.multiply(position.A);
            Mat3 A1dash = position.A1.multiply(Q);

            return new Position(Adash, A1dash, X0dash);
        };
    }

    @Override
    public Obj apply(Obj obj) {
        return obj.motion(this);
    }

    public Motion compose(Motion before) {
        return new Motion(before.angles.add(angles), before.dX.add(dX));
    }
}
