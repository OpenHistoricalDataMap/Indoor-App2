package de.htwberlin.f4.ai.ma.fingerprint;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FingerprintFactory {

    public static Fingerprint getFingerprint(){
        return new FingerprintImplementation();
    }

    private static class FingerprintImplementation implements Fingerprint
    {
        private boolean average;
        private boolean kalman;
        private boolean euclideanDistance;
        private boolean knn;

        int averageOrder;
        int knnValue;

        List<NodeInterface> allExistingNodes;
        List<NodeInterface> measuredNode;

        @Override
        public void setMovingAverage(boolean average) {
            this.average = average;
        }

        @Override
        public boolean getMovingAverage() {
            return this.average;
        }

        @Override
        public void setAverageOrder(int order) {
            this.averageOrder = order;
        }

        @Override
        public int getAverageOrder() {
            return this.averageOrder;
        }

        @Override
        public void setKalman(boolean kalman) {
            this.kalman = kalman;
        }

        @Override
        public boolean getKalman() {
            return this.kalman;
        }

        @Override
        public void setEuclideanDistance(boolean euclideanDistance) {
            this.euclideanDistance = euclideanDistance;
        }

        @Override
        public boolean getEuclidienDistance() {
            return this.euclideanDistance;
        }

        @Override
        public void setKNN(boolean knn) {
            this.knn = knn;
        }

        @Override
        public boolean getKNN() {
            return this.knn;
        }

        @Override
        public void setKNNValue(int value) {
            this.knnValue = value;
        }

        @Override
        public int getKNNValue() {
            return this.knnValue;
        }

        @Override
        public void setAllNodes(List<?> allNodes) {
            this.allExistingNodes = (List<NodeInterface>) allNodes;
        }


        @Override
        public void setActuallyNode(List<?> measuredNode) {
            this.measuredNode = (List<NodeInterface>) measuredNode;
        }

        @Override
        public String getCalculatedPOI() {
            String poi = null;

            List<RestructedNode> restructedNodeList = calculateNewNodeDateSet(allExistingNodes);
            List<RestructedNode> calculatedNodeList = new ArrayList<>();
            if(allExistingNodes!=null){
                if(average){
                    MovingAverage movingAverageClass = new MovingAverage();
                    calculatedNodeList = movingAverageClass.calculation(restructedNodeList, averageOrder);
                }
                else if(kalman){
                    KalmanFilter kalmanFilterClass = new KalmanFilter();
                    calculatedNodeList = kalmanFilterClass.calculationKalmann(restructedNodeList);
                }

                if(euclideanDistance){
                    List<MeasuredNode> actuallyNode = getActuallyNode(measuredNode);
                    EuclideanDistance euclideanDistanceClass = new EuclideanDistance();
                    List<String> distanceNames = euclideanDistanceClass.calculateDistance(calculatedNodeList, actuallyNode);
                    if(knn){
                        KNN KnnClass = new KNN();
                        poi = KnnClass.calculateKnn(distanceNames);
                    }
                    else {
                        poi = distanceNames.get(0);
                    }
                }

                return poi;
            }
            else {
                return null;
            }
        }

        private List<MeasuredNode> getActuallyNode(List<NodeInterface> nodeList) {
            List<MeasuredNode> measuredNodeList = new ArrayList<>();

            for(int i=0; i<nodeList.size(); i++){
                List<SignalInformationInterface> signalInformation = nodeList.get(i).getSignalInformationList();
                for(SignalInformationInterface test : signalInformation)
                    for (SignalStrengthInformationInterface juhu : test.getSignalStrengthInformationList()) {
                        String macAdress = juhu.getMacAdress();
                        int signalStrenght = juhu.getSignalStrength();
                        MeasuredNode measuredNode = new MeasuredNode(macAdress,signalStrenght);
                        measuredNodeList.add(measuredNode);
                    }
            }

            return measuredNodeList;
        }

        private List<RestructedNode> calculateNewNodeDateSet(List<NodeInterface> allExistingNodes) {
            List<String> macAdresses = new ArrayList<>();
            int count = 0;

            List<RestructedNode> restructedNodes = new ArrayList<>();
            Multimap<String, Integer> multiMap = null;

            for(NodeInterface node : allExistingNodes){
                count = node.getSignalInformationList().size();
                double minValue = (((double)1/(double)3) * (double)count);
                macAdresses = getMacAdresses(node);
                multiMap = getMultiMap(node ,macAdresses);

                //delete weak Adresses
                for (String checkMacAdress : macAdresses){
                    int countValue = 0;

                    for (Integer signalValue : multiMap.get(checkMacAdress)) {
                        if ( signalValue != null ) {
                            countValue++;
                        }
                    }
                    if(countValue <= minValue){
                        multiMap.removeAll(checkMacAdress);
                    }

                }

                //fill restructed Nodes
                RestructedNode restructedNode = new RestructedNode(node.getId(),multiMap);
                restructedNodes.add(restructedNode);

            }

            return restructedNodes;
        }

        private List<String> getMacAdresses(NodeInterface node){
            HashSet<String> macAdresses =new HashSet<String>();
            for(SignalInformationInterface signal : node.getSignalInformationList()){
                for(SignalStrengthInformationInterface signalStrength : signal.getSignalStrengthInformationList()){
                    macAdresses.add(signalStrength.getMacAdress());
                }
            }
            List<String> uniqueList = new ArrayList<String>(macAdresses);
            return uniqueList;
        }

        private Multimap<String, Integer> getMultiMap(NodeInterface node, List<String> macAdresses) {
            Multimap<String, Integer> multiMap = ArrayListMultimap.create();
            for(SignalInformationInterface signal : node.getSignalInformationList()){
                HashSet<String> actuallyMacAdresses = new HashSet<String>();
                for(SignalStrengthInformationInterface signalStrength : signal.getSignalStrengthInformationList()){
                    multiMap.put(signalStrength.getMacAdress(), signalStrength.getSignalStrength());
                    actuallyMacAdresses.add(signalStrength.getMacAdress());
                }
                for (String checkMacAdress : macAdresses){
                    if(!actuallyMacAdresses.contains(checkMacAdress)){
                        multiMap.put(checkMacAdress,null);
                    }
                }
            }
            return multiMap;
        }


        private class RestructedNode{
            String id;
            Multimap<String, Integer> restructedSignals;

            private RestructedNode(String id, Multimap<String, Integer> restructedSignals) {
                this.id = id;
                this.restructedSignals = restructedSignals;
            }
        }

        private class MeasuredNode{
            String macAdress;
            int signalStrenght;

            private MeasuredNode(String macAdress, int signalStrenght) {
                this.macAdress = macAdress;
                this.signalStrenght = signalStrenght;
            }
        }

        private class MovingAverage{
            private List<RestructedNode> calculation(List<RestructedNode> restructedNodeList, int order){
                List<RestructedNode> calculatedNodes = new ArrayList<>();
                Multimap<String, Integer> calculadetMultiMap = null;

                for (int i = 0; i<restructedNodeList.size() ; i++){
                    RestructedNode Node = restructedNodeList.get(i);
                    calculadetMultiMap = ArrayListMultimap.create();

                    double average;

                    for(String Key : Node.restructedSignals.keySet())
                    {
                        int counter = 0;
                        int tempAverage = 0;
                        Integer[] Values = Node.restructedSignals.get(Key).toArray(new Integer[0]);

                        for(Integer Signal : Values) {
                            if(Signal!= null){
                                counter++;
                                tempAverage += Signal;
                            }
                        }
                        average = (double)tempAverage/(double)counter;

                        for(int j = 0; j < Values.length; j++) {

                            if(order == 3){
                                if(j >=2){
                                    double t1,t2,t3;
                                    if(Values[j-2] != null){
                                        t1 =Values[j-2];
                                    }else {
                                        t1 = average;
                                    }
                                    if(Values[j-1] != null){
                                        t2 =Values[j-1];
                                    }else {
                                        t2 = average;
                                    }
                                    if(Values[j] != null){
                                        t3 =Values[j];
                                    }else {
                                        t3 = average;
                                    }

                                    double movingAverageValue = ((double)1/(double)3) * (t1+t2+t3);
                                    calculadetMultiMap.put(Key, (int) movingAverageValue);
                                }

                            }else if (order == 5){
                                if(j >=4){
                                    double t1,t2,t3,t4,t5;
                                    if(Values[j-4] != null){
                                        t1 =Values[j-4];
                                    }else {
                                        t1 = average;
                                    }
                                    if(Values[j-3] != null){
                                        t2 =Values[j-3];
                                    }else {
                                        t2 = average;
                                    }
                                    if(Values[j-2] != null){
                                        t3 =Values[j-2];
                                    }else {
                                        t3 = average;
                                    }
                                    if(Values[j-1] != null){
                                        t4 =Values[j-1];
                                    }else {
                                        t4 = average;
                                    }
                                    if(Values[j] != null){
                                        t5 =Values[j];
                                    }else {
                                        t5 = average;
                                    }

                                    double movingAverageValue = ((double)1/(double)5) * (t1+t2+t3+t4+t5);
                                    calculadetMultiMap.put(Key, (int) movingAverageValue);
                                }

                            }

                        }

                    }
                    calculatedNodes.add(new RestructedNode(Node.id,calculadetMultiMap));
                }

                return calculatedNodes;
            }
        }

        private class KalmanFilter{
            private List<RestructedNode> calculationKalmann(List<RestructedNode> restructedNodeList){

                List<RestructedNode> calculatedNodes = new ArrayList<>();
                Multimap<String, Integer> calculadetMultiMap = null;

                for (int i = 0; i<restructedNodeList.size() ; i++){
                    RestructedNode Node = restructedNodeList.get(i);
                    calculadetMultiMap = ArrayListMultimap.create();

                    double average;
                    double Xk, Pk, Kk, Pkt, Xkt, value, deviation;

                    for(String Key : Node.restructedSignals.keySet())
                    {
                        int counter = 0;
                        int tempAverage = 0;

                        Integer[] Values = Node.restructedSignals.get(Key).toArray(new Integer[0]);

                        for(Integer Signal : Values) {
                            if(Signal!= null){
                                counter++;
                                tempAverage += Signal;
                            }
                        }
                        average = (double)tempAverage/(double)counter;

                        Xk = 0;
                        Pk = 1;
                        deviation = 0.1;
                        //deviation = calculateDeviation(Values,average,counter);

                        for(int j = 0; j < Values.length; j++) {

                            if (Values[j] != null){
                                value = Values[j];
                            }else {
                                value = average;
                            }

                            Kk = (Pk)/(Pk+deviation);
                            Pkt = ((double)1-Kk)*Pk;
                            Pk = Pkt;
                            Xkt = Xk + Kk * (value -Xk);
                            Xk = Xkt;

                            calculadetMultiMap.put(Key, (int) Xkt);
                        }
                    }
                    calculatedNodes.add(new RestructedNode(Node.id,calculadetMultiMap));
                }

                return calculatedNodes;
            }

            private double calculateDeviation(Integer[] values, double average, double count) {
                int x = 0;
                for(int i = 0; i< values.length; i++){
                    if(values[i] != null){
                        x+=Math.pow((values[i] - average),2);
                    }
                    else {
                        x+=Math.pow((average - average),2);
                    }

                }

                double temp = ((double)1/((double)count-1))*x;
                return Math.sqrt(temp);
            }
        }

        private class EuclideanDistance{
            private List<String> calculateDistance(List<RestructedNode> restructedNodes, List<MeasuredNode> measuredNodeList){
                List<String> distanceName = new ArrayList<>();
                List<DistanceClass> distanceClassList = new ArrayList<>();

                for(int i = 0; i<restructedNodes.size(); i++){

                    List<Collection<Integer>> matchingSignalStrengths = new ArrayList<>();
                    List<Integer> measuredSignalStrength = new ArrayList<>();
                    for(int j= 0; j<measuredNodeList.size();j++){
                        Boolean contains = restructedNodes.get(i).restructedSignals.containsKey(measuredNodeList.get(j).macAdress);
                        if(contains){
                            matchingSignalStrengths.add(restructedNodes.get(i).restructedSignals.get(measuredNodeList.get(j).macAdress));
                            measuredSignalStrength.add(measuredNodeList.get(j).signalStrenght);
                        }
                    }

                    if(matchingSignalStrengths.size() >= 2){
                        List<Iterator<Integer>> signalIterators = new ArrayList<>();

                        for(int j = 0; j<matchingSignalStrengths.size(); j++) {
                            signalIterators.add(matchingSignalStrengths.get(j).iterator());
                        }

                        while(signalIterators.get(0).hasNext()) {
                            double distance = 0d;

                            for(int j = 0; j<matchingSignalStrengths.size(); j++){
                                Integer restructedSignalStregnth = signalIterators.get(j).next();
                                distance += Math.pow(measuredSignalStrength.get(j) - restructedSignalStregnth, 2);
                            }

                            distance /= (double)matchingSignalStrengths.size();
                            distance = Math.sqrt(distance);

                            DistanceClass distanceClass = new DistanceClass();
                            distanceClass.name = restructedNodes.get(i).id;
                            distanceClass.distance = distance;
                            distanceClassList.add(distanceClass);
                        }
                    }
                }


                //List<DistanceClass> distanceList2 = doSelectionSort(distanceClassList);
                //List<String> distanceList2 = doSelectionSort(distanceClassList);

                return doSelectionSort(distanceClassList);
            }

            private List<String> doSelectionSort(List<DistanceClass> distanceList){

                List<String> distanceNameList = new ArrayList<>();

                for (int i = 0; i < distanceList.size() - 1; i++)
                {
                    int index = i;
                    for (int j = i + 1; j < distanceList.size(); j++)
                        if (distanceList.get(j).distance < distanceList.get(index).distance)
                            index = j;

                    double smallerNumber = distanceList.get(index).distance;
                    distanceList.get(index).distance = distanceList.get(i).distance;
                    distanceList.get(i).distance = smallerNumber;

                    distanceNameList.add(i, distanceList.get(i).name);
                }
                return distanceNameList;
            }


            private class DistanceClass{
                String name;
                double distance;
            }
        }

        private class KNN{
            private String calculateKnn(List<String> distanceNames) {
                int knnValue = getKNNValue();
                Map<String, Integer> stringsCount = new HashMap<String, Integer>();

                if (distanceNames.size() >= knnValue && knnValue!=0){
                    for (int i = 0; i<knnValue; i++ ){
                        if(distanceNames.get(i).length()>0){
                            //String distanceNameString = distanceNames.get(i).toLowerCase();
                            Integer count = stringsCount.get(distanceNames.get(i));
                            if(count == null) count = new Integer(0);
                            count++;
                            stringsCount.put(distanceNames.get(i),count);
                        }
                    }
                }
                Map.Entry<String,Integer> mostRepeated = null;
                for(Map.Entry<String, Integer> e: stringsCount.entrySet())
                {
                    if(mostRepeated == null || mostRepeated.getValue()<e.getValue())
                        mostRepeated = e;
                }
                try {
                    return mostRepeated.getKey();
                } catch (NullPointerException e) {
                    return null;
                }
            }
        }

    }
}

