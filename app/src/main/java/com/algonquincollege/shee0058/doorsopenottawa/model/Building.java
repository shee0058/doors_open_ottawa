package com.algonquincollege.shee0058.doorsopenottawa.model;

import java.lang.reflect.Array;
import java.util.List;

import android.graphics.Bitmap;

/**
 * Created by owensheehan on 2016-11-08.
 */

public class Building {

    private int buildingId;
    private String name;
    private String address;
    private String image;
    private Bitmap bitmap;
    private String description;
    private List<String> open_hours;


    public int getBuildingId(){return buildingId;}
    public void setBuildingId(int buildingId){this.buildingId = buildingId;}

    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    public String getAddress(){return address;}
    public void setAddress(String address){this.address = address + " Ottawa, Ontario";}

    public String getImage(){return image;}
    public void setImage(String image){this.image = image;}

    public Bitmap getBitmap(){return bitmap;}
    public void setBitmap(Bitmap bitmap){this.bitmap = bitmap;}

    public String getDescription(){return description;}
    public void setDescription(String description){this.description = description;}

    public List<String> getOpenHours(){return open_hours;}
    public void setOpen_hours(List<String> open_hours){this.open_hours = open_hours;}
}
