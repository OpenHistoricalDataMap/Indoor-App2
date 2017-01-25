package htw_berlin.de.mapmanager.navi;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.FileNotFoundException;

import htw_berlin.de.mapmanager.MainActivity;
import htw_berlin.de.mapmanager.R;
import htw_berlin.de.mapmanager.StartActivity;
import htw_berlin.de.mapmanager.graph.Graph;
import htw_berlin.de.mapmanager.graph.Node;

import htw_berlin.de.mapmanager.permissions.PermissionManager;
import htw_berlin.de.mapmanager.persistence.PersistenceManager;

public class WhereAmIActivity extends AppCompatActivity {

    private Button findPos;
    private Graph navigationGraph;
    private Node currentNode;
    private Button findWay;
    private TextView currentPos;
    private TextView errorMsg;
    private PermissionManager permissionManager;
    private static final String LOG_TAG = "Navigation";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_where_am__i);

        setTitle("Start Navigation");

        try {
            StartActivity.graph = PersistenceManager.loadGraph();
        } catch (FileNotFoundException e) {
            Log.d(LOG_TAG, "Creating empty graph");
            // data not loaded, create a new graph
            StartActivity.graph = StartActivity.emptyGraph();
        }

        Intent intent = getIntent();
        navigationGraph = StartActivity.graph;


        currentNode = null;
        initPermissions();

        findPos = (Button) this.findViewById(R.id.btnWhereAmI_FindCurrent);
        findWay = (Button) this.findViewById(R.id.btnWhereAmI_FindWay);
        currentPos = (TextView) this.findViewById(R.id.tvWhereAmI_CurrentPos);
        errorMsg = (TextView) this.findViewById(R.id.tvWhereAmI_ErrorMessage);

        findPos.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                currentNode = getCurrentPosition();
                currentPos.setText("You are currently located at: " + currentNode.getId());
            }
        });
        findWay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (currentNode == null) {
                    errorMsg.setText("Your current Position couldn't be detected");
                } else {
                    goToSelectTarget();
                }
            }
        });
    }


    private void initPermissions() {
        permissionManager = new PermissionManager(this);
        permissionManager.checkWifiPermissions();
    }

    private Node getCurrentPosition() {
        return navigationGraph.getNodes().get(0);
    }

    private void goToSelectTarget() {
        final Intent intent = new Intent(this, SelectTarget.class);
        intent.putExtra(MainActivity.EXTRA_MESSAGE_POI_ID, currentNode.getId());
        startActivity(intent);
    }
}
