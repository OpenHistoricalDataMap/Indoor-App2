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
import android.widget.EditText;
import android.widget.TextView;

import htw_berlin.de.mapmanager.R;

public class StepCountActivity extends AppCompatActivity {

    private TextView textViewStpCount, textViewStepInMeter,textViewAvgDistanceStep;
    private Button buttonStepCalibration,buttonStepsToMeter;
    private EditText editTextDinstance;
    private float stepCount;
    private float stepVar;
    private float stepView;
    private float disdanceInMeter;
    private float avgStepDistance;

    private SensorManager sm;
    private Sensor stepSensor;
    private static final String LOG_TAG ="StepCountActivity";

    //Test Distanz
    Float distanz =0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_count);
        stepCount=0f;
        stepView=0f;
        stepVar=0f;
        disdanceInMeter=0;

        //Anmelder und Inizialisieren aller Bedienelemente
        textViewStpCount=(TextView)findViewById(R.id.texViewStepCount);
        textViewStepInMeter=(TextView)findViewById(R.id.textViewStepInMeter);
        textViewAvgDistanceStep=(TextView)findViewById(R.id.textViewAvgDistanceStep);
        buttonStepCalibration=(Button)findViewById(R.id.buttonStepCalibration);
        editTextDinstance=(EditText)findViewById(R.id.editTextDistanz);
        buttonStepsToMeter=(Button)findViewById(R.id.butonStepsToMeter);

        //Alle Sensoren anmelden und Inizialisiern
        sm=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        stepSensor=sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        //Ausgabe auf View aktuelle Schritte Seit der Kalibrierung
        textViewStpCount.setText("Schritte: "+ stepView);

        Log.d(LOG_TAG,"getTxt:+ "+editTextDinstance);
        Log.d(LOG_TAG,"In Step!");

    }
    public float averageMeter(float steps,float selectedDistance){

        float dist=selectedDistance/steps;

        return dist;
    }
    public void onPause(){
        super.onPause();
        sm.unregisterListener(listener);
    }
    public void onResume(){
        super.onResume();
        sm.registerListener(listener,stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onClickButtonStepCalibration(View view){

        stepVar=stepCount;
        stepView=stepCount-stepVar;
        textViewStpCount.setText("Schritte:"+stepView);
        //Test Float Wert Auslesen





    }
    public void onClickButtonStepsToMeter(View view){

        distanz = Float.valueOf(String.valueOf(editTextDinstance.getText()));
        avgStepDistance=averageMeter(stepView,distanz);
        textViewAvgDistanceStep.setText("avgMeter: "+avgStepDistance);

    }

    SensorEventListener listener=new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            if(event.sensor.getType()== Sensor.TYPE_STEP_COUNTER){
                stepCount=event.values[0];
                stepView=stepCount-stepVar;
                textViewStpCount.setText("Schritte: "+ stepView);
                disdanceInMeter=stepView*avgStepDistance;
                textViewStepInMeter.setText("Meter: "+disdanceInMeter);
                Log.d(LOG_TAG,"Schritte VALUES:0: "+stepCount);
                Log.d(LOG_TAG,"StepVar "+stepVar);
                Log.d(LOG_TAG,"StepView "+stepView);
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
}
