package ch.fhnw.comgr.fractal;

import ch.fhnw.comgr.fractal.fractals.IFractal;
import ch.fhnw.comgr.fractal.util.UpdateType;

/**
 * Created by benjamin on 05.11.2015.
 */
public interface IUpdateListener {
    void notifyUpdate(IFractal source, UpdateType t);
}
