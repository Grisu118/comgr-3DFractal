package ch.fhnw.comgr.fractal.fractals.shaderOnly;

import ch.fhnw.ether.render.shader.IShader;
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
    private float scale = 2;
    private float power = 8;
    private float surfaceDetail = 0.6f;
    private float surfaceSmoothness = 0.8f;
    private float boundingRadius = 5;
    private Vec3 offset = Vec3.ZERO;
    private Vec3  shift = Vec3.ZERO;;
    private Vec3 size = new Vec3(1, 1, 0);
    private Vec3 outputSize = new Vec3(500,500,0);

    private Vec3 cameraPosition = new Vec3(0,0,7.5);
    private float cameraRoll = 0;

    public ShaderOnlyMaterial() {
        super(material(new MaterialAttribute<Float>("mandelbulbO.scale"), new MaterialAttribute<Float>("mandelbulbO.power"), new MaterialAttribute<IVec3>("mandelbulbO.cameraPosition"), new MaterialAttribute<Float>("mandelbulbO.cameraRoll"), new MaterialAttribute<IVec3>("mandelbulbO.size"), new MaterialAttribute<IVec3>("mandelbulbO.outputSize")), geometry(IGeometry.POSITION_ARRAY, IGeometry.NORMAL_ARRAY, IGeometry.COLOR_MAP_ARRAY));
        this.shader = new ShaderOnlyShader(getClass(), Arrays.asList(getProvidedAttributes()));
    }

    public void setScale(float scale) {
        this.scale = scale;
        updateRequest();
    }

    public void moveCamera(double dx, double dy, double dz) {
        cameraPosition = new Vec3(cameraPosition.x + dx, cameraPosition.y + dy, cameraPosition.z + dz);
        System.out.println(cameraPosition);
        updateRequest();
    }

    public void setCameraRoll(float cameraRoll) {
        this.cameraRoll = cameraRoll;
        System.out.println(cameraRoll);
        updateRequest();
    }

    public void setOutputSize(Vec3 outputSize) {
        this.outputSize = outputSize;
        updateRequest();
    }

    public void setSize(Vec3 size) {
        this.size = size;
        updateRequest();
    }

    public float getCameraRoll() {
        return cameraRoll;
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
        return data(scale, power, cameraPosition, cameraRoll, size, outputSize);
    }
}
