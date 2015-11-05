package ch.fhnw.comgr.fractal.fractals;

import ch.fhnw.comgr.fractal.IUpdateListener;
import ch.fhnw.ether.ui.IWidget;

import java.util.List;

/**
 * Created by benjamin on 29.10.2015.
 */
public interface IFractal {

    /**
     * Method for init the fractal data, add to scene.
     */
    void init();

    /**
     * List with all widgets which are needed for the fractal.
     * @return A list containing all needed widgets.
     */
    List<IWidget> getWidgets();

    /**
     * Cleanup Memory etc.
     */
    void cleanup();

    int getVerticesCount();

    int getTrianglesCount();

    void registerUpdateListener(IUpdateListener listener);
    void removeUpdateListener(IUpdateListener listener);

}
