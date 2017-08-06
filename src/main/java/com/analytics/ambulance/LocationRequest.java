package com.analytics.ambulance;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by lenovo on 1/8/17.
 */
public class LocationRequest {

    @JsonProperty("lat") private Double latitude;
    @JsonProperty("long") private Double longitude;
    @JsonProperty("d") private Double distance;

    @JsonCreator
    public LocationRequest(@JsonProperty("lat") Double latitude, @JsonProperty("long") Double longitude, @JsonProperty("d") Double distance) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
    }
    @JsonCreator
    public void setLatitude(@JsonProperty("lat") Double latitude) {
        this.latitude = latitude;
    }

    @JsonCreator
    public void setLongitude(@JsonProperty("long") Double longitude) {
        this.longitude = longitude;
    }

    @JsonCreator
    public void setDistance(@JsonProperty("d") Double distance) {
        this.distance = distance;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getDistance() {
        return distance;
    }
}
