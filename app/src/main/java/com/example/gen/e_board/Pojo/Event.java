package com.example.gen.e_board.Pojo;

import com.google.android.gms.maps.model.LatLng;

public class Event {
    private String eventName;
    private String eventDesc;
    private String targetGroup;
    private String cost;
    private String eventDate;
    private String eventTime;
    private String placeName;
    private LatLng latLng;

    public Event(String eventName, String eventDesc, String targetGroup, String cost, String eventDate, String eventTime, String placeName, LatLng latLng) {
        this.eventName = eventName;
        this.eventDesc = eventDesc;
        this.targetGroup = targetGroup;
        this.cost = cost;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.placeName = placeName;
        this.latLng = latLng;
    }

    public Event() {
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }

    public String getTargetGroup() {
        return targetGroup;
    }

    public void setTargetGroup(String targetGroup) {
        this.targetGroup = targetGroup;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
