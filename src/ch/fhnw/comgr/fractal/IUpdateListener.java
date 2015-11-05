package ch.fhnw.comgr.fractal;

import ch.fhnw.comgr.fractal.fractals.IFractal;

/**
 * Created by benjamin on 05.11.2015.
 */
public interface IUpdateListener {
    void notifyUpdate(IFractal frac);
}
