package com.trianglez;

import com.trianglez.node.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * A representation of a start node <code>key</code> and a set of edge-pairs that create a triangle with it. Used as an
 * intermediate representation during triangle finding. Usually, there is one {@link LocalTriangles} object per vertex
 * during triangle finding, and they all get mapped and merged into a HashMap at the end.
 *
 * @param <N>
 */
public class LocalTriangles<N extends Node> {
    private N key;
    private List<Triangle<N>> triangles;

    public LocalTriangles(N key) {
        this.key = key;
        triangles = new ArrayList<>();
    }

    public N getKey() {
        return key;
    }

    public List<Triangle<N>> getTriangles() {
        return triangles;
    }
}
