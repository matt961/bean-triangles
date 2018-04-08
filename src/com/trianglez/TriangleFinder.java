package com.trianglez;

import com.google.common.graph.Graph;
import com.trianglez.node.Node;

import java.util.*;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TriangleFinder<N extends Node> {

    private Graph<N> g;
    private Map<N, List<Triangle<N>>> triangles;
    private boolean parallelism;

    public TriangleFinder(final Graph<N> g, final boolean parallelism) throws Exception {
        if (g.allowsSelfLoops() || g.isDirected()) {
            throw new Exception("Only works with undirected, self-loopless graphs. Sorry!");
        }
        this.g = g;
        this.parallelism = parallelism;
        this.triangles = new HashMap<>();
        forEachNode();
    }

    /**
     * Finds all triangles for each node in the graph.
     */
    private void forEachNode() {
        Stream<N> nodes = this.parallelism ? this.g.nodes().parallelStream() : this.g.nodes().stream();
        List<LocalTriangles<N>> listLocalTriangles = nodes.map(start -> {
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

        listLocalTriangles.forEach(x -> this.triangles.put(x.getKey(), x.getTriangles()));
    }

    /**
     * Sums up the count of each vertex's local triangles in the Graph.
     * @return Count of all local triangles.
     */
    public long countLocalTriangles() {
        Stream<N> localTrianglesStream = this.parallelism ?
                this.triangles.keySet().parallelStream() : this.triangles.keySet().stream();

        return localTrianglesStream
                .map(this.triangles::get)
                .mapToLong(List::size)
                .sum();
    }

    public Map<N, List<Triangle<N>>> getTriangles() {
        return triangles;
    }

    /**
     * Get a stream of all triangles that the TriangleFinder found.
     */
    public Stream<Triangle<N>> streamTriangles() {
        return this.triangles.keySet().stream()
                .map(this.triangles::get)
                .flatMap(List::stream);
    }
}
