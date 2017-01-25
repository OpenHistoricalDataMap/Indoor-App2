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

import htw_berlin.de.mapmanager.MainActivity;
import htw_berlin.de.mapmanager.R;
import htw_berlin.de.mapmanager.StartActivity;
import htw_berlin.de.mapmanager.graph.Node;

public class Compass2Activity extends AppCompatActivity {

    private TextView textViewX, textViewDistance, textViewZ, textViewListe;
    private Button calibrationButton, buttonDefineWay;

    //Everage euro step length
    private static final float EURO_STEP_LENGTH= 0.74f;


    //StepCount onSensorChange


    private SensorManager pressureSensorManager, magnetSensorManager, stepSensorManager;
    private float azimut, stepView, stepCount, stepVar;
    private Sensor stepCountSensor, pressureSensor;


    // onClickDefineWay
    private boolean onClickDefineWay;

    private int mod = 0;

    //Steps in meter
    private float stpsInMeter=0;

    // private float nullPressure = 0.0f;
    private static final String TAG = "Magnet";

    //WayPointListe = define a Way
    private ArrayList<WayPoint> listWayPoint = new ArrayList<>();
    private int index;

    //MagnetCalculation
    SensorData sensorDaten = new SensorData();

    // ID of ParentNode to save the "WayPoit" as a way
    private String parentNodeId="";
    private Node parentNode;

    // ID of destiantionNode
    private String destinationNodeID="";
    private Node destinationNode;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass2);


        textViewX = (TextView) findViewById(R.id.txtCompassX);
        textViewDistance = (TextView) findViewById(R.id.txtCompassY);
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

        // ID of ParentNode to save the "WayPoit" as a way
        Bundle extra=getIntent().getExtras();
        //

        //get the parent Node to store the data
        parentNodeId=extra.getString(MainActivity.EXTRA_MESSAGE_POI_ID);
        parentNode = StartActivity.graph.getNode(parentNodeId);

        //get the destination Node
        destinationNodeID=extra.getString(DefineEdgeActivity.POI_ID_DESTINATION);
        destinationNode = StartActivity.graph.getNode(destinationNodeID);

        Log.d(TAG, "Magnet Klasse");
        Log.d(TAG,"Ausgabe ParentNode: "+ parentNode.getId());
        Log.d(TAG,"Ausgabe DestinationNode: "+ destinationNode.getId());


        ArrayList<Edge> test=parentNode.getEdges();
        for(Edge edge : test)
        {
            Log.d(TAG,"Ausagabe DijkstraEdge: "+edge);
        }
    }

    /**
     * Listener for the compass, use the magnetic field sensor and the accelometer to find the
     * right direction.§
     */

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


    /**
     * Listener for the pressure sensor, is used to calculate the high to a defined point.
     */
    private SensorEventListener pressureStepListner = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {

                sensorDaten.setPrssureValue(event.values[0]);
               // Log.d(TAG, "" + sensorDaten.getPrssureValue());
                sensorDaten.calcHigh();
                textViewZ.setText("Höhe über Null " + sensorDaten.getHighOverSee());
            }
            if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {

                stepCount = event.values[0];
                stepView = stepCount - stepVar;
                //Calculate to meter
                stpsInMeter=stepView*EURO_STEP_LENGTH;
                textViewDistance.setText("Meter: " + stpsInMeter);
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
        stpsInMeter=0.0f;
        listWayPoint=new ArrayList<>();
        textViewDistance.setText("Schritte:" + stpsInMeter);
        calibrationButton.setClickable(false);


    }

    public void onClickDefineWay(View view) {


        //Log.d(TAG, "mode: " + mod);

        if (mod % 2 == 0) {
            buttonDefineWay.setText("Stop define way");
            onClickDefineWay = true;

        }
        if (mod % 2 > 0) {
            buttonDefineWay.setText("Start define way");
            onClickDefineWay = false;

            //


            //edge=new DijkstraEdge(stpsInMeter,parentNodeId,destinationNodeID,
              //      parentNodeId+destinationNodeID,listWayPoint);

            /* tognimat: This is old from fhausler. The edge exists already (as soon as we checked
             * the checkbox), what we define here is simply the way points that are part of that edge
             */
            //MainActivity.graph.addEdge(new DijkstraEdge(stpsInMeter,parentNodeId,destinationNodeID,parentNodeId+destinationNodeID,listWayPoint));
            Edge edgeBetween = parentNode.getEdge(destinationNode);
            edgeBetween.setWay(listWayPoint);


           // MainActivity.graph.getNode(parentNodeId).setWay(listWayPoint);


            Log.d(TAG,"Way: "+ edgeBetween);


        }
        mod++;


    }


    /**
     * Show you the list of saved WayPoints
     * @param view
     */
    public void onClickShowList(View view) {

        textViewListe.setText("Liste: "+parentNode.getEdge(destinationNode));

    }

    public void onClickSetHighNull(View view){
        sensorDaten.setCalibration(true);
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
        magnetSensorManager.unregisterListener(magnetListener);
        stepSensorManager.unregisterListener(pressureStepListner);
    }





    }




