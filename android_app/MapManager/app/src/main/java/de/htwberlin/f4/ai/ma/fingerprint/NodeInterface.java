package de.htwberlin.f4.ai.ma.fingerprint;

import java.util.List;

import htw_berlin.de.mapmanager.graph.SignalInformation;

public interface NodeInterface {
    void  setId(String id);
    String getId();

    void setSignalInformationList(List<SignalInformation> signalInformationList);
    List<SignalInformation> getSignalInformationList();

}
