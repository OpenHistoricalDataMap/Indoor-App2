package htw_berlin.de.mapmanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;

import htw_berlin.de.mapmanager.graph.Node;
import htw_berlin.de.mapmanager.permissions.PermissionManager;
import htw_berlin.de.mapmanager.persistence.PersistenceManager;

public class POIConnectionsActivity extends AppCompatActivity {

    private static final String LOG_TAG = "POIConnectionsActivity";

    private ListView listView;
    private ConnectionListAdapter adapter;


    private PermissionManager permissionManager;
    private Node parentNode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poiconnections);

        // Get information about the connections
        Intent intent = getIntent();
        int poiId = intent.getIntExtra(MainActivity.EXTRA_MESSAGE_POI_ID, -1);
        if(poiId == -1){
            throw new IllegalArgumentException("The given poiId is invalid: " + poiId);
        }

        this.parentNode = MainActivity.graph.getNodeById(poiId);
        setTitle(MainActivity.graph.getNodeAsText(parentNode));

        initPermissions();
        initListView();
    }

    private void initPermissions() {
        permissionManager = new PermissionManager(this);
        permissionManager.checkExternalWritePermissions();
        permissionManager.checkExternalReadPermissions();
    }



    private void initListView() {

        listView = (ListView) findViewById(R.id.connectionListView);
        ArrayList<Node> allOtherNodes = new ArrayList<>();
        allOtherNodes.addAll(MainActivity.graph.getNodes());
        allOtherNodes.remove(parentNode);
        adapter = new ConnectionListAdapter(parentNode, allOtherNodes, this);
        listView.setAdapter(adapter);

    }



    @Override
    public void onBackPressed() {
        // TODO: do you want to be able to cancel?
        askForSave();
    }

    /**
     * Save to file if user presses yes
     */

    private void askForSave() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                POIConnectionsActivity.this);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                PersistenceManager persistenceManager = new PersistenceManager(permissionManager);
                //persistenceManager.storeGraph(MainActivity.graph);
                try {
                    persistenceManager.storeGraph(MainActivity.graph);
                    //persistenceManager.storeGraph(MainActivity.graph);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(LOG_TAG, "Storing the json graph unseccsful");
                }

                finish();
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        alertDialog.setMessage("Do you want to save the changes to file before exiting?");
        alertDialog.setTitle("Save changes to file?");
        alertDialog.show();
    }
}
