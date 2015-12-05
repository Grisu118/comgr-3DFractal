package ch.fhnw.comgr.fractal.fractals.mandel;

import ch.fhnw.ether.render.shader.base.AbstractShader;
import ch.fhnw.ether.render.variable.base.BooleanUniform;
import ch.fhnw.ether.render.variable.base.FloatUniform;
import ch.fhnw.ether.render.variable.base.StateInject;
import ch.fhnw.ether.render.variable.builtin.*;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry;
import ch.fhnw.ether.scene.mesh.material.IMaterial;
import ch.fhnw.util.color.RGBA;
import com.jogamp.opengl.GL3;

/**
 * Created by benjamin on 03.12.2015.
 */
public class MandelBulbShader extends AbstractShader {
    public MandelBulbShader(Class<?> root) {
        super(root, "mandelbulb.point", "mandelbulb",
                IGeometry.Primitive.POINTS);
        boolean useVertexColors = true;
        boolean useVertexPointSize = true;

        addArray(new PositionArray());

        if (useVertexColors)
            addArray(new ColorArray());

        if (useVertexPointSize)
            addArray(new PointSizeArray());

        addUniform(new BooleanUniform("shader.vertex_colors_flag", "useVertexColors", () -> useVertexColors));
        addUniform(new BooleanUniform("shader.texture_flag", "useTexture", () -> false));

        addUniform(new ColorUniform(() -> RGBA.WHITE));
        addUniform(new FloatUniform(IMaterial.POINT_SIZE, "pointSize", () -> 3f));

        addUniform(new StateInject("shader.point_size_program", (gl, p) -> gl.glEnable(GL3.GL_PROGRAM_POINT_SIZE),
                (gl, p) -> gl.glDisable(GL3.GL_PROGRAM_POINT_SIZE)));

        addUniform(new ViewUniformBlock());
    }
}
