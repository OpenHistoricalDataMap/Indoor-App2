package htw_berlin.de.mapmanager.graph;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

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
    private LinkedHashMap<String, Integer> edges; // <toNodeId, meters>, Linked preserves order

    @Expose
    List<SignalInformation> signalInformationList;
    private MarkableNode markedPredecessor;
    private int markedLength;


    public static class SignalInformation {
        @Expose
        String timestamp;

        @Expose
        List<SignalStrengthInformation> signalStrengthInformationList;

        public SignalInformation(String timestamp, List<SignalStrengthInformation> signalStrengthInformationList) {
            this.timestamp = timestamp;
            this.signalStrengthInformationList = signalStrengthInformationList;
        }
    }

    public static class SignalStrengthInformation {
        @Expose
        String macAdress;

        @Expose
        int signalStrength;

        public SignalStrengthInformation(String macAdress, int signalStrength) {
            this.macAdress = macAdress;
            this.signalStrength = signalStrength;
        }
    }


    public Node(String id, float zValue, List<SignalInformation> signalInformationList, LinkedHashMap<String, Integer> edges) {
        this.id = id;
        this.zValue = zValue;
        this.signalInformationList = signalInformationList;
        this.edges = edges;
    }

    public Node(String id, float zValue, List<SignalInformation> signalInformationList) {
        this(id, zValue, signalInformationList, new LinkedHashMap<String, Integer>());
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

    public LinkedHashMap<String, Integer> getEdges() {
        return edges;
    }

    public void setEdges(LinkedHashMap<String, Integer> edges) {
        this.edges = edges;
    }
}
