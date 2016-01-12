package ch.fhnw.comgr.fractal.fractals;

import ch.fhnw.comgr.fractal.util.Points;

/**
 * Created by joel on 06/12/2015.
 */
public class FractalGenerator extends Thread {

    Points points;

    float boxRadius = 1.5f;
    float pointsPerAxis = 200f;
    float order = 6f;
    float maxIterations = 8f;
    int way;
    private boolean firstPoint;
    private boolean lastIn;

    public FractalGenerator(Points _points, int way) {
        points = _points;
        this.way = way;
    }

    @Override
    public void run() {
        generate();
    }

    private void generate() {
        float minX = -boxRadius, maxX = boxRadius;
        float minY = -boxRadius, maxY = boxRadius;
        float minZ = -boxRadius, maxZ = boxRadius;

        float pointIncrement = (boxRadius * 2.0f / pointsPerAxis);
        float incrX = pointIncrement, incrY = pointIncrement, incrZ = pointIncrement;

        lastIn = false;

        switch (way) {
            case 0:
                for (float z0 = minZ; z0 <= 0; z0 += incrZ) {
                    for (float x0 = minX; x0 <= maxX; x0 += incrX) {
                        firstPoint = true;
                        for (float y0 = minY; y0 <= maxY; y0 += incrY) {
                            if (Thread.interrupted()) {
                                return;
                            }
                            calc(x0, y0, z0);
                        }
                    }
                }
                break;
            case 1:
                for (float y0 = minY - pointIncrement * 0.66f; y0 <= 0; y0 += incrY) {
                    for (float z0 = minZ - pointIncrement * 0.66f; z0 <= maxZ; z0 += incrZ) {
                        firstPoint = true;
                        for (float x0 = minX - pointIncrement * 0.66f; x0 <= maxX; x0 += incrX) {
                            if (Thread.interrupted()) {
                                return;
                            }
                            calc(x0, y0, z0);
                        }
                    }
                }
                break;
            case 2:
                for (float x0 = minX - pointIncrement * 0.33f; x0 <= 0; x0 += incrX) {
                    for (float y0 = minY - pointIncrement * 0.33f; y0 <= maxY; y0 += incrY) {
                        firstPoint = true;
                        for (float z0 = minZ - pointIncrement * 0.33f; z0 <= maxZ; z0 += incrZ) {
                            if (Thread.interrupted()) {
                                return;
                            }
                            calc(x0, y0, z0);
                        }
                    }
                }
                break;
            case 3:
                for (float z0 = maxZ; z0 >= 0; z0 -= incrZ) {
                    for (float x0 = minX; x0 <= maxX; x0 += incrX) {
                        firstPoint = true;
                        for (float y0 = minY; y0 <= maxY; y0 += incrY) {
                            if (Thread.interrupted()) {
                                return;
                            }
                            calc(x0, y0, z0);
                        }
                    }
                }
                break;
            case 4:
                for (float y0 = maxY + pointIncrement * 0.66f; y0 >= 0; y0 -= incrY) {
                    for (float z0 = minZ - pointIncrement * 0.66f; z0 <= maxZ; z0 += incrZ) {
                        firstPoint = true;
                        for (float x0 = minX - pointIncrement * 0.66f; x0 <= maxX; x0 += incrX) {
                            if (Thread.interrupted()) {
                                return;
                            }
                            calc(x0, y0, z0);
                        }
                    }
                }
                break;
            case 5:
                for (float x0 = maxX + pointIncrement * 0.33f; x0 >= 0; x0 -= incrX) {
                    for (float y0 = minY - pointIncrement * 0.33f; y0 <= maxY; y0 += incrY) {
                        firstPoint = true;
                        for (float z0 = minZ - pointIncrement * 0.33f; z0 <= maxZ; z0 += incrZ) {
                            if (Thread.interrupted()) {
                                return;
                            }
                            calc(x0, y0, z0);

                        }
                    }
                }
                break;
        }
    }

    private void calc(float x0, float y0, float z0) {
        float xsquare, ysquare, zsquare, bail = 10;
        float x, y, z, r, theta, phi, powr, sinThetaOrder;
        int iteration;

        x = 0;
        y = 0;
        z = 0;
        iteration = 0;

        while (true) {
            xsquare = x * x;
            ysquare = y * y;
            zsquare = z * z;

            r = (float) Math.sqrt(xsquare + ysquare + zsquare);

            theta = (float) Math.atan2(Math.sqrt(xsquare + ysquare), z);
            phi = (float) Math.atan2(y, x);
            powr = (float) Math.pow(r, order);

            sinThetaOrder = (float) Math.sin(theta * order);
            x = (float) (powr * sinThetaOrder * Math.cos(phi * order));
            y = (float) (powr * sinThetaOrder * Math.sin(phi * order));
            z = (float) (powr * Math.cos(theta * order));

            x += x0;
            y += y0;
            z += z0;

            iteration++;

            if (iteration > maxIterations) {
                //inside the set!
                if (!lastIn) {
                    if (!firstPoint) points.addPoint(x0, y0, z0);
                    lastIn = true;
                }
                firstPoint = false;
                break;
            }
            if ((xsquare + ysquare + zsquare) > bail) {
                if (lastIn) {
                    if (!firstPoint) points.addPoint(x0, y0, z0);
                    lastIn = false;
                }
                firstPoint = false;
                break;
            }
            if (Thread.interrupted()) {
                return;
            }
        }
    }

    public float getOrder() {
        return order;
    }

    public void setOrder(float order) {
        this.order = order;
    }

    public float getMaxIterations() {
        return maxIterations;
    }

    public void setMaxIterations(float maxIterations) {
        this.maxIterations = maxIterations;
    }
}
