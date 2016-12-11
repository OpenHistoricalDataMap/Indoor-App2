package htw_berlin.de.mapmanager;

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

import java.io.File;

import htw_berlin.de.mapmanager.graph.Node;
import htw_berlin.de.mapmanager.permissions.PermissionManager;
import htw_berlin.de.mapmanager.persistence.PersistenceManager;

public class POIDetailsActivity extends AppCompatActivity{

    private static final String LOG_TAG = "POIDetailsActivity";

    private Node parentNode;
    private static final int TAKE_PHOTO_CODE = 0;

    private Button capture;
    private Button setConnections;
    private ImageView currentPOIImage;

    private PermissionManager permissionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poidetails);

        // Get information about the POI
        Intent intent = getIntent();
        int poiId = intent.getIntExtra(MainActivity.EXTRA_MESSAGE_POI_ID, -1);
        if(poiId == -1){
            throw new IllegalArgumentException("The given poiId is invalid: " + poiId);
        }

        this.parentNode = MainActivity.graph.getNodeById(poiId);
        setTitle(MainActivity.graph.getNodeAsText(parentNode));


        initPermissions();

        // TODO: get the ArrayList of images of the POI
        initImageView();
        initCamera();
        initSetConnections();
    }

    private void initSetConnections() {
        setConnections = (Button) findViewById(R.id.btnSetConnections);

        final Intent intent = new Intent(this, POIConnectionsActivity.class);
        intent.putExtra(MainActivity.EXTRA_MESSAGE_POI_ID,  parentNode.id);

        setConnections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });
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
                Uri outputFileUri = FileProvider.getUriForFile(POIDetailsActivity.this, getApplicationContext().getPackageName() + ".provider", newFile);
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


}