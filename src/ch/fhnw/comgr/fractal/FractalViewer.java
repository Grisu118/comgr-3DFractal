package ch.fhnw.comgr.fractal;

import ch.fhnw.comgr.fractal.fractals.FractalType;
import ch.fhnw.comgr.fractal.fractals.IFractal;
import ch.fhnw.comgr.fractal.fractals.mandel.MandelBulb;
import ch.fhnw.comgr.fractal.fractals.shaderOnly.ShaderOnly;
import ch.fhnw.comgr.fractal.fractals.simpleTree.SimpleTree2;
import ch.fhnw.comgr.fractal.util.UpdateType;
import ch.fhnw.ether.controller.FractalController;
import ch.fhnw.ether.scene.DefaultScene;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.ether.scene.camera.Camera;
import ch.fhnw.ether.scene.camera.ICamera;
import ch.fhnw.ether.ui.Button;
import ch.fhnw.ether.view.IView;
import ch.fhnw.ether.view.gl.FractalView;
import ch.fhnw.util.Viewport;
import ch.fhnw.util.math.Vec3;

import java.awt.event.KeyEvent;
import java.util.Objects;

/**
 * Main Class for the Fractal Viewer.
 * @author Benjamin Leber
 */
public final class FractalViewer implements IUpdateListener {

    private IFractal activeFractal;
    private final FractalController controller;
    private IScene scene;

    public FractalViewer() {
        this(FractalType.MANDELBULB);
    }


    public FractalViewer(FractalType type) {
        controller = new FractalController();

        controller.run((time) -> {
            // Create view
            FractalView view = new FractalView(controller, 100, 100, 500, 500, new IView.Config(IView.ViewType.INTERACTIVE_VIEW, 0, IView.ViewFlag.SMOOTH_LINES), "3D Fractal Viewer");
            // Create scene
            scene = new DefaultScene(controller);
            controller.setScene(scene);
            switch (type) {
                case SIMPLE_TREE:
                    activeFractal = new SimpleTree2(0.5f, 0.05f,0.05f, 30, scene);
                    break;
                case MANDELBULB:
                    activeFractal = new MandelBulb(scene, view.getViewport());
                    break;
                case SHADER:
                    activeFractal = new ShaderOnly(scene);
                    view.setViewer(this);
                    controller.setNavigationToolActivate(false);
                    break;
                default:
                    activeFractal = new MandelBulb(scene, view.getViewport());
            }

            // Create and add camera
            ICamera camera = activeFractal.getCamera() == null ? new Camera(new Vec3(0, -2, 2), Vec3.ZERO) : activeFractal.getCamera();
            scene.add3DObject(camera);
            controller.setCamera(view, camera);



            activeFractal.init();
            activeFractal.registerUpdateListener(this);
            if (activeFractal.getTool() != null) {
                controller.setCurrentTool(activeFractal.getTool());
            }

            scene.add3DObject(activeFractal.getLight());

            // Add an exit button
            controller.getUI().addWidget(new Button(0, 0, "Quit", "Quit", KeyEvent.VK_ESCAPE, (button, v) -> System.exit(0)));
            controller.getUI().addWidgets(activeFractal.getWidgets());
        });


    }

    @Override
    public void notifyUpdate(IFractal source, UpdateType t) {
        System.out.println("FractalViewer - notifyUpdate: " + source + ", " + t);
        if (Objects.equals(activeFractal, source)) {
            switch (t) {
                case LIGHT:
                    if (activeFractal.getLightState()) {
                        scene.add3DObject(activeFractal.getLight());
                    } else {
                        scene.remove3DObject(activeFractal.getLight());
                    }
                    break;
            }
        }
    }

    public void reshape(Viewport viewport) {
        if (activeFractal instanceof ShaderOnly) {
            ShaderOnly f = (ShaderOnly) activeFractal;
            f.reshape(viewport);
        }
    }
}
