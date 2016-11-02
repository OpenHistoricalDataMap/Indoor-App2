package htw_berlin.de.mapmanager;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.widget.ExpandableListView;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.file.FileSink;
import org.graphstream.stream.file.FileSinkDGS;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceDGS;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

public class ManagePOIConnectionsActivity extends AppCompatActivity {

    private static final String LOG_TAG = "ManagePOIConnections";
    private static final String GRAPH_NAME = "Alexanderplatz";
    private static final String NODE_NAME_ATTRIBUTE = "NODE_NAME_ATTRIBUTE";


    // more efficient than HashMap for mapping integers to objects, for ui groups of expandablelistview
    private SparseArray<Group> groups = new SparseArray<Group>();
    private MultiGraph graph;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_poiconnections);


        File graphFile = getGraphFile();
        this.graph = readGraphFile(graphFile);
/*
        if(this.graph == null){
            // file does not exist or error occured
            System.out.println("this.graph is null");
        }
        else {*/

            Iterator<Node> nodeIterator = this.graph.getNodeIterator();
            while(nodeIterator.hasNext()){
                Node node = nodeIterator.next();
                String nodeId = node.getId();
                System.out.printf("Node: id (%s) \n", nodeId);
            }

        //}


        /*
         * Get information about the connections
         */
        Intent intent = getIntent();
        String poiId = intent.getStringExtra(MainActivity.EXTRA_MESSAGE_POI_ID);
        // TODO: using the poiId, get the POI object from the list of nodes in the graph
        String poiName = "McDonald's";

        // TODO: get the ArrayList of images of the POI
        // TODO: get the edges and corresponding nodes (other POIs) that are directly connected to the current POI


        // TODO : get real data
        createData();
        ExpandableListView listView = (ExpandableListView) findViewById(R.id.expandableListView);
        MyExpandableListAdapter adapter = new MyExpandableListAdapter(this, groups);
        listView.setAdapter(adapter);


        // TODO remove
        fakeGraph();

    }

    private void fakeGraph() {

        FileSinkDGS output = new FileSinkDGS();

        System.setProperty("gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        Graph graph = new MultiGraph(GRAPH_NAME);
        // create automatically nodes if an     edge containing a new node gets added
        graph.addNode("A");
        graph.addNode("B");
        graph.addNode("C");

        graph.addEdge("AB", "A", "B");
        graph.addEdge("BC", "B", "C");
        graph.addEdge("CA", "C", "A");

        writeToFile(graph, output);
    }

    private void writeToFile(Graph graph, FileSinkDGS output) {


        if(!isExternalStorageReadable()){
            System.err.println("not readable");
        }

        if(!isExternalStorageWritable()){
            System.err.println("not writable");
        }

        String filename = "graphData.dgs";
        //output.begin();?? <-- USe this method?
        try {
            File path = getGraphStorageDir();
            path.mkdirs(); // ensure the directory exists

            // get the file containing the graph
            File storageFile = getGraphFile();

            System.out.println("file exists: " + storageFile.exists());
            System.out.println("file name: " + storageFile.getAbsolutePath());
            System.out.println("file isHidden " + storageFile.isHidden());
            System.out.println("file is File " + !storageFile.isDirectory());
            System.out.println("file can write: " + storageFile.canWrite());

            boolean created = storageFile.createNewFile();
            System.out.println("created? " + created);
/*
            FileWriter fw = new FileWriter(storageFile);
            fw.write("HELLO?");r
            */
            FileSinkDGS fileSinkDGS = new FileSinkDGS();
            fileSinkDGS.writeAll(graph, storageFile.getAbsolutePath());

            //graph.write(output, filename);
            //output.writeAll(graph, fw);
            //System.out.println("file exists after writeAll: " + storageFile.exists());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createData() {
        for (int j = 0; j < 5; j++) {
            Group group = new Group("Test " + j);
            for (int i = 0; i < 5; i++) {
                group.children.add("Sub Item" + i);
            }
            groups.append(j, group);
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    private File getGraphStorageDir() {
        // Get the directory for the user's public documents directory.
        // TODO: Warning, files saved in this directory can't be seen from the user itself (from the phone ui)
        return getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);

        /*if(file.exists()) {
            file.delete();
        }
        */
/*
        if (!file.mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        }
*/
        /*
        if(!file.exists()){
            file.createNewFile();
        }
*/
    }

    public File getGraphFile() {
        return new File(this.getGraphStorageDir(), GRAPH_NAME);
    }

    /**
     * Tutorial:
     * @link {http://graphstream-project.org/doc/Tutorials/Reading-files-using-FileSource/}
     * @param graphFile
     * @return
     */
    public MultiGraph readGraphFile(File graphFile){
        MultiGraph graph = new MultiGraph(GRAPH_NAME) ;
        FileSource fs = new FileSourceDGS();

        fs.addSink(graph);

        try {
            fs.readAll(graphFile.getAbsolutePath());
        } catch( IOException e) {
            System.err.println("Error reading graph");
            e.printStackTrace();
        } finally {
            fs.removeSink(graph);
        }

        return graph;
    }


}