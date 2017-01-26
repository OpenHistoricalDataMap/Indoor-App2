package htw_berlin.de.mapmanager.graph;

import com.google.gson.annotations.Expose;

import de.htwberlin.f4.ai.ma.fingerprint.SignalStrengthInformationInterface;

/**
 * Created by tognitos on 26.01.17.
 */
public class SignalStrengthInformation implements SignalStrengthInformationInterface {
    @Expose
    private String macAdress;

    @Expose
    private int signalStrength;

    public SignalStrengthInformation(String macAdress, int signalStrength) {
        this.macAdress = macAdress;
        this.signalStrength = signalStrength;
    }


    public int getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(int signalStrength) {
        this.signalStrength = signalStrength;
    }

    public String getMacAdress() {
        return macAdress;
    }

    public void setMacAdress(String macAdress) {
        this.macAdress = macAdress;
    }
}
