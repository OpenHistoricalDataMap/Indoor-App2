package de.htwberlin.f4.ai.ma.fingerprint;

import java.util.List;

/**
 * Created by tognitos on 26.01.17.
 */
public interface SignalInformationInterface {

    public String getTimestamp();

    public void setTimestamp(String timestamp);

    public List<SignalStrengthInformationInterface> getSignalStrengthInformationList();

    public void setSignalStrengthInformationList(List<SignalStrengthInformationInterface> signalStrengthInformationList);
}