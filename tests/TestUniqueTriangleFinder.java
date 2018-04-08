import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.trianglez.Triangle;
import com.trianglez.TriangleFinder;
import com.trianglez.UniqueTriangleFinder;
import com.trianglez.node.types.StringNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

public class TestUniqueTriangleFinder {
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
    public void testFrom() throws Exception {
        TriangleFinder<StringNode> tf = new TriangleFinder<>(this.g, true);
        Set<Triangle<StringNode>> uniqueTriangles = new UniqueTriangleFinder<StringNode>().from(tf);
        Assert.assertNotEquals(uniqueTriangles.size(), tf.countLocalTriangles());
        Assert.assertEquals(uniqueTriangles.size(), 2);

        System.out.println("All local triangles...");
        tf.streamTriangles().forEach(System.out::println);
        System.out.println("Unique triangles only...");
        uniqueTriangles.forEach(System.out::println);
    }
}
