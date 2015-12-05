package ch.fhnw.comgr.fractal.fractals.mandel;

import ch.fhnw.ether.render.shader.IShader;
import ch.fhnw.ether.scene.attribute.IAttribute;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry;
import ch.fhnw.ether.scene.mesh.material.AbstractMaterial;
import ch.fhnw.ether.scene.mesh.material.ICustomMaterial;
import ch.fhnw.ether.scene.mesh.material.IMaterial;
import ch.fhnw.util.color.RGBA;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by benjamin on 03.12.2015.
 */
public class MandelBulbMaterial extends AbstractMaterial implements ICustomMaterial {

    private final IShader shader;
    private float pointSize;
    private RGBA color;

    public MandelBulbMaterial() {
        super(material(IMaterial.COLOR, IMaterial.POINT_SIZE), geometry(IGeometry.COLOR_ARRAY, IGeometry.POINT_SIZE_ARRAY));

        pointSize = 3;
        color = RGBA.GRAY;
        this.shader = new MandelBulbShader(getClass(), Arrays.asList(getProvidedAttributes()));
    }

    @Override
    public IShader getShader() {
        return shader;
    }

    @Override
    public IGeometry.Primitive getType() {
        // default to triangles, as this is the majority of all materials
        return IGeometry.Primitive.POINTS;
    }

    @Override
    public Object[] getData() {
        return data(color, pointSize);
    }
}
