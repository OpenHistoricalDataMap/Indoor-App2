package htw_berlin.de.mapmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;

import htw_berlin.de.mapmanager.graph.Node;
import htw_berlin.de.mapmanager.graph.dijkstra.DijkstraAlgorithm;
import htw_berlin.de.mapmanager.permissions.PermissionManager;
import htw_berlin.de.mapmanager.persistence.PersistenceManager;

import htw_berlin.de.mapmanager.persistence.WritePermissionException;

import htw_berlin.de.mapmanager.prefs.SettingsActivity;

import htw_berlin.de.mapmanager.ui.adapter.PoiListAdapter;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    public final static String EXTRA_MESSAGE_POI_ID = "htw_berlin.de.MapManager.POI_ID";
    public static float nullPressure = 0;
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
        permissionManager.checkExternalWritePermissions();

        persistenceManager = new PersistenceManager(permissionManager);

        //todo "delete all data" button ?

        loadGraph();
        initGUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_item:

                Intent intent = new Intent(this, SettingsActivity.class);
                this.startActivity(intent);

                break;
            case R.id.dump_graph_item:
                try {
                    persistenceManager.dumpGraph();
                } catch (IOException e) {
                    // dump unsuccessful
                    e.printStackTrace();
                }
                break;
            case R.id.reload_graph_item:
                loadGraph();
                updateAdapterAndListView();
                Toast.makeText(MainActivity.this, "Graph reloaded from memory", Toast.LENGTH_LONG).show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    protected void loadGraph() {
        try {
            StartActivity.graph = PersistenceManager.loadGraph();
        } catch (FileNotFoundException e) {
            Log.d(LOG_TAG, "Creating empty graph");
            // data not loaded, create a new graph
            StartActivity.graph = StartActivity.emptyGraph();
            initGUI();
        }

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
        adapter = new PoiListAdapter(StartActivity.graph.getNodes(), this);
        listView.setAdapter(adapter);

        // clickable and click listener
        listView.setOnItemClickListener(this);
        listView.setClickable(true);

        // long-clickable and long-click listneer
        listView.setOnItemLongClickListener(this);
        listView.setLongClickable(true);
    }

    // TODO: Someone use dijkstra
    private void dijkstra() {
        // Construct it just when the graph changes!
        final DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(StartActivity.graph);

        // has to be executed everytime the current position changes
        dijkstra.execute("aaa");
        //dijkstra.execute("node_id_of_current_position");

        // pick the path to the destination
        //LinkedList<NodeInterface> path = dijkstra.getPath("node_id_of_destination");
        LinkedList<Node> path = dijkstra.getPath("zzz");

        if (path == null) {
            Log.d(LOG_TAG, "No path found");
            Toast.makeText(this, "No path found", Toast.LENGTH_LONG).show();
        } else {
            Log.d(LOG_TAG, "Path found");
            for (Node node : path) {
                Log.d(LOG_TAG, node.toString());
            }
        }
    }

    /**
     * Called when the user clicks the new POI button
     */
    public void newPOI(View view) {
        final String poiName = poiNameTextView.getText().toString();
        poiNameTextView.setText("");
        if (!poiName.equalsIgnoreCase("")) {
            // Adding just if the node does not exist.
            // Warning! With true --> "override" --> edges will be deleted!!
            boolean added = StartActivity.graph.addNode(new Node(poiName), false);

            if (!added) {
                Toast.makeText(this, "POI Not added. Check if the name already exists and " +
                        "delete the node eventually.", Toast.LENGTH_LONG).show();
            }

            // close the keyboard
            View currentFocus = this.getCurrentFocus();
            if (currentFocus != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            }

            // refresh gui (not going to work if Graph.nodes is a (Linked)HashSet
            // not going to work also with the new graph that returns copies of the lists for
            // security reasons
            //adapter.notifyDataSetChanged();

            // update the whole adapter
            updateAdapterAndListView();
        } else {
            showSimpleAlert("Invalid POI Name", "Please insert a valid POI Name " +
                    "(minimum 1 non-special Character)");
        }


    }

    private void showSimpleAlert(String title, String message) {
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

    /**
     * Called when the user taps on a POI in the list
     */
    public void goToManageConnections(Node nodeSelected) {
        Intent intent = new Intent(this, POIDetailsActivity.class);
        intent.putExtra(EXTRA_MESSAGE_POI_ID, nodeSelected.getId());
        startActivity(intent);
    }


    /**
     * android:clickable="false" attribute in containing Layout tag in the layout/list_item.xml is
     * necessary in order to catch the event from the activity.
     * However, I suppose this removes (or makes extremely difficult) the possibility to execute
     * different actions with different elements being pressed.
     *
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

    /**
     * @param parent
     * @param view
     * @param position
     * @param id
     * @return true if the callback consumed the long click, false otherwise
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final Node node = adapter.getItem(position);
        // TODO: AlertDialog is blocking! Use @cbos method for more!
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked

                        try {
                            deleteNodePermanently(node);
                            updateAdapterAndListView();
                        } catch (WritePermissionException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Are you sure?\n This will delete permanently the POI " +
                "and all its contents")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();


        return true;
    }

    /**
     * Call this just if the list of nodes has been changed (if new elements have been added or
     * existing node have been removed).
     * To refresh the listview (for example if the default picture of a node has changed), use
     * listView.invalidateViews();
     * as it is done as well in the method onStart();
     */
    private void updateAdapterAndListView() {
        // update adpater and list view
        adapter = new PoiListAdapter(StartActivity.graph.getNodes(), MainActivity.this);
        listView.setAdapter(adapter);
        listView.invalidateViews();
    }


    /**
     * Deletes the node and all its files permanently and stores the new graph
     *
     * @param node
     * @throws WritePermissionException
     * @throws IOException
     */
    public void deleteNodePermanently(Node node) throws WritePermissionException, IOException {
        // delete the measurements file
        persistenceManager.deleteNodeMeasurementsFile(node);

        // delete the pics
        persistenceManager.deleteAlbumStorageDir(node);


        // remove the node from the graph
        StartActivity.graph.removeNode(node.getId());

        // store the updated graph
        persistenceManager.storeGraph(StartActivity.graph);
    }
}
