package htw_berlin.de.mapmanager.model;

import android.media.Image;

import java.util.ArrayList;

public class PointOfInterest implements IModel {
    private String name;
    private String description;
    private ArrayList<Image> images;
    private ArrayList<PointOfInterest> directConnections;

    public PointOfInterest(String name, String description){
        this(name, description, null, null);
    }

    public PointOfInterest(String name, String description, ArrayList<Image> images, ArrayList<PointOfInterest> directConnections) {
        this.name = name;
        this.description = description;
        this.images = images != null?images:new ArrayList<Image>();
        this.directConnections = directConnections != null?directConnections:new ArrayList<PointOfInterest>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Image> getImages() {
        return images;
    }

    public void setImages(ArrayList<Image> images) {
        this.images = images;
    }

    public ArrayList<PointOfInterest> getDirectConnections() {
        return directConnections;
    }

    public void addDirectConnections(PointOfInterest... POIs){
        for(PointOfInterest poi: POIs) {
            this.directConnections.add(poi);
        }
    }
}
