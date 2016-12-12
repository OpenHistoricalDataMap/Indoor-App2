package htw_berlin.de.mapmanager.graph;

import com.google.gson.annotations.Expose;

import java.util.HashSet;

/**
 * Created by tognitos
 */

public class Graph {
    @Expose
    private HashSet<Node> nodes;

    public Graph() {
        this.nodes = new HashSet<>();
    }

    /**
     * Adds a node to the graph
     * @return true if the node was new */
    public boolean addNode(Node node){
        return this.nodes.add(node);
    }



    public HashSet<Node> getNodes() {
        return nodes;
    }

    public Node getNodeById(String nodeId) {
        for(Node node: nodes){
            if(node.getId().equals(nodeId)){
                return node;
            }
        }

        return null;
    }
}
