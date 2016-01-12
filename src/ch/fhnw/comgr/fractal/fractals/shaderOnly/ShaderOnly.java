package ch.fhnw.comgr.fractal.fractals.shaderOnly;

import ch.fhnw.comgr.fractal.IUpdateListener;
import ch.fhnw.comgr.fractal.fractals.IFractal;
import ch.fhnw.comgr.fractal.ui.SmallSlider;
import ch.fhnw.ether.controller.event.IKeyEvent;
import ch.fhnw.ether.controller.event.IPointerEvent;
import ch.fhnw.ether.controller.tool.ITool;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.ether.scene.camera.Camera;
import ch.fhnw.ether.scene.camera.ICamera;
import ch.fhnw.ether.scene.light.ILight;
import ch.fhnw.ether.scene.mesh.DefaultMesh;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.geometry.DefaultGeometry;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry;
import ch.fhnw.ether.ui.IWidget;
import ch.fhnw.ether.view.IView;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.GeometryUtilities;

import java.util.ArrayList;
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

    public static final float[] CUBE_TRIANGLES = {
            // bottom
            -500, -500, -500, -500, +500, -500, +500, +500, -500,
            -500, -500, -500, +500, +500, -500, +500, -500, -500,

            // top
            +500, -500, +500, +500, +500, +500, -500, +500, +500,
            +500, -500, +500, -500, +500, +500, -500, -500, +500,

            // front
            -500, -500, -500, +500, -500, -500, +500, -500, +500,
            -500, -500, -500, +500, -500, +500, -500, -500, +500,

            // back
            +500, +500, -500, -500, +500, -500, -500, +500, +500,
            +500, +500, -500, -500, +500, +500, +500, +500, +500,

            // left
            -500, +500, -500, -500, -500, -500, -500, -500, +500,
            -500, +500, -500, -500, -500, +500, -500, +500, +500,

            // right
            +500, -500, -500, +500, +500, -500, +500, +500, +500,
            +500, -500, -500, +500, +500, +500, +500, -500, +500
    };

    public ShaderOnly(IScene _scene) {
        scene = _scene;
    }

    @Override
    public void init() {
        mat = new ShaderOnlyMaterial();
        mesh = new DefaultMesh(mat, DefaultGeometry.createVN(IGeometry.Primitive.TRIANGLES, CUBE_TRIANGLES, GeometryUtilities.calculateNormals(CUBE_TRIANGLES)));
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

    @Override
    public ITool getTool() {
        return new ITool() {
            @Override
            public void activate() {

            }

            @Override
            public void deactivate() {

            }

            @Override
            public void refresh(IView view) {

            }

            @Override
            public void keyPressed(IKeyEvent e) {
                double delta = 0.1;
                switch (e.getKeyCode()) {
                    case IKeyEvent.VK_LEFT:
                        mat.moveCamera(-delta, 0, 0);
                        break;
                    case IKeyEvent.VK_RIGHT:
                        mat.moveCamera(delta, 0, 0);
                        break;
                    case IKeyEvent.VK_UP:
                        mat.moveCamera(0, -delta, 0);
                        break;
                    case IKeyEvent.VK_DOWN:
                        mat.moveCamera(0, delta, 0);
                        break;
                    case IKeyEvent.VK_PAGE_UP:
                        mat.moveCamera(0, 0, delta);
                        break;
                    case IKeyEvent.VK_PAGE_DOWN:
                        mat.moveCamera(0, 0, -delta);
                        break;
                    case IKeyEvent.VK_E :
                        mat.setCameraRoll((float) (mat.getCameraRoll() + delta*10));
                        break;
                    case IKeyEvent.VK_Q :
                        mat.setCameraRoll((float) (mat.getCameraRoll() - delta*10));
                        break;
                }
            }

            @Override
            public void pointerPressed(IPointerEvent e) {

            }

            @Override
            public void pointerReleased(IPointerEvent e) {

            }

            @Override
            public void pointerMoved(IPointerEvent e) {

            }

            @Override
            public void pointerDragged(IPointerEvent e) {

            }

            @Override
            public void pointerScrolled(IPointerEvent e) {

            }
        };
    }

    @Override
    public ICamera getCamera() {
        return new Camera(new Vec3(0, -1500, 0), Vec3.ZERO);
    }
}
