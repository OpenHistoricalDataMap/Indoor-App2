package htw_berlin.de.mapmanager.wlan;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import htw_berlin.de.mapmanager.graph.Node;

/**
 * Created by spaezzle on 1/18/17.
 */

public class ScanAssistant {

    private Integer counter;
    private Integer maximum;

    public ScanAssistant(Integer minutes)
    {
        counter = 0;
        maximum = minutes*60;
    }

    public void saveIntervallInNode(Node pNode)
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
                scanAgain();
            }
            catch(InterruptedException e){
                break;
            }
        }
        pNode.setSignalInformationList(signalList);
    }

    public void scanAgain() {
        ThatApp.getThatApp().getWifiManager().startScan();
    }



}
