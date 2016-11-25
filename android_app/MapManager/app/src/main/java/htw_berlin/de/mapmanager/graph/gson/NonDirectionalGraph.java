package htw_berlin.de.mapmanager.graph.gson;

import java.io.InputStream;
import java.util.List;

public interface NonDirectionalGraph {
	void loadFromInputStream(InputStream inputStream);

	int nodesCount();

	int edgesCount();

	List<Node> getNodes();

	List<Edge> getEdges(Node node);
}