package htw_berlin.de.mapmanager.graph;

import java.util.List;


public class Main {

	public static void main(String[] args) {
		/*
		TranslatableAdjacencyMatrixGraph graph = new TranslatableAdjacencyMatrixGraph(
				"places_net.txt",
				"places.properties");

		graph.print(System.out);
		printTotalWeight(graph);
		System.out.println("");
		System.out.println("");


		Node aufgangA = graph.getNodes().get(0);
		Node u5_honoew = graph.getNodes().get(4);
		
		List<Node> path = graph.getShortestPath(aufgangA, u5_honoew);
		for(Node n : path){
			System.out.println(graph.getNodeAsText(n));
		}
		*/

	}

	public static void printArray(Object[] array) {
		for (Object o : array) {
			System.out.println(String.format("%s", o));
		}
	}

	public static void printEdgesArray(Edge[] array) {
		for (Edge e : array) {
			System.out.println(String.format("%s", e));
		}
	}

	public static void printTotalWeight(AbstractGraph graph) {
		int graphWeight = (int) (graph.getWeight()/2); // undirected graph
		System.out.println(String.format("Total weight: %d", graphWeight));
	}
}
