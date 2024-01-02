package com.vertx.example.verticle;

public class GeoPoint {
    private final double latitude; // 纬度
    private final double longitude; // 经度

    public GeoPoint(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
