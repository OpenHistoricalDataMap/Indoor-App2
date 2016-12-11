package htw_berlin.de.mapmanager.comparator;

import java.util.Comparator;

import htw_berlin.de.mapmanager.graph.Edge;


public class EdgeComparator implements Comparator<Edge> {
    @Override
    public int compare(Edge o1, Edge o2) {
        // -1 = o1 < o2
        // 0 = o1 == o2
        // 1 = o1 > o2
        NodeComparator nc = new NodeComparator();
        int node1Comparison = nc.compare(o1.getNode1(), o2.getNode1());
        if(node1Comparison == 0){
            // if first node is equal, then compare the second node
            return nc.compare(o1.getNode2(), o2.getNode2());
        }
        else {
            return node1Comparison;
        }
    }
}
