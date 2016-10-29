package htw_berlin.de.mapmanager;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DisplayPOIDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_poi_detail);

        Intent intent = getIntent();
        String poiId = intent.getStringExtra(MainActivity.EXTRA_MESSAGE_POI_ID);
        // TODO: using the poiId, get the POI object from the list
        String poiName = "McDonald's";

        // TODO: get the ArrayList of images of the POI

        TextView textViewPOIName = (TextView) findViewById(R.id.poiNameTextView);
        textViewPOIName.setTextSize(40);
        textViewPOIName.setText(poiId);

    }
    /** Called when user clicks on "Manage Connections" button */
    public void poiManageConnections(View view) {
        Intent intent = new Intent(this, ManagePOIConnectionsActivity.class);
        // TODO: pass unique id of POI element instead of constant -1
        intent.putExtra(MainActivity.EXTRA_MESSAGE_POI_ID, "1");
        startActivity(intent);
    }

    public void takePicture(View view) {
    }
}
