package htw_berlin.de.mapmanager.graph;

import com.google.gson.annotations.Expose;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by tognitos
 */

public class Graph {
    @Expose
    private ArrayList<Node> nodes;

    public Graph() {
        this.nodes = new ArrayList<>();
    }

    /**
     * Adds a node to the graph.
     * Note that having an ArrayList is almost forced, if we want to use this data easily for
     * the ListView Adapter in the Main Activity. Moreover, having a List guarantees the order of
     * the elements, whilst a simple HashSet (non-Linked) does not.
     *
     * @param node the node to be added
     * @return true if the node was new */
    public boolean addNode(Node node){
        final String nodeId = node.getId();
        for(Node presentNode:nodes){
            if(presentNode.getId().equals(nodeId)){
                // do not add
                return false;
            }
        }
        return this.nodes.add(node);
    }

    public void removeNode(Node toDelete){
        // remove reference from all edges
        for(String connectedId : toDelete.getEdges().keySet()){
            Node connectedNode = this.getNodeById(connectedId);
            connectedNode.getEdges().remove(toDelete.getId());
        }
        // remove from the list of nodes
        this.nodes.remove(toDelete);

    }

    public ArrayList<Node> getNodes() {
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
