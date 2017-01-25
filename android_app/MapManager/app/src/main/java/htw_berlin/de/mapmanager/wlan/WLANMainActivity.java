package htw_berlin.de.mapmanager.wlan;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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

import htw_berlin.de.mapmanager.MainActivity;
import htw_berlin.de.mapmanager.R;
import htw_berlin.de.mapmanager.StartActivity;
import htw_berlin.de.mapmanager.graph.Node;
import htw_berlin.de.mapmanager.permissions.PermissionManager;
import htw_berlin.de.mapmanager.persistence.PersistenceManager;
import htw_berlin.de.mapmanager.persistence.ReadPermissionException;
import htw_berlin.de.mapmanager.persistence.WritePermissionException;


public class WLANMainActivity extends AppCompatActivity implements View.OnClickListener {


    private Node parentNode;
    private Button saveJsonButton;
    private PermissionManager permissionManager;
    private EditText timeToMeasure;
    private boolean isCancelPressed = false;
    private Integer current = 0, total = 180;

    private class AsyncSave extends AsyncTask<Integer,Integer,Integer>
    {

        private AlertDialog.Builder builder;
        private AlertDialog alertDialog;
        int waitMilliseconds = 1000;
        private boolean cancelthis;

        @Override
        protected Integer doInBackground(Integer... params) {
            List<Node.SignalInformation> backupList = WLANMainActivity.this.parentNode.getSignalInformationList();
            List<Node.SignalInformation> signalList= WLANMainActivity.this.parentNode.getSignalInformationList();
            Integer count = 0;
            Integer max = 60*params[0];
            for(count = 0; count <max;count++)
            {
                Date d = new Date();
                List<Node.SignalStrengthInformation> signalStrengthList = new ArrayList<Node.SignalStrengthInformation>();
                List<ScanResult> scanResults = ThatApp.getThatApp().getWifiManager().getScanResults();
                for (ScanResult sr : scanResults) {
                    if (sr.SSID.equals("BVG-Wifi")) {
                        Node.SignalStrengthInformation signalStrengthEntry = new Node.SignalStrengthInformation(sr.BSSID,sr.level);
                        signalStrengthList.add(signalStrengthEntry);
                    }
                }
                signalList.add(new Node.SignalInformation(d.toString(),signalStrengthList));
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

    private void initNode()
    {
        Intent intent = getIntent();
        String poiId = intent.getStringExtra(MainActivity.EXTRA_MESSAGE_POI_ID);
        if(poiId == null || poiId == ""){
            throw new IllegalArgumentException("The given poiId is invalid: " + poiId);
        }

        // TODO this operation could run through all the nodes. Consider passing the whole Node
        // TODO look on the internet what would be more performance expensive
        this.parentNode = StartActivity.graph.getNode(poiId);
        setTitle(parentNode.getId()+" Measurement");
    }

    private void initPermissions(){
        permissionManager = new PermissionManager(this);
        permissionManager.checkWifiPermissions();
        permissionManager.checkExternalReadPermissions();
        permissionManager.checkExternalWritePermissions();
    }

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

    public static void saveIntervallInNode(Node pNode)
    {
        List<Node.SignalInformation> backupList = pNode.getSignalInformationList();
        List<Node.SignalInformation> signalList= pNode.getSignalInformationList();
        for(int i = 0; i < 120;i++)
        {
            Date d = new Date();
            List<Node.SignalStrengthInformation> signalStrengthList = new ArrayList<Node.SignalStrengthInformation>();
            List<ScanResult> scanResults = ThatApp.getThatApp().getWifiManager().getScanResults();
            for (ScanResult sr : scanResults) {
                if (sr.SSID.equals("BVG-Wifi")) {
                    Node.SignalStrengthInformation signalStrengthEntry = new Node.SignalStrengthInformation(sr.BSSID,sr.level);
                    signalStrengthList.add(signalStrengthEntry);
                }
            }
            signalList.add(new Node.SignalInformation(d.toString(),signalStrengthList));
            try{
                Thread.sleep(1000);
            }
            catch(InterruptedException e){
                break;
            }
        }
        pNode.setSignalInformationList(signalList);
    }

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

    public void scanAgain() {
        ThatApp.getThatApp().getWifiManager().startScan();
    }

    /**
     * Speichern der momentanen aufnahme in eine Datei mit Timestamp
     */
    private int saveFile(String ssid, int x, int y)
    {
        try{
            File newDir = new File(Environment.getExternalStorageDirectory(),"wlanscan");
            if(!newDir.exists()) {
                newDir.mkdir();
            }
            String filename = "ergebnisse"+x+"-"+y+".log";
            File target = new File(Environment.getExternalStorageDirectory() + "/wlanscan/", filename);
            while(target.exists()) {
                x++;
                target = new File(Environment.getExternalStorageDirectory() + "/wlanscan/", "ergebnisse"+x+"-"+y+".log");
            }
            target.createNewFile();
            FileWriter fw = new FileWriter(target.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            String content = "";
            List<ScanResult> scanResults = ThatApp.getThatApp().getWifiManager().getScanResults();
            for(ScanResult sr : scanResults)
            {
                if(sr.SSID.equals(ssid))
                {
                    content += ""+sr.BSSID+"|"+sr.level+"|"+"\n";
                }
            }
            bw.write(content,0,content.length());
            bw.close();


        } catch(IOException ioe)
        {
            System.out.println(ioe.toString());
            System.out.println(ioe.getStackTrace());
        }
        return x;
    }

    private int saveFileJSON(String ssid, int x)
    {
        try{
            JSONObject jObj = new JSONObject();
            jObj.put("id","Placeholder");
            jObj.put("zValue",0);

            JSONObject signalInf = new JSONObject();
            JSONArray signalInfList = new JSONArray();
            JSONArray signalStrength = new JSONArray();
            List<ScanResult> scanResults = ThatApp.getThatApp().getWifiManager().getScanResults();
            for(ScanResult sr : scanResults)
            {
                if(sr.SSID.equals(ssid))
                {
                    JSONObject measurement = new JSONObject();
                    measurement.put("macAdress",sr.BSSID);
                    measurement.put("strength",sr.level);
                    signalStrength.put(measurement);
                }
            }
            signalInfList.put(signalStrength);
            signalInf.put("timestamp","0000"); //PLACEHOLDER
            signalInf.put("signalStrenghtInformationList", signalStrength);
            jObj.put("signalInformationList",signalInf);

            File newDir = new File(Environment.getExternalStorageDirectory(),"wlanscan");
            if(!newDir.exists()) {
                newDir.mkdir();
            }
            String filename = "ergebnisse"+x+".txt";
            File target = new File(Environment.getExternalStorageDirectory() + "/wlanscan/", filename);
            while(target.exists()) {
                x++;
                target = new File(Environment.getExternalStorageDirectory() + "/wlanscan/", "ergebnisse"+x+".log");
            }
            target.createNewFile();
            FileWriter fw = new FileWriter(target.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            String content =jObj.toString();
            bw.write(content,0,content.length());
            bw.close();


        } catch(IOException ioe)
        {
            System.out.println(ioe.toString());
            System.out.println(ioe.getStackTrace());
        }
        catch(JSONException je)
        {

        }
        return x;
    }

    /**
     * Speichern der Messdaten in einem Intervall in 5 Dateien.
     * Ruft saveFile() auf
     */
    private void saveIntervall(String ssid)
    {
        int x = 0;
        for(int i = 0; i < 5; i++) {
            x = saveFile(ssid, x, i);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        synchronized (this) {
                            wait(10000);
                            scanAgain();
                            refresh();
                        }
                    } catch (InterruptedException ex) {}
                }
            });
            thread.run();
        }
        average(x);
    }

