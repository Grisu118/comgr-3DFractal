package ch.fhnw.comgr.fractal.fractals.mandel;

import ch.fhnw.comgr.fractal.IUpdateListener;
import ch.fhnw.comgr.fractal.fractals.IFractal;
import ch.fhnw.ether.controller.DefaultController;
import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.scene.DefaultScene;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.ether.scene.camera.Camera;
import ch.fhnw.ether.scene.light.ILight;
import ch.fhnw.ether.scene.mesh.DefaultMesh;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.geometry.DefaultGeometry;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry;
import ch.fhnw.ether.scene.mesh.material.ColorMaterial;
import ch.fhnw.ether.scene.mesh.material.IMaterial;
import ch.fhnw.ether.scene.mesh.material.PointMaterial;
import ch.fhnw.ether.scene.mesh.material.ShadedMaterial;
import ch.fhnw.ether.ui.Button;
import ch.fhnw.ether.ui.IWidget;
import ch.fhnw.ether.view.IView;
import ch.fhnw.ether.view.gl.DefaultView;
import ch.fhnw.util.color.RGB;
import ch.fhnw.util.color.RGBA;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.MathUtilities;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.GeodesicSphere;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TODO this needs a nice description.
 */
public class MandelBulb implements IFractal{

    IGeometry geometry;
    IMaterial mat;
    IMesh mesh;
    IScene scene;

    public MandelBulb(IScene _scene) {
        scene = _scene;
    }

    /**
     *
     * @param v The vector as float array that will be transformed into a MandelBulb vector.
     * @param n The n-th power of the vector. (Dimension)
     * @return The transformed vector.
     */
    private static float[] transformVector(float[] v, int n) {
        float x0 = v[0];
        float y0 = v[1];
        float z0 = v[2];
        float x = 0, y = 0, z = 0;
        int iter = 0, maxIter = 8;
        while(true) {


            // Mandelbulb calculation source: https://en.wikipedia.org/wiki/Mandelbulb

            // Radius
            float r = (float) Math.sqrt(x * x + y * y + z * z);
            float phi = (float) (Math.atan2(y, x));
            float theta = (float) (Math.atan2(Math.sqrt(x * x + y * y), z));

            // Store "r to the power" of n to save some calculation time.
            float r_pow_n = (float) Math.pow(r, n);
            x = (float) (r_pow_n * Math.sin(theta * n) * Math.cos(phi * n));
            y = (float) (r_pow_n * Math.sin(theta * n) * Math.sin(phi * n));
            z = (float) (r_pow_n * Math.cos(theta * n));

            x += x0; y+=y0; z+=z0;
            iter++;

            if(x*x + y*y + z*z > 10) {
                x = 0; y = 0; z = 0;
                break;
            }
            if(iter > maxIter) {
                break;
            }
        }

        return new float[]{x, y, z};
    }

    /*
     * The input is the array of all points that have to be transformed.
     * Groups of 3 will be transformed and the input is overwritten and returned.
     */
    private static float[]  formMandelBulb(float[] input, int n) {
        // To avoid problems we have to check if it's a legal amount of values in the input array.
        if (input.length == 0 || input.length % 3 != 0) {
            throw new IllegalArgumentException("The array is empty or has not enough values.");
        } else {

            float[] res;
            for (int i = 0; i < input.length; i += 3) {
                res = transformVector(new float[]{input[i], input[i+1], input[i+2]}, n);
                input[i]   = res[0];
                input[i+1] = res[1];
                input[i+2] = res[2];
            }
        }
        return input;
    }

    @Override
    public void init() {
        mat = new MandelBulbMaterial(new MandelBulbShader(getClass()));
        float[] vert = createCube(100);
        for(int i = 0; i < 4; i++) {
            formMandelBulb(vert, 8);
        }
        float[] norm = new float[vert.length];

        geometry = DefaultGeometry.createVN(IGeometry.Primitive.POINTS, vert, norm);
        mesh = new DefaultMesh(mat, geometry);
        scene.add3DObject(mesh);
    }

    @Override
    public List<IWidget> getWidgets() {
        return new ArrayList<IWidget>();
    }

    @Override
    public void cleanup() {

    }

    @Override
    public int getVerticesCount() {
        return 0;
    }

    @Override
    public int getTrianglesCount() {
        return 0;
    }

    @Override
    public void registerUpdateListener(IUpdateListener listener) {

    }

    @Override
    public void removeUpdateListener(IUpdateListener listener) {

    }

    @Override
    public boolean getLightState() {
        return false;
    }

    @Override
    public ILight getLight() {
        return null;
    }

    private static float[] createCube(int n) {
        float step = 1.5f/(2*((float)n));
        int c = 0;
        float[] out = new float[n*n*n*3];
        for(int i = -n; i < n; i+=2) {
            for(int j = -n;j < n; j+=2) {
                for(int k = -n; k < n; k+=2) {
                    out[c] = step*i;
                    out[c+1] = step*j;
                    out[c+2] = step*k;
                    c+=3;
                }
            }
        }
        return out;
    }

}
