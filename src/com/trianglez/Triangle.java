package com.trianglez;

import com.google.common.hash.Funnel;
import com.google.common.hash.Hashing;
import com.google.common.hash.PrimitiveSink;
import com.trianglez.node.Node;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

/**
 * Class that represents two other nodes that can be associated with a second node, which altogether create a Triangle.
 * It is useful to hash a Triangle if they are going to be put into sets.
 * @param <N>
 */
public class Triangle<N extends Node> implements Funnel<Triangle<N>> {

    /**
     * nodes is a SortedSet of N because {@link Triangle::hashCode} needs to produce the same hash regardless of the
     * ordering of Triangle constructor. To achieve this, a sorted order iteration needs to be possible in the hashCode
     * method.
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
     * Overridden to be used by a Set.
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
     * Overridden to use in a set containing some generic N type.
     * <strong>Ordering of node parameters does not matter, the hash will be the same based on intersecting nodes.
     * </strong>
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Hashing.murmur3_32().hashObject(this, this).asInt();
    }

    @Override @ParametersAreNonnullByDefault
    public void funnel(Triangle<N> triangle, PrimitiveSink primitiveSink) {
        triangle.getNodes().forEach(node -> primitiveSink.putInt(node.hashCode()));
    }

    @Override
    public String toString(){
        final StringBuilder text = new StringBuilder("(");
        this.getNodes().forEach((N n) -> text.append(n.toString()).append(","));
        return text.toString().substring(0,text.length() - 1) +")";
    }
}
