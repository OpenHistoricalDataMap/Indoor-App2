package htw_berlin.de.mapmanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import htw_berlin.de.mapmanager.graph.Node;
import htw_berlin.de.mapmanager.permissions.PermissionManager;
import htw_berlin.de.mapmanager.persistence.PersistenceManager;

public class ManagePOIConnectionsActivity extends AppCompatActivity{

    private static final String LOG_TAG = "ManagePOIConnections";

    private Node parentNode;
    private static final int TAKE_PHOTO_CODE = 0;

    // user defined, any code that is not used for any other permission request in this activity
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    private ListView listView;
    private ConnectionListAdapter adapter;
    private Button capture;
    private ImageView currentPOIImage;

    private PermissionManager permissionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_poiconnections);

        // Get information about the connections
        Intent intent = getIntent();
        int poiId = intent.getIntExtra(MainActivity.EXTRA_MESSAGE_POI_ID, -1);
        if(poiId == -1){
            throw new IllegalArgumentException("The given poiId is invalid: " + (-1));
        }

        this.parentNode = MainActivity.graph.getNodeById(poiId);
        setTitle(MainActivity.graph.getNodeAsText(parentNode));



        initPermissions();

        // TODO: get the ArrayList of images of the POI
        initImageView();

        initListView();
        initCamera();
    }

    private void initImageView() {
        currentPOIImage = (ImageView) findViewById(R.id.currentPOIImage);
        File nodeImageFile = PersistenceManager.getNodeImageFile(parentNode.id);
        if(!nodeImageFile.exists()){
            currentPOIImage.setImageResource(R.mipmap.ic_launcher);
        }
        else {
            Uri nodeImageUri = Uri.fromFile(nodeImageFile);
            currentPOIImage.setImageURI(nodeImageUri);
        }
    }

    private void initPermissions() {
        permissionManager = new PermissionManager(this);
        permissionManager.checkCameraPermissions();
        permissionManager.checkExternalWritePermissions();
        permissionManager.checkExternalReadPermissions();
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
        // Here, we are making a folder named picFolder to store
        // pics taken by the camera using this application.
        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
        File newdir = new File(dir);
        newdir.mkdirs();

        capture = (Button) findViewById(R.id.btnCapture);

        capture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(LOG_TAG, "" + permissionManager.isWriteExternalAllowed());
                if(!permissionManager.isCameraAllowed()){
                    Log.e(LOG_TAG, "Permissions for the camera are not given");
                    return;
                }

                File newFile = PersistenceManager.getNodeImageFile(parentNode.id);
                Uri outputFileUri = FileProvider.getUriForFile(ManagePOIConnectionsActivity.this, getApplicationContext().getPackageName() + ".provider", newFile);
                // alternative
                //Uri outputFileUri = Uri.fromFile(newFile);

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

                startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == TAKE_PHOTO_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // Picture was taken
                // refresh imageview
                currentPOIImage.setImageURI(null);
                currentPOIImage.setImageDrawable(null);
                initImageView();
            }
            else {
                // Picture was not taken
            }
        }
    }




    @Override
    public void onBackPressed() {
        // TODO: do you want to be able to cancel?
        askForSave();
    }

    /**
     * Save to file if user presses yes
     */

    private void askForSave() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                ManagePOIConnectionsActivity.this);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                PersistenceManager persistenceManager = new PersistenceManager(permissionManager);
                persistenceManager.storeGraph(MainActivity.graph);
                finish();
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        alertDialog.setMessage("Do you want to save the changes to file before exiting?");
        alertDialog.setTitle("Save changes to file?");
        alertDialog.show();
    }
}