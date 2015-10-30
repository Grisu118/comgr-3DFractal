package ch.fhnw.comgr.fractal.util;

import ch.fhnw.ether.scene.mesh.geometry.AbstractGeometry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by benjamin on 29.10.2015.
 */
public class MergeGeometry extends AbstractGeometry {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private final Primitive type;
    private final IGeometryAttribute[] attributes;
    private final float[][] data;
    private ArrayList<float[][]> dataList;

    private int verticesCount = 0;
    private int trianglesCount = 0;

    /**
     * Generates geometry from the given data with the given attribute-layout.
     * All data is copied. Changes on the passed arrays will not affect this
     * geometry.
     *
     * @param type       Primitive type of this geometry (points, lines, triangles)
     * @param attributes Kind of attributes, must be same order as attribData
     * @param data
     */
    public MergeGeometry(Primitive type, IGeometryAttribute[] attributes, float[][] data) {
        super(type);

        if (attributes[0] != POSITION_ARRAY)
            throw new IllegalArgumentException("first attribute must be position");
        if (attributes.length != data.length)
            throw new IllegalArgumentException("# attribute type != # attribute data");
        checkAttributeConsistency(attributes, data);
        this.type = type;
        this.attributes = Arrays.copyOf(attributes, attributes.length);
        this.data = new float[data.length][];
        for (int i = 0; i < data.length; ++i) {
            this.data[i] = Arrays.copyOf(data[i], data[i].length);
        }
        verticesCount += data[0].length;
        trianglesCount += data[0].length/9;
        this.dataList = new ArrayList<>();
        this.dataList.add(data);
    }


    private MergeGeometry(MergeGeometry g) {
        super(g.getType());
        this.type = g.type;
        attributes = Arrays.copyOf(g.attributes, g.attributes.length);
        this.data = new float[g.data.length][];
        for (int i = 0; i < g.data.length; ++i) {
            this.data[i] = Arrays.copyOf(g.data[i], g.data[i].length);
        }
        this.dataList = (ArrayList<float[][]>) g.dataList.clone();
        this.verticesCount = g.verticesCount;
        this.trianglesCount = g.trianglesCount;
    }

    /**
     * Create copy of this geometry.
     *
     * @return the copy
     */
    public MergeGeometry copy() {
        try {
            lock.readLock().lock();
            return new MergeGeometry(this);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public IGeometryAttribute[] getAttributes() {
        return attributes;
    }

    @Override
    public float[][] getData() {
        return data;
    }

    @Override
    public void inspect(int index, IAttributeVisitor visitor) {
        try {
            lock.readLock().lock();
            visitor.visit(attributes[index], data[index]);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void inspect(IAttributesVisitor visitor) {
        try {
            lock.readLock().lock();
            visitor.visit(attributes, data);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void modify(int index, IAttributeVisitor visitor) {
        try {
            lock.writeLock().lock();
            visitor.visit(attributes[index], data[index]);
        } finally {
            lock.writeLock().unlock();
        }
        updateRequest();
    }

    @Override
    public void modify(IAttributesVisitor visitor) {
        try {
            lock.writeLock().lock();
            visitor.visit(attributes, data);
            checkAttributeConsistency(attributes, data);
        } finally {
            lock.writeLock().unlock();
        }
        updateRequest();
    }

    private static void checkAttributeConsistency(IGeometryAttribute[] attributes, float[][] data) {
        // check for correct individual lengths
        for (int i = 0; i < attributes.length; ++i) {
            if (data[i].length % attributes[i].getNumComponents() != 0)
                throw new IllegalArgumentException(attributes[i].id() + ": size " + data[i].length + " is not a multiple of attribute size " + attributes[i].getNumComponents());
        }

        // check for correct overall lengths
        int numElements = data[0].length / attributes[0].getNumComponents();
        for (int i = 1; i < attributes.length; ++i) {
            int ne = data[i].length / attributes[i].getNumComponents();
            if (ne != numElements)
                throw new IllegalArgumentException(attributes[i].id() + ": size " + ne + " does not match size of position attribute (" + numElements + ")");
        }
    }

    public void merge(MergeGeometry g) {
        if (!checkAttributeConsistency(g) || !type.equals(g.type)) {
            throw new IllegalArgumentException("Not same attributes");
        }
        dataList.add(g.data);
        this.verticesCount += g.data[0].length;
        this.trianglesCount += g.data[0].length/9;
    }

    public MergeGeometry mergedGeometry() {
        float[][] rData = new float[attributes.length][];
        int[] lengths = new int[attributes.length];
        for (float[][] floats : dataList) {
            for (int i = 0; i < floats.length; i++) {
                lengths[i] += floats[i].length;
            }
        }
        for (int i = 0; i<lengths.length; i++) {
            rData[i] = new float[lengths[i]];
        }
        lengths = new int[attributes.length];
        for (float[][] floats : dataList) {
            for (int i = 0; i < floats.length; i++) {
                for (int j = 0; j < floats[i].length; j++, lengths[i]++) {
                    rData[i][lengths[i]] = floats[i][j];
                }
            }
        }
        return new MergeGeometry(type, attributes, rData);
    }

    public int getVerticesCount() {
        return verticesCount;
    }

    public int getTrianglesCount() {
        return trianglesCount;
    }

    private boolean checkAttributeConsistency(MergeGeometry g) {
        return Arrays.equals(this.attributes, g.attributes);
    }

    public static MergeGeometry createVC(Primitive type, float[] vertices, float[] colors) {
        IGeometryAttribute[] attributes = { POSITION_ARRAY, COLOR_ARRAY };
        float[][] data = { vertices, colors };
        return new MergeGeometry(type, attributes, data);
    }


}
