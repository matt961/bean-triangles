package com.trianglez;

import com.google.common.graph.Graph;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.PrimitiveSink;
import com.trianglez.node.Node;

import java.util.*;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TriangleFinder<N extends Node> {

    private Graph<N> g;
    private Map<N, List<Triangle<N>>> trianglesMap;
    private boolean parallelism;

    public TriangleFinder(final Graph<N> g, boolean parallelism) throws Exception {
        if (g.allowsSelfLoops() || g.isDirected()) {
            throw new Exception("Only works with undirected, self-loopless graphs. Sorry!");
        }
        this.g = g;
        this.parallelism = parallelism;
        this.trianglesMap = new HashMap<>();
        forEachNode();
    }

    /**
     * Finds all local trianglesMap for each node in the graph.
     */
    private void forEachNode() {
        Stream<N> nodes = this.parallelism ? this.g.nodes().parallelStream() : this.g.nodes().stream();
        this.trianglesMap = nodes.map(start -> {
            LocalTriangles localTriangles = new LocalTriangles(start);
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
        }).collect(Collectors.toMap(LocalTriangles::getKey, LocalTriangles::getTriangles));
    }

    /**
     * Sums up the count of each vertex's local trianglesMap in the Graph.
     *
     * @return Count of all local trianglesMap.
     */
    public long countLocalTriangles() {
        Stream<N> localTrianglesStream = this.parallelism ?
                this.trianglesMap.keySet().parallelStream() : this.trianglesMap.keySet().stream();

        return localTrianglesStream
                .map(this.trianglesMap::get)
                .mapToLong(List::size)
                .sum();
    }

    public Map<N, List<Triangle<N>>> getTriangles() {
        return trianglesMap;
    }

    /**
     * Get a stream of all trianglesMap that the TriangleFinder found.
     */
    public Stream<Triangle<N>> streamTriangles() {
        return this.trianglesMap.keySet().stream()
                .map(this.trianglesMap::get)
                .flatMap(List::stream);
    }

    /**
     * Clustering Coefficient of a node on an undirected graph is
     * (2 * count(trianglesMap[n]) / (degree[n] * (degree[n] - 1))
     *
     * @param n The node to calculate a clustering coefficient for.
     * @return clustering coefficient.
     */
    public double clusteringCoefficient(N n) {
        double ki = g.degree(n);
        if (ki < 2) // will result in divide by zero
            return -1d;
        double li = this.trianglesMap.get(n).size();
        double top = 2d * li;
        double bottom = ki * (ki - 1d);
        return top / bottom;
    }

    /**
     * @return average clustering coefficient of nodes in the graph.
     */
    public double avgClusteringCoefficient() {
        Stream<N> keyStream = this.parallelism ?
                this.trianglesMap.keySet().parallelStream() :
                this.trianglesMap.keySet().stream();
        return keyStream
                .mapToDouble(this::clusteringCoefficient)
                .filter(value -> value != -1d) // filter out the would-be NaN's
                .average().orElse(-1);
    }


    /**
     * Uses a {@link BloomFilter<Triangle>} and {@link HashSet<Triangle>} to generate a set of unique triangles where
     * uniqueness is defined by the nodes included in the triangle. E.g. Triangle(a, b, c) == Triangle(c, b, a), so
     * only one of them would be included in the set.
     *
     * @return A {@link Set<Triangle>}, {@link Triangle<N>}.
     */
    public Set<Triangle<N>> uniqueTriangles(boolean useBloomFilter) {
        if (useBloomFilter) {
            BloomFilter<Triangle<N>> triangleBloomFilter = BloomFilter.create(
                    (Triangle<N> nTriangle, PrimitiveSink primitiveSink) ->
                            nTriangle.getNodes().stream()
                                    .mapToInt(N::hashCode)
                                    .forEach(primitiveSink::putInt),
                    this.countLocalTriangles());

            Set<Triangle<N>> triangleSet = new HashSet<>();
            this.trianglesMap.keySet().stream()
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
        } else {
            Set<Triangle<N>> triangleSet = new HashSet<>();
            this.trianglesMap.keySet().stream()
                    .map(trianglesMap::get)
                    .flatMap(List::stream)
                    .forEach(triangleSet::add);
            return triangleSet;
        }
    }

    public List<Triangle<N>> uniqueTrianglesBloomOnly() {
        BloomFilter<Triangle<N>> triangleBloomFilter = BloomFilter.create(
                (Triangle<N> nTriangle, PrimitiveSink primitiveSink) ->
                        nTriangle.getNodes().stream()
                                .mapToInt(N::hashCode)
                                .forEach(primitiveSink::putInt),
                this.countLocalTriangles());

        List<Triangle<N>> triangleList = new ArrayList<>();
        this.trianglesMap.keySet().stream()
                .map(trianglesMap::get)
                .flatMap(List::stream)
                .forEach(triangle -> {
                    if (!triangleBloomFilter.mightContain(triangle)) {
                        triangleBloomFilter.put(triangle);
                        triangleList.add(triangle);
                    }
                });
        return triangleList;
    }

    /**
     * Auxiliary class to keep track of local triangles for a node. Gets mapped down into a {@link Map} where
     * K = {@link N} and V = {@link Set<Triangle>}, {@link Triangle<N>}.
     */
    class LocalTriangles {
        private N key;
        private List<Triangle<N>> triangles;

        LocalTriangles(N key) {
            this.key = key;
            triangles = new ArrayList<>();
        }

        N getKey() {
            return key;
        }

        List<Triangle<N>> getTriangles() {
            return triangles;
        }
    }
}
