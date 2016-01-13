package ch.fhnw.comgr.fractal.fractals.mandel;

import ch.fhnw.comgr.fractal.IUpdateListener;
import ch.fhnw.comgr.fractal.fractals.FractalGenerator;
import ch.fhnw.comgr.fractal.fractals.IFractal;
import ch.fhnw.comgr.fractal.ui.BooleanWidget;
import ch.fhnw.comgr.fractal.ui.SmallButton;
import ch.fhnw.comgr.fractal.ui.SmallSlider;
import ch.fhnw.comgr.fractal.util.Points;
import ch.fhnw.ether.controller.DefaultController;
import ch.fhnw.ether.controller.event.IEventScheduler;
import ch.fhnw.ether.controller.event.IKeyEvent;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.ether.scene.light.ILight;
import ch.fhnw.ether.scene.mesh.DefaultMesh;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.geometry.DefaultGeometry;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry;
import ch.fhnw.ether.ui.IWidget;
import ch.fhnw.util.Viewport;
import ch.fhnw.util.color.RGBA;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO this needs a nice description.
 */
public class MandelBulb implements IFractal, IEventScheduler.IAnimationAction{

    List<FractalGenerator> generators;
    DefaultController controller = new DefaultController(30);
    Points points = new Points();
    IGeometry geometry;
    MandelBulbMaterial mat;
    IMesh mesh;
    IScene scene;
    Viewport viewPort;
    private List<IWidget> widgets;
    private SmallSlider distanceSlider;
    private SmallSlider iterations;
    private SmallSlider order;

    public MandelBulb(IScene _scene, Viewport viewPort) {
        scene = _scene;
        this.viewPort = viewPort;
    }

    @Override
    public void init() {
        generators = new ArrayList<>();
        geometry = DefaultGeometry.createV(IGeometry.Primitive.POINTS, new float[0]);
        mat = new MandelBulbMaterial(RGBA.GRAY, 4f);
        mesh = new DefaultMesh(mat, geometry);
        scene.add3DObject(mesh);

        controller.animate(this);

        widgets = new ArrayList<>();
        widgets.add(new BooleanWidget(0, 3, "Color", "On", "Off", "Turn on/off Color", false, (w, v) -> {mat.setColorized(w.getValue()); distanceSlider.setActivated(w.getValue());}));
        distanceSlider = new SmallSlider(0,2,"max Distance", null, mat.getMaxDistance(), (w, v) -> mat.setMaxDistance(w.getValue(0.01f, 1f)));
        distanceSlider.setActivated(false);
        widgets.add(distanceSlider);
        iterations = new SmallSlider(0,6,"Iterations", null, 1f/15f*7, (w, v) -> {});
        iterations.setRange(3, 20);
        order  = new SmallSlider(0,5,"Order", null, 1f/15f*5, (w, v) -> {});
        order.setRange(2, 20);
        widgets.add(iterations);
        widgets.add(order);
        SmallButton generate = new SmallButton(0,4,"Generate", "Generates the new Fractal", IKeyEvent.VK_G, (b, v) -> {
            generate();
        });
        widgets.add(generate);
    }

    private void generate() {
        if (!generators.isEmpty()) {
            generators.forEach(FractalGenerator::interrupt);
        }
        if (points != null) {
            points = new Points();
        }
        generators.clear();
        for (int i = 0; i < 6; i++) {
            generators.add(new FractalGenerator(points, i));
        }

        for (FractalGenerator generator : generators) {
            generator.setMaxIterations(iterations.getValue(3, 15));
            generator.setOrder(order.getValue(2, 15));
            generator.start();
        }
    }

    @Override
    public List<IWidget> getWidgets() {
        return widgets;
    }

    @Override
    public void cleanup() {
        scene.remove3DObject(mesh);
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
