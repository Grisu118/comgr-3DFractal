package ch.fhnw.comgr.fractal.fractals;

import ch.fhnw.ether.ui.IWidget;

import java.util.List;

/**
 * Created by benjamin on 29.10.2015.
 */
public interface IFractal {

    void init();

    List<IWidget> getWidgets();

    void cleanup();

}
