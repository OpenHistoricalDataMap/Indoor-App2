package htw_berlin.de.mapmanager.persistence;


import android.os.Environment;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.graph.GraphAdapterBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import htw_berlin.de.mapmanager.graph.Edge;
import htw_berlin.de.mapmanager.graph.Node;
import htw_berlin.de.mapmanager.graph.TranslatableAdjacencyMatrixGraph;
import htw_berlin.de.mapmanager.permissions.PermissionManager;

public class PersistenceManager {
    public static final String LOG_TAG = "PersistenceManager";


    // Contains the albums for this app
    private static final String ALBUMS_FOLDER = "mapamanager_albums";
    private static final String POI_PICTURE_NAME = "poi_picture.png";
    private static final String GRAPH_FOLDER = "graph";
    public static final String GRAPH_JSON_NAME = "json_graph.json";
    private PermissionManager permissionManager;

    private static final Gson gson;
    // initialize gson
    static {
        // create the builder. This allows us to set multiple settings
        final GsonBuilder gsonBuilder = new GsonBuilder().disableHtmlEscaping()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting()
                .serializeNulls();

        // manage circular references in the graph (Edge->Node->Edge->...)
        new GraphAdapterBuilder()
                .addType(TranslatableAdjacencyMatrixGraph.class)
                .addType(Node.class)
                .addType(Edge.class)
                .registerOn(gsonBuilder);

        // Create the Gson object. THis is what we use to parse and convert
        gson = gsonBuilder.create();
    }


    public PersistenceManager(final PermissionManager permissionManager){
        if(permissionManager == null){
            throw new IllegalArgumentException("Context cannot be null, it is required to check permissions at runtime");
        }
        this.permissionManager = permissionManager;
    }

    /**
     * Returns the File reference associated with the node's image. Does not check for file existence
     * @param nodeId The id of the node
     * @return File reference
     */
    public static File getNodeImageFile(final int nodeId){
        final File folder = getAlbumStorageDir(String.format("Node_%d", nodeId));
        final File image = new File(folder, POI_PICTURE_NAME);
        return image;
    }

    // for the pics
    private static File getAlbumStorageDir(final String albumName) {
        // Get the directory for the user's public pictures directory.
        // TODO: use internal directory for files, use DownloadManager to transfer this files to the Download folder on an "Export" action
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), ALBUMS_FOLDER);
        file = new File(file, albumName);
        if (!file.mkdirs()) {
            Log.i(LOG_TAG, String.format("Directory %s structure MAY have not been completely created (possibly because it already existed)", file.getAbsolutePath()));
        }
        return file;
    }
    
    private static File getGraphStorageDir(){
                // Get the directory for the user's public documents directory.
        final File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), GRAPH_FOLDER);
        if (!file.mkdirs()) {
            Log.i(LOG_TAG, String.format("Directory %s structure MAY have not been completely created (possibly because it already existed)", file.getAbsolutePath()));
        }
        return file;
    }


    // TODO: use internal hidden directory for files, then use DownloadManager to transfer this files to the Download folder on an "Export" action
    public void exportConfiguration(){

    }

    public boolean storeGraph(TranslatableAdjacencyMatrixGraph graph) throws IOException {
        if(!this.permissionManager.isWriteExternalAllowed()){
            Log.e(LOG_TAG, "Permissions were never requested for this permission manager. \n Make sure to have called checkExternalWritePermissions(). Aborting storeGraph()...");
            return false;
        }

        boolean errorOccured = false;

        if (!isExternalStorageReadable()) {
            Log.e(LOG_TAG, "not readable");
        }

        if (!isExternalStorageWritable()) {
            System.err.println("not writable");
            // TODO: probably should not continue writing if storage is not writable --> throw exception?
        }

        final JsonWriter writer = new JsonWriter(new FileWriter(getGraphFile()));
        gson.toJson(graph, TranslatableAdjacencyMatrixGraph.class, writer);
        writer.flush();
        writer.close();

        return true;
    }

    public static TranslatableAdjacencyMatrixGraph loadGraph() throws FileNotFoundException {
        if (!isExternalStorageReadable()) {
            Log.e(LOG_TAG, "not readable");
        }

        if (!isExternalStorageWritable()) {
            System.err.println("not writable");
            // TODO: probably should not continue writing if storage is not writable --> throw exception?
        }

        final JsonReader reader = new JsonReader(new FileReader(getGraphFile()));
        return gson.fromJson(reader, TranslatableAdjacencyMatrixGraph.class);

    }

    public static File getGraphFile() {
        return new File(getGraphStorageDir(), GRAPH_JSON_NAME);
    }


    /* Checks if external storage is available for read and write*/
    public static boolean isExternalStorageWritable() {
        final String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read*/
    public static boolean isExternalStorageReadable() {
        final String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
