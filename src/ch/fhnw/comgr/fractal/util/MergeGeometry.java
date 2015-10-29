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
    private ArrayList<Float>[] dataList;

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
        this.dataList = new ArrayList[data.length];
        for (int i = 0; i < dataList.length; i++) {
            dataList[i] = new ArrayList<>(data[i].length);
            for (float f : data[i]) {
                dataList[i].add(f);
            }
        }
    }

    public void merge(MergeGeometry g) {
        if (!checkAttributeConsistency(g) || !type.equals(g.type)) {
            throw new IllegalArgumentException("Not same attributes");
        }
        for (int i = 0; i<dataList.length; i++) {
            dataList[i].addAll(g.dataList[i]);
        }
    }

    public MergeGeometry mergedGeometry() {
        float[][] rData = new float[dataList.length][];
        for (int i = 0; i < rData.length; i++) {
            float[] r = new float[dataList[i].size()];
            for (int j = 0; j<r.length;j++) {
                r[j] = dataList[i].get(j);
            }
            rData[i] = r;
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
