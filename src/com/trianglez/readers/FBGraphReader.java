package com.trianglez.readers;

import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.trianglez.node.types.FBPageNode;

import java.io.*;

public class FBGraphReader {
    private MutableGraph<FBPageNode> graph;

    /**
     * Initializes a {@link MutableGraph} to put edges into.
     */
    public FBGraphReader() {
        this.graph = GraphBuilder.undirected().allowsSelfLoops(false).build();
    }

    public FBGraphReader read(File graphFile) throws IOException {
        String category = graphFile.getName().split("_")[0];
        BufferedReader f = new BufferedReader(new FileReader(graphFile));
        f.lines().skip(1).forEach(line -> {
            String[] ids = line.split(",");
            if (ids.length < 2)
                return; // skip bad input

            int node1ID = Integer.parseInt(ids[0]);
            int node2ID = Integer.parseInt(ids[1]);

            FBPageNode node1 = new FBPageNode(node1ID, category);
            FBPageNode node2 = new FBPageNode(node2ID, category);

            // gets category of the FB page from file name
            if (!node1.equals(node2))
                this.graph.putEdge(node1, node2);
        });
        f.close();
        return this;
    }

    public Graph<FBPageNode> getGraph() {
        return this.graph;
    }
}
