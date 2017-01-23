package htw_berlin.de.mapmanager.graph.dijkstra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import htw_berlin.de.mapmanager.compass.Edge;
import htw_berlin.de.mapmanager.graph.Graph;
import htw_berlin.de.mapmanager.graph.Node;

class DijkstraVertex {

    private final Node node;
    private final String id;

    public DijkstraVertex(Node node) {
        this.node = node;
        this.id = node.getId();
    }
    public String getId() {
        return id;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DijkstraVertex other = (DijkstraVertex) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return id;
    }

    public Node getNode() {
        return node;
    }
}

class DijkstraEdge {
    private final DijkstraVertex source;
    private final DijkstraVertex destination;
    private final double weight;

    public DijkstraEdge(DijkstraVertex source, DijkstraVertex destination, double weight) {
        this.source = source;
        this.destination = destination;
        this.weight = weight == 0.0?15000.0:weight;
    }

    public DijkstraVertex getDestination() {
        return destination;
    }

    public DijkstraVertex getSource() {
        return source;
    }
    public double getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return source + " " + destination;
    }


}


/**
 * Created by tognitos on 22.01.17.
 *
 * Dijkstra algorithm uses maps the common Node and Edge objects to its own DijkstraVertex and
 * DijkstraEdge objects in order to avoid loading the model objects with the algorithm's logic.
 * It is important that methods from the Node and Edge classes (of the Model) are avoided.
 * This allows us to change just how the mapping to the Dijkstra's Vertices and Edges work.
 * "Prinzip der lose Kopplung"
 */
public class DijkstraAlgorithm {

    // nodes and edges
    private final List<DijkstraVertex> nodes;
    private final List<DijkstraEdge> edges;
    // graph reference
    private final Graph graph;

    // settled and unsettled nodes (seen / not seen)
    private Set<DijkstraVertex> settledNodes;
    private Set<DijkstraVertex> unSettledNodes;

    // (smallest) predecessors
    private Map<DijkstraVertex, DijkstraVertex> predecessors;

    // smallest distance (weight) for the node
    private Map<DijkstraVertex, Double> distance;

    public DijkstraAlgorithm(Graph graph) {
        this.graph = graph;

        // create a copy of the array so that we can operate on this array
        this.nodes = mapNodes(graph.getNodes());
        this.edges = mapEdges(graph.getEdges());
    }

    /**
     * Map the normal Node objects from the Model to the custom Vertex of the Dijkstra Algorithm.
     * This is done to avoid filling the model objects with logic elements.
     * @param nodes
     * @return
     */
    private List<DijkstraVertex> mapNodes(ArrayList<Node> nodes) {
        ArrayList<DijkstraVertex> vertices = new ArrayList<>(nodes.size());
        for(Node node : nodes){
            vertices.add(new DijkstraVertex(node));
        }
        return vertices;
    }

    /**
     * Map the normal Edge objects from the Graph model to the custom Edge of the Dijkstra
     * algorithm. This is done in order to avoid filling the model objects with logic elements.
     * Since the graph is bidirectional, it maps 2 edges: one for poiA->poiB, one for poiB->poiA
     * @param edges
     * @return
     */
    private List<DijkstraEdge> mapEdges(Set<Edge> edges) {
        ArrayList<DijkstraEdge> dijkstraEdges = new ArrayList<>(edges.size());
        for(Edge edge : edges){
            // since it is an undirected graph, add both directions
            DijkstraVertex source = new DijkstraVertex(edge.getPoiA());
            DijkstraVertex destination = new DijkstraVertex(edge.getPoiB());

            DijkstraEdge sourceToDestination = new DijkstraEdge(source, destination, edge.getWeight());
            DijkstraEdge destinationToSource = new DijkstraEdge(destination, source, edge.getWeight());

            dijkstraEdges.add(sourceToDestination);
            dijkstraEdges.add(destinationToSource);
        }
        return dijkstraEdges;
    }

