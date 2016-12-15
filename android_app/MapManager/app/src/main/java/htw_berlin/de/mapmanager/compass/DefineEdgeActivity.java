package htw_berlin.de.mapmanager.compass;

import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import htw_berlin.de.mapmanager.R;
import htw_berlin.de.mapmanager.compass.Compass2Activity;
import htw_berlin.de.mapmanager.compass.StepCountActivity;

public class DefineEdgeActivity extends AppCompatActivity {

    private static final String LOG_TAG ="DefineEdgeActivity";


    // TODO remove unused mgr?
    SensorManager mgr;
    Button step,compass;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_define_edge);
        Log.d(LOG_TAG,"Aufruf main");

        compass=(Button)findViewById(R.id.btnGotoCompass);
        step =(Button)findViewById(R.id.btnGotoStepCounter);


    }

    public void onClickCompass(View view){
        Intent intent =new Intent(this,Compass2Activity.class);
        startActivity(intent);

    }

    public void onClickStepCounter(View view){
        Intent intent =new Intent(this,StepCountActivity.class);
        startActivity(intent);
    }




}