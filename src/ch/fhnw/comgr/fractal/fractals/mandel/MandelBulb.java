package ch.fhnw.comgr.fractal.fractals.mandel;

import ch.fhnw.comgr.fractal.IUpdateListener;
import ch.fhnw.comgr.fractal.fractals.FractalGenerator;
import ch.fhnw.comgr.fractal.fractals.IFractal;
import ch.fhnw.comgr.fractal.ui.BooleanWidget;
import ch.fhnw.comgr.fractal.ui.SmallSlider;
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

    FractalGenerator generator;
    DefaultController controller = new DefaultController(30);
    Points points = new Points();
    IGeometry geometry;
    MandelBulbMaterial mat;
    IMesh mesh;
    IScene scene;
    private List<IWidget> widgets;
    private SmallSlider distanceSlider;

    public MandelBulb(IScene _scene) {
        scene = _scene;
    }

    @Override
    public void init() {
        generator = new FractalGenerator(points);
        generator.start();
        geometry = DefaultGeometry.createV(IGeometry.Primitive.POINTS, new float[0]);
        mat = new MandelBulbMaterial(RGBA.GRAY, 4f);
        mesh = new DefaultMesh(mat, geometry);
        scene.add3DObject(mesh);

        controller.animate(this);

        widgets = new ArrayList<>();
        widgets.add(new BooleanWidget(0, 5, "Color", "On", "Off", "Turn on/off Color", false, (w, v) -> {mat.setColorized(w.getValue()); distanceSlider.setActivated(w.getValue());}));
        distanceSlider = new SmallSlider(0,4,"max Distance", null, mat.getMaxDistance(), (w, v) -> mat.setMaxDistance(w.getValue(0.01f, 1f)));
        distanceSlider.setActivated(false);
        widgets.add(distanceSlider);
    }

    @Override
    public List<IWidget> getWidgets() {
        return widgets;
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
            }
        });
    }
}
