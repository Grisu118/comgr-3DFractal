package ch.fhnw.comgr.fractal.fractals.mandel;

import ch.fhnw.ether.render.shader.IShader;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry;
import ch.fhnw.ether.scene.mesh.material.AbstractMaterial;
import ch.fhnw.ether.scene.mesh.material.ICustomMaterial;
import ch.fhnw.ether.scene.mesh.material.IMaterial;

/**
 * Created by benjamin on 03.12.2015.
 */
public class MandelBulbMaterial extends AbstractMaterial implements ICustomMaterial {

    private final IShader shader;

    public MandelBulbMaterial(MandelBulbShader shader) {
        super(material(IMaterial.COLOR), geometry(IGeometry.COLOR_ARRAY));

        this.shader = shader;
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
        return null;
    }
}
