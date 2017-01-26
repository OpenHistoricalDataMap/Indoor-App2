package htw_berlin.de.mapmanager.graph;

import android.util.Log;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import htw_berlin.de.mapmanager.compass.Edge;
import htw_berlin.de.mapmanager.compass.WayPoint;

/**
 * Created by tognitos
 */

public class Graph {
    // ssid filter
    private String ssid;

    @Expose
    // <Id, NodeObject>
    private LinkedHashMap<String, Node> nodes;

    @Expose
    // <HashCode, EdgeObject>
    private HashMap<Integer, Edge> edges;

    public Graph() {
        this.nodes = new LinkedHashMap<>();
        this.edges = new HashMap<>();
    }


    /**
     * This method adds am edge between Vertices poiA and poiB
     * of weight 1, if no DijkstraEdge between these Vertices already
     * exists in the Graph.
     *
     * @param poiA The first vertex to add
     * @param poiB The second vertex to add
     * @return true iff no DijkstraEdge relating poiA and poiB exists in the Graph
     */
    public boolean addEdge(Node poiA, Node poiB) {
        return addEdge(poiA, poiB, null);
    }

    /**
     * Accepts poiB vertices and a weight, and adds the edge
     * ({poiA, poiB}, weight) iff no DijkstraEdge relating poiA and poiB
     * exists in the Graph.
     *
     * @param poiA The first DijkstraVertex of the DijkstraEdge
     * @param poiB The second DijkstraVertex of the DijkstraEdge
     * @param way  The list of waypoints of the DijkstraEdge
     * @return true iff no DijkstraEdge already exists in the Graph
     */
    public boolean addEdge(Node poiA, Node poiB, ArrayList<WayPoint> way) {
        // TODO Remove logs
        final String LOG_TAG = "Graph.java";

        if (poiA.equals(poiB)) {
            Log.d(LOG_TAG, "A==B");
            return false;
        }

        if (way == null) {
            Log.d(LOG_TAG, "way==null");
            way = new ArrayList<WayPoint>();
        }

        //ensures the Edge is not in the Graph
        Edge e = new Edge(poiA, poiB, way);
        if (edges.containsKey(e.hashCode())) {
            Log.d(LOG_TAG, "edge already contained: " + e);
            return false;
        }

        //and that the Edge isn't already incident to poiA of the vertices
        else if (poiA.containsEdge(e) || poiB.containsEdge(e)) {
            Log.d(LOG_TAG, "2 Nodes already connected");
            return false;
        }

        edges.put(e.hashCode(), e);
        poiA.addEdge(e);
        poiB.addEdge(e);
        return true;
    }

    public boolean containsEdge(Edge e) {
        if (e.getPoiA() == null || e.getPoiB() == null) {
            return false;
        }
        return this.edges.containsKey(e.hashCode());
    }

    /**
     * This method removes the specified DijkstraEdge from the Graph,
     * including as each vertex's incidence neighborhood.
     *
     * @param e The DijkstraEdge to remove from the Graph
     * @return DijkstraEdge The DijkstraEdge removed from the Graph
     */
    public Edge removeEdge(Edge e) {
        Log.d("DIO?", "LOL");
        boolean rA = e.getPoiA().removeEdge(e);
        boolean rB = e.getPoiB().removeEdge(e);
        System.out.println("DSHUDHSAHDUSHDHS DUSAHDUAH DHSADHUSAHDUSA dudhusa hdsahd hsdu hsduhsudhs dhsauhdus hdshdushad11111111111111111111111111111");
        Log.d("graph", e.getPoiA().toString());
        Log.d("graph", e.getPoiB().toString());
        Log.d("graph", "removed a " + rA);
        Log.d("graph", "removed b " + rB);
        return this.edges.remove(e.hashCode());
    }


    /**
     * Adds a node to the graph.
     * Note that having an ArrayList is almost forced, if we want to use this data easily for
     * the ListView Adapter in the Main Activity. Moreover, having a List guarantees the order of
     * the elements, whilst a simple HashSet (non-Linked) does not.
     *
     * @param node the node to be added
     * @return true if the node was new
     * <p>
     * /*
     * public boolean addNode(NodeInterface node) {
     * final String nodeId = node.getId();
     * for (Map.Entry<String, NodeInterface> presentNode : nodes) {
     * if (presentNode.getId().equals(nodeId)) {
     * // do not add
     * return false;
     * }
     * }
     * return this.nodes.add(node);
     * }
     */
    /*
    /**
     * Add a edge to the Graph
     *
     * @param addEdge
     * @return

    public boolean addEdge(DijkstraEdge addEdge){
        final String addEdgeId = addEdge.getId();
        for(DijkstraEdge presentEdge:edges){
            if(presentEdge.getId().equals(addEdgeId)){
                // do not add
                return false;
            }
        }
        return this.edges.add(addEdge);

    }
*/
    /*
    public void removeNode(NodeInterface toDelete) {
        // remove reference from all edges
        for (NodeInterface connectedNode : toDelete.getEdges().keySet()) {
            //NodeInterface connectedNode = this.getNode(connectedId);
            connectedNode.getEdges().remove(toDelete.getId());
        }
        // remove from the list of nodes
        this.nodes.remove(toDelete);

    }
    */
    public boolean containsNode(Node node) {
        return this.nodes.containsKey(node.getId());
    }

    public Node getNode(String nodeId) {
        return nodes.get(nodeId);
    }

    /**
     * This method adds a DijkstraVertex to the graph. If a DijkstraVertex with the same label
     * as the parameter exists in the Graph, the existing DijkstraVertex is overwritten
     * only if overwriteExisting is true. If the existing DijkstraVertex is overwritten,
     * the Edges incident to it are all removed from the Graph.
     *
     * @param node
     * @param overwriteExisting
     * @return true if node was added to the Graph
     */
    public boolean addNode(Node node, boolean overwriteExisting) {
        Node current = this.nodes.get(node.getId());
        if (current != null) {
            if (!overwriteExisting) {
                return false;
            }

            while (current.getEdgesCount() > 0) {
                this.removeEdge(current.getEdge(0));
            }
        }


        nodes.put(node.getId(), node);
        return true;
    }


    /**
     * @param nodeId The id of the NodeInterface to remove
     * @return NodeInterface The removed NodeInterface object
     */
    public Node removeNode(String nodeId) {
        Node v = nodes.remove(nodeId);

        while (v.getEdgesCount() > 0) {
            this.removeEdge(v.getEdge((0)));
        }

        return v;
    }

    /**
     * @return Set<String> The unique ids of the Graph's NodeInterface objects
     */
    public Set<String> nodesKeys() {
        return this.nodes.keySet();
    }

    public ArrayList<Node> getNodes() {
        return new ArrayList<>(nodes.values());
    }

    /**
     * @return Set<DijkstraEdge> The Edges of this graph
     */
    public Set<Edge> getEdges() {
        return new HashSet<Edge>(this.edges.values());
    }

    /**
     * Get you the DijkstraEdge between two POI`s
     *
     * @param nodeIdA,nodeIdB
     * @return public DijkstraEdge getEdgeNodeId(String nodeIdA, String nodeIdB) {
    for (DijkstraEdge s : edges) {
    if ((s.getPoiA().equals(nodeIdA)) && (s.getPoiB().equals(nodeIdB)) ||
    (s.getPoiA().equals(nodeIdB)) && (s.getPoiB().equals(nodeIdA))) {
    return s;
    }
    }

    return null;
    }
     */

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }
}
