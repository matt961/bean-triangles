package com.trianglez;

import com.google.common.hash.PrimitiveSink;
import com.trianglez.node.Node;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.hash.BloomFilter;

public class UniqueTriangleFinder<N extends Node> {
    public Set<Triangle<N>> from(TriangleFinder<N> triangleFinder) {
        BloomFilter<Triangle<N>> triangleBloomFilter = BloomFilter.create(
                (Triangle<N> nTriangle, PrimitiveSink primitiveSink) ->
                        nTriangle.getNodes().stream()
                                .mapToInt(N::hashCode)
                                .forEach(primitiveSink::putInt),
                triangleFinder.countLocalTriangles());

        Map<N, List<Triangle<N>>> trianglesMap = triangleFinder.getTriangles();
        Set<Triangle<N>> triangleSet = new HashSet<>();
        triangleFinder.getTriangles().keySet().stream()
                .map(trianglesMap::get)
                .flatMap(List::stream)
                .forEach(triangle -> {
                    if (triangleBloomFilter.mightContain(triangle)) {
                        if (triangleSet.add(triangle)) {
                            triangleBloomFilter.put(triangle);
                        }
                    } else {
                        triangleBloomFilter.put(triangle);
                        triangleSet.add(triangle);
                    }
                });
        return triangleSet;
    }
}
