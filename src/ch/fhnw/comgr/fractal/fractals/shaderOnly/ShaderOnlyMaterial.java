package ch.fhnw.comgr.fractal.fractals.shaderOnly;

import ch.fhnw.ether.render.shader.IShader;
import ch.fhnw.ether.render.variable.base.FloatArray;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry;
import ch.fhnw.ether.scene.mesh.material.AbstractMaterial;
import ch.fhnw.ether.scene.mesh.material.ICustomMaterial;
import ch.fhnw.util.math.IVec3;
import ch.fhnw.util.math.Vec3;

import java.util.Arrays;

/**
 * Created by benjamin on 03.12.2015.
 */
public class ShaderOnlyMaterial extends AbstractMaterial implements ICustomMaterial {

    private final IShader shader;

    private float power = 8;
    private float time = 12f;
    /* for mandelbulb0 Shader
    private float scale = 2;
    private float surfaceDetail = 0.6f;
    private float surfaceSmoothness = 0.8f;
    private float boundingRadius = 5;
    private Vec3 offset = Vec3.ZERO;
    private Vec3  shift = Vec3.ZERO;;
    private Vec3 size = new Vec3(1, 1, 0);
     */
    private Vec3 outputSize = new Vec3(500, 500, 0);

    private Vec3 cameraPosition = new Vec3(0, 0, 7.5);

    private Vec3[] colors = {
            new Vec3(0.4, 0.82, 0.91),
            new Vec3(0, 0.31, 1),
            new Vec3(0.15,0.12,0.49),
            new Vec3(0.44,0.12,0.76),
            new Vec3(0.05,0.06,0.18)

    };

    private int iterations = 6;

    private float phi = (float) (Math.PI / 2), theta = 0, psi = 0;

    public ShaderOnlyMaterial() {
        super(material(new MaterialAttribute<Float>("mandelbulbO.power"), new MaterialAttribute<IVec3>("mandelbulbO.cameraPosition"), new MaterialAttribute<IVec3>("mandelbulbO.outputSize"), new MaterialAttribute<IVec3>("mandelbulbO.color1"), new MaterialAttribute<IVec3>("mandelbulbO.color2"), new MaterialAttribute<IVec3>("mandelbulbO.color3"), new MaterialAttribute<IVec3>("mandelbulbO.color4"), new MaterialAttribute<IVec3>("mandelbulbO.color5"), new MaterialAttribute<Integer>("mandelbulbO.iterations"), new MaterialAttribute<Float>("mandelbulbO.phi"), new MaterialAttribute<Float>("mandelbulbO.theta"), new MaterialAttribute<Float>("mandelbulbO.psi"), new MaterialAttribute<Float>("mandelbulbO.time")), geometry(IGeometry.POSITION_ARRAY, IGeometry.NORMAL_ARRAY, IGeometry.COLOR_MAP_ARRAY));
        this.shader = new ShaderOnlyShader(getClass(), Arrays.asList(getProvidedAttributes()));
    }

    public void moveCamera(double dx, double dy, double dz) {
        cameraPosition = new Vec3(cameraPosition.x + dx, cameraPosition.y + dy, cameraPosition.z + dz);
        System.out.println(cameraPosition);
        updateRequest();
    }

    public void setPower(float power) {
        this.power = power;
        updateRequest();
    }

    public void setOutputSize(Vec3 outputSize) {
        this.outputSize = outputSize;
        updateRequest();
    }

    @Override
    public IShader getShader() {
        return shader;
    }

    @Override
    public IGeometry.Primitive getType() {
        return IGeometry.Primitive.TRIANGLES;
    }

    @Override
    public Object[] getData() {
        return data(power, cameraPosition, outputSize, colors[0], colors[1], colors[2], colors[3], colors[4], iterations, phi, theta, psi, time);
    }

    public void setColor(Vec3[] color) {
        this.colors = color;
        updateRequest();
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
        updateRequest();
    }

    public void updateAngle(float dphi, float dtheta, float dpsi) {
        this.phi += dphi;
        this.theta += dtheta;
        this.psi += dpsi;
        updateRequest();
    }
}
