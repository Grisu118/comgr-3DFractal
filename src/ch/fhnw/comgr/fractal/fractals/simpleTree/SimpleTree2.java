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
import ch.fhnw.ether.scene.mesh.MeshUtilities;
import ch.fhnw.ether.scene.mesh.geometry.DefaultGeometry;
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
public class SimpleTree2 implements IFractal {

    private float length;
    private float width;
    private float height;
    private float alpha;
    private int depth;
    private int minDepth = 1;
    private int maxDepth = 10;

    private IGeometry geometry;
    private List<IMesh>[] trees;

    private IScene scene;
    private ILight light = new PointLight(new Vec3(0, 0, 2), RGB.BLACK, RGB.WHITE, 10); //new DirectionalLight(Vec3.Z, RGB.BLACK, RGB.WHITE);
    private List<IWidget> widgets = new ArrayList<>();
    private List<IMesh> msh;
    private List<IMesh> meshes = new ArrayList<>();

    private Set<IUpdateListener> listeners = new HashSet<>();
    private TextWidget verticesCount;
    private TextWidget trianglesCount;
    private boolean lightState = true;

    public SimpleTree2(float length, float width, float height, float alpha, IScene scene) {
        this.length = length;
        this.width = width;
        this.height = height;
        this.alpha = alpha;
        this.depth = 1;
        this.scene = scene;

        float w = width * 0.5f;
        float h = height * 0.5f;
        float[] vec4s = {
                // bottom
                -w, -h, -length, -w, h, -length,w, h, -length,
                -w, -h, -length,w, h, -length,w, -h, -length,

                // top
                w, -h, 0,w, h, 0,-w, h, 0,
                        w, -h, 0,-w, h, 0,-w, -h, 0,

                // front
                -w, -h, -length,w, -h, -length,w, -h, 0,
                        -w, -h, -length,w, -h, 0,-w, -h, 0,

                // back
                w, h, -length,-w, h, -length,-w, h, 0,
                w, h, -length,-w, h, 0,w, h, 0,

                // left
                -w, h, -length,-w, -h, -length,-w, -h, 0,
                -w, h, -length,-w, -h, 0,-w, h, 0,

                // right
                w, -h, -length,w, h, -length,w, h, 0,
                w, -h, -length,w, h, 0,w, -h, 0
        };

        float[] normals = {
                // bottom
               0, 0, -1,0, 0, -1,0, 0, -1,
                0, 0, -1,0, 0, -1,0, 0, -1,
                //top
                0, 0, 1,0, 0, 1, 0, 0,1,
                0, 0, 0, 1,0,1, 0, 0,1,
                //front
                0, -1, 0,0, -1, 0,0, -1, 0,
                0, -1, 0,0, -1, 0,0, -1, 0,
                //back
                0, 1, 0,0, 1, 0,0, 1, 0,
                0, 1, 0,0, 1, 0,0, 1, 0,
                //left
                -1, 0, 0,-1, 0, 0,-1, 0, 0,
                -1, 0, 0,-1, 0, 0,-1, 0, 0,
                //right
                1, 0, 0,1, 0, 0,1, 0, 0,
                1, 0, 0,1, 0, 0,1, 0, 0
        };
        this.geometry = DefaultGeometry.createVN(IGeometry.Primitive.TRIANGLES, vec4s, normals);

        createWidgets();

        init();
        trianglesCount.setContent(String.format("%,d", getTrianglesCount()));
        verticesCount.setContent(String.format("%,d", getVerticesCount()));
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
            trees[depth - 1] = MeshUtilities.mergeMeshes(meshes);
        }
        System.out.println("Mesh Generated");
        msh = trees[depth - 1];
        scene.add3DObjects(trees[depth - 1]);
        System.out.println("Mesh Added");
    }

    private void updateDepth(int value) {
        if (depth != value) {
            depth = value;
            scene.remove3DObjects(msh);
            createObjects();
            notifyUpdate(UpdateType.DEPTH);
        }
    }

    private void recursiveFract(int level, float alpha, Vec3 rot, Mat4 transform) {
        if (level > depth) {
            return;
        }

        transform = Mat4.multiply(transform, Mat4.rotate(alpha, rot), Mat4.translate(0, 0, length / level));

        IMesh mesh = new DefaultMesh(new ShadedMaterial(RGB.BLACK, RGB.BLUE, RGB.GRAY, RGB.WHITE, 10, 1, 1f), geometry);
        mesh.setTransform(Mat4.multiply(transform, Mat4.scale(1.0f / level)));
        meshes.add(mesh);

        recursiveFract(++level, this.alpha, Vec3.Y, transform);
        recursiveFract(level, -this.alpha, Vec3.Y, transform);
        recursiveFract(level, this.alpha, Vec3.X, transform);
        recursiveFract(level, -this.alpha, Vec3.X, transform);
    }

    @Override
    public void init() {
        trees = new ArrayList[maxDepth];
        createObjects();
    }

    @Override
    public List<IWidget> getWidgets() {
        return widgets;
    }

    @Override
    public void cleanup() {
        scene.remove3DObjects(msh);
        msh = null;
        trees = null;
    }

    @Override
    public int getVerticesCount() {
        return 0;
    }

    @Override
    public int getTrianglesCount() {
        return 0;
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
