package ch.fhnw.comgr.fractal.fractals.simpleTree;

import ch.fhnw.comgr.fractal.IUpdateListener;
import ch.fhnw.comgr.fractal.fractals.IFractal;
import ch.fhnw.comgr.fractal.ui.BooleanWidget;
import ch.fhnw.comgr.fractal.ui.SmallSlider;
import ch.fhnw.comgr.fractal.ui.TextWidget;
import ch.fhnw.comgr.fractal.util.MergeGeometry;
import ch.fhnw.comgr.fractal.util.TransformableGeometry;
import ch.fhnw.comgr.fractal.util.UpdateType;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.ether.scene.light.ILight;
import ch.fhnw.ether.scene.light.PointLight;
import ch.fhnw.ether.scene.mesh.DefaultMesh;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry;
import ch.fhnw.ether.scene.mesh.material.ShadedMaterial;
import ch.fhnw.ether.ui.IWidget;
import ch.fhnw.util.color.RGB;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.Vec4;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private ILight light = new PointLight(new Vec3(0, 0, 2), RGB.BLACK, RGB.WHITE, 10); //new DirectionalLight(Vec3.Z, RGB.BLACK, RGB.WHITE);
    private List<IWidget> widgets = new ArrayList<>();
    private IMesh msh;

    private Set<IUpdateListener> listeners = new HashSet<>();
    private TextWidget verticesCount;
    private TextWidget trianglesCount;
    private boolean lightState = true;

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

        Vec3[] normals = {
                // bottom
                new Vec3(0, 0, -1), new Vec3(0, 0, -1), new Vec3(0, 0, -1),
                new Vec3(0, 0, -1), new Vec3(0, 0, -1), new Vec3(0, 0, -1),
                //top
                new Vec3(0, 0, 1), new Vec3(0, 0, 1), new Vec3(0, 0, 1),
                new Vec3(0, 0, 1), new Vec3(0, 0, 1), new Vec3(0, 0, 1),
                //front
                new Vec3(0, -1, 0), new Vec3(0, -1, 0), new Vec3(0, -1, 0),
                new Vec3(0, -1, 0), new Vec3(0, -1, 0), new Vec3(0, -1, 0),
                //back
                new Vec3(0, 1, 0), new Vec3(0, 1, 0), new Vec3(0, 1, 0),
                new Vec3(0, 1, 0), new Vec3(0, 1, 0), new Vec3(0, 1, 0),
                //left
                new Vec3(-1, 0, 0), new Vec3(-1, 0, 0), new Vec3(-1, 0, 0),
                new Vec3(-1, 0, 0), new Vec3(-1, 0, 0), new Vec3(-1, 0, 0),
                //right
                new Vec3(1, 0, 0), new Vec3(1, 0, 0), new Vec3(1, 0, 0),
                new Vec3(1, 0, 0), new Vec3(1, 0, 0), new Vec3(1, 0, 0)
        };
        this.geometry = new TransformableGeometry(vec4s, normals);

        createWidgets();



    }

    private void createWidgets() {
        widgets.add(new SmallSlider(0, 4, "Tiefe", null, 0, (s, v) -> updateDepth(s.getValue(minDepth, maxDepth))));
        verticesCount = new TextWidget(0, 2, 60, "Vertices:");
        trianglesCount = new TextWidget(0, 3, 60, "Triangles:");
        widgets.add(verticesCount);
        widgets.add(trianglesCount);
        widgets.add(new BooleanWidget(0, 5, "Light", "On", "Off", "Turn on/off Light", (w, v) -> setLightState(w.getValue())));
    }

    private void createObjects() {
        if (trees[depth - 1] == null) {
            recursiveFract(1, 0, Vec3.Y, Mat4.ID);
            System.out.println("Fractal Calculated");
            trees[depth - 1] = tree.mergedGeometry();
        }
        msh = new DefaultMesh(new ShadedMaterial(RGB.BLACK, RGB.BLUE, RGB.GRAY, RGB.WHITE, 10, 1, 1f), trees[depth - 1], IMesh.Queue.TRANSPARENCY);
        msh.setName(String.format("Tiefe: %d", depth));
        System.out.println("Mesh Generated");
        scene.add3DObject(msh);
        System.out.println("Mesh Added");
    }

    private void updateDepth(int value) {
        if (depth != value) {
            depth = value;
            scene.remove3DObject(msh);
            tree = null;
            createObjects();
            notifyUpdate(UpdateType.DEPTH);
        }
    }

    private void recursiveFract(int level, float alpha, Vec3 rot, Mat4 transform) {
        if (level > depth) return;

        transform = Mat4.multiply(transform, Mat4.rotate(alpha, rot), Mat4.translate(0, 0, length / level));
        {
            TransformableGeometry tg = geometry.transform(Mat4.multiply(transform, Mat4.scale(1.0f / level)));
            if (tree != null) {
                tree.merge(createGeometry(tg.getVertices(), tg.getNormals()));
            } else {
                tree = createGeometry(tg.getVertices(), tg.getNormals());
            }
        }
        recursiveFract(++level, this.alpha, Vec3.Y, transform);
        recursiveFract(level, -this.alpha, Vec3.Y, transform);
        recursiveFract(level, this.alpha, Vec3.X, transform);
        recursiveFract(level, -this.alpha, Vec3.X, transform);
    }

    private MergeGeometry createGeometry(float[] vertices, float[] normals) {
        float[] colors = new float[36 * 4];
        for (int i = 0; i < colors.length; i += 4) {
            colors[i + 0] = 0.2f; //R
            colors[i + 1] = 0.8f; //G
            colors[i + 2] = 0.2f; //B
            colors[i + 3] = 1; //A
        }
        return MergeGeometry.createVNC(IGeometry.Primitive.TRIANGLES, vertices, normals, colors);
    }

    @Override
    public void init() {
        trees = new MergeGeometry[maxDepth];
        createObjects();

        trianglesCount.setContent(String.format("%,d", getTrianglesCount()));
        verticesCount.setContent(String.format("%,d", getVerticesCount()));
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

    @Override
    public int getVerticesCount() {
        return trees[depth - 1].getVerticesCount();
    }

    @Override
    public int getTrianglesCount() {
        return trees[depth - 1].getTrianglesCount();
    }

    @Override
    public void registerUpdateListener(IUpdateListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeUpdateListener(IUpdateListener listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    @Override
    public ILight getLight() {
        return light;
    }

    private void setLightState(boolean state) {
        if (this.lightState != state) {
            this.lightState = state;
            notifyUpdate(UpdateType.LIGHT);
        }
    }

    @Override
    public boolean getLightState() {
        return lightState;
    }

    private void notifyUpdate(UpdateType t) {
        switch (t) {
            case DEPTH:
                verticesCount.setContent(String.format("%,d", getVerticesCount()));
                trianglesCount.setContent(String.format("%,d", getTrianglesCount()));
                break;
        }
        listeners.forEach(l -> l.notifyUpdate(this, t));
    }
}
