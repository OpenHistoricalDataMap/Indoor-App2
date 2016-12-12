package htw_berlin.de.mapmanager.graph;

/**
 * Created by tognitos on 12.12.16.
 */

public interface Dijkstrable extends MarkableNode {
    public MarkableNode getShortestPath(MarkableNode targetNode);
}
