package htw_berlin.de.mapmanager.compass;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

import htw_berlin.de.mapmanager.graph.Node;

/**
 * Created by florianhausler on 14.01.17.
 */

public class Edge {



    @Expose
    private float cost;



    @Expose
    private String poiA;

    @Expose
    private String poiB;

    @Expose
    private ArrayList<WayPoint> way;

    @Expose
    private String id=poiA+poiB;

    public Edge(float cost, String poiA, String poiB, String id, ArrayList<WayPoint> way) {
        this.cost = cost;
        this.poiA = poiA;
        this.poiB = poiB;
        this.id = id;
        this.way = way;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public ArrayList<WayPoint> getWay() {
        return way;
    }

    public void setWay(ArrayList<WayPoint> way) {
        this.way = way;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public String getPoiA() {
        return poiA;
    }

    public void setPoiA(String poiA) {
        this.poiA = poiA;
    }

    public String getPoiB() {
        return poiB;
    }

    public void setPoiB(String poiB) {
        this.poiB = poiB;
    }


    public String toString() {
        return "Edge{" +
                "cost=" + cost +
                ", poiA='" + poiA + '\'' +
                ", poiB='" + poiB + '\'' +
                ", id='" + id + '\'' +
                ", way=" + way +
                '}';
    }
}
