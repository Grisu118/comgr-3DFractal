package ch.fhnw.comgr.fractal.fractals.mandel;

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
public class MandelBulbShader extends AbstractShader {
    public MandelBulbShader(Class<?> root, Collection<IAttribute> attributes) {
        super(root, "mandelbulb.point", "mandelbulb",
                IGeometry.Primitive.POINTS);
        boolean useVertexColors = attributes.contains(IGeometry.COLOR_ARRAY);
        boolean useVertexPointSize = attributes.contains(IGeometry.POINT_SIZE_ARRAY);

        addArray(new PositionArray());

        if (useVertexColors)
            addArray(new ColorArray());

        if (useVertexPointSize)
            addArray(new PointSizeArray());

        addUniform(new BooleanUniform("shader.vertex_colors_flag", "useVertexColors", () -> true));
        addUniform(new BooleanUniform("shader.texture_flag", "useTexture", () -> false));

        addUniform(new ColorUniform(attributes.contains(IMaterial.COLOR) ? null : () -> RGBA.WHITE));
        addUniform(new FloatUniform(IMaterial.POINT_SIZE, "vertexPointSize", attributes.contains(IMaterial.POINT_SIZE) ? null : () -> 3f));
        addUniform(new FloatUniform("custom.maxDistance", "maxDistance", () -> 0.4f));

        addUniform(new StateInject("shader.point_size_program", (gl, p) -> gl.glEnable(GL3.GL_PROGRAM_POINT_SIZE),
                (gl, p) -> gl.glDisable(GL3.GL_PROGRAM_POINT_SIZE)));

        addUniform(new ViewUniformBlock());
    }
}
