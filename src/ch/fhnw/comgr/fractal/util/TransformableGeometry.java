package ch.fhnw.comgr.fractal.util;

import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec4;

/**
 * Created by benjamin on 29.10.2015.
 */
public class TransformableGeometry {
    private final Vec4[] vertices;

    public TransformableGeometry(Vec4[] vertices) {
        this.vertices = vertices;
    }

    public TransformableGeometry transform(Mat4 m) {
        Vec4[] r = new Vec4[vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            r[i] = m.transform(vertices[i]);
        }
        return new TransformableGeometry(r);
    }

    public float[] toArray() {
        float[] r = new float[vertices.length * 3];
        for (int i = 0, j = 0; i < vertices.length; i++,j+=3) {
            r[j+0] = vertices[i].x;
            r[j+1] = vertices[i].y;
            r[j+2] = vertices[i].z;
        }
        return r;
    }
}
