package htw_berlin.de.mapmanager.navi;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
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
import java.util.ArrayList;
import java.util.List;

import de.htwberlin.f4.ai.ma.fingerprint.Fingerprint;
import de.htwberlin.f4.ai.ma.fingerprint.FingerprintFactory;
import de.htwberlin.f4.ai.ma.fingerprint.NodeInterface;
import de.htwberlin.f4.ai.ma.fingerprint.SignalInformationInterface;
import de.htwberlin.f4.ai.ma.fingerprint.SignalStrengthInformationInterface;
import htw_berlin.de.mapmanager.MainActivity;
import htw_berlin.de.mapmanager.R;
import htw_berlin.de.mapmanager.StartActivity;
import htw_berlin.de.mapmanager.graph.Graph;
import htw_berlin.de.mapmanager.graph.Node;

import htw_berlin.de.mapmanager.graph.SignalInformation;
import htw_berlin.de.mapmanager.graph.SignalStrengthInformation;
import htw_berlin.de.mapmanager.permissions.PermissionManager;
import htw_berlin.de.mapmanager.persistence.PersistenceManager;
import htw_berlin.de.mapmanager.wlan.ThatApp;

/**
 * Activity to Perform a check of your current position
 * @author  Christoph Bose
 */
public class WhereAmIActivity extends AppCompatActivity {

    private Button findPos;
    private Graph navigationGraph;
    private Node currentNode;
    private Button findWay;
    private TextView currentPos;
    private TextView errorMsg;
    private PermissionManager permissionManager;
    private static final String LOG_TAG = "Navigation";
    private Fingerprint fingerprint = FingerprintFactory.getFingerprint();

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


        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        boolean movingAverage = sharedPrefs.getBoolean("pref_movingAverage", true);
        boolean kalmanFilter = sharedPrefs.getBoolean("pref_kalman", false);
        boolean euclideanDistance = sharedPrefs.getBoolean("pref_euclideanDistance", false);
        boolean knnAlgorithm = sharedPrefs.getBoolean("pref_knnAlgorithm", true);

        //TODO: cbos hier anschauen
        final String ssid = sharedPrefs.getString("pref_ssid", "BVG-Wifi");
        StartActivity.graph.setSsid(ssid);

        fingerprint.setMovingAverage(movingAverage);
        fingerprint.setKalman(kalmanFilter);
        fingerprint.setEuclideanDistance(euclideanDistance);
        fingerprint.setKNN(knnAlgorithm);

        fingerprint.setAverageOrder(Integer.parseInt(sharedPrefs.getString("pref_movivngAverageOrder", "3")));
        fingerprint.setKNNValue(Integer.parseInt(sharedPrefs.getString("pref_knnNeighbours", "3")));
        //fingerprint.setAllNodes(navigationGraph.getNodes());

        findPos.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                fingerprint.setAllNodes(navigationGraph.getNodes());
                List<Node> measuredNode = getMeasuredNode();
                fingerprint.setActuallyNode(measuredNode);
                String actually = fingerprint.getCalculatedPOI();
                currentNode = getCurrentPosition(actually);
                if(currentNode != null){
                    currentPos.setText("You are currently located at: " + currentNode.getId());
                }
                else{
                    currentPos.setText("Position not found");
                }
            }

            private List<Node> getMeasuredNode() {
                //WifiManager mainWifiObj;
                //mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                //List<ScanResult> wifiScanList = mainWifiObj.getScanResults();
                ThatApp.initThatApp(WhereAmIActivity.this);
                List<ScanResult> wifiScanList = ThatApp.getThatApp().getWifiManager().getScanResults();
                List<Node> actuallyNode = new ArrayList<Node>();


                List<SignalInformation> signalInformationList = new ArrayList<>();
            for (ScanResult sr : wifiScanList) {

                if (sr.SSID.equals(ssid)) {
                    List<SignalStrengthInformation> signalStrenghtList = new ArrayList<>();
                    SignalStrengthInformation signal = new SignalStrengthInformation(sr.BSSID,sr.level);
                    signalStrenghtList.add(signal);
                    SignalInformation signalInformation = new SignalInformation("",signalStrenghtList);
                    signalInformationList.add(signalInformation);
                }
            }
                Node node = new Node(null,0,signalInformationList);
                actuallyNode.add(node);
                return actuallyNode;
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

    /**
     * Currently just Placeholder
     * @return Returns first node of Graph
     */
    private Node getCurrentPosition(String nodeName) {
        return navigationGraph.getNode(nodeName);
        //return navigationGraph.getNodes().get(0);
    }

    private void goToSelectTarget() {
        final Intent intent = new Intent(this, SelectTarget.class);
        intent.putExtra(MainActivity.EXTRA_MESSAGE_POI_ID, currentNode.getId());
        startActivity(intent);
    }
}
