package com.trianglez.node;

import com.google.common.hash.Funnel;

public abstract class Node implements Comparable<Node>, Funnel<Node> {
    /**
     * Must be overridden because Node objects will be hashed.
     * @return the hashcode of a Node.
     */
    public abstract int hashCode();
    public abstract boolean equals(Object obj);
}
