package htw_berlin.de.mapmanager.compass;

import android.content.Intent;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

import htw_berlin.de.mapmanager.MainActivity;
import htw_berlin.de.mapmanager.R;
import htw_berlin.de.mapmanager.compass.Compass2Activity;
import htw_berlin.de.mapmanager.persistence.PersistenceManager;


public class DefineEdgeActivity extends AppCompatActivity {

    private static final String LOG_TAG ="DefineEdgeActivity";




    // TODO remove unused mgr?
    SensorManager mgr;
    Button step,compass;
// Id of the destination NodeInterface
    public static final String POI_ID_DESTINATION="POI_ID_DESTINATION";
    private String parentNodeId;
    private String destinationNodeId;

    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_define_edge);


        Bundle extra = getIntent().getExtras();
        parentNodeId= extra.getString(MainActivity.EXTRA_MESSAGE_POI_ID);
        destinationNodeId=extra.getString(POI_ID_DESTINATION);

        compass=(Button)findViewById(R.id.btnGotoCompass);

        initImageView();


    }

    public void onClickCompass(View view){
        Intent intent =new Intent(this,Compass2Activity.class);
        intent.putExtra(MainActivity.EXTRA_MESSAGE_POI_ID,parentNodeId);
        intent.putExtra(POI_ID_DESTINATION,destinationNodeId);
        startActivity(intent);

    }


    private void initImageView(){
        imageView = (ImageView)findViewById(R.id.destinationPOIImage);

        File nodeImageFile = PersistenceManager.getNodeImageFile(destinationNodeId);
        if(!nodeImageFile.exists()){
            imageView.setImageResource(R.mipmap.ic_launcher);
        }
        else {
            Uri nodeImageUri = Uri.fromFile(nodeImageFile);
            imageView.setImageURI(nodeImageUri);
        }
    }




}
