package htw_berlin.de.mapmanager.compass;


import android.hardware.SensorManager;
import android.util.Log;

import htw_berlin.de.mapmanager.MainActivity;

/**
 * Created by florianhausler on 23.12.16.
 */

public class SensorData {
    private float[] mGravity;
    private float[] mGeomagnetic;
    private float azimut;
    private float prssureValue ;
    //private float nullPressure= MainActivity.nullPressure;
    private float highOverSee;
    private boolean calibrate=false;




    public void setCalibration(boolean calibration) {
        this.calibrate = calibration;
    }

    public float getPrssureValue() {
        return prssureValue;
    }

    public void setPrssureValue(float prssureValue) {
        this.prssureValue = prssureValue;
    }

    public float[] getGravity() {
        return mGravity;
    }

    public void setGravity(float[] gravity) {
        mGravity = gravity;
    }

    public float[] getGeomagnetic() {
        return mGeomagnetic;
    }

    public void setGeomagnetic(float[] geomagnetic) {
        mGeomagnetic = geomagnetic;
    }

    public float getAzimut() {
        return azimut;
    }

    public float getHighOverSee() {
        return highOverSee;
    }



    public float calcAzimut() {

    if (mGravity != null && mGeomagnetic != null) {

        float R[] = new float[9];
        float I[] = new float[9];
        boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
        if (success) {
            float orientation[] = new float[3];


            SensorManager.getOrientation(R, orientation);
            float azimut2 = orientation[0];
            int deg = (int) (azimut2 * (float) 57.295);

            // Log.d(TAG, "Azimut X: " + (float) Math.toDegrees(orientation[2]));
            azimut = (float) Math.toDegrees(orientation[0]); //

        }
    }
    return azimut;
}

    public void calcHigh(){


            if (calibrate == true) {
             //   Log.d("Magnet","Drin: "+prssureValue);

                MainActivity.nullPressure = prssureValue;
                calibrate = false;
            }


            highOverSee = SensorManager.getAltitude(MainActivity.nullPressure , prssureValue);
    }



}
