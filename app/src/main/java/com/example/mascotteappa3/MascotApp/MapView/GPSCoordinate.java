package com.example.mascotteappa3.MascotApp.MapView;

class GPSCoordinate {

    private double latitude;
    private double longitude;

    public GPSCoordinate(double latitude, double longitude) {
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
