package org.github.zymosi3.mg;

import org.github.zymosi3.mg.math.Vec3;

@FunctionalInterface
public interface ColorFunction {

    int apply(int x, int y, float z);
}