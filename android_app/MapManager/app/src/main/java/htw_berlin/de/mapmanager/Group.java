package htw_berlin.de.mapmanager;

import java.util.ArrayList;
import java.util.List;
/** For expandable List View in the Mange Connections sactivity */
public class Group {

    public String string;
    public final List<String> children = new ArrayList<String>();

    public Group(String string) {
        this.string = string;
    }

}