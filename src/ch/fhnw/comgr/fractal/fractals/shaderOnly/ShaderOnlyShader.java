package ch.fhnw.comgr.fractal.fractals.shaderOnly;

import ch.fhnw.ether.render.shader.base.AbstractShader;
import ch.fhnw.ether.render.variable.base.FloatUniform;
import ch.fhnw.ether.render.variable.base.Vec3FloatUniform;
import ch.fhnw.ether.render.variable.builtin.ColorMapArray;
import ch.fhnw.ether.render.variable.builtin.NormalArray;
import ch.fhnw.ether.render.variable.builtin.PositionArray;
import ch.fhnw.ether.render.variable.builtin.ViewUniformBlock;
import ch.fhnw.ether.scene.attribute.IAttribute;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry;
import ch.fhnw.util.math.IVec3;

import java.util.Collection;

/**
 * Created by benjamin on 03.12.2015.
 */
public class ShaderOnlyShader extends AbstractShader {
    public ShaderOnlyShader(Class<?> root, Collection<IAttribute> attributes) {
        super(root, "mandelbulbO", "mandelbulbO",
                IGeometry.Primitive.TRIANGLES);

        addArray(new PositionArray());
        //addArray(new NormalArray());
        //addArray(new ColorMapArray());
        addUniform(new FloatUniform("mandelbulbO.scale", "scale"));
        addUniform(new FloatUniform("mandelbulbO.power", "power"));
        addUniform(new Vec3FloatUniform("mandelbulbO.cameraPosition", "cameraPosition"));
        addUniform(new FloatUniform("mandelbulbO.cameraRoll", "cameraRoll"));
        addUniform(new Vec3FloatUniform("mandelbulbO.size", "size"));
        addUniform(new Vec3FloatUniform("mandelbulbO.outputSize", "outputSize"));
        addUniform(new ViewUniformBlock());
    }
}
