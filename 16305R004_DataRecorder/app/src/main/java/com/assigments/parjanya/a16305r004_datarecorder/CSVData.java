package com.assigments.parjanya.a16305r004_datarecorder;

import java.util.ArrayList;

/**
 * Created by parjanya on 15/2/18.
 */

class CSVData {
    String label;
    boolean isGPSRecorded;
    boolean isAccelerometerRecorded;
    ArrayList<SensorData> sensorData;

    public CSVData(String label, boolean isAccelerometerRecorded, boolean isGPSRecorded) {
        this.label = label;
        this.isAccelerometerRecorded = isAccelerometerRecorded;
        this.isGPSRecorded = isGPSRecorded;
        sensorData = new ArrayList<>();
    }
}
