package htw_berlin.de.mapmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE_POI_ID = "htw_berlin.de.MapManager.POI_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /** Called when the user clicks the new POI button */
    public void newPOI(View view ){
        // TODO: add new POI to list of POIs
        // add new POI to the list of POIs
    }

    /** Called when the user taps on a POI in the list */
    public void poiDetail(View view){
        Intent intent = new Intent(this, DisplayPOIDetailActivity.class);
        // TODO: pass unique id of POI element instead of constant -1
        intent.putExtra(EXTRA_MESSAGE_POI_ID, "1");
        startActivity(intent);
    }
}
