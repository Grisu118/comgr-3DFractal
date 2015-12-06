package ch.fhnw.comgr.fractal.fractals;

import ch.fhnw.comgr.fractal.util.Points;

/**
 * Created by joel on 06/12/2015.
 */
public class FractalGenerator implements Runnable {

    Points points;

    float boxRadius = 1.5f;
    float pointsPerAxis = 200f;
    float order = 6f;
    float maxIterations = 8f;

    public FractalGenerator(Points _points) {
        points = _points;
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

        float xsquare, ysquare, zsquare, bail = 10;
        float x, y, z, r, theta, phi, powr, sinThetaOrder;
        float tempx, tempy, tempz;
        int iteration;
        boolean lastIn = false, firstPoint;

        for (float z0 = minZ; z0 <= maxZ; z0 += incrZ) {
            for (float x0 = minX; x0 <= maxX; x0 += incrX) {
                firstPoint = true;
                for (float y0 = minY; y0 <= maxY; y0 += incrY) {
                    x = 0; y = 0; z = 0;
                    iteration = 0;

                    while (true)
                    {
                        xsquare = x * x;
                        ysquare = y * y;
                        zsquare = z * z;

                        tempx = x; tempy = y; tempz = z;


                        r = (float)Math.sqrt(xsquare + ysquare + zsquare);

                        theta = (float)Math.atan2(Math.sqrt(xsquare + ysquare), z);
                        phi = (float)Math.atan2(y, x);
                        powr = (float)Math.pow(r, order);

                        sinThetaOrder = (float)Math.sin(theta * order);
                        x = (float)(powr * sinThetaOrder * Math.cos(phi * order));
                        y = (float)(powr * sinThetaOrder * Math.sin(phi * order));
                        z = (float)(powr * Math.cos(theta * order));


                        x += x0; y += y0; z += z0;


                        iteration++;

                        if (iteration > maxIterations)
                        {
                            //inside the set!
                            if (!lastIn)
                            {
                                if (!firstPoint) points.addPoint(x0, y0, z0);
                                lastIn = true;
                            }
                            firstPoint = false;
                            break;
                        }
                        if ((xsquare + ysquare + zsquare) > bail)
                        {
                            if (lastIn)
                            {
                                if (!firstPoint) points.addPoint(x0, y0, z0);
                                lastIn = false;
                            }
                            firstPoint = false;
                            break;
                        }
                    }
                }
            }
        }

    }
}
