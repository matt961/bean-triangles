import com.trianglez.node.types.FBPageNode;
import com.trianglez.node.types.StringNode;
import org.junit.Assert;
import org.junit.Test;

public class TestFBPageNode {
    private FBPageNode fbp1 = new FBPageNode(1, "Sports");
    private FBPageNode fbp2 = new FBPageNode(2, "Cooking");
    private FBPageNode fbp3 = new FBPageNode(2, "Cooking");

    @Test
    public void testHashCode() {
        Assert.assertNotEquals(fbp1.hashCode(), fbp2.hashCode());
        Assert.assertEquals(fbp2.hashCode(), fbp3.hashCode());
    }

    @Test
    public void testEquals() {
        Assert.assertNotEquals(fbp1, fbp2);
        Assert.assertEquals(fbp2, fbp3);
        Assert.assertEquals(fbp1, fbp1);
        Assert.assertNotEquals(fbp1, new StringNode(""));
    }

    @Test
    public void testGetters() {
        Assert.assertEquals(fbp1.getId(), 1);
        Assert.assertEquals(fbp1.getCategory(), "Sports");
    }

    @Test
    public void testCompareTo() {
        Assert.assertEquals(fbp1.compareTo(fbp2), -1);
        Assert.assertEquals(fbp2.compareTo(fbp3), 0);
        Assert.assertEquals(fbp2.compareTo(fbp1), 1);
    }

    @Test
    public void testToString() {
        Assert.assertEquals(fbp1.toString(), "1 of Sports");
    }
}
