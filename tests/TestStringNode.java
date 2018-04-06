import com.trianglez.node.types.FBPageNode;
import com.trianglez.node.types.StringNode;
import org.junit.Assert;
import org.junit.Test;

public class TestStringNode {
    private StringNode a = new StringNode("hello");
    private StringNode b = new StringNode("hello");
    private StringNode c = new StringNode("goodbye");

    @Test
    public void testCompareTo() {
        Assert.assertEquals(a.compareTo(b), 0);
        Assert.assertEquals(a.compareTo(c), 1);
        Assert.assertEquals(c.compareTo(a), -1);
    }

    @Test
    public void testEquals() {
        Assert.assertEquals(a, b);
        Assert.assertEquals(a, a);
        Assert.assertNotEquals(a, c);
        Assert.assertNotEquals(a, new FBPageNode(1, "x"));
    }

    @Test
    public void testHashCode() {
        Assert.assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void testToString() {
        Assert.assertEquals(a.toString(), b.toString());
    }
}
