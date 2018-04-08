import com.google.common.graph.Graph;
import com.trianglez.readers.FBGraphReader;
import com.trianglez.node.types.FBPageNode;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class TestGraphReader {

    /**
     * See <a href=https://snap.stanford.edu/data/gemsec_facebook_dataset.html>SNAP Facebook Dataset</a> for stats on
     * the graph data used for testing.
     *
     * This test will estimate the number of edges within an error of 1000 because self-referential edges get dropped.
     * @throws IOException The file might not exist or something?
     */
    @Test
    public void testFBPageReader() throws IOException {
        FBGraphReader graphReader = new FBGraphReader();
        File fbFile2 = Paths.get("testres/artist_edges.csv").toFile();
        graphReader.read(fbFile2);
        Graph<FBPageNode> g = graphReader.getGraph();
        Assert.assertEquals(g.nodes().size(), 50515);
        Assert.assertTrue(819306 - g.edges().size() < 1000);
    }

}
