package ch.fhnw.comgr.fractal.util;

import ch.fhnw.ether.scene.mesh.geometry.AbstractGeometry;
import ch.fhnw.ether.scene.mesh.geometry.DefaultGeometry;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by benjamin on 29.10.2015.
 */
public class MergeGeometry extends DefaultGeometry {

    private Primitive type;
    private IGeometryAttribute[] attributes;
    private float[][] data;
    private ArrayList<float[][]> dataList;

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
        super(type, attributes, data);
        this.type = type;
        this.attributes = attributes;
        this.dataList = new ArrayList<>();
        this.dataList.add(data);
        this.data = data;
    }

    public void merge(MergeGeometry g) {
        if (!checkAttributeConsistency(g) || !type.equals(g.type)) {
            throw new IllegalArgumentException("Not same attributes");
        }
        dataList.add(g.data);
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

    private boolean checkAttributeConsistency(MergeGeometry g) {
        return Arrays.equals(this.attributes, g.attributes);
    }

    public static MergeGeometry createVC(Primitive type, float[] vertices, float[] colors) {
        IGeometryAttribute[] attributes = { POSITION_ARRAY, COLOR_ARRAY };
        float[][] data = { vertices, colors };
        return new MergeGeometry(type, attributes, data);
    }


}