    /**
     * Executes all the calculations and the shortest paths from the specified source node.
     * @param sourceNodeId
     * @throws IllegalArgumentException if the sourcenodeid does not exist
     */
    public void execute(String sourceNodeId) throws IllegalArgumentException {
        final Node sourceNode = graph.getNode(sourceNodeId);
        if(sourceNode == null){
            throw new IllegalArgumentException("Source Node Id is invalid! Given was:" + sourceNodeId);
        }
        final DijkstraVertex sourceVertex = new DijkstraVertex(graph.getNode(sourceNodeId));

        settledNodes = new HashSet<>();
        unSettledNodes = new HashSet<>();
        distance = new HashMap<>();
        predecessors = new HashMap<>();
        distance.put(sourceVertex, 0.0);
        unSettledNodes.add(sourceVertex);
        while (unSettledNodes.size() > 0) {
            DijkstraVertex node = getMinimum(unSettledNodes);
            settledNodes.add(node);
            unSettledNodes.remove(node);
            findMinimalDistances(node);
        }
    }

    /**
     * Find the minimal distance from a node
     * @param node
     */
    private void findMinimalDistances(DijkstraVertex node) {
        List<DijkstraVertex> adjacentNodes = getNeighbors(node);
        for (DijkstraVertex target : adjacentNodes) {
            if (getShortestDistance(target) > getShortestDistance(node)
                    + getDistance(node, target)) {
                distance.put(target, getShortestDistance(node)
                        + getDistance(node, target));
                predecessors.put(target, node);
                unSettledNodes.add(target);
            }
        }

    }

    /**
     * Get the distance between the two vertices
     * @param node
     * @param target
     * @return
     */
    private double getDistance(DijkstraVertex node, DijkstraVertex target) {
        for (DijkstraEdge dijkstraEdge : edges) {
            if (dijkstraEdge.getSource().getNode().equals(node.getNode())
                    && dijkstraEdge.getDestination().getNode().equals(target.getNode())) {
                return dijkstraEdge.getWeight();
            }
        }
        throw new RuntimeException("Should not happen");
    }

    /**
     * Get all the neighbours (directly connected nodes) for the specified node.
     * @param node
     * @return
     */
    private List<DijkstraVertex> getNeighbors(DijkstraVertex node) {
        List<DijkstraVertex> neighbors = new ArrayList<DijkstraVertex>();
        for (DijkstraEdge dijkstraEdge : edges) {
            if (dijkstraEdge.getSource().equals(node)
                    && !isSettled(dijkstraEdge.getDestination())) {
                neighbors.add(dijkstraEdge.getDestination());
            }
        }
        return neighbors;
    }

    /**
     * Get the minimum shortest distance of all the vertices.
     * @param dijkstraVertices
     * @return
     */
    private DijkstraVertex getMinimum(Set<DijkstraVertex> dijkstraVertices) {
        DijkstraVertex minimum = null;
        for (DijkstraVertex dijkstraVertex : dijkstraVertices) {
            if (minimum == null) {
                minimum = dijkstraVertex;
            } else {
                if (getShortestDistance(dijkstraVertex) < getShortestDistance(minimum)) {
                    minimum = dijkstraVertex;
                }
            }
        }
        return minimum;
    }

    /**
     *
     * @param dijkstraVertex
     * @return true if the vertex was already settled
     */
    private boolean isSettled(DijkstraVertex dijkstraVertex) {
        return settledNodes.contains(dijkstraVertex);
    }

    /**
     * Gets the shortest distance to the destination, from the calculated start node (called through
     * the method execute)
     * @param destination
     * @return
     */
    private double getShortestDistance(DijkstraVertex destination) {
        Double d = distance.get(destination);
        if (d == null) {
            return Double.MAX_VALUE;
        } else {
            return d;
        }
    }

    /**
     * This method returns the path from the source to the selected target and
     * NULL if no path exists
     */
    public LinkedList<Node> getPath(String targetSourceId) {
        LinkedList<Node> path = new LinkedList<>();

        DijkstraVertex step = new DijkstraVertex(graph.getNode(targetSourceId));
        // check if a path exists
        if (predecessors.get(step) == null) {
            return null;
        }
        path.add(step.getNode());
        while (predecessors.get(step) != null) {
            step = predecessors.get(step);
            path.add(step.getNode());
        }
        // Put it into the correct order
        Collections.reverse(path);
        return path;
    }

}