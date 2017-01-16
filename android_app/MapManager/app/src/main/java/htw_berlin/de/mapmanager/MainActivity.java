package htw_berlin.de.mapmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.FileNotFoundException;

import htw_berlin.de.mapmanager.graph.Graph;
import htw_berlin.de.mapmanager.graph.Node;
import htw_berlin.de.mapmanager.permissions.PermissionManager;
import htw_berlin.de.mapmanager.persistence.PersistenceManager;
import htw_berlin.de.mapmanager.ui.adapter.PoiListAdapter;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    public final static String EXTRA_MESSAGE_POI_ID = "htw_berlin.de.MapManager.POI_ID";
    public static Graph graph;
    private PoiListAdapter adapter;
    private Button newPoiButton;
    private ListView listView;
    private TextView poiNameTextView;

    private PermissionManager permissionManager;
    public PersistenceManager persistenceManager;

    private static final String LOG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Liste der POI");


        permissionManager = new PermissionManager(this);
        permissionManager.checkExternalReadPermissions();

        persistenceManager = new PersistenceManager(permissionManager);

        //todo "delete all data" button ?


        try {
            graph = PersistenceManager.loadGraph();
        } catch (FileNotFoundException e) {
            // data not loaded, create a new graph
            graph = emptyGraph();
        }


      initGUI();
    }

    private static final Graph emptyGraph() {
        return new Graph();
    }


    @Override
    protected void onStart() {
        super.onStart();

        // just redraw all the views (in particular the images) in case new pictures have been taken
        // Attention: this does not update the adapter's listModel, it just redraws what is already available!
        listView.invalidateViews();
    }



    private void initGUI() {
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
        poiNameTextView.setText("");
        if(!poiName.equalsIgnoreCase("")){

            graph.addNode(new Node(poiName));

            // close the keyboard
            View currentFocus = this.getCurrentFocus();
            if (currentFocus != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            }

            // refresh gui (not going to work if Graph.nodes is a (Linked)HashSet
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
        Intent intent = new Intent(this, POIDetailsActivity.class);
        intent.putExtra(EXTRA_MESSAGE_POI_ID,  nodeSelected.getId());
        startActivity(intent);
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
