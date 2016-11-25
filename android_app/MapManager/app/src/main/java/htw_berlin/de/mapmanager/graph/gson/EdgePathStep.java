package htw_berlin.de.mapmanager.graph.gson;

import com.google.gson.annotations.SerializedName;

public class EdgePathStep {

    /**
     * North, North-East, East, South-East, South, South-West, West, North-West
     */
    public enum CompassDirection {
        N, NE, E, SE, S, SW, W, NW
    }

    @SerializedName("nSteps")
    private int numberOfSteps;

    @SerializedName("direction")
    private CompassDirection direction;

}
