package htw_berlin.de.mapmanager.graph.gson;

import com.google.gson.annotations.SerializedName;

public class Edge implements Comparable<Edge> {
	@SerializedName("node1")
	private Node node1;

	@SerializedName("node2")
	private Node node2;

	@SerializedName("weight")
	private int weight;

	@SerializedName("barrierefrei")
	private boolean barrierefrei = false;

	public Edge(Node node1, Node node2, int weight) {
		this.node1 = node1;
		this.node2 = node2;
		this.weight = weight;
        // TODO barriereFrei implementieren (in txt Datei als {weight, barrierefrei} repraesentieren)
		this.barrierefrei = barrierefrei;
	}

	public Node getNode1() {
		return node1;
	}

	public void setNode1(Node node1) {
		this.node1 = node1;
	}

	public Node getNode2() {
		return node2;
	}

	public void setNode2(Node node2) {
		this.node2 = node2;
	}

	public int getWeight() {
		return this.weight;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Edge))
			return false;

		Edge edge = (Edge) o;

		if (weight != edge.weight)
			return false;
		if (node1 != null ? !node1.equals(edge.node1) : edge.node1 != null)
			return false;
		if (node2 != null ? !node2.equals(edge.node2) : edge.node2 != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = node1 != null ? node1.hashCode() : 0;
		result = 31 * result + (node2 != null ? node2.hashCode() : 0);
		result = 31 * result + weight;
		return result;
	}

	@Override
	public int compareTo(Edge o) {
		if (this.equals(o))
			return 0;
		else if (this.weight < o.weight)
			return -1;
		else
			return 1;
	}

	public boolean isBarrierefrei() {
		return barrierefrei;
	}

	public void setBarrierefrei(boolean barrierefrei) {
		this.barrierefrei = barrierefrei;
	}
}
