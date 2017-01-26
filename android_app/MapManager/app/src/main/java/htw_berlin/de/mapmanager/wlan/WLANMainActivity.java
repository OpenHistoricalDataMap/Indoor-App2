package htw_berlin.de.mapmanager.wlan;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.htwberlin.f4.ai.ma.fingerprint.SignalInformationInterface;
import de.htwberlin.f4.ai.ma.fingerprint.SignalStrengthInformationInterface;
import htw_berlin.de.mapmanager.MainActivity;
import htw_berlin.de.mapmanager.R;
import htw_berlin.de.mapmanager.StartActivity;
import htw_berlin.de.mapmanager.graph.Node;
import htw_berlin.de.mapmanager.graph.SignalInformation;
import htw_berlin.de.mapmanager.graph.SignalStrengthInformation;
import htw_berlin.de.mapmanager.permissions.PermissionManager;
import htw_berlin.de.mapmanager.persistence.PersistenceManager;
import htw_berlin.de.mapmanager.persistence.ReadPermissionException;
import htw_berlin.de.mapmanager.persistence.WritePermissionException;

/**
 * Activity to Perform Wlan Scans and safe them for a specific Node
 * @author  Christoph Bose
 */
public class WLANMainActivity extends AppCompatActivity implements View.OnClickListener {


    private Node parentNode;
    private Button saveJsonButton;
    private PermissionManager permissionManager;
    private EditText timeToMeasure;
    private boolean isCancelPressed = false;
    private Integer current = 0, total = 180;

    /**
     * AsyncTask to Perform Scans and show progress in an AlertDialog
     */
    private class AsyncSave extends AsyncTask<Integer,Integer,Integer>
    {
        private String ssid;
        private AlertDialog.Builder builder;
        private AlertDialog alertDialog;
        int waitMilliseconds = 1000;
        private boolean cancelthis;

        @Override
        protected Integer doInBackground(Integer... params) {
            List<SignalInformationInterface> backupList = WLANMainActivity.this.parentNode.getSignalInformationList();
            List<SignalInformationInterface> signalList= WLANMainActivity.this.parentNode.getSignalInformationList();
            Integer count = 0;
            Integer max = 60*params[0];
            for(count = 0; count <max;count++)
            {
                Date d = new Date();
                List<SignalStrengthInformationInterface> signalStrengthList = new ArrayList<SignalStrengthInformationInterface>();
                List<ScanResult> scanResults = ThatApp.getThatApp().getWifiManager().getScanResults();
                for (ScanResult sr : scanResults) {
                    if (sr.SSID.equals(ssid)) {
                        SignalStrengthInformationInterface signalStrengthEntry = new SignalStrengthInformation(sr.BSSID,sr.level);
                        signalStrengthList.add(signalStrengthEntry);
                    }
                }
                signalList.add(new SignalInformation(d.toString(),signalStrengthList));
                if(cancelthis)
                {
                    signalList = backupList;
                    break;
                }
                try {
                    Thread.sleep(1000);
                    scanAgain();
                    publishProgress(count, max);
                }
                catch(InterruptedException e) {
                    break;
                }
            }
            WLANMainActivity.this.parentNode.setSignalInformationList(signalList);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(WLANMainActivity.this);
            ssid = sharedPrefs.getString("pref_ssid", "BVG-Wifi");
            StartActivity.graph.setSsid(ssid);

            cancelthis = false;
            this.builder = new AlertDialog.Builder(WLANMainActivity.this);
            this.builder.setMessage("Progress Status init");
            this.builder.setTitle("Progress Status");
            this.builder.setNegativeButton("Cancel!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            this.alertDialog = builder.create();
            this.alertDialog.setCancelable(false);
            this.alertDialog.setCanceledOnTouchOutside(false);
            this.alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelthis = true;
                }

            });
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            cancelthis = false;
            this.alertDialog.hide();
            this.alertDialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            this.alertDialog.setMessage(values[0].toString()+" von "+values[1].toString());
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

    /**
     * Setup Activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initPermissions();
        initNode();

        ThatApp.initThatApp(this);



        setContentView(R.layout.activity_wlan_main);

        Button button = (Button) this.findViewById(R.id.refresh);
        button.setOnClickListener(this);

        button = (Button) this.findViewById(R.id.saveIntervall);
        button.setOnClickListener(this);

        timeToMeasure = (EditText) this.findViewById(R.id.edit_timer);


        // Tognimat persisting node with measurements
        saveJsonButton = (Button) this.findViewById(R.id.saveJSON);
        saveJsonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final PersistenceManager persistenceManager = new PersistenceManager(permissionManager);
                    persistenceManager.storeNodeMeasurements(parentNode);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WritePermissionException e) {
                    e.printStackTrace();
                } catch (ReadPermissionException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Get current Node ID from Intent and Initialize
     */
    private void initNode()
    {
        Intent intent = getIntent();
        String poiId = intent.getStringExtra(MainActivity.EXTRA_MESSAGE_POI_ID);
        if(poiId == null || poiId == ""){
            throw new IllegalArgumentException("The given poiId is invalid: " + poiId);
        }

        // TODO this operation could run through all the nodes. Consider passing the whole NodeInterface
        // TODO look on the internet what would be more performance expensive
        this.parentNode = StartActivity.graph.getNode(poiId);
        setTitle(parentNode.getId()+" Measurement");
    }

    /**
     * Check and Request Permissions
     */
    private void initPermissions(){
        permissionManager = new PermissionManager(this);
        permissionManager.checkWifiPermissions();
        permissionManager.checkExternalReadPermissions();
        permissionManager.checkExternalWritePermissions();
    }

    /**
     * Implements onClick from onClickListener, for Buttons on Activity
     * @param v
     */
    public void onClick(View v) {
        Button refreshButton = (Button) this.findViewById(R.id.refresh);
        Button saveIntervall = (Button) this.findViewById(R.id.saveIntervall);

        if(v == refreshButton) {
            this.scanAgain();
            this.refresh();
        }

        if(v == saveIntervall){
            Integer timer;
            try{
                timer = Integer.parseInt(timeToMeasure.getText().toString());
            }
            catch(Exception e)
            {
                timer = 3;
            }
            new AsyncSave().execute(timer);
        }

    }


    /**
     * Refreshes output on the activities TextView
     */
    public void refresh() {
        TextView tv_connectedWLAN = (TextView) this.findViewById(R.id.connectedWLAN);
        tv_connectedWLAN.setText("nix");

        TextView tv = (TextView) this.findViewById(R.id.textView);
        tv.setText("nix");

        try {
            WifiInfo connectionInfo = ThatApp.getThatApp().getWifiManager().getConnectionInfo();
            if(connectionInfo != null) {
                String infoString = "connected W-LAN: ";

                infoString += "\nBSSID: ";
                infoString += connectionInfo.getBSSID();

                infoString += "\nMacAddress: ";
                infoString += connectionInfo.getMacAddress();

                infoString += "\nSSID: ";
                infoString += connectionInfo.getSSID();

                tv_connectedWLAN.setText(infoString);

                ThatApp.getThatApp().printScan();
            }
        }
        catch (Exception e) {
            tv.setText("Exception: " + e.getLocalizedMessage());
        }
    }

    /**
     * Perform another scan
     */
    public void scanAgain() {
        ThatApp.getThatApp().getWifiManager().startScan();
    }






}
