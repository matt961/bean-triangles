package com.trianglez.node.types;

import com.google.common.base.Charsets;
import com.google.common.collect.ComparisonChain;
import com.google.common.hash.Hashing;
import com.google.common.hash.PrimitiveSink;
import com.trianglez.node.Node;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A type that represents a Facebook page by an anonymous ID and a category. See
 * https://snap.stanford.edu/data/gemsec_facebook_dataset.html for more detail.
 */
public class FBPageNode extends Node {
    private int id;
    private String category;

    public FBPageNode(final int id, final String category) {
        this.id = id;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return String.valueOf(this.getId()) +
                " of " +
                this.getCategory();
    }

    /**
     * Overridden for testing true equality between {@link FBPageNode} instances. {@link com.google.common.graph.Graph}
     * misbehaves otherwise.
     *
     * @param obj The other {@link FBPageNode} object.
     * @return true if objects are the same reference or if their fields match, false if otherwise or if
     * <code>obj</code> is not an instance of {@link FBPageNode}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof FBPageNode))
            return false;
        FBPageNode p = (FBPageNode) obj;
        return p.getId() == this.getId() && p.getCategory().equals(this.getCategory());
    }

    /**
     * Overridden to use {@link Hashing::murmur3_32} with {@link FBPageNode ::funnel}.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Hashing.murmur3_32().hashObject(this, this).asInt();
    }

    /**
     * @param node          A {@link Node} to get primitives uniqueTriangles.
     * @param primitiveSink The sink to funnel said primitives.
     */
    @Override
    @ParametersAreNonnullByDefault
    public void funnel(Node node, PrimitiveSink primitiveSink) {
        FBPageNode fbPageNode = (FBPageNode) node;
        primitiveSink
                .putInt(fbPageNode.getId())
                .putString(fbPageNode.getCategory(), Charsets.UTF_8);
    }

    /**
     * see {@link Node}
     *
     * @param o The other FBPageNode.
     * @return -1, 0, 1 for less than, equal, and greater than.
     */
    @Override
    @ParametersAreNonnullByDefault
    public int compareTo(Node o) {
        FBPageNode fbPageNode = (FBPageNode) o;
        return ComparisonChain.start()
                .compare(this.getId(), fbPageNode.getId())
                .compare(this.getCategory(), fbPageNode.getCategory())
                .result();
    }
}
