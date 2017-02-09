package htw_berlin.de.mapmanager.navi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import de.htwberlin.f4.ai.ma.fingerprint.Fingerprint;
import de.htwberlin.f4.ai.ma.fingerprint.FingerprintFactory;
import de.htwberlin.f4.ai.ma.fingerprint.SignalInformationInterface;
import de.htwberlin.f4.ai.ma.fingerprint.SignalStrengthInformationInterface;
import htw_berlin.de.mapmanager.MainActivity;
import htw_berlin.de.mapmanager.R;
import htw_berlin.de.mapmanager.StartActivity;
import htw_berlin.de.mapmanager.graph.Node;
import htw_berlin.de.mapmanager.graph.SignalInformation;
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

    private AsyncChecks checks;
    private Fingerprint fingerprint = FingerprintFactory.getFingerprint();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_dijkstra_);
        pathlist = new ArrayList<>();

        tvDijkstra = (TextView) findViewById(R.id.textViewDijkstra);
        textviewString = tvDijkstra.getText();
        dijkstraview = (ListView) findViewById(R.id.lv_dijkstra);
        adapter = new DijkstraAdapter(pathlist, this);
        dijkstraview.setAdapter(adapter);
    }

    @Override
    public void onResume() {  // After a pause OR at startup
        super.onResume();

        Intent intent = getIntent();
        startNode = intent.getStringExtra(MainActivity.EXTRA_MESSAGE_POI_ID);
        if (startNode == null || startNode.isEmpty()) {
            throw new IllegalArgumentException("The given poiId is invalid: " + startNode);
        }
        targetNode = intent.getStringExtra(SelectTarget.TARGET_POI_ID);
        if (targetNode == null || targetNode.isEmpty()) {
            throw new IllegalArgumentException("The given target poId is invalid: " + targetNode);
        }

        checks = new AsyncChecks();
        checks.execute();

        if(targetNode != startNode) {
            dijkstraAlgorithm = new DijkstraAlgorithm(StartActivity.graph);
            dijkstraAlgorithm.execute(startNode);
            LinkedList<Node> path = dijkstraAlgorithm.getPath(targetNode);
            pathlist = new ArrayList<Node>();
            for (Node n : path) {
                pathlist.add(n);
            }

            adapter.clear();
            adapter.addAll(path);

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
        checks.execute();

        //hier zufügen?
        fingerprint.setAllNodes(pathlist);
    }

    @Override
    protected void onPause() {
        super.onPause();
        checks.isFinished = true;
    }

    private class AsyncChecks extends AsyncTask<Void, Integer, Void> {
        private String ssid;
        int waitMilliseconds = 200;
        private boolean isFinished;
        ArrayList<Node> path;

        @Override
        protected Void doInBackground(Void... params) {
            Integer counter = 0;
            Node currentNode = path.get(0);
            List<ScanResult> scanResults = ThatApp.getThatApp().getWifiManager().getScanResults();

            while (!isFinished) {
                //CALCULATE CURRENT NODE FROM SCANRESULTS
                //TODO @zoeddle
                List<Node> actuallyNodeList = new ArrayList<Node>();

                for (ScanResult sr : scanResults) {

                    if (sr.SSID.equals(ssid)) {
                        List<SignalInformation> signalInformationList = new ArrayList<>();
                        List<SignalStrengthInformation> signalStrenghtList = new ArrayList<>();
                        SignalStrengthInformation signal = new SignalStrengthInformation(sr.BSSID,sr.level);
                        signalStrenghtList.add(signal);
                        SignalInformation signalInformation = new SignalInformation("",signalStrenghtList);
                        signalInformationList.add(signalInformation);
                        Node node = new Node(null,0,signalInformationList);
                        actuallyNodeList.add(node);
                    }
                }
                fingerprint.setActuallyNode(actuallyNodeList);
                String actuallyNode = fingerprint.getCalculatedPOI();
                int position = -1;
                position = path.indexOf(actuallyNode);
                if (position == -1) {
                    //nicht gefunden
                } else {
                    currentNode= path.get(position);
                }


                if (currentNode == path.get(path.size() - 1)) {
                    isFinished = true;
                } else if (currentNode == this.path.get(counter)) {
                    publishProgress(counter);
                    counter++;
                }

                //REFRESH SCANRESULTS AFTER 200 MS
                try {
                    Thread.sleep(waitMilliseconds);
                    scanResults = ThatApp.getThatApp().getWifiManager().getScanResults();
                } catch (InterruptedException e) {
                    break;
                }
            }
            return null;
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
        protected void onProgressUpdate(Integer... values) {
            View v = VisualDijkstra_Activity.this.dijkstraview.getChildAt(values[0]);
            if (v != null) {
                DijkstraAdapter.ViewHolder vh = (DijkstraAdapter.ViewHolder) v.getTag();
                VisualDijkstra_Activity.this.tvDijkstra.setText(textviewString + "\n" + path.get(values[0]).getId());
                vh.cBoxReached.setChecked(true);
            }
        }
    }
}