    /**
     * Speichern von Messdaten zu spezifischer SSID in einer JSON file
     * @param ssid SSID für die messdaten erhoben und gespeichert werden sollen über Zeitintervall
     */
    private void saveIntervallJSON(String ssid)
    {
        int x =0;
        Thread t = new Thread(new Runnable(){

            int waitMilliseconds = 1000;

            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(waitMilliseconds);
                        scanAgain();
                        refresh();
                    }
                } catch (InterruptedException ex) {}
            }
        });
        try{
            JSONObject node = new JSONObject();
            node.put("id","PLACEHOLDER");
            node.put("zValue",0);
            JSONArray signalInformationList = new JSONArray();
            String timestamp;
            Date d;
            for(int i = 0; i<20;i++){
                d = new Date();
                timestamp = d.toString();
                JSONObject signalInformation = new JSONObject();
                signalInformation.put("timestamp",timestamp);
                JSONArray signalStrengthInformationList = new JSONArray();

                List<ScanResult> scanResults = ThatApp.getThatApp().getWifiManager().getScanResults();
                for (ScanResult sr : scanResults) {
                    if (sr.SSID.equals(ssid)) {
                        JSONObject signalStrengthInformation = new JSONObject();
                        signalStrengthInformation.put("macAdress", sr.BSSID);
                        signalStrengthInformation.put("strength", sr.level);
                        signalStrengthInformationList.put(signalStrengthInformation);
                    }
                }
                signalInformation.put("signalStrength", signalStrengthInformationList);
                signalInformationList.put(signalInformation);
                t.run();
            }
            node.put("signalInformation",signalInformationList);
            File newDir = new File(Environment.getExternalStorageDirectory(),"wlanscan");
            if(!newDir.exists()) {
                newDir.mkdir();
            }
            String filename = "ergebnisse"+x+".txt";
            File target = new File(Environment.getExternalStorageDirectory() + "/wlanscan/", filename);
            while(target.exists()) {
                x++;
                target = new File(Environment.getExternalStorageDirectory() + "/wlanscan/", "ergebnisse"+x+".txt");
            }
            target.createNewFile();
            FileWriter fw = new FileWriter(target.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            String content =node.toString();
            bw.write(content,0,content.length());
            bw.close();


        } catch(IOException ioe)
        {
            System.out.println(ioe.toString());
            System.out.println(ioe.getStackTrace());
        }
        catch(JSONException je) {

        }
    }

    private void average(int x)
    {
        int y=0;
        ArrayList<BssidRelevant> bssids = new ArrayList<BssidRelevant>();
        String filename = "ergebnisse"+x+"-"+y+".log";
        File target = new File(Environment.getExternalStorageDirectory() + "/wlanscan/", filename);
        try {
            //SOLANGE NOCH MESSDATEN VORLIEGEN
            while (target.exists()) {
                //AUSLESEN VON ALLEM AUS EINER DATEI UND ABLEGEN IN DIE ARRAYLISTE
                FileReader fr = new FileReader(target);
                BufferedReader br = new BufferedReader(fr);
                String s;
                //JEDE ZEILE AUSLESEN
                while((s = br.readLine())!= null ) {
                    String[] sar = s.split("|");
                    boolean entryfound = false;
                    //LVL für SSID ADDIEREN
                    for(int i = 0; i<bssids.size(); i++)
                    {
                        if(bssids.get(i).getName().equals(sar[0]))
                        {
                            bssids.get(i).setLvl(bssids.get(i).getLvl()+Integer.parseInt(sar[1]));
                            bssids.get(i).incrementCounter();
                            entryfound = true;
                        }
                    }
                    //FALLS BSSID NOCH NICHT DABEI ZUR LISTE HINZUFÜGEN
                    if(!entryfound)
                    {
                        bssids.add(new BssidRelevant(sar[0],Integer.parseInt(sar[1])));
                    }
                }
                y+=1;
                target = new File(Environment.getExternalStorageDirectory() + "/wlanscan/", "ergebnisse"+x+"-"+y+".log");
            }
            String c = "";
            for(BssidRelevant br : bssids)
            {
                br.computeAverage();
                c+=br.getName()+"|"+br.getLvl()+"\n";
            }
            File savetar = new File(Environment.getExternalStorageDirectory() + "/wlanscan/", "ergebnisse"+x+"-"+"AVERAGE.log");
            FileWriter fw = new FileWriter(savetar.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(c,0,c.length());
            bw.close();
        }catch(FileNotFoundException fne)
        {
            TextView tv = (TextView) this.findViewById(R.id.textView);
            tv.setText("FILE NOT FOUND \n"+fne.getLocalizedMessage());
        }catch(IOException ioe)
        {
            TextView tv = (TextView) this.findViewById(R.id.textView);
            tv.setText("IO EXCEPTION \n"+ioe.getLocalizedMessage());
        }
    }


}
