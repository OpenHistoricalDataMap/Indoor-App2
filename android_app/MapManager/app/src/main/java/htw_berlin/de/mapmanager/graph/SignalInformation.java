package htw_berlin.de.mapmanager.graph;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

import de.htwberlin.f4.ai.ma.fingerprint.SignalInformationInterface;
import de.htwberlin.f4.ai.ma.fingerprint.SignalStrengthInformationInterface;

/**
 * Created by tognitos on 26.01.17.
 */
public class SignalInformation implements SignalInformationInterface {
    @Expose
    String timestamp;

    @Expose
    List<SignalStrengthInformationInterface> signalStrengthInformationList;

    public SignalInformation(String timestamp, List<SignalStrengthInformationInterface> signalStrengthInformationList) {
        this.timestamp = timestamp;
        this.signalStrengthInformationList = signalStrengthInformationList;
    }

    @Override
    public String getTimestamp() {
        return this.timestamp;
    }

    @Override
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public List<SignalStrengthInformationInterface> getSignalStrengthInformationList() {
        return this.signalStrengthInformationList;
    }

    @Override
    public void setSignalStrengthInformationList(List<SignalStrengthInformationInterface> signalStrengthInformationList) {
        this.signalStrengthInformationList = new ArrayList<>(signalStrengthInformationList);
    }
}