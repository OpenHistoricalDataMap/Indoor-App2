package htw_berlin.de.mapmanager.graph;

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
