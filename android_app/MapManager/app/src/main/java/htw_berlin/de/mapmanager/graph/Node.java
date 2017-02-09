package htw_berlin.de.mapmanager.graph;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

import de.htwberlin.f4.ai.ma.fingerprint.NodeInterface;
import de.htwberlin.f4.ai.ma.fingerprint.SignalInformationInterface;
import htw_berlin.de.mapmanager.compass.Edge;

/**
 * Created by Carola Walter
 * Changed by Christoph Bose, tognitos
 */

public class Node implements NodeInterface {
    @Expose
    String id;

    @Expose
    float zValue;

    @Expose
    // TODO Florian fhausler Hausler: change Integer to Streak or Path or "Strecke" or whatsoever
    private ArrayList<Edge> edges; // <toNodeId, meters>, Linked preserves order

    //private ArrayList<WayPoint> way = new ArrayList<>();

    @Expose
    List<SignalInformation> signalInformationList;



    public Node(String id, float zValue, List<SignalInformation> signalInformationList, ArrayList<Edge> edges) {
        this.id = id;
        this.zValue = zValue;
        this.signalInformationList = signalInformationList;
        this.edges = edges;
    }

    public Node(String id, float zValue, List<SignalInformation> signalInformationList) {
        this(id, zValue, signalInformationList, new ArrayList<Edge>());
    }


    public Node(String id){
        this(id, 0, new ArrayList<SignalInformation>());
    }

    public void addEdge(Edge edge){
        if(this.edges.contains(edge)){
            return;
        }

        this.edges.add(edge);
    }


    /**
     *
     * @param index The index of the DijkstraEdge to retrieve
     * @return DijkstraEdge The DijkstraEdge at the specified index in this.neighborhood
     */
    public Edge getEdge(int index){
        return this.edges.get(index);
    }

    /**
     * Returns the DijkstraEdge between this node and the specified destination node.
     * @param toNode the destination NodeInterface to which this node is connected
     * @return DijkstraEdge the edge that connects this node to the destination node, or null if there is no edge between
     */
    public Edge getEdge(Node toNode){
        if(toNode == null){
            return null;
        }

        for(Edge edge : edges){
            if(edge.getNeighbor(this).equals(toNode)){
                return edge;
            }
        }
        return null;
    }

    public boolean containsEdge(Edge other){
        return this.edges.contains(other);
    }

    public boolean removeEdge(Edge e){
        return this.edges.remove(e);
    }

    public int getEdgesCount(){
        return this.edges.size();
    }

    /**
     *
     * @return copy of edges
     */
    public ArrayList<Edge> getEdges() {
        return new ArrayList<Edge>(edges);
    }

    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }



    @Override
    public List<SignalInformation> getSignalInformationList() {
        return new ArrayList<>(signalInformationList);
    }

    /**
     * Set a COPY of the passed List as the new signalInformationList
     * @param signalInformationList
     */
    public void setSignalInformationList(List<SignalInformation> signalInformationList) {
        this.signalInformationList = new ArrayList<>(signalInformationList);
    }


    @Override
    public String toString() {
        return "NodeInterface " + id;
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Node)){
            return false;
        }
        Node node = (Node) other;
        return this.id.equals(((Node) other).id);
    }
}
