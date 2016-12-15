package htw_berlin.de.mapmanager.compass;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import htw_berlin.de.mapmanager.R;

public class Compass2Activity extends AppCompatActivity {

    private TextView textViewX,textViewY,textViewZ;
    private Button calibrationButton;

    private SensorManager mSensorManager;
    private float azimut;
    private Sensor pressureSensor;
    boolean calibration=true;

    float nullPressure=0.0f;
    private static final String TAG="Magnet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass2);

        textViewX=(TextView)findViewById(R.id.txtCompassX);
        textViewY=(TextView)findViewById(R.id.txtCompassY);
        textViewZ=(TextView)findViewById(R.id.txtCompassZ);
        calibrationButton=(Button)findViewById(R.id.btnCalibrate);

        mSensorManager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        pressureSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        Log.d(TAG,"Magnet Klasse");
    }

    float[] mGravity;
    float[]mGeomagnetic;
    private SensorEventListener magnetListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float prssureValue=0.0f;
            float highOverSee=0.0f;



            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                mGravity = event.values;
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                mGeomagnetic = event.values;
            if (mGravity != null && mGeomagnetic != null) {

                float R[] = new float[9];
                float I[] = new float[9];
                boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
                if (success) {
                    float orientation[] = new float[3];


                    SensorManager.getOrientation(R, orientation);
                    float azimut2=orientation[0];
                    int deg = (int)(azimut2 * (float)57.295);

                    Log.d(TAG,"Azimut X: "+(float) Math.toDegrees(orientation[2]));
                    azimut =(float) Math.toDegrees(orientation[0]) ; //

                }
            }
            if (event.sensor.getType()== Sensor.TYPE_PRESSURE){
                if(calibration==true){

                    nullPressure=event.values[0];
                    calibration=false;
                }
                prssureValue=event.values[0];
                highOverSee= SensorManager.getAltitude(nullPressure,prssureValue);

                Log.d(TAG,"Druck: "+event.values[0]);
                Log.d(TAG,"Null Druck"+nullPressure);
            }

            textViewX.setText("Value X: "+azimut);
            textViewY.setText("Genauigkeit: "+event.accuracy);
            textViewZ.setText("Höhe über Null "+highOverSee);



        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {


        }
    };
    public void onResume(){
        super.onResume();
        mSensorManager.registerListener(magnetListener,mSensorManager
                .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);

        mSensorManager.registerListener(magnetListener,mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(magnetListener,pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onPause(){
        super.onPause();
        mSensorManager.unregisterListener(magnetListener);
    }

    public void onClickCalibrationButton(View view){
        calibration=true;

    }


}
