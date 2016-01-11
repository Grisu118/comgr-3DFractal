package ch.fhnw.comgr.fractal.util;

import ch.fhnw.util.math.Mat3;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.Vec4;

/**
 * Created by benjamin on 29.10.2015.
 */
public class TransformableGeometry {
    private final Vec4[] vertices;
    private final Vec3[] normals;

    public TransformableGeometry(Vec4[] vertices, Vec3[] normals) {
        this.vertices = vertices;
        this.normals = normals;
    }

    public TransformableGeometry transform(Mat4 m) {
        Vec4[] r = new Vec4[vertices.length];
        Vec3[] n = new Vec3[normals.length];
        Mat3 m2 = new Mat3(m).inverse().transpose();
        for (int i = 0; i < vertices.length; i++) {
            r[i] = m.transform(vertices[i]);
            n[i] = m2.transform(normals[i]);
        }
        return new TransformableGeometry(r, n);
    }

    public float[] getVertices() {
        float[] r = new float[vertices.length * 3];
        for (int i = 0, j = 0; i < vertices.length; i++,j+=3) {
            r[j+0] = vertices[i].x;
            r[j+1] = vertices[i].y;
            r[j+2] = vertices[i].z;
        }
        return r;
    }

    public float[] getNormals() {
        float[] n = new float[normals.length * 3];
        for (int i = 0, j = 0; i < normals.length; i++,j+=3) {
            n[j+0] = normals[i].x;
            n[j+1] = normals[i].y;
            n[j+2] = normals[i].z;
        }
        return n;
    }
}
