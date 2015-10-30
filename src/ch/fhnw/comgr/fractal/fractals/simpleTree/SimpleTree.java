package ch.fhnw.comgr.fractal.fractals.simpleTree;

import ch.fhnw.comgr.fractal.fractals.IFractal;
import ch.fhnw.comgr.fractal.util.MergeGeometry;
import ch.fhnw.comgr.fractal.util.TransformableGeometry;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.ether.scene.mesh.DefaultMesh;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry;
import ch.fhnw.ether.scene.mesh.material.ColorMaterial;
import ch.fhnw.ether.ui.IWidget;
import ch.fhnw.ether.ui.Slider;
import ch.fhnw.ether.view.IView;
import ch.fhnw.util.color.RGBA;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.Vec4;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by benjamin on 29.10.2015.
 */
public class SimpleTree implements IFractal {

    private float length;
    private float width;
    private float height;
    private float alpha;
    private int depth;
    private int minDepth = 1;
    private int maxDepth = 10;

    private TransformableGeometry geometry;
    private MergeGeometry tree;
    private MergeGeometry[] trees = new MergeGeometry[maxDepth];

    private IScene scene;
    private List<IWidget> widgets = new ArrayList<>();
    private IMesh msh;

    public SimpleTree(float length, float width, float height, float alpha, IScene scene) {
        this.length = length;
        this.width = width;
        this.height = height;
        this.alpha = alpha;
        this.depth = 1;
        this.scene = scene;

        float w = width * 0.5f;
        float h = height * 0.5f;
        Vec4[] vec4s = {
                // bottom
                new Vec4(-w, -h, -length, 1), new Vec4(-w, h, -length, 1), new Vec4(w, h, -length, 1),
                new Vec4(-w, -h, -length, 1), new Vec4(w, h, -length, 1), new Vec4(w, -h, -length, 1),

                // top
                new Vec4(w, -h, 0, 1), new Vec4(w, h, 0, 1), new Vec4(-w, h, 0, 1),
                new Vec4(w, -h, 0, 1), new Vec4(-w, h, 0, 1), new Vec4(-w, -h, 0, 1),

                // front
                new Vec4(-w, -h, -length, 1), new Vec4(w, -h, -length, 1), new Vec4(w, -h, 0, 1),
                new Vec4(-w, -h, -length, 1), new Vec4(w, -h, 0, 1), new Vec4(-w, -h, 0, 1),

                // back
                new Vec4(w, h, -length, 1), new Vec4(-w, h, -length, 1), new Vec4(-w, h, 0, 1),
                new Vec4(w, h, -length, 1), new Vec4(-w, h, 0, 1), new Vec4(w, h, 0, 1),

                // left
                new Vec4(-w, h, -length, 1), new Vec4(-w, -h, -length, 1), new Vec4(-w, -h, 0, 1),
                new Vec4(-w, h, -length, 1), new Vec4(-w, -h, 0, 1), new Vec4(-w, h, 0, 1),

                // right
                new Vec4(w, -h, -length, 1), new Vec4(w, h, -length, 1), new Vec4(w, h, 0, 1),
                new Vec4(w, -h, -length, 1), new Vec4(w, h, 0, 1), new Vec4(w, -h, 0, 1)
        };
        this.geometry = new TransformableGeometry(vec4s);

        widgets.add(new Slider(1,1,"Tiefe", "Die Maximale Tiefe", 0, (slider, view) -> updateDepth(slider.getValue(), view)));

        init();
    }

    private void createObjects() {
        if (trees[depth-1] == null) {
            recursiveFract(1, 0, Vec3.Y, Mat4.ID);
            System.out.println("Fractal Calculated");
            trees[depth-1] = tree.mergedGeometry();
        }
        msh = new DefaultMesh(new ColorMaterial(RGBA.WHITE, true), trees[depth-1], IMesh.Queue.TRANSPARENCY);
        msh.setName(String.format("Tiefe: %d", depth));
        System.out.println("Mesh Generated");
        scene.add3DObject(msh);
        System.out.println("Mesh Added");
    }

    private void updateDepth(float value, IView view) {
        int n = maxDepth - minDepth;
        int d = Math.round(value*n)+1;
        if (depth != d) {
            depth = d;
            scene.remove3DObject(msh);
            tree = null;
            createObjects();
        }
    }

    private void recursiveFract(int level, float alpha, Vec3 rot, Mat4 transform) {
        if (level > depth) return;

        transform = Mat4.multiply(transform, Mat4.rotate(alpha, rot), Mat4.translate(0, 0, length / level));

        if (tree != null) {
            tree.merge(createGeometry(geometry.transform(Mat4.multiply(transform, Mat4.scale(1.0f / level))).toArray()));
        } else {
            tree = createGeometry(geometry.transform(Mat4.multiply(transform, Mat4.scale(1.0f / level))).toArray());
        }
        recursiveFract(++level, this.alpha, Vec3.Y, transform);
        recursiveFract(level, -this.alpha, Vec3.Y, transform);
        recursiveFract(level, this.alpha, Vec3.X, transform);
        recursiveFract(level, -this.alpha, Vec3.X, transform);
    }

    private MergeGeometry createGeometry(float[] vertices) {
        float[] colors = new float[36 * 4];
        for (int i = 0; i < colors.length; i += 4) {
            colors[i + 0] = 0.2f; //R
            colors[i + 1] = 0.8f; //G
            colors[i + 2] = 0.2f; //B
            colors[i + 3] = 1; //A
        }
        return MergeGeometry.createVC(IGeometry.Primitive.TRIANGLES, vertices, colors);
    }

    @Override
    public void init() {
        trees = new MergeGeometry[maxDepth];
        createObjects();
    }

    @Override
    public List<IWidget> getWidgets() {
        return widgets;
    }

    @Override
    public void cleanup() {
        scene.remove3DObject(msh);
        tree = null;
        msh = null;
        trees = null;
    }
}
