package htw_berlin.de.mapmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import htw_berlin.de.mapmanager.graph.Node;
import htw_berlin.de.mapmanager.graph.TranslatableAdjacencyMatrixGraph;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    public final static String EXTRA_MESSAGE_POI_ID = "htw_berlin.de.MapManager.POI_ID";
    public static TranslatableAdjacencyMatrixGraph graph;
    private PoiListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadGraphData();


        final ListView listView = (ListView) findViewById(R.id.poiListView);
        adapter = new PoiListAdapter(new ArrayList<>(graph.getNodes()), this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        listView.setClickable(true);
    }

    /** Called when the user clicks the new POI button */
    public void newPOI(View view ){
        // TODO: add new POI to list of POIs
        // add new POI to the list of POIs
    }

    /** Called when the user taps on a POI in the list */
    public void goToManageConnections(Node nodeSelected){
        Intent intent = new Intent(this, ManagePOIConnectionsActivity.class);
        intent.putExtra(EXTRA_MESSAGE_POI_ID,  nodeSelected.id);
        startActivity(intent);
    }

    public void loadGraphData() {
        graph = new TranslatableAdjacencyMatrixGraph(
                getResources().openRawResource(
                        getResources().getIdentifier("places_net",
                                "raw", getPackageName())),
                getResources().openRawResource(
                        getResources().getIdentifier("places",
                                "raw", getPackageName())));

    }

    /**
     * android:clickable="false" attribute in containing Layout tag in the layout/list_item.xml is
     * necessary in order to catch the event from the activity.
     * However, I suppose this removes (or makes extremely difficult) the possibility to execute
     * different actions with different elements being pressed.
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Node node = adapter.getItem(position);
        goToManageConnections(node);
    }
}
