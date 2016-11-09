package htw_berlin.de.mapmanager.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class Node implements MarkableNode, Dijkstrable {
	public final int id;
	private List<Edge> edges;
	private Node markedPredecessor;
	private int markedLength = 0;

	public Node(int id) {
		this.id = id;
	}

	public void addEdge(Node node, int weight) {
		if (edges == null)
			edges = new ArrayList<Edge>();
		edges.add(new Edge(this, node, weight));
	}

	public List<Edge> getEdges() {
		if (edges == null)
			edges = new ArrayList<Edge>();
		return edges;
	}

	public boolean hasEdgeToNode(int nodeId){
        return getEdgeToNode(nodeId) != null;
    }

    public Edge getEdgeToNode(int nodeId){
        for(Edge edge:getEdges()){
            if(edge.getNode2().id == nodeId){
                return edge;
            }
        }
        return null;
    }

    /**
     *
     * @param nodeId
     * @return true if an element was removed
     */
    public boolean removeEdgeToNode(int nodeId) {
        ListIterator<Edge> iterator = getEdges().listIterator();
        Edge e;
        while(iterator.hasNext()){
            e = iterator.next();
            if(e.getNode2().id == nodeId){
                iterator.remove();
                return true;
            }
        }
        return false;
    }

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Node))
			return false;

		Node node = (Node) o;

		if (id != node.id)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return id;
	}

	/**
	 * Sets the predecessor node used as mark for this node and sets the total
	 * length of the path to get to this node
	 * */
	@Override
	public void setPredecessor(MarkableNode predecessor, int edgeWeight) {
		this.markedPredecessor = (Node) predecessor;
		if (predecessor == null) // this node is the starting node
			return;
		this.markedLength = predecessor.getMarkedLength() + edgeWeight;
	}

	@Override
	public MarkableNode getPredecessor() {
		return this.markedPredecessor;
	}

	public boolean hasPredecessor() {
		return this.markedPredecessor != null;
	}

	/**
	 * Gets the marked length to get to this node
	 * */
	@Override
	public int getMarkedLength() {
		return this.markedLength;
	}

	@Override
	public MarkableNode getShortestPath(MarkableNode targetNode) {
		if (this.equals(targetNode)) {
			// the node we were searching for is this node self, so just return
			// ourselves
			return this;
		}

		// get the edges of this node
		List<Edge> edges = getEdges();
		// sort the edges, putting the smallest first, we will try with the
		// smallest edge first
		edges.sort(new EdgeComparator());

		setPredecessorIfNecessary(edges);

		for (int i = 0; i < edges.size(); i++) {
			Edge edge = edges.get(i);
			Node destinationNode = edge.getNode2();
			if (!destinationNode.hasPredecessor()) // is the starting node, from
													// which we arrived
				continue;
			if (destinationNode.getPredecessor().equals(this)) {
				MarkableNode path = destinationNode.getShortestPath(targetNode);
				if (path == null)
					continue;
				else
					return path;
			}
		}

		return null;

	}

	public void setPredecessorIfNecessary(List<Edge> edges) {
		for (Edge edge : edges) {
			int newMarkedLength = this.getMarkedLength() + edge.getWeight();
			Node destinationNode = edge.getNode2();
			if (destinationNode.hasPredecessor()) {
				Node predecessor = (Node) destinationNode.getPredecessor();
				if (newMarkedLength < predecessor.getMarkedLength())
					destinationNode.setPredecessor(this, edge.getWeight());
				else {
					continue;
				}
			} else {
				if (!this.hasPredecessor()) {
					// this node is the starting one, no risk of a cycle
					destinationNode.setPredecessor(this, edge.getWeight());
					continue;
				}

				if (!destinationNode.hasPredecessor() && !this.getPredecessor().equals(destinationNode)) {
					destinationNode.setPredecessor(this, edge.getWeight());
				}
			}
		}
	}

	public static void printEdges(List<Edge> edges2) {
		for (Edge e : edges2) {
			System.out.println((String.format("%s => %s : %d", e.getNode1(), e.getNode2(), e.getWeight())));
		}
	}

}
