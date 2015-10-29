package ch.fhnw.comgr.fractal;

import ch.fhnw.comgr.fractal.fractals.IFractal;
import ch.fhnw.comgr.fractal.fractals.simpleTree.SimpleTree;
import ch.fhnw.ether.controller.DefaultController;
import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.scene.DefaultScene;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.ether.scene.camera.Camera;
import ch.fhnw.ether.scene.camera.ICamera;
import ch.fhnw.ether.scene.mesh.MeshLibrary;
import ch.fhnw.ether.ui.Button;
import ch.fhnw.ether.view.IView;
import ch.fhnw.ether.view.gl.DefaultView;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by benjamin on 29.10.2015.
 */
public final class FractalViewer {

    private List<IFractal> fractals = new ArrayList<>();

    public FractalViewer() {
        IController controller = new DefaultController();
        controller.run((time) -> {
            // Create view
            IView view = new DefaultView(controller, 100, 100, 500, 500, IView.INTERACTIVE_VIEW, "Simple Cube");

            // Create scene
            IScene scene = new DefaultScene(controller);
            controller.setScene(scene);

            // Create and add camera
            ICamera camera = new Camera(new Vec3(0, -5, 5), Vec3.ZERO);
            //scene.add3DObject(camera);
            controller.setCamera(view, camera);

            IFractal fractal = new SimpleTree(1,0.05f,0.05f,30, scene);

            // Add an exit button
            controller.getUI().addWidget(new Button(0, 0, "Quit", "Quit", KeyEvent.VK_ESCAPE, (button, v) -> System.exit(0)));
        });



    }

}
