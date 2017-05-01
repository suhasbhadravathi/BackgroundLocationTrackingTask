package com.suhas.map;

import java.io.Serializable;

/**
 * Created by suhasvijay on 01/05/2017.
 */

public class DataDictionary implements Serializable{

    public DataDictionary() {

    }
    private Double startedLatitude;
    private Double startedLongitude;
    private Double stoppedLatitude;
    private Double stoppedLongitude;
    private Double currentLatitude;
    private Double currentLongitude;
    private Double latitudeOnLocationChange;
    private Double longitudeOnLocationChange;
    private Double totalDistance;


    public Double getStartedLatitude() {
        return startedLatitude;
    }

    public void setStartedLatitude(Double startedLatitude) {
        this.startedLatitude = startedLatitude;
    }

    public Double getStartedLongitude() {
        return startedLongitude;
    }

    public void setStartedLongitude(Double startedLongitude) {
        this.startedLongitude = startedLongitude;
    }

    public Double getStoppedLatitude() {
        return stoppedLatitude;
    }

    public void setStoppedLatitude(Double stoppedLatitude) {
        this.stoppedLatitude = stoppedLatitude;
    }

    public Double getStoppedLongitude() {
        return stoppedLongitude;
    }

    public void setStoppedLongitude(Double stoppedLongitude) {
        this.stoppedLongitude = stoppedLongitude;
    }

    public Double getCurrentLatitude() {
        return currentLatitude;
    }

    public void setCurrentLatitude(Double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public Double getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(Double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    public Double getLatitudeOnLocationChange() {
        return latitudeOnLocationChange;
    }

    public void setLatitudeOnLocationChange(Double latitudeOnLocationChange) {
        this.latitudeOnLocationChange = latitudeOnLocationChange;
    }

    public Double getLongitudeOnLocationChange() {
        return longitudeOnLocationChange;
    }

    public void setLongitudeOnLocationChange(Double longitudeOnLocationChange) {
        this.longitudeOnLocationChange = longitudeOnLocationChange;
    }

    public Double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(Double totalDistance ) {
        this.totalDistance = totalDistance;
    }
}
