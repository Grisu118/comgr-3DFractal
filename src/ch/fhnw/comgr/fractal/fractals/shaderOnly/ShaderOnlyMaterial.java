package ch.fhnw.comgr.fractal.fractals.shaderOnly;

import ch.fhnw.ether.render.shader.IShader;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry;
import ch.fhnw.ether.scene.mesh.material.AbstractMaterial;
import ch.fhnw.ether.scene.mesh.material.ICustomMaterial;
import ch.fhnw.util.color.RGBA;
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

    public ShaderOnlyMaterial() {
        super(material(new MaterialAttribute<Float>("mandelbulbO.scale"), new MaterialAttribute<Float>("mandelbulbO.power")), geometry(IGeometry.POSITION_ARRAY));
        this.shader = new ShaderOnlyShader(getClass(), Arrays.asList(getProvidedAttributes()));
    }

    public void setScale(float scale) {
        this.scale = scale;
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
        return data(scale, power);
    }
}
