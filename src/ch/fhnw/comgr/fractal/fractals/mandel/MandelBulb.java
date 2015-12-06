package ch.fhnw.comgr.fractal.fractals.mandel;

import ch.fhnw.comgr.fractal.IUpdateListener;
import ch.fhnw.comgr.fractal.fractals.FractalGenerator;
import ch.fhnw.comgr.fractal.fractals.IFractal;
import ch.fhnw.comgr.fractal.util.Points;
import ch.fhnw.ether.controller.DefaultController;
import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.controller.event.DefaultEventScheduler;
import ch.fhnw.ether.controller.event.IEventScheduler;
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
public class MandelBulb implements IFractal, IEventScheduler.IAnimationAction{

    DefaultController controller = new DefaultController(30);
    Points points = new Points();
    IGeometry geometry;
    IMaterial mat;
    IMesh mesh;
    IScene scene;
    float[] color = null;
    float[] pointSize = null;

    public MandelBulb(IScene _scene) {
        scene = _scene;
    }

    @Override
    public void init() {
        new Thread(new FractalGenerator(points)).start();

        float[] vert = new float[]{1, 1, 1};
//        color = new float[]{0.8f, 0.8f, 0.8f, 0.8f};
//        pointSize = new float[]{2};
//        geometry = createVCP(IGeometry.Primitive.POINTS, vert, color, pointSize);
//        mat = new MandelBulbMaterial();
//        mesh = new DefaultMesh(mat, geometry);
//        scene.add3DObject(mesh);
        geometry = DefaultGeometry.createVN(IGeometry.Primitive.POINTS, vert ,new float[vert.length] );
        mat = new PointMaterial(RGBA.GRAY, 3f);
        mesh = new DefaultMesh(mat, geometry);
        scene.add3DObject(mesh);

        controller.animate(this);
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


    public static DefaultGeometry createVCP(IGeometry.Primitive type, float[] vertices, float[] colors, float[] pointSize) {
        IGeometry.IGeometryAttribute[] attributes = { IGeometry.POSITION_ARRAY, IGeometry.COLOR_ARRAY, IGeometry.POINT_SIZE_ARRAY };
        float[][] data = { vertices, colors, pointSize };
        return new DefaultGeometry(type, attributes, data);
    }

    @Override
    public void run(double v, double v1) {
        mesh.getGeometry().modify((IGeometry.IGeometryAttribute[] id, float[][] data) -> {
            if(!points.isEmpty()) {
                float[] vert = points.getAllPoints();
                data[0] = vert;
                data[1] = new float[vert.length];
            }
        });
    }
}
