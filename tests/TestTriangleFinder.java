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

public class TestTriangleFinder {
    private Graph<StringNode> g;
    private StringNode u;
    private StringNode v;
    private StringNode w;
    private StringNode x;

    @Before
    public void before() {
        MutableGraph<StringNode> g = GraphBuilder.undirected().allowsSelfLoops(false).build();
        u = new StringNode("u");
        v = new StringNode("v");
        w = new StringNode("w");
        x = new StringNode("x");
        g.putEdge(u, v);
        g.putEdge(u, w);
        g.putEdge(u, x);
        g.putEdge(v, w);
        g.putEdge(x, w);
        this.g = g;
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

    @Ignore("For benchmarking only.")
    @Test
    public void testBenchmarkParallel() throws Exception {
        FBGraphReader fbGraphReader = new FBGraphReader();
        File[] fbFiles = Paths.get("testres/").toFile().listFiles((dir, name) -> name.endsWith(".csv"));
        if (fbFiles != null) {
            Arrays.stream(fbFiles).forEach(f ->  {
                try {
                    System.out.println("Reading from file " + f.getName());
                    fbGraphReader.read(f);
                }
                catch (IOException e) {
                    System.err.println("Could not read file " + f.getName());
                }
            });
        } else Assert.fail();

        Graph<FBPageNode> bigGraph = fbGraphReader.getGraph();
        System.out.println("\nGraph has been read into memory.");
        System.out.println("|V| = " + String.valueOf(bigGraph.nodes().size()));

        long start;
        long end;

        TriangleFinder<FBPageNode> triangleFinder;
        {
            start = System.currentTimeMillis();
            System.out.println("Starting parallel triangle finder...");
            triangleFinder = new TriangleFinder<>(bigGraph, true);
            System.out.println("countLocalTriangles = " + String.valueOf(triangleFinder.countLocalTriangles()));
            end = System.currentTimeMillis();
            System.out.println(
                    "Parallel took " + String.valueOf(end - start) + "ms"
            );
        }

        {
            System.out.println("Starting sequential triangle finder...");
            start = System.currentTimeMillis();
            triangleFinder = new TriangleFinder<>(bigGraph, false);
            System.out.println("countLocalTriangles = " + String.valueOf(triangleFinder.countLocalTriangles()));
            end = System.currentTimeMillis();
            System.out.println(
                    "Sequential took " + String.valueOf(end - start) + "ms"
            );
        }
    }
}
