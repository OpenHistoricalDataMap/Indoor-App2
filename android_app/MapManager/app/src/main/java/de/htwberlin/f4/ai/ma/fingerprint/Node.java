package de.htwberlin.f4.ai.ma.fingerprint;

import java.util.List;

public interface Node {
    void  setId(String id);
    String getId();

    void setSignalInformationList(List<SignalInformation> signalInformationList);
    List<SignalInformation> getSignalInformation();

    class SignalInformation {
        String timestamp;
        List<SignalStrengthInformation> signalStrengthInformationList;

        public SignalInformation(String timestamp, List<SignalStrengthInformation> signalStrengthInformationList) {
            this.timestamp = timestamp;
            this.signalStrengthInformationList = signalStrengthInformationList;
        }
    }

    class SignalStrengthInformation {
        String macAdress;
        int signalStrength;

        public SignalStrengthInformation(String macAdress, int signalStrength) {
            this.macAdress = macAdress;
            this.signalStrength = signalStrength;
        }
    }
}
