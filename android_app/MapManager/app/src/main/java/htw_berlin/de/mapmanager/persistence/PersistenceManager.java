package htw_berlin.de.mapmanager.persistence;


import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import htw_berlin.de.mapmanager.graph.Edge;
import htw_berlin.de.mapmanager.graph.Node;
import htw_berlin.de.mapmanager.graph.TranslatableAdjacencyMatrixGraph;
import htw_berlin.de.mapmanager.permissions.PermissionManager;

public class PersistenceManager {
    public static final String LOG_TAG = "PersistenceManager";


    // Contains the albums for this app
    private static final String ALBUMS_FOLDER = "mapamanager_albums";
    private static final String GRAPH_FOLDER = "graph";
    private static final String GRAPH_FILE_NAME = "places_net.txt";
    private static final String GRAPH_PROPERTIES_FILE_NAME = "places.properties";
    private final PermissionManager permissionManager;


    public PersistenceManager(PermissionManager permissionManager){
        if(permissionManager == null){
            throw new IllegalArgumentException("Context cannot be null, it is required to check permissions at runtime");
        }
        this.permissionManager = permissionManager;
    }

    // for the pics
    public static File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        // TODO: use internal directory for files, use DownloadManager to transfer this files to the Download folder on an "Export" action
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), ALBUMS_FOLDER);
        file = new File(file, albumName);
        if (!file.mkdirs()) {
            Log.e(LOG_TAG, String.format("Directory %s not created", file.getAbsolutePath()));
        }
        return file;
    }
    
    public File getGraphStorageDir(){
                // Get the directory for the user's public documents directory.
        // TODO: use internal directory for files, use DownloadManager to transfer this files to the Download folder on an "Export" action
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), GRAPH_FOLDER);
        if (!file.mkdirs()) {
            Log.i(LOG_TAG, String.format("Directory %s structure MAY have not been completely created (possibly because it already existed)", file.getAbsolutePath()));
        }
        return file;
    }

    public boolean storeGraph(TranslatableAdjacencyMatrixGraph graph){
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
            // TODO: probably should not continue writing if storage is not writable
        }

        final File graphFile = new File(getGraphStorageDir(), GRAPH_FILE_NAME);
        /* TODO possibly just simply throw an IOException (or a custom exception) so that the activity can handle it as necessary (e.g. ask the user to try save again the file)
         */
        graphFile.delete();
        if(!graphFile.exists()){
            try {
                graphFile.createNewFile();
            } catch (IOException e) {
                System.out.println("Unable to create new File " + graphFile.getAbsolutePath());
                e.printStackTrace();
            }
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(graphFile))){
            // print the first line "header" of the matrix)
            pw.print("_" + "\t");
            for (Node node : graph.getNodes()) {
                pw.print(node.id + "\t");
            }
            pw.println();
            // TODO: Verify if it is necessary to ensure the correct order of the nodes (eventually use NodeComparator class to sort the nodes)
            int edgeWeight;
            for(Node nodeRow : graph.getNodes()){
                pw.print(nodeRow.id + "\t");
                for(Node nodeCol : graph.getNodes()){
                    Edge edgeRowToCol = nodeRow.getEdgeToNode(nodeCol.id);
                    if(edgeRowToCol != null) {
                        edgeWeight = edgeRowToCol.getWeight();
                    }
                    else {
                        // no edge, weight = 0
                        edgeWeight = 0;
                    }
                    // TODO, add printing of barrierefrei attribute (maybe as {weight, barrierefrei}) ? Remember to adapt the parser in the graph classes
                    pw.print(edgeWeight + "\t");
                }
                pw.println();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        final File graphPropertiesFile = new File(getGraphStorageDir(), GRAPH_PROPERTIES_FILE_NAME);
        graphPropertiesFile.delete();
        if(!graphPropertiesFile.exists()){
            try {
                graphPropertiesFile.createNewFile();
            } catch (IOException e) {
                System.out.println("Unable to create new File " + graphPropertiesFile.getAbsolutePath());
                e.printStackTrace();
            }
        }
        try (PrintWriter pw = new PrintWriter(new FileWriter(graphPropertiesFile))){
            Properties props = graph.getProperties();

            props.store(pw, "no comments");
            errorOccured = pw.checkError();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return !errorOccured;
    }



    /* Checks if external storage is available for read and write*/
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read*/
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
