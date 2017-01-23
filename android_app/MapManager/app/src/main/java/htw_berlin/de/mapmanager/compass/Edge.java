package htw_berlin.de.mapmanager.compass;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

import htw_berlin.de.mapmanager.graph.Node;

/**
 * Created by florianhausler on 14.01.17.
 */

public class Edge implements Comparable<Edge>{


    @Expose
    //private String poiA;
    private Node poiA;

    @Expose
    //private String poiB;
    private Node poiB;

    @Expose
    private ArrayList<WayPoint> way;

    @Expose
    private String id;

    @Expose
    private double weight;

    public Edge(Node poiA, Node poiB){
        this(poiA, poiB, new ArrayList<WayPoint>());
    }

    public Edge(Node poiA, Node poiB, ArrayList<WayPoint> way) {
        // Add the node with lower id as the first node.
        // This way just one DijkstraEdge is needed because the connection will be bidirectional
        this.poiA = (poiA.getId().compareTo(poiB.getId()) <= 0) ? poiA : poiB;
        this.poiB = (this.poiA == poiA) ? poiB : poiA;

        this.id = poiA.getId() + "-" + poiB.getId();
        setWay(way);
    }

    public String getId() {
        return id;
    }


    public Node getNeighbor(Node current){
        if(!(current.equals(poiA) || current.equals(poiB))){
            return null;
        }

        return (current.equals(poiA))? poiB : poiA;
    }

    /**
     * Note that the compareTo() method deviates from
     * the specifications in the Comparable interface. A
     * return value of 0 does not indicate that this.equals(other).
     * The equals() method checks the DijkstraVertex endpoints, while the
     * compareTo() is used to compare DijkstraEdge weights099
     *
     * @param other The DijkstraEdge to compare against this
     * @return int this.weight - other.weight
     */
    public int compareTo(Edge other){
        return (int)(this.weight - other.weight);
    }


    /*
    public void setId(String id) {
        this.id = id;
    }
    */

    /*
    public ArrayList<WayPoint> getWay() {
        return way;
    }
*/
    /*
    public void setWay(ArrayList<WayPoint> way) {
        this.way = way;
    }
    */

    /**
     * Gets the weight.
     * Attention, do not create a setWeight, since the weight depends on the distance of the way.
     * @return
     */
    public double getWeight() {
        return weight;
    }

    public Node getPoiA() {
        return poiA;
    }

    public Node getPoiB() {
        return poiB;
    }

    public String toString() {
        return "Edge{" +
                "weight=" + weight +
                ", poiA='" + poiA + '\'' +
                ", poiB='" + poiB + '\'' +
                ", id='" + id + '\'' +
                ", way=" + way +
                '}';
    }

    /*
    public boolean isShorterThan(DijkstraEdge other){
        return (this.getWeight() - other.getWeight()) < 0;
    }
    */

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public void setWay(ArrayList<WayPoint> way) {
        this.way = new ArrayList<WayPoint>(way);

        // update weight
        double totalWeight = 0.0;
        for(WayPoint wayPoint : way){
            totalWeight += wayPoint.getDistance();
        }
        this.weight = totalWeight;

    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Edge)){
            return false;
        }
        Edge e = (Edge) other;
        return e.getPoiA().equals(this.getPoiA()) &&
                e.getPoiB().equals(this.getPoiB());
    }
}
