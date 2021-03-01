package com.example.direct_order.customermain;

public class AdrData {
    private double latitude;
    private double longitude;

    public AdrData(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public AdrData() {

    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
