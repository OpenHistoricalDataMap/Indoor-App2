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


    private class AsyncDijkstraImpl extends AsyncTask<String, String, Integer>
    {
        private DijkstraAlgorithm dijk;
        LinkedList<Node> path;
        private AlertDialog.Builder builder;
        private AlertDialog alertDialog;
        int waitMilliseconds = 1000;
        private boolean cancelthis;
        private boolean poiReached;
        Node currentNode;

        @Override
        protected Integer doInBackground(String... params) {
            Graph graph = StartActivity.graph;
            dijk = new DijkstraAlgorithm(graph);
            dijk.execute(params[0]);
            path = dijk.getPath(params[1]);
            ThatApp scanapp = ThatApp.getThatApp();
            List<ScanResult> scanResults  = scanapp.getWifiManager().getScanResults();
            if(path != null) {
                currentNode = path.get(0);
                path.remove(0);
                publishProgress("Please go to POI "+path.get(0).getId());
                while(!cancelthis){

                    //COMPARE SCANRESULT with to be reached POI. -> set currentNode
                    if (currentNode.getId() == path.get(0).getId() || poiReached) {
                        if(path.size() == 1)
                        {
                            publishProgress("Congratulations you've reached your final destination");
                            try{
                                Thread.sleep(5000);
                            }
                            catch (InterruptedException e)
                            {
                                break;
                            }
                            break;
                        }
                        path.remove(0);
                        publishProgress("Please go to POI" + path.get(0).getId());
                        poiReached = false;
                    }
                    try {
                        Thread.sleep(1000);
                        scanResults = scanapp.getWifiManager().getScanResults();
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }

            return 1;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            cancelthis = false;
            poiReached = false;
            this.builder = new AlertDialog.Builder(SelectTarget.this);
            this.builder.setMessage("Initialize");
            this.builder.setTitle("Navigation in Progresss");
            this.builder.setNegativeButton("Cancel!", null);
            this.builder.setNeutralButton("Forward", null);
            this.alertDialog = builder.create();
            this.alertDialog.setCancelable(false);
            this.alertDialog.setCanceledOnTouchOutside(false);
            this.alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button buttonNeutral = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEUTRAL);
                    buttonNeutral.setOnClickListener(new View.OnClickListener(){

                        @Override
                        public void onClick(View v) {
                            poiReached = true;
                        }
                    });
                    Button buttonCancel = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                    buttonCancel.setOnClickListener(new View.OnClickListener(){

                        @Override
                        public void onClick(View v) {
                            cancelthis = true;
                        }
                    });
                }
            });
            this.alertDialog.show();

        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            cancelthis = false;
            poiReached = false;
            this.alertDialog.hide();
            this.alertDialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            this.alertDialog.setMessage(values[0]);
        }

        @Override
        protected void onCancelled(Integer integer) {
            super.onCancelled(integer);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

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
        goToVisualizeDjikstra(node.getId());
    }

    private void goToVisualizeDjikstra(String targetNodeID)
    {
        this.targetNode = targetNodeID;
        AsyncDijkstraImpl asyncDijkstra = new AsyncDijkstraImpl();
        asyncDijkstra.execute(startNode,targetNodeID);
    }
}
