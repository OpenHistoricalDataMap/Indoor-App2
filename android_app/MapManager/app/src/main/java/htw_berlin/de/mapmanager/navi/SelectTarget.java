package htw_berlin.de.mapmanager.navi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.LinkedList;
import java.util.List;

import htw_berlin.de.mapmanager.MainActivity;
import htw_berlin.de.mapmanager.R;
import htw_berlin.de.mapmanager.StartActivity;
import htw_berlin.de.mapmanager.graph.Graph;
import htw_berlin.de.mapmanager.graph.Node;
import htw_berlin.de.mapmanager.graph.dijkstra.DijkstraAlgorithm;
import htw_berlin.de.mapmanager.ui.adapter.PoiListAdapter;
import htw_berlin.de.mapmanager.wlan.ThatApp;

public class SelectTarget extends AppCompatActivity implements AdapterView.OnItemClickListener  {

    private String startNode,targetNode;
    private ListView lv_selectTarget;
    private PoiListAdapter adapter;

    public static final String TARGET_POI_ID = "targetPoiID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_target);

        ThatApp.initThatApp(this);
        Intent intent = getIntent();
        startNode = intent.getStringExtra(MainActivity.EXTRA_MESSAGE_POI_ID);
        if(startNode == null || startNode == ""){
            throw new IllegalArgumentException("The given poiId is invalid: " + startNode);
        }

        this.lv_selectTarget = (ListView) this.findViewById(R.id.selectTargetList);
        this.lv_selectTarget.setOnItemClickListener(this);
        this.lv_selectTarget.setClickable(true);
        adapter = new PoiListAdapter(StartActivity.graph.getNodes(), this);
        this.lv_selectTarget.setAdapter(adapter);

        this.setTitle("Select Target");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Node node = adapter.getItem(position);
        if(!node.getId().equalsIgnoreCase(startNode))
            goToVisualizeDjikstra(node.getId());
    }

    private void goToVisualizeDjikstra(String targetNodeID)
    {
        this.targetNode = targetNodeID;
        Intent intent = new Intent(this, VisualDijkstra_Activity.class);
        intent.putExtra(MainActivity.EXTRA_MESSAGE_POI_ID, startNode);
        intent.putExtra(SelectTarget.TARGET_POI_ID, targetNode);
        startActivity(intent);

    }
}
