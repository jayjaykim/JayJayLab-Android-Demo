package com.jayjaylab.androiddemo.app.greyhound.event;

import android.location.Location;

import java.util.List;

/**
 * Created by jongjoo on 1/10/15.
 */
public class OnGPXParsingCompleteEvent {
    List<Location> locationList;

    public OnGPXParsingCompleteEvent(List<Location> list) {
        locationList = list;
    }

    public List<Location> getLocationList() {
        return locationList;
    }

    public void setLocationList(List<Location> locationList) {
        this.locationList = locationList;
    }
}
