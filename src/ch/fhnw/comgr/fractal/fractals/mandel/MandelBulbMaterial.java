package ch.fhnw.comgr.fractal.fractals.mandel;

import ch.fhnw.ether.render.shader.IShader;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry;
import ch.fhnw.ether.scene.mesh.material.AbstractMaterial;
import ch.fhnw.ether.scene.mesh.material.ICustomMaterial;
import ch.fhnw.ether.scene.mesh.material.IMaterial;
import ch.fhnw.util.color.RGBA;

import java.util.Arrays;

/**
 * Created by benjamin on 03.12.2015.
 */
public class MandelBulbMaterial extends AbstractMaterial implements ICustomMaterial {

    private final IShader shader;
    private float size;
    private RGBA color;
    private boolean isColorized = false;
    private float maxDistance = 0.4f;

    public MandelBulbMaterial() {
        this(RGBA.GRAY, 5f);
    }

    public MandelBulbMaterial(RGBA color, float pointSize) {
        super(material(IMaterial.COLOR, IMaterial.POINT_SIZE, new MaterialAttribute<Boolean>("mandelbulb.isColorized"), new MaterialAttribute<Float>("mandelbulb.maxDistance")), geometry(IGeometry.POSITION_ARRAY, null, null));

        this.size = pointSize;
        this.color = color;
        this.shader = new MandelBulbShader(getClass(), Arrays.asList(getProvidedAttributes()));
    }

    public void setColorized(boolean isColorized) {
        this.isColorized = isColorized;
        updateRequest();
    }

    public void setMaxDistance(float maxDistance) {
        this.maxDistance = maxDistance;
        updateRequest();
    }

    @Override
    public IShader getShader() {
        return shader;
    }

    @Override
    public IGeometry.Primitive getType() {
        return IGeometry.Primitive.POINTS;
    }

    @Override
    public Object[] getData() {
        return data(color, size, isColorized, maxDistance);
    }
}
