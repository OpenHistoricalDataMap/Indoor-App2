package htw_berlin.de.mapmanager.graph;

import java.util.Comparator;

public class EdgeComparator implements Comparator<Edge> {

	@Override
	public int compare(Edge o1, Edge o2) {
		if (o1 != null)
			return o1.compareTo(o2);
		throw new IllegalArgumentException("Can not be null");
	}

}
