package htw_berlin.de.mapmanager.compass;

/**
 * Created by florianhausler on 19.12.16.
 */

public class WayPoint {
    private float direction;
    private float distance;
    private float high;

    public void setDirection(float direction) {
        this.direction = direction;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public void setHigh(float high) {
        this.high = high;
    }

    public float getDirection() {

        return direction;
    }

    public float getDistance() {
        return distance;
    }

    public float getHigh() {
        return high;
    }

    public String toString(){
        return "WayPoint: Distance: "+distance+" Direction: "+direction+" High: "+high;
    }

    public WayPoint(float direction, float distance, float high) {

        this.direction = direction;
        this.distance = distance;
        this.high = high;
    }
}
