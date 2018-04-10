import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.trianglez.Triangle;
import com.trianglez.TriangleFinder;
import com.trianglez.readers.FBGraphReader;
import com.trianglez.node.types.FBPageNode;
import com.trianglez.node.types.StringNode;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestTriangleFinder {
    private MutableGraph<StringNode> g = GraphBuilder.undirected().allowsSelfLoops(false).build();
    private StringNode u;
    private StringNode v;
    private StringNode w;
    private StringNode x;

    @Before
    public void before() {
        u = new StringNode("u");
        v = new StringNode("v");
        w = new StringNode("w");
        x = new StringNode("x");
        g.putEdge(u, v);
        g.putEdge(u, w);
        g.putEdge(u, x);
        g.putEdge(v, w);
        g.putEdge(x, w);
    }

    @Test
    public void testCountTriangles() throws Exception {
        TriangleFinder<StringNode> tf = new TriangleFinder<>(g, true);
        Assert.assertEquals(tf.countLocalTriangles(), 6);
    }

    @Test
    public void testMapOfTriangles() throws Exception {
        TriangleFinder<StringNode> tf = new TriangleFinder<>(g, true);
        Map<StringNode, List<Triangle<StringNode>>> triangles = tf.getTriangles();
        Assert.assertEquals(triangles.get(u).size(), 2);
        Assert.assertEquals(triangles.get(v).size(), 1);
        Assert.assertEquals(triangles.get(w).size(), 2);
        Assert.assertEquals(triangles.get(x).size(), 1);
    }

    @Test
    public void testWithInvalidGraph() {
        try {
            new TriangleFinder<>(GraphBuilder.directed().build(), true);
            new TriangleFinder<>(GraphBuilder.undirected().allowsSelfLoops(true).build(), true);
        } catch (Exception e) {
            return;
        }
        Assert.fail();
    }

    @Test
    public void testCalculateClustering() throws Exception {
        TriangleFinder<StringNode> tf = new TriangleFinder<>(this.g, false);
        Assert.assertEquals(
                1.0d,
                tf.clusteringCoefficient(x),
                0.001);
        Assert.assertEquals(
                0.66666d,
                tf.clusteringCoefficient(u),
                0.001);
        this.g.removeEdge(this.u, this.v);
        tf.clusteringCoefficient(u);
        tf.avgClusteringCoefficient();
    }

    @Test
    public void testAvgCalculateClustering() throws Exception {
        TriangleFinder<StringNode> tf = new TriangleFinder<>(this.g, false);
        Assert.assertEquals(
                0.83333,
                tf.avgClusteringCoefficient(),
                0.001);
        tf = new TriangleFinder<>(this.g, true);
        Assert.assertEquals(
                0.83333,
                tf.avgClusteringCoefficient(),
                0.001);
    }

    @Test
    public void testGlobalCalculateClustering() throws Exception {
        TriangleFinder<StringNode> tf = new TriangleFinder<>(this.g, false);
        System.out.println(tf.globalClusteringCoefficient());
    }

    @Test
    public void testUniqueTriangles() throws Exception {
        TriangleFinder<StringNode> tf = new TriangleFinder<>(this.g, false);
        Set<Triangle<StringNode>> uniqueTrianglesBF = tf.uniqueTriangles(true);
        Set<Triangle<StringNode>> uniqueTriangleNoBF = tf.uniqueTriangles(false);
        Assert.assertNotEquals(uniqueTrianglesBF.size(), tf.countLocalTriangles());
        Assert.assertEquals(uniqueTrianglesBF.size(), uniqueTriangleNoBF.size());
        Assert.assertEquals(uniqueTrianglesBF.size(), 2);

        System.out.println("All local triangles...");
        tf.streamTriangles().forEach(System.out::println);
        System.out.println("Unique triangles only...");
        uniqueTrianglesBF.forEach(System.out::println);

        System.out.println("List of triangles where only filter mechanism is BloomFilter");
        tf.uniqueTrianglesBloomOnly().forEach(System.out::println);
    }

    @Ignore("For benchmarking only.")
    @Test
    public void benchmarkTriangleFinder() throws Exception {
        FBGraphReader fbGraphReader = new FBGraphReader();
        File[] fbFiles = Paths.get("testres/").toFile().listFiles((dir, name) -> name.endsWith(".csv"));
        if (fbFiles != null) {
            Arrays.stream(fbFiles).forEach(f -> {
                try {
                    System.out.println("Reading uniqueTriangles file " + f.getName());
                    fbGraphReader.read(f);
                } catch (IOException e) {
                    System.err.println("Could not read file " + f.getName());
                }
            });
        } else Assert.fail();

        Graph<FBPageNode> bigGraph = fbGraphReader.getGraph();
        System.out.println("\nGraph has been read into memory.");
        System.out.println("|V| = " + String.valueOf(bigGraph.nodes().size()) + " |E| = " + String.valueOf(bigGraph.edges().size()));

        long start;
        long end;

        TriangleFinder<FBPageNode> triangleFinder;

        //start = System.currentTimeMillis();
        //System.out.println("Starting parallel triangle finder...");
        //triangleFinder = new TriangleFinder<>(bigGraph, true);
        //System.out.println("countLocalTriangles = " + String.valueOf(triangleFinder.countLocalTriangles()));
        //System.out.println("avgClusteringCoefficient = " + triangleFinder.avgClusteringCoefficient());
        //end = System.currentTimeMillis();
        //System.out.println(
        //        "...took " + String.valueOf(end - start) + "ms"
        //);

        System.out.println("Starting sequential triangle finder...");
        start = System.currentTimeMillis();
        triangleFinder = new TriangleFinder<>(bigGraph, false);
        System.out.println("countLocalTriangles = " + String.valueOf(triangleFinder.countLocalTriangles()));
        System.out.println("avgClusteringCoefficient = " + triangleFinder.avgClusteringCoefficient());
        end = System.currentTimeMillis();
        System.out.println(
                "...took " + String.valueOf(end - start) + "ms"
        );

        //System.out.println("Finding unique triangles...");
        //start = System.currentTimeMillis();
        //System.out.println("Count of unique triangles = " + triangleFinder.uniqueTriangles(false).size());
        //end = System.currentTimeMillis();
        //System.out.println(
        //        "...took " + String.valueOf(end - start) + "ms"
        //);

        //System.out.println("Finding unique triangles with bloom filter...");
        //start = System.currentTimeMillis();
        //System.out.println("Count of unique triangles = " + triangleFinder.uniqueTriangles(false).size());
        //end = System.currentTimeMillis();
        //System.out.println(
        //        "...took " + String.valueOf(end - start) + "ms"
        //);

        //System.out.println("Finding list of triangles relying only on bloom filter...");
        //start = System.currentTimeMillis();
        //System.out.println("Count of unique triangles ≈ " + triangleFinder.uniqueTrianglesBloomOnly().size());
        //end = System.currentTimeMillis();
        //System.out.println(
        //        "...took " + String.valueOf(end - start) + "ms"
        //);
    }

}
