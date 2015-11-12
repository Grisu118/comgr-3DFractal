package ch.fhnw.comgr.fractal.fractals;

import ch.fhnw.comgr.fractal.fractals.IFractal;
import ch.fhnw.ether.controller.DefaultController;
import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.scene.DefaultScene;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.ether.scene.camera.Camera;
import ch.fhnw.ether.scene.mesh.DefaultMesh;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.geometry.DefaultGeometry;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry;
import ch.fhnw.ether.scene.mesh.material.ColorMaterial;
import ch.fhnw.ether.ui.Button;
import ch.fhnw.ether.ui.IWidget;
import ch.fhnw.ether.view.IView;
import ch.fhnw.ether.view.gl.DefaultView;
import ch.fhnw.util.color.RGBA;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.GeodesicSphere;

import java.awt.event.KeyEvent;
import java.util.List;

/**
 * TODO this needs a nice description.
 */
public class MandelBulb {

    /**
     *
     * @param v The vector as float array that will be transformed into a MandelBulb vector.
     * @param n The n-th power of the vector. (Dimension)
     * @return The transformed vector.
     */
    private static float[] transformVector(float[] v, int n) {

        float x = v[0];
        float y = v[1];
        float z = v[2];

        double toRad = (Math.PI/180.0);

        // Mandelbulb calculation source: https://en.wikipedia.org/wiki/Mandelbulb

        // Radius
        float r = (float) Math.sqrt(x*x + y*y + z*z);
        float phi = (float) (Math.atan(y/x) * toRad);
        float theta = (float) (Math.atan(Math.sqrt(x*x+y*y)) * toRad);

        // Store "r to the power" of n to save some calculation time.
        float r_pow_n = (float) Math.pow(r, n);
        float newX = (float) (r_pow_n * Math.sin(theta*n) * Math.cos(phi*n));
        float newY = (float) (r_pow_n * Math.sin(theta*n) * Math.sin(phi*n));
        float newZ = (float) (r_pow_n * Math.cos(theta*n));

        return new float[]{newX, newY, newZ};
    }

    /*
     * The input is the array of all points that have to be transformed.
     * Groups of 3 will be transformed and the input is overwritten and returned.
     */
    private static float[]  formMandelBulb(float[] input, int n) throws Exception {
        // To avoid problems we have to check if it's a legal amount of values in the input array.
        if (input.length == 0 || input.length % 3 != 0) {
            throw new Exception("The array is empty or has not enough values.");
        } else {

            float[] res = null;
            for (int i = 0; i < input.length; i = i + 3) {
                res = transformVector(new float[]{input[i], input[i+1], input[i+2]}, n);
                input[i]   = res[i];
                input[i+1] = res[i+1];
                input[i+2] = res[i+2];
            }
        }
        return input;
    }
}
