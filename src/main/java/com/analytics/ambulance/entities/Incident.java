package com.analytics.ambulance.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * Created by lenovo on 27/8/17.
 */
@Document(collection = "incidents")
public class Incident {
    @Id
    String id;
    @Field("inc_location")
    String incLocation;
    @Field("inc_details")
    String incDetails;
    @Field("Ambulance_no")
    String ambulanceNo;
    @Field("incidentLatLng")
    String[] incidentLatLng;
    @Field("routeTaken")
    List<String[]> routeTaken;
    @Field("Time")
    String[] Time;
    @Field("Total_time")
    String totalTime;
    @Field("Total_distance")
    String totalDistance;
    @Field("date")
    String date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIncLocation() {
        return incLocation;
    }

    public void setIncLocation(String incLocation) {
        this.incLocation = incLocation;
    }

    public String getIncDetails() {
        return incDetails;
    }

    public void setIncDetails(String incDetails) {
        this.incDetails = incDetails;
    }

    public String getAmbulanceNo() {
        return ambulanceNo;
    }

    public void setAmbulanceNo(String ambulanceNo) {
        this.ambulanceNo = ambulanceNo;
    }

    public String[] getIncidentLatLng() {
        return incidentLatLng;
    }

    public void setIncidentLatLng(String[] incidentLatLng) {
        this.incidentLatLng = incidentLatLng;
    }

    public List<String[]> getRouteTaken() {
        return routeTaken;
    }

    public void setRouteTaken(List<String[]> routeTaken) {
        this.routeTaken = routeTaken;
    }

    public String[] getTime() {
        return Time;
    }

    public void setTime(String[] time) {
        Time = time;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public String getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(String totalDistance) {
        this.totalDistance = totalDistance;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
