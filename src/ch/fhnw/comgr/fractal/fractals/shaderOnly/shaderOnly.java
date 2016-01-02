package ch.fhnw.comgr.fractal.fractals.shaderOnly;

import ch.fhnw.comgr.fractal.IUpdateListener;
import ch.fhnw.comgr.fractal.fractals.FractalGenerator;
import ch.fhnw.comgr.fractal.fractals.IFractal;
import ch.fhnw.comgr.fractal.fractals.mandel.MandelBulbMaterial;
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
import ch.fhnw.ether.scene.mesh.MeshUtilities;
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
import ch.fhnw.util.math.geometry.GeometryUtilities;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TODO this needs a nice description.
 */
public class ShaderOnly implements IFractal {

    ShaderOnlyMaterial mat;
    IMesh mesh;
    IScene scene;
    private List<IWidget> widgets;
    private SmallSlider distanceSlider;

    public ShaderOnly(IScene _scene) {
        scene = _scene;
    }

    @Override
    public void init() {
        mat = new ShaderOnlyMaterial();
        mesh = MeshUtilities.createCube(mat);
        mesh.setTransform(Mat4.scale(2));
        scene.add3DObject(mesh);

        widgets = new ArrayList<>();
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
}
