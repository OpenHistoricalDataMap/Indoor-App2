package htw_berlin.de.mapmanager.graph;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Carola Walter
 * Changed by Christoph Bose, tognitos
 */

public class Node {
    @Expose
    String id;

    @Expose
    float zValue;

    @Expose
    private HashMap<String, Integer> edges; // <toNodeId, meters>

    @Expose
    List<SignalInformation> signalInformationList;
    private MarkableNode markedPredecessor;
    private int markedLength;


    public static class SignalInformation {
        @Expose
        String timestamp;

        @Expose
        List<SignalStrenghtInformation> signalStrenghtInformationList;

        public SignalInformation(String timestamp, List<SignalStrenghtInformation> signalStrenghtInformationList) {
            this.timestamp = timestamp;
            this.signalStrenghtInformationList = signalStrenghtInformationList;
        }
    }

    public static class SignalStrenghtInformation {
        @Expose
        String macAdress;

        @Expose
        int signalStrength;

        public SignalStrenghtInformation(String macAdress, int signalStrength) {
            this.macAdress = macAdress;
            this.signalStrength = signalStrength;
        }
    }


    public Node(String id, float zValue, List<SignalInformation> signalInformationList, HashMap<String, Integer> edges) {
        this.id = id;
        this.zValue = zValue;
        this.signalInformationList = signalInformationList;
        this.edges = edges;
    }

    public Node(String id, float zValue, List<SignalInformation> signalInformationList) {
        this(id, zValue, signalInformationList, new HashMap<String, Integer>());
    }

    public Node(String id){
        this(id, 0, new ArrayList<SignalInformation>());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getzValue() {
        return zValue;
    }

    public void setzValue(float zValue) {
        this.zValue = zValue;
    }

    public List<SignalInformation> getSignalInformationList() {
        return signalInformationList;
    }

    public void setSignalInformationList(List<SignalInformation> signalInformationList) {
        this.signalInformationList = signalInformationList;
    }

    public HashMap<String, Integer> getEdges() {
        return edges;
    }

    public void setEdges(HashMap<String, Integer> edges) {
        this.edges = edges;
    }
}
