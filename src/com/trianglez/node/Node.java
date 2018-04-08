package com.trianglez.node;

import com.google.common.hash.Funnel;

/**
 * Implements {@link Comparable<Node>} for sorting in a {@link com.trianglez.Triangle<Node>}.
 * Implements {@link Funnel<Node>} to assist with creating a good hashCode.
 */
public abstract class Node implements Comparable<Node>, Funnel<Node> {
    /**
     * Must be overridden because Node objects will be hashed.
     *
     * @return the hashcode of a Node.
     */
    public abstract int hashCode();

    /**
     * Subclasses must implement equality based on their fields. Recommended to use
     * {@link com.google.common.collect.ComparisonChain} to assist with this.
     *
     * @param obj The other node to compare to. It should get casted to the extending subclass.
     * @return true if equal, false if not based on overridden implementation.
     */
    public abstract boolean equals(Object obj);
}
