package com.assigments.parjanya.a16305r004_datarecorder;

/**
 * Created by parjanya on 24/2/18.
 */

public class SensorData {
    String timeStamp;

    float accelerationX;
    float accelerationY;
    float accelerationZ;

    double latitude;
    double longitude;

    SensorData(String timeStamp, float accelerationX, float accelerationY, float accelerationZ) {
        this.timeStamp = timeStamp;
        this.accelerationX = accelerationX;
        this.accelerationY = accelerationY;
        this.accelerationZ = accelerationZ;
    }

    SensorData(String timeStamp, double latitude, double longitude) {
        this.timeStamp = timeStamp;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    SensorData(String timeStamp,
               float accelerationX, float accelerationY, float accelerationZ,
               double latitude, double longitude) {
        this.timeStamp = timeStamp;
        this.accelerationX = accelerationX;
        this.accelerationY = accelerationY;
        this.accelerationZ = accelerationZ;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
