package htw_berlin.de.mapmanager.graph;

/**
 * Created by tognitos on 12.12.16.
 */
public interface MarkableNode {
    void setPredecessor(MarkableNode predecessor, int edgeWeight);

    public MarkableNode getPredecessor();

    public int getMarkedLength();
}
