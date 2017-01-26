package htw_berlin.de.mapmanager.navi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.htwberlin.f4.ai.ma.fingerprint.SignalStrengthInformationInterface;
import htw_berlin.de.mapmanager.MainActivity;
import htw_berlin.de.mapmanager.R;
import htw_berlin.de.mapmanager.StartActivity;
import htw_berlin.de.mapmanager.graph.Node;
import htw_berlin.de.mapmanager.graph.SignalStrengthInformation;
import htw_berlin.de.mapmanager.graph.dijkstra.DijkstraAlgorithm;
import htw_berlin.de.mapmanager.ui.adapter.DijkstraAdapter;
import htw_berlin.de.mapmanager.wlan.ThatApp;

public class VisualDijkstra_Activity extends AppCompatActivity {

    private ArrayList<Node> pathlist;
    private String startNode, targetNode;
    private CharSequence textviewString;
    private ListView dijkstraview;
    private TextView tvDijkstra;
    private DijkstraAdapter adapter;
    private DijkstraAlgorithm dijkstraAlgorithm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_dijkstra_);

        Intent intent = getIntent();
        startNode = intent.getStringExtra(MainActivity.EXTRA_MESSAGE_POI_ID);
        if(startNode == null || startNode == ""){
            throw new IllegalArgumentException("The given poiId is invalid: " + startNode);
        }
        targetNode = intent.getStringExtra(SelectTarget.TARGET_POI_ID);
        if(targetNode == null || targetNode == ""){
            throw new IllegalArgumentException("The given target poId is invalid: "+targetNode);
        }

        if(targetNode != startNode) {
            dijkstraAlgorithm = new DijkstraAlgorithm(StartActivity.graph);
            dijkstraAlgorithm.execute(startNode);
            LinkedList<Node> path = dijkstraAlgorithm.getPath(targetNode);
            pathlist = new ArrayList<Node>();
            for (Node n : path) {
                pathlist.add(n);
            }
        }
        else{
            pathlist.add(StartActivity.graph.getNode(startNode));
        }
        tvDijkstra = (TextView) findViewById(R.id.textViewDijkstra);
        textviewString = tvDijkstra.getText();
        dijkstraview = (ListView) findViewById(R.id.lv_dijkstra);
        adapter = new DijkstraAdapter(pathlist, this);
        dijkstraview.setAdapter(adapter);
        AsyncChecks checks = new AsyncChecks();
        checks.execute(pathlist);
    }

    private class AsyncChecks extends AsyncTask<ArrayList<Node>,Integer,Integer>
    {
        private String ssid;
        int waitMilliseconds = 200;
        private boolean isFinished;
        ArrayList<Node> path;

        @Override
        protected Integer doInBackground(ArrayList<Node>... params) {
            Integer counter = 0;
            Node currentNode = path.get(0);
            List<ScanResult> scanResults = ThatApp.getThatApp().getWifiManager().getScanResults();
            while(!isFinished){


                //CALCULATE CURRENT NODE FROM SCANRESULTS
                //TODO @zoeddle

                if(currentNode == path.get(path.size()-1))
                {
                    isFinished = true;
                }
                else if(currentNode == this.path.get(counter))
                {
                    publishProgress(counter);
                    counter++;
                }

                //REFRESH SCANRESULTS AFTER 200 MS
                try {
                    Thread.sleep(waitMilliseconds);
                    scanResults = ThatApp.getThatApp().getWifiManager().getScanResults();
                }
                catch(InterruptedException e) {
                    break;
                }
            }
            return 1;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(VisualDijkstra_Activity.this);
            ssid = sharedPrefs.getString("pref_ssid", "BVG-Wifi");
            StartActivity.graph.setSsid(ssid);
            path = VisualDijkstra_Activity.this.pathlist;
            isFinished = false;

        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            DijkstraAdapter.ViewHolder vh = (DijkstraAdapter.ViewHolder) VisualDijkstra_Activity.this.dijkstraview.getChildAt(values[0]).getTag();
            VisualDijkstra_Activity.this.tvDijkstra.setText(textviewString+"\n"+path.get(values[0]).getId());
            vh.cBoxReached.setChecked(true);
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


}
