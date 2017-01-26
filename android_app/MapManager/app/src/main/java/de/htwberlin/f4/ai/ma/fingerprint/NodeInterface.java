package de.htwberlin.f4.ai.ma.fingerprint;

import java.util.List;

public interface NodeInterface {
    void  setId(String id);
    String getId();

    void setSignalInformationList(List<SignalInformationInterface> signalInformationList);
    List<SignalInformationInterface> getSignalInformationList();

}
