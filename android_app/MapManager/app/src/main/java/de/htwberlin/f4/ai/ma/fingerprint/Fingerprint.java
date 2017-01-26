package de.htwberlin.f4.ai.ma.fingerprint;

import java.util.List;

public interface Fingerprint{

    void  setMovingAverage(boolean average);
    boolean getMovingAverage();
    void setAverageOrder(int order);
    int getAverageOrder();

    void  setKalman(boolean average);
    boolean getKalman();

    void  setEuclideanDistance(boolean average);
    boolean getEuclidienDistance();

    void  setKNN(boolean average);
    boolean getKNN();
    void setKNNValue(int value);
    int getKNNValue();

    void setAllNodes(List<NodeInterface> allNodes);
    //List<NodeInterface> getAllNodes();

    void setActuallyNode(List<NodeInterface> measuredNodes);
    //List<NodeInterface> getMeasuredNode();

    String getCalculatedPOI();
}
