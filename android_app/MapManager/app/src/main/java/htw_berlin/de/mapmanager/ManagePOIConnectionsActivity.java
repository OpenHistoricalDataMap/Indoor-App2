package htw_berlin.de.mapmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ManagePOIConnectionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_poiconnections);


        Intent intent = getIntent();
        String poiId = intent.getStringExtra(MainActivity.EXTRA_MESSAGE_POI_ID);
        // TODO: using the poiId, get the POI object from the list
        String poiName = "McDonald's";

        // TODO: get the ArrayList of images of the POI
        // TODO: get the edges and corresponding nodes (other POIs) that are directly connected to the current POI

    }



}
