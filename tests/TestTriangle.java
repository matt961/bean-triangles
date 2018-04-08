import com.trianglez.Triangle;
import com.trianglez.node.types.StringNode;
import org.junit.Assert;
import org.junit.Test;

public class TestTriangle {
    private Triangle<StringNode> triangle1 = new Triangle<>(
            new StringNode("One"), new StringNode("Two"), new StringNode("Three"));
    private Triangle<StringNode> triangle2 = new Triangle<>(
            new StringNode("One"), new StringNode("Two"), new StringNode("Three"));
    private Triangle<StringNode> triangle3 = new Triangle<>(
            new StringNode("Two"), new StringNode("Three"), new StringNode("Four"));
    private Triangle<StringNode> triangle4 = new Triangle<>(
            new StringNode("Three"), new StringNode("Two"), new StringNode("One"));

    @Test
    public void testEquals() {
        Assert.assertEquals(triangle1, triangle2);
        Assert.assertNotEquals(triangle1, triangle3);
        Assert.assertEquals(triangle1, triangle4);
        Assert.assertEquals(triangle1, triangle1);
        Assert.assertNotEquals(triangle1, "");
    }

    @Test
    public void testGetNodes() {
        Assert.assertTrue(triangle1.getNodes().containsAll(triangle2.getNodes()));
        Assert.assertTrue(triangle1.getNodes().containsAll(triangle4.getNodes()));
        Assert.assertFalse(triangle1.getNodes().containsAll(triangle3.getNodes()));
    }

    @Test
    public void testToString() {
        Assert.assertEquals(triangle1.toString(),"(One,Three,Two)");
        Assert.assertEquals(triangle3.toString(),"(Four,Three,Two)");
        Assert.assertNotEquals(triangle1.toString(), triangle3.toString());
    }
}
