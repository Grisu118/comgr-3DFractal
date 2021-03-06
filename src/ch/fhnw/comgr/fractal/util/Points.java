package ch.fhnw.comgr.fractal.util;

import java.util.ArrayList;

/**
 * Created by joel on 06/12/2015.
 */
public class Points {

    ArrayList<Float> points = new ArrayList<>();
    ArrayList<Float> size = new ArrayList<>();

    public Points(){}

    public synchronized void addPoint(float x, float y, float z) {
        points.add(x);
        points.add(y);
        points.add(z);
        size.add(2f);
    }

    public synchronized float[] getAllPoints() {
        float[] ret = new float[points.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = points.get(i);
        }
        return ret;
    }

    public synchronized float[] getAllSizes() {
        float[] ret = new float[size.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = size.get(i);
        }
        return ret;
    }

    public boolean isEmpty() {
        return points.isEmpty();
    }

    public void clear() {
        points.clear();
        size.clear();
    }

}
