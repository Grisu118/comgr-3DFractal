package ch.fhnw.comgr.fractal.fractals.shaderOnly;

import ch.fhnw.comgr.fractal.IUpdateListener;
import ch.fhnw.comgr.fractal.fractals.IFractal;
import ch.fhnw.comgr.fractal.ui.BooleanWidget;
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
import ch.fhnw.util.Viewport;
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

    private Vec3[][] colors = {
            new Vec3[]{
                    new Vec3(0.4, 0.82, 0.91),
                    new Vec3(0, 0.31, 1),
                    new Vec3(0.15, 0.12, 0.49),
                    new Vec3(0.44, 0.12, 0.76),
                    new Vec3(0.05, 0.06, 0.18)
            },
            new Vec3[]{new Vec3(0.9, 0, 0),
                    new Vec3(1, 0.67, 0.25),
                    new Vec3(0.68, 0.99, 0.24),
                    new Vec3(0.99, 1, 0.19),
                    new Vec3(1, 0.42, 0.0235)
            },
            new Vec3[]{
                    new Vec3(0.94, 1, 0),
                    new Vec3(0.53, 1, 0),
                    new Vec3(0.18, 1, 0),
                    new Vec3(0, 1, 0.37),
                    new Vec3(0, 1, 0.82)
            },
            new Vec3[]{
                    new Vec3(0.35, 0.02, 0.25),
                    new Vec3(0.67, 0, 0.41),
                    new Vec3(0.67, 0, 0.07),
                    new Vec3(1, 0.42, 0.01),
                    new Vec3(1, 0.76, 0.02)
            }
    };

    public static final float[] CUBE_TRIANGLES = {
            -0.5f, 0, -0.5f, +0.5f, 0, -0.5f, -0.5f, 0, +0.5f,
            +0.5f, 0, -0.5f, -0.5f, 0, +0.5f, +0.5f, 0, +0.5f,
    };

    public static final float[] CUBE_TEXTURES = {
            0, 0, 1, 0, 0, 1,
            1, 0, 0, 1, 1, 1
    };
    private SmallSlider order;

    public ShaderOnly(IScene _scene) {
        scene = _scene;
    }

    @Override
    public void init() {
        mat = new ShaderOnlyMaterial();
        mesh = new DefaultMesh(mat, DefaultGeometry.createVNM(IGeometry.Primitive.TRIANGLES, CUBE_TRIANGLES, GeometryUtilities.calculateNormals(CUBE_TRIANGLES), CUBE_TEXTURES));
        scene.add3DObject(mesh);

        widgets = new ArrayList<>();

        order = new SmallSlider(0, 5, "Order", null, 1f / 25f * 7, (w, v) -> mat.setPower(w.getValue(1, 25)));
        order.setRange(1, 25);
        widgets.add(order);
        SmallSlider color = new SmallSlider(0, 4, "Color", null, 0, (w, v) -> mat.setColor(colors[w.getValue(0, 3)]));
        color.setRange(0, 3);
        BooleanWidget iter = new BooleanWidget(0,3, "Smooth", "On", "Off", "Smooth", false, (w, v) -> mat.setIterations(w.getValue() ? 3 : 4));

        widgets.add(color);
        widgets.add(iter);
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
        return new Camera(new Vec3(0, -0.5f, 0), Vec3.ZERO);
    }

    public void reshape(Viewport viewport) {
        //mat.setSize(new Vec3(viewport.w, viewport.h, 0));
        mat.setOutputSize(new Vec3(viewport.w, viewport.h, 0));
        System.out.println("W: " + viewport.w + ", H: " + viewport.h);
    }
}
