package htw_berlin.de.mapmanager.model;

import android.media.Image;

import java.util.ArrayList;

public class PointOfInterest implements IModel {
    private String name;
    private String description;
    private ArrayList<Image> images;
    private ArrayList<PointOfInterest> directConnections;

    public PointOfInterest(String name, String description, ArrayList<Image> images, ArrayList<PointOfInterest> directConnections) {
        this.name = name;
        this.description = description;
        this.images = images;
        this.directConnections = directConnections;
    }



}
