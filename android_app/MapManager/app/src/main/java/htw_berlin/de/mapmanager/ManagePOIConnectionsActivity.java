package htw_berlin.de.mapmanager;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

/*
Graph library
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.file.FileSinkDGS;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceDGS;
*/


import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import htw_berlin.de.mapmanager.graph.Node;
import htw_berlin.de.mapmanager.graph.TranslatableAdjacencyMatrixGraph;

public class ManagePOIConnectionsActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String LOG_TAG = "ManagePOIConnections";
    private static final String GRAPH_NAME = "Alexanderplatz";

    private Node parentNode;
    private static final int TAKE_PHOTO_CODE = 0;
    private static int picturesCount = 0;

    // user defined, any code that is not used for any other permission request in this activity
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    private ListView listView;
    private ConnectionListAdapter adapter;
    private Button capture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_poiconnections);


        /*
         * Get information about the connections
         */

        Intent intent = getIntent();
        int poiId = intent.getIntExtra(MainActivity.EXTRA_MESSAGE_POI_ID, -1);
        if(poiId == -1){
            throw new IllegalArgumentException("The given poiId is invalid: " + (-1));
        }
        this.parentNode = MainActivity.graph.getNodeById(poiId);


        // TODO: get the ArrayList of images of the POI

        initListView();

        initCamera();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        // TODO: Save current graph state to file when coming back from the details
        //saveGraphToFile();
    }

    private void saveGraphToFile() {
        throw new UnsupportedOperationException("TODO: implement saveGraphToFile");
    }

    private void initListView() {

        listView = (ListView) findViewById(R.id.connectionListView);
        ArrayList<Node> allOtherNodes = new ArrayList<>();
        allOtherNodes.addAll(MainActivity.graph.getNodes());
        allOtherNodes.remove(parentNode);
        adapter = new ConnectionListAdapter(parentNode, allOtherNodes, this);
        listView.setAdapter(adapter);

    }

    private void initCamera() {
        // check and respectively request camera permissions
        checkCameraPermissions();

        // Here, we are making a folder named picFolder to store
        // pics taken by the camera using this application.
        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
        File newdir = new File(dir);
        newdir.mkdirs();

        capture = (Button) findViewById(R.id.btnCapture);

        capture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Here, the counter will be incremented each time, and the
                // picture taken by camera will be stored as 1.jpg,2.jpg
                // and likewise.
                picturesCount++;
                String file = dir+ picturesCount +".jpg";
                File newfile = new File(file);
                try {
                    newfile.createNewFile();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                Uri outputFileUri = FileProvider.getUriForFile(ManagePOIConnectionsActivity.this, getApplicationContext().getPackageName() + ".provider", newfile);

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

                startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
            }
        });
    }

    private void checkCameraPermissions() {
        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        }
    }


    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now user should be able to use camera
            }
            else {
                // Your app will not have this permission. Turn off all functions
                // that require this permission or it will force close
                capture.setEnabled(false);
            }
        }
    }

    @Override
    public void onBackPressed() {
        // TODO: at the moment the changes are saved already when the checkboxes are changed
        askForSave();
    }

    /**
     * Exit the app if user select yes.
     */

    private void askForSave() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                ManagePOIConnectionsActivity.this);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        alertDialog.setMessage("Do you want to save the changes before exiting?");
        alertDialog.setTitle("Save changes?");
        alertDialog.show();
    }


    private void writeToFile(TranslatableAdjacencyMatrixGraph graph, OutputStream outputStream) {


        if (!isExternalStorageReadable()) {
            System.err.println("not readable");
        }

        if (!isExternalStorageWritable()) {
            System.err.println("not writable");
        }

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

    private File getGraphStorageDir() {
        // Get the directory for the user's public documents directory.
        // TODO: Warning, files saved in this directory can't be seen from the user itself (from the phone ui)
        return getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
/*
        if(file.exists()) {
            file.delete();
        }
        if (!file.mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        }


        if(!file.exists()){
            file.createNewFile();
        }*/

    }

    public File getGraphFile() {
        return new File(this.getGraphStorageDir(), GRAPH_NAME);
    }



}