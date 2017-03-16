package org.github.zymosi3.mg;

import org.github.zymosi3.mg.math.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Context {

    public final int width = 1024;
    public final int height = 768;

    public final float scale = 1f;

    public final int scaledWidth = (int) (width * scale);
    public final int scaledHeight = (int) (height * scale);

    public final List<Obj> objects = new ArrayList<>();

    // light direction in world coords
    public final Vec3 light = new Vec3(0, 0, -1);
}
