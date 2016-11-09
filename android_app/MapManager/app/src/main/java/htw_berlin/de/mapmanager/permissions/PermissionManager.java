package htw_berlin.de.mapmanager.permissions;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;



/**
 * Created by tognitos on 07.11.16.
 */

// TODO: should every activity request permissions? Then we should store the booleans in a hashmap per activity
public class PermissionManager implements ActivityCompat.OnRequestPermissionsResultCallback  {
    private final Activity activity;
    private boolean cameraAllowed;
    private boolean readExternalAllowed;
    private boolean writeExternalAllowed;
    private static final String LOG_TAG = "PermissionManager";


    private static enum RuntimePermissionsRequestCodes {
        CAMERA,
        READ_EXTERNAL_STORAGE,
        WRITE_EXTERNAL_STORAGE
    }

    public PermissionManager(Activity activity){
        this.activity = activity;
    }


    public void checkCameraPermissions() {
        final String permission = Manifest.permission.CAMERA;

        if (activity.checkSelfPermission(permission)
                != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{permission},
                    RuntimePermissionsRequestCodes.CAMERA.ordinal());
        }
        else {
            this.cameraAllowed = true;
        }
    }


    /**
     * Mandatory from Android 6 to check permissions at runtime
     */
    public void checkExternalReadPermissions() {
        final String permission = Manifest.permission.READ_EXTERNAL_STORAGE;

        if (activity.checkSelfPermission(permission)
                != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{permission},
                    RuntimePermissionsRequestCodes.READ_EXTERNAL_STORAGE.ordinal());
        }
        else {
            this.readExternalAllowed = true;
        }
    }

    /**
     * Mandatory from Android 6 to check permissions at runtime
     */
    public void checkExternalWritePermissions() {
        final String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;

        System.out.println("COMING ?????");
        System.out.println("COMING ?????");
        System.out.println("COMING ?????");
        System.out.println("COMING ?????");
        if (activity.checkSelfPermission(permission)
                != PackageManager.PERMISSION_GRANTED) {
            System.out.println("HERE?????");
            System.out.println("HERE?????");
            System.out.println("HERE?????");
            System.out.println("HERE?????");

            activity.requestPermissions(new String[]{permission},
                    RuntimePermissionsRequestCodes.WRITE_EXTERNAL_STORAGE.ordinal());
        }
        else {
            this.writeExternalAllowed = true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean resultBoolean = false;
        for(int result:grantResults) {
            resultBoolean = result == PackageManager.PERMISSION_GRANTED;
            if (requestCode == RuntimePermissionsRequestCodes.CAMERA.ordinal()) {
                Log.d(LOG_TAG, "camera allowed " + resultBoolean);
                cameraAllowed = resultBoolean;
                continue;
            }
            else if(requestCode == RuntimePermissionsRequestCodes.READ_EXTERNAL_STORAGE.ordinal()){
                Log.d(LOG_TAG, "read allowed " + resultBoolean);
                readExternalAllowed = resultBoolean;
                continue;
            }
            else if(requestCode == RuntimePermissionsRequestCodes.WRITE_EXTERNAL_STORAGE.ordinal()){
                Log.d(LOG_TAG, "write allowed " + resultBoolean);
                writeExternalAllowed = resultBoolean;
                continue;
            }
            else {
                Log.e(LOG_TAG, "requestCode not recognised, did you method requestPermissions() with the wrong RuntimePermissionsRequestCode?");
            }
        }
    }

    public boolean isCameraAllowed(){
        return cameraAllowed;
    }

    public boolean isReadExternalAllowed() {
        return readExternalAllowed;
    }

    public boolean isWriteExternalAllowed() {
        return writeExternalAllowed;
    }
}
