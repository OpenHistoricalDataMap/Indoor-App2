package htw_berlin.de.mapmanager.graph;

public interface MarkableNode {
	void setPredecessor(MarkableNode predecessor, int edgeWeight);

	public MarkableNode getPredecessor();

	public int getMarkedLength();
}
