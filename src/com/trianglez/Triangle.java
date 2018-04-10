package com.trianglez;

import com.google.common.hash.Funnel;
import com.google.common.hash.Hashing;
import com.google.common.hash.PrimitiveSink;
import com.trianglez.node.Node;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents two other nodes that can be associated with a second node, which altogether create a Triangle.
 * It is useful to hash a Triangle if they are going to be put into a set.
 *
 * @param <N> a subclass of Node.
 */
public class Triangle<N extends Node> implements Funnel<Triangle<N>> {

    /**
     * nodes is a sorted list of N because {@link Triangle::hashCode} needs to produce the same hash regardless of the
     * node ordering. To achieve this, a sorted order iteration needs to be possible for the hashCode method.
     */
    private List<N> nodes;

    public Triangle(N start, N first, N second) {
        this.nodes = new ArrayList<>(List.of(start, first, second));
        this.nodes.sort(Node::compareTo);
    }

    public List<N> getNodes() {
        return this.nodes;
    }

    /**
     * Overridden to be usable in HashSet.
     *
     * @param obj The other Triangle.
     * @return true if their hashCode matches or is same ref, false if otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof Triangle))
            return false;
        return this.hashCode() == (obj).hashCode();
    }

    /**
     * Overridden to be usable in {@link java.util.HashSet<N>}.
     * <strong>Ordering of node parameters from the constructor doesn't matter because the nodes get sorted first. The
     * same contents will result in the same hash.
     * </strong>
     *
     * @return murmur3_32 (Integer) hash code
     */
    @Override
    public int hashCode() {
        return Hashing.murmur3_32().hashObject(this, this).asInt();
    }

    /**
     * Overridden for sanity check to make sure order of nodes doesn't matter when calculating the hash.
     *
     * @param triangle      A Triangle to funnel primitives from. Since its generic {@link N} overrides
     *                      {@link Object::hashCode}, its nodes are hashed and inserted as ints into the sink.
     * @param primitiveSink The sink to funnel primitives to.
     */
    @Override
    @ParametersAreNonnullByDefault
    public void funnel(Triangle<N> triangle, PrimitiveSink primitiveSink) {
        triangle.getNodes().forEach(node -> primitiveSink.putInt(node.hashCode()));
    }

    @Override
    public String toString() {
        final StringBuilder text = new StringBuilder("(");
        this.getNodes().forEach((N n) -> text.append(n.toString()).append(","));
        return text.toString().substring(0, text.length() - 1) + ")";
    }
}
