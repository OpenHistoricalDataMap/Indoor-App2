package de.htwberlin.f4.ai.ma.fingerprint;

import java.util.List;

import htw_berlin.de.mapmanager.graph.SignalStrengthInformation;

/**
 * Created by tognitos on 26.01.17.
 */
public interface SignalInformationInterface {

    public String getTimestamp();

    public void setTimestamp(String timestamp);

    public List<SignalStrengthInformation> getSignalStrengthInformationList();

    public void setSignalStrengthInformationList(List<SignalStrengthInformation> signalStrengthInformationList);
}