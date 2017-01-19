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

import java.io.File;
import java.io.IOException;

import htw_berlin.de.mapmanager.graph.Node;
import htw_berlin.de.mapmanager.permissions.PermissionManager;
import htw_berlin.de.mapmanager.persistence.PersistenceManager;
import htw_berlin.de.mapmanager.persistence.ReadPermissionException;
import htw_berlin.de.mapmanager.persistence.WritePermissionException;
import htw_berlin.de.mapmanager.wlan.WLANMainActivity;

public class POIDetailsActivity extends AppCompatActivity{

    private static final String LOG_TAG = "POIDetailsActivity";

    private Node parentNode;
    private static final int TAKE_PHOTO_CODE = 0;

    private Button capture;
    private Button setConnections;
    private Button btnGoToMeasurement;
    private ImageView currentPOIImage;
    private Button resetPoi;

    private PermissionManager permissionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poidetails);

        // Get information about the POI
        Intent intent = getIntent();
        String poiId = intent.getStringExtra(MainActivity.EXTRA_MESSAGE_POI_ID);
        if(poiId == null || poiId == ""){
            throw new IllegalArgumentException("The given poiId is invalid: " + poiId);
        }

        // TODO this operation could run through all the nodes. Consider passing the whole Node
        // TODO look on the internet what would be more performance expensive
        this.parentNode = MainActivity.graph.getNodeById(poiId);
        setTitle(parentNode.getId());

        initPermissions();

        // TODO: get the ArrayList of images of the POI
        initImageView();
        initMeasurements();
        initCamera();
        initSetConnections();
        initResetPOI();
    }

    private void initMeasurements()
    {
        btnGoToMeasurement = (Button) findViewById(R.id.btnMeasurements);

        final Intent intent = new Intent(this, WLANMainActivity.class);
        intent.putExtra(MainActivity.EXTRA_MESSAGE_POI_ID, parentNode.getId());

        btnGoToMeasurement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });

    }

    private void initSetConnections() {
        setConnections = (Button) findViewById(R.id.btnSetConnections);

        final Intent intent = new Intent(this, POIConnectionsActivity.class);
        intent.putExtra(MainActivity.EXTRA_MESSAGE_POI_ID,  parentNode.getId());

        setConnections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });
    }

    private void initImageView() {
        currentPOIImage = (ImageView) findViewById(R.id.currentPOIImage);
        File nodeImageFile = PersistenceManager.getNodeImageFile(parentNode.getId());
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

                File newFile = PersistenceManager.getNodeImageFile(parentNode.getId());
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

    /**
     * Reset ALL the registered signal information if user presses and confirms with Yes
     */
    private void initResetPOI() {
        resetPoi = (Button) findViewById(R.id.btnResetPoi);
        // TODO: AlertDialog is blocking! Use @cbos method for more!
        resetPoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                parentNode.getSignalInformationList().clear();
                                try {
                                    final PersistenceManager persistenceManager = new PersistenceManager(permissionManager);
                                    // store the empty measurements
                                    persistenceManager.storeNodeMeasurements(parentNode);
                                    // store the graph
                                    persistenceManager.storeGraph(MainActivity.graph);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (WritePermissionException e) {
                                    e.printStackTrace();
                                } catch (ReadPermissionException e) {
                                    e.printStackTrace();
                                }

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(POIDetailsActivity.this);
                builder.setMessage("Are you sure?\n This will delete permanently all recorded signals").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            }
        });
    }



}