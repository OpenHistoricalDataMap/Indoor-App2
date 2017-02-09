package htw_berlin.de.mapmanager.persistence;


import android.app.DownloadManager;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.graph.GraphAdapterBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


import htw_berlin.de.mapmanager.graph.Graph;
import htw_berlin.de.mapmanager.graph.Node;
import htw_berlin.de.mapmanager.permissions.PermissionManager;

import static android.content.Context.DOWNLOAD_SERVICE;

public class PersistenceManager {
    public static final String LOG_TAG = "PersistenceManager";


    // Contains the albums for this app
    private static final String ALBUMS_FOLDER = "mapamanager_albums";
    private static final String POI_PICTURE_NAME = "poi_picture.png";
    private static final String GRAPH_FOLDER = "graph";
    public static final String GRAPH_JSON_NAME = "json_graph.json";
    private static final String GRAPH_DUMP_FOLDER = "graph_dump";
    private PermissionManager permissionManager;

    public static final Gson gson;
    // initialize gson
    static {
        // create the builder. This allows us to set multiple settings
        final GsonBuilder gsonBuilder = new GsonBuilder().disableHtmlEscaping()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation()
                //.registerTypeAdapter(SignalInformationInterface.class, new SignalInformationSerializer())
                .serializeNulls();

        // manage circular references in the graph (DijkstraEdge->NodeInterface->DijkstraEdge->...)
        new GraphAdapterBuilder()
                // enable if cyclic dependencies make the application crash
                // (you will probably need to delete the graph.json data)
                .addType(Node.class)
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
    public static File getNodeImageFile(final String nodeId){
        final File folder = getAlbumStorageDir(nodeId);
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

    private boolean storeGraph(Graph graph, File location) throws IOException {
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

        final JsonWriter writer = new JsonWriter(new FileWriter(location));
        gson.toJson(graph, Graph.class, writer);

        //System.out.println(gson.toJson(graph, Graph.class));

        writer.flush();
        writer.close();

        return true;
    }

    public boolean storeGraph(Graph graph) throws IOException {
        return storeGraph(graph, getGraphFile());
    }

    /**
     * Stores the node to a file with the name of the node and its storage
     * date and time
     * @param node
     * @throws IOException
     * @throws WritePermissionException
     */
    public void storeNodeMeasurements(Node node) throws IOException, WritePermissionException, ReadPermissionException {
        if(!this.permissionManager.isWriteExternalAllowed()){
            Log.e(LOG_TAG, "Permissions were never requested for this permission manager. \n Make sure to have called checkExternalWritePermissions(). Aborting storeNodeMeasurements()...");
            throw new WritePermissionException("Write permissions denied");
        }

        if (!isExternalStorageReadable()) {
            Log.e(LOG_TAG, "not readable");
            throw new ReadPermissionException("Read permission denied");
        }

        if (!isExternalStorageWritable()) {
            System.err.println("not writable");
            // TODO: probably should not continue writing if storage is not writable --> throw IOException?
        }

        final JsonWriter writer = new JsonWriter(new FileWriter(getNodeMeasurementsFile(node)));
        gson.toJson(node, Node.class, writer);

        writer.flush();
        writer.close();
    }

    /**
     * Deletes permanently the file used to store all the measurements of the node.
     * @param node
     */
    public void deleteNodeMeasurementsFile(Node node) throws WritePermissionException {
        if(!this.permissionManager.isWriteExternalAllowed()){
            Log.e(LOG_TAG, "Permissions were never requested for this permission manager. \n Make sure to have called checkExternalWritePermissions(). Aborting storeNodeMeasurements()...");
            throw new WritePermissionException("Write permissions denied");
        }

        if (!isExternalStorageWritable()) {
            System.err.println("not writable");
            // TODO: probably should not continue writing if storage is not writable --> throw IOException?
        }

        File measurementsFile = getNodeMeasurementsFile(node);
        if(measurementsFile.exists()){
            measurementsFile.delete();
        }
    }

    /**
     * deletes the node's album of pictures
     * @param node
     */
    public void deleteAlbumStorageDir(Node node){
        final File albumDir = getAlbumStorageDir(node.getId());
        if (albumDir.isDirectory())
        {
            String[] children = albumDir.list();
            for (int i = 0; i < children.length; i++)
            {
                new File(albumDir, children[i]).delete();
                Log.d(LOG_TAG, "Deleted " + i);
            }
        }
        albumDir.delete();
    }


    /**
     * File to save the whole node data to
     * @param node
     * @return
     */
    private File getNodeMeasurementsFile(Node node) {
        String timeInfo =  new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String filename = node.getId() + "_" + timeInfo + ".json";
        Log.d(LOG_TAG, filename);
        System.out.println(filename);
        return new File(getGraphStorageDir(), filename);
    }

    public static Graph loadGraph() throws FileNotFoundException {
        if (!isExternalStorageReadable()) {
            Log.e(LOG_TAG, "not readable");
        }

        if (!isExternalStorageWritable()) {
            System.err.println("not writable");
            // TODO: probably should not continue writing if storage is not writable --> throw exception?
        }

        final JsonReader reader = new JsonReader(new FileReader(getGraphFile()));
        return gson.fromJson(reader, Graph.class);

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

    /**
     * Dumps the graph json file into the GRAPH_DUMP_FOLDER folder in the download folder,
     * so that it can easily be accessed from a computer.
     * Attention: sometimes you need to restart your phone to make the files visible from
     * file explorer from pc (known bug)
     */
    // TODO: dump graph in AsyncTask?
    public void dumpGraph() throws IOException {
        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        final File source = getGraphFile();
        final File dumpDir = new File(downloadDir,GRAPH_DUMP_FOLDER);
        final File destination = new File(dumpDir, "graph_dump"+(new Date()).getTime());

        if(!dumpDir.exists()){
            dumpDir.mkdirs();
        }

        // destination file should not exist, since its name is based on the current timestamp
        if(!destination.exists()){
            destination.createNewFile();
        }

        InputStream inStream = null;
        OutputStream outStream = null;

        try{

            inStream = new FileInputStream(source);
            outStream = new FileOutputStream(destination);

            byte[] buffer = new byte[1024];

            int length;
            //copy the file content in bytes

            while ((length = inStream.read(buffer)) > 0){
                outStream.write(buffer, 0, length);
                Log.d(LOG_TAG, new String(buffer));
            }

            inStream.close();
            outStream.close();


            Log.d(LOG_TAG, "File is copied successfully");

        }catch(IOException e){
            e.printStackTrace();
        }

        Log.d(LOG_TAG, "Dump terminated");


        /*
        CANNOT USE THIS SYSTEM!
        It will throw an error because the uri MUST be http:/// or https:/// and is actually file:///
        // uri and file info
        final File graphFile = getGraphFile();
        final String filename = graphFile.getName();
        final android.net.Uri uri = Uri.fromFile(graphFile);

        // request
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
        // to notify when download is complete
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        // if you want to be available from media players
        request.allowScanningByMediaScanner();

        // getSystemService is a Context method, therefore we access getActivity()
        DownloadManager manager = (DownloadManager) permissionManager.getActivity().getSystemService(DOWNLOAD_SERVICE);
        manager.enqueue(request);
        */


    }

}
