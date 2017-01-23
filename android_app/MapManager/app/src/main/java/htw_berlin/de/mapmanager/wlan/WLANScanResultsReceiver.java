package htw_berlin.de.mapmanager.wlan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class WLANScanResultsReceiver extends BroadcastReceiver {
    public WLANScanResultsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        ThatApp.getThatApp().printScan();
    }
}
