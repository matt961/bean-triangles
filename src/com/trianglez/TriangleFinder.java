package com.trianglez;

import com.google.common.graph.Graph;
import com.trianglez.node.Node;

import java.util.*;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TriangleFinder<N extends Node> {

    private Graph<N> g;
    private List<LocalTriangles<N>> triangles;
    private boolean parallelism;

    public TriangleFinder(final Graph<N> g, final boolean parallelism) throws Exception {
        if (g.allowsSelfLoops() || g.isDirected()) {
            throw new Exception("Only works with undirected, self-loopless graphs. Sorry!");
        }
        this.g = g;
        this.parallelism = parallelism;
        forEachNode();
    }

    /**
     * Finds all triangles for each node in the graph.
     */
    private void forEachNode() {
        Stream<N> nodes = this.parallelism ? this.g.nodes().parallelStream() : this.g.nodes().stream();
        this.triangles = nodes.map(start -> {
            LocalTriangles<N> localTriangles = new LocalTriangles<>(start);
            ArrayList<N> adjNS = new ArrayList<>(this.g.adjacentNodes(start));
            IntStream.range(0, adjNS.size() - 1).forEach(i -> {
                N first = adjNS.get(i);
                adjNS.stream().skip(i + 1)
                        .forEach(second -> {
                            if (this.g.hasEdgeConnecting(first, second)) {
                                localTriangles.getTriangles().add(new Triangle<>(start, first, second));
                            }
                        });

            });
            return localTriangles;
        }).collect(Collectors.toList());
    }

    /**
     * Sums up the count of each vertex's local triangles in the Graph.
     * @return Count of all local triangles.
     */
    public long countLocalTriangles() {
        Stream<LocalTriangles<N>> localTrianglesStream = this.parallelism ?
                this.triangles.parallelStream() : this.triangles.stream();

        return localTrianglesStream
                .map(LocalTriangles::getTriangles)
                .mapToLong(List::size)
                .sum();
    }

    public Map<N, List<Triangle<N>>> mapOfTriangles() {
        Map<N, List<Triangle<N>>> tmap = new HashMap<>();
        this.triangles.forEach(t -> tmap.put(t.getKey(), t.getTriangles()));
        return tmap;
    }
}
