package ch.fhnw.comgr.fractal.fractals.mandel;

import ch.fhnw.ether.render.shader.IShader;
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

    public MandelBulbMaterial() {
        super(material(IMaterial.COLOR, IMaterial.POINT_SIZE), geometry(IGeometry.POSITION_ARRAY, null, null));

        this.size = 3;
        this.color = RGBA.GRAY;
        this.shader = new MandelBulbShader(getClass(), Arrays.asList(getProvidedAttributes()));
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
        return data(color, size);
    }
}
