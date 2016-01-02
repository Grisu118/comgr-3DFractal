package ch.fhnw.comgr.fractal.fractals.shaderOnly;

import ch.fhnw.ether.render.shader.base.AbstractShader;
import ch.fhnw.ether.render.variable.base.BooleanUniform;
import ch.fhnw.ether.render.variable.base.FloatUniform;
import ch.fhnw.ether.render.variable.base.StateInject;
import ch.fhnw.ether.render.variable.builtin.*;
import ch.fhnw.ether.scene.attribute.IAttribute;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry;
import ch.fhnw.ether.scene.mesh.material.IMaterial;
import ch.fhnw.util.color.RGBA;
import com.jogamp.opengl.GL3;

import java.util.Collection;

/**
 * Created by benjamin on 03.12.2015.
 */
public class ShaderOnlyShader extends AbstractShader {
    public ShaderOnlyShader(Class<?> root, Collection<IAttribute> attributes) {
        super(root, "mandelbulbO", "mandelbulbO",
                IGeometry.Primitive.TRIANGLES);

        addArray(new PositionArray());

        addUniform(new FloatUniform("mandelbulbO.scale", "scale"));
        addUniform(new FloatUniform("mandelbulbO.power", "power"));

        addUniform(new ViewUniformBlock());
    }
}
