package htw_berlin.de.mapmanager.comparator;

import java.util.Comparator;

import htw_berlin.de.mapmanager.graph.Node;


public class NodeComparator implements Comparator<Node> {
    @Override
    public int compare(Node o1, Node o2) {
        // -1 = o1 < o2
        // 0 = o1 == o2
        // 1 = o1 > o2
        if(o1.id < o2.id){
            return -1;
        }
        else if(o1.id == o2.id){
            return 0;
        }
        else {
            return 1;
        }
    }
}
