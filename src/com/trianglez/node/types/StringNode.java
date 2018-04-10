package com.trianglez.node.types;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.google.common.hash.PrimitiveSink;
import com.trianglez.node.Node;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Graph node data used for simple testing.
 */
public class StringNode extends Node {
    private String inner;

    public StringNode(String inner) {
        this.inner = inner;
    }

    @Override
    public String toString() {
        return inner;
    }

    @Override
    @ParametersAreNonnullByDefault
    public int compareTo(Node o) {
        return this.inner.compareTo(((StringNode) o).inner);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof StringNode))
            return false;
        return this.inner.equals(
                ((StringNode) obj).inner
        );
    }

    @Override
    public int hashCode() {
        return Hashing.murmur3_32().hashObject(this, this).asInt();
    }

    @Override
    @ParametersAreNonnullByDefault
    public void funnel(Node o, PrimitiveSink primitiveSink) {
        primitiveSink.putString(
                ((StringNode) o).inner,
                Charsets.UTF_8
        );
    }
}
