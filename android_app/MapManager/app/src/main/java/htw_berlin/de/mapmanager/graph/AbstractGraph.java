package htw_berlin.de.mapmanager.graph;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGraph implements NonDirectionalGraph {
	//@SerializedName("nodes")
	protected List<Node> nodes;

    /** Used to assign a new incremental ID to the node*/
	//@SerializedName("lastNodeId")
    protected int lastNodeId;

	@Override
	public int nodesCount() {
		return nodes.size();
	}

	@Override
	public int edgesCount() {
		int cnt = 0;
		for (Node node : nodes)
			cnt += node.getEdges().size();

		// divide by 2 to delete double way edges
		return cnt / 2;
	}

	@Override
	public List<Node> getNodes() {
		return nodes;
	}

	@Override
	public List<Edge> getEdges(Node node) {
		return node.getEdges();
	}

	protected static int[] convertToIntArray(String[] strings) {
		int[] result = new int[strings.length];
		for (int i = 0; i < strings.length; i++) {
			try {
				result[i] = Integer.parseInt(strings[i]);
			} catch (NumberFormatException nfe) {
				result[i] = -1;
			}
		}
		return result;
	}

	/**
	 * Gets the weight of the whole graph
	 * */
	public long getWeight() {
		
		long totalWeight = 0;
		for (Node n : nodes)
			for (Edge e : getEdges(n))
				totalWeight += e.getWeight();
		return totalWeight;
	}

	/**
	 * Gets the shortest path to a node
	 * 
	 * @return - empty ArrayList if the start node is equal the target node -
	 *         null if there isn't such a path connecting the two nodes - list
	 *         of edges that connect this two nodes in the shortest possible way
	 * */
	/**
	 * @param startNode
	 * @param targetNode
	 * @return
	 */
	public List<Node> getShortestPath(Node startNode, Node targetNode) {
		if (startNode == null || targetNode == null) {
			throw new IllegalArgumentException(String.format("Either startNode or targetNode is null or invalid"));
		}
		if (startNode.equals(targetNode)) {
			return new ArrayList<Node>();
		}

		startNode.setPredecessor(null, 0);
		MarkableNode path = startNode.getShortestPath(targetNode);
		if (path == null) // no path found
			return null;
		ArrayList<Node> pathNodes = new ArrayList<Node>();
		MarkableNode pathNode = path;

		while (pathNode != null) {
			pathNodes.add((Node) pathNode);
			pathNode = pathNode.getPredecessor();
		}
		return pathNodes;
	}

	public Node getNodeById(int nodeId) {
		for(Node n: getNodes()){
            if(n.id == nodeId){
                return n;
            }
        }
        return null;
	}
}
