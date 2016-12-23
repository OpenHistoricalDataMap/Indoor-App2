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

import java.util.ArrayList;

import htw_berlin.de.mapmanager.R;

public class Compass2Activity extends AppCompatActivity {

    private TextView textViewX, textViewSteps, textViewZ, textViewListe;
    private Button calibrationButton, buttonDefineWay;


    //StepCount onSensorChange


    private SensorManager pressureSensorManager, magnetSensorManager, stepSensorManager;
    private float azimut, stepView, stepCount, stepVar;
    private Sensor stepCountSensor, pressureSensor;


    // onClickDefineWay
    private boolean onClickDefineWay;

    private int mod = 0;

    //  private float highOverSee = 0.0f;

    // private float nullPressure = 0.0f;
    private static final String TAG = "Magnet";

    //WayPointListe
    private ArrayList<WayPoint> listWayPoint = new ArrayList<>();
    private int index;

    //MagnetBerechnung
    SensorData sensorDaten = new SensorData();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass2);

        textViewX = (TextView) findViewById(R.id.txtCompassX);
        textViewSteps = (TextView) findViewById(R.id.txtCompassY);
        textViewZ = (TextView) findViewById(R.id.txtCompassZ);
        textViewListe = (TextView) findViewById(R.id.txtViewListe);
        calibrationButton = (Button) findViewById(R.id.btnCalibrate);
        buttonDefineWay = (Button) findViewById(R.id.btnDefineWay);

        //PressureSensor
        pressureSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        pressureSensor = pressureSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);


        //  stepCountSensor
        stepSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCountSensor = stepSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);


        //MagnetSensor
        magnetSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);


        Log.d(TAG, "Magnet Klasse");


    }

    private SensorEventListener magnetListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {

            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                sensorDaten.setGravity(event.values);
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                sensorDaten.setGeomagnetic(event.values);

            sensorDaten.calcAzimut();

            textViewX.setText("Value X: " + sensorDaten.getAzimut());
        }


        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {


        }
    };



    private SensorEventListener pressureStepListner = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {

                sensorDaten.setPrssureValue(event.values[0]);
                Log.d(TAG, "" + sensorDaten.getPrssureValue());
                sensorDaten.calcHigh();
                textViewZ.setText("Höhe über Null " + sensorDaten.getHighOverSee());
            }
            if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {

                stepCount = event.values[0];
                stepView = stepCount - stepVar;
                textViewSteps.setText("Schritte: " + stepView);
                if (stepView % 2 == 0 && onClickDefineWay) {
                    defineWay();
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public void onClickCalibrationButton(View view) {
        stepVar = stepCount;
        stepView = stepCount - stepVar;
        textViewSteps.setText("Schritte:" + stepView);
        sensorDaten.setCalibration(true);

    }

    public void onClickDefineWay(View view) {


        Log.d(TAG, "mode: " + mod);

        if (mod % 2 == 0) {
            buttonDefineWay.setText("Stop define way");
            onClickDefineWay = true;

        }
        if (mod % 2 > 0) {
            buttonDefineWay.setText("Start define way");
            onClickDefineWay = false;
            // listOfWays.addWay("test",listWayPoint);


        }
        mod++;


    }
    public ArrayList<WayPoint> getListWayPoint() {
        return listWayPoint;
    }

    public void onClickShowList(View view) {

        for (WayPoint s : listWayPoint) {
            textViewListe.setText(textViewListe.getText() + s.toString() + System.lineSeparator());


        }
    }

    public void defineWay() {


        WayPoint newPoint = new WayPoint(sensorDaten.getAzimut(),
                stepView, sensorDaten.getHighOverSee());

        listWayPoint.add(index, newPoint);
        Log.d(TAG, "defineWay() ->" + newPoint);
        Log.d(TAG, "defineWay() ->" + listWayPoint.get(index).toString());
        Log.d(TAG, "defineWay() ->" + index);
        index++;


    }

    public void onResume() {
        super.onResume();
        magnetSensorManager.registerListener(magnetListener, magnetSensorManager
                .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);

        magnetSensorManager.registerListener(magnetListener, magnetSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        pressureSensorManager.registerListener(pressureStepListner, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);


        stepSensorManager.registerListener(pressureStepListner, stepCountSensor, SensorManager.SENSOR_DELAY_NORMAL);


    }
    public void onPause() {
        super.onPause();
        pressureSensorManager.unregisterListener(pressureStepListner);
        //magnetSensorManager.unregisterListener(magnetListener);
        stepSensorManager.unregisterListener(pressureStepListner);
    }




    }




