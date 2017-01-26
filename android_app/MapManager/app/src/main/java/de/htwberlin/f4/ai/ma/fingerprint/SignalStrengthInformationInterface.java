package de.htwberlin.f4.ai.ma.fingerprint;

/**
 * Created by tognitos on 26.01.17.
 */
public interface SignalStrengthInformationInterface {
    public int getSignalStrength();
    public void setSignalStrength(int signalStrength);

    public String getMacAdress();

    public void setMacAdress(String macAdress);
}