package htw_berlin.de.mapmanager.graph.gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;

public class TranslatableAdjacencyMatrixGraph extends AdjacencyMatrixGraph {
	private InputStream propertiesInputStream;
	private Properties properties;

	public TranslatableAdjacencyMatrixGraph(InputStream fileToLoad, InputStream propertiesFile) {
		super();
		this.loadFromInputStream(fileToLoad);
		this.propertiesInputStream = propertiesFile;
		loadProperties();
	}

	public String getNodeAsText(Node n) {
		return this.properties.getProperty(String.valueOf(n.id));
	}


	private void loadProperties() {

		this.properties = new Properties();
		InputStream input = null;

		try {

			input = this.propertiesInputStream;

			// load a properties file
			this.properties.load(input);

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

    /**
     * Adds and returns the node element
     * @param nodeName
     * @return the added node element
     */
	public Node addNewNode(String nodeName) {
		lastNodeId++;
        System.out.println("lastNodeId: " +lastNodeId);
		Node newNode = new Node(lastNodeId);
        properties.setProperty(String.valueOf(lastNodeId), nodeName);
		this.nodes.add(newNode);
        return newNode;
	}

    public void removeNode(Node node){
        int nodeId = node.id;
        // first remove from the id of the node from translation (naming) table
        properties.remove(String.valueOf(nodeId));

        // remove all the edges pointing at this node from all the nodes that will still exist
        for(Edge edge : node.getEdges()){
            Node existingNode = edge.getNode2();
            existingNode.removeEdgeToNode(nodeId);
        }

        // actually remove the node from the node list
        nodes.remove(node);
    }

	public Properties getProperties() {
		return properties;
	}

	public void print(PrintStream out) {
		for (Node node : this.getNodes()) {
			for (Edge edge : this.getEdges(node)) {
				System.out.println(String.format(
						"%s to %s = %d ",
						this.getNodeAsText(edge.getNode1()),
						this.getNodeAsText(edge.getNode2()),
						edge.getWeight()));
			}
		}
	}
}
