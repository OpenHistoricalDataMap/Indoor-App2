package htw_berlin.de.mapmanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import htw_berlin.de.mapmanager.graph.Node;
import htw_berlin.de.mapmanager.graph.TranslatableAdjacencyMatrixGraph;
import htw_berlin.de.mapmanager.persistence.PersistenceManager;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    public final static String EXTRA_MESSAGE_POI_ID = "htw_berlin.de.MapManager.POI_ID";
    public static TranslatableAdjacencyMatrixGraph graph;
    private PoiListAdapter adapter;
    private Button newPoiButton;
    private ListView listView;
    private TextView poiNameTextView;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Liste der POI");

        //gsonTest();
       loadGraphData();
        initGuiElements();
    }

    private void gsonTest() {

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("yyyy/MM/dd hh:mm a");
        gson = gsonBuilder.create();


        // create the 'gson' saveable graph
        try {
            JsonReader reader = new JsonReader(new FileReader(new File(PersistenceManager.getGraphStorageDir(), "json_graph.json")));

            htw_berlin.de.mapmanager.graph.gson.TranslatableAdjacencyMatrixGraph gsonGraph = gson.fromJson(reader, htw_berlin.de.mapmanager.graph.gson.TranslatableAdjacencyMatrixGraph.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initGuiElements() {
        // new POI Name text field
        poiNameTextView = (TextView) findViewById(R.id.newPOIName);

        // new POI button
        newPoiButton = (Button) findViewById(R.id.newPOI);

        // list view
        listView = (ListView) findViewById(R.id.poiListView);
        adapter = new PoiListAdapter(graph.getNodes(), this);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(this);
        listView.setClickable(true);
    }

    /** Called when the user clicks the new POI button */
    public void newPOI(View view){
        final String poiName = poiNameTextView.getText().toString();
        if(poiName != null &! poiName.equalsIgnoreCase("")){
           graph.addNewNode(poiName);

            // refresh gui
            adapter.notifyDataSetChanged();
        }
        else {
            showSimpleAlert("Invalid POI Name", "Please insert a valid POI Name (minimum 1 non-special Character)");
        }
    }

    private void showSimpleAlert(String title, String message){
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
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
