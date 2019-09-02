package com.assigments.parjanya.a16305r004_datarecorder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SensorRecorderService extends Service implements LocationListener, SensorEventListener{
    private CSVData csvData;

    private double latestLatitude;
    private double latestLongitude;

    private float latestAccelerationX;
    private float latestAccelerationY;
    private float latestAccelerationZ;

    private LocationManager locationManager;
    private SensorManager sensorManager;

    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private PrintWriter recordingFilePrintWriter;
    private int currentRecordingNumber;

    private void updateLatestRecordInSharedPreference(String record) {
        editor = sharedPreferences.edit();
        String record1 = sharedPreferences.getString(Constants.RECORDING_ROW_1_SHARED_PREFERENCE_KEY,"");
        String record2 = sharedPreferences.getString(Constants.RECORDING_ROW_2_SHARED_PREFERENCE_KEY,"");
        String record3 = sharedPreferences.getString(Constants.RECORDING_ROW_3_SHARED_PREFERENCE_KEY,"");
        String record4 = sharedPreferences.getString(Constants.RECORDING_ROW_4_SHARED_PREFERENCE_KEY,"");
        String record5 = sharedPreferences.getString(Constants.RECORDING_ROW_5_SHARED_PREFERENCE_KEY,"");
        if (record1.equals("")) {
            editor.putString(Constants.RECORDING_ROW_1_SHARED_PREFERENCE_KEY, record);
        } else if (record2.equals("")) {
            editor.putString(Constants.RECORDING_ROW_2_SHARED_PREFERENCE_KEY, record);
        } else if (record3.equals("")) {
            editor.putString(Constants.RECORDING_ROW_3_SHARED_PREFERENCE_KEY, record);
        } else if (record4.equals("")) {
            editor.putString(Constants.RECORDING_ROW_4_SHARED_PREFERENCE_KEY, record);
        } else if (record5.equals("")) {
            editor.putString(Constants.RECORDING_ROW_5_SHARED_PREFERENCE_KEY, record);
        } else {
            editor.putString(Constants.RECORDING_ROW_1_SHARED_PREFERENCE_KEY, record2);
            editor.putString(Constants.RECORDING_ROW_2_SHARED_PREFERENCE_KEY, record3);
            editor.putString(Constants.RECORDING_ROW_3_SHARED_PREFERENCE_KEY, record4);
            editor.putString(Constants.RECORDING_ROW_4_SHARED_PREFERENCE_KEY, record5);
            editor.putString(Constants.RECORDING_ROW_5_SHARED_PREFERENCE_KEY, record);
        }
        editor.apply();
    }

    private String getFirstTwoLines() {
        String currentGender;
        switch (sharedPreferences.getInt(Constants.GENDER_SHARED_PREFERENCE_KEY, -1)) {
            case Constants.MALE_GENDER_VALUE_SHARED_PREFERENCE:
                currentGender = getString(R.string.male);
                break;
            case Constants.FEMALE_GENDER_VALUE_SHARED_PREFERENCE:
                currentGender = getString(R.string.female);
                break;
            default:
                currentGender = getString(R.string.other);
        }
        return sharedPreferences.getString(Constants.FIRST_NAME_SHARED_PREFERENCE_KEY, "")
                + ", " + sharedPreferences.getString(Constants.LAST_NAME_SHARED_PREFERENCE_KEY, "")
                + ", " + sharedPreferences.getString(Constants.MOBILE_NUMBER_SHARED_PREFERENCE_KEY, "")
                + ", " + sharedPreferences.getString(Constants.EMAIL_SHARED_PREFERENCE_KEY, "")
                + ", " + currentGender
                + ", " + sharedPreferences.getString(Constants.AGE_SHARED_PREFERENCE_KEY, "")
                +"\ntimestamp :: lat, long, accelx, accely, accelz, label";
    }

    private void updateSharedPreferences() {
        editor = sharedPreferences.edit();
        editor.putBoolean(Constants.SERVICE_RUNNING_SHARED_PREFERENCE_KEY, true);
        editor.putInt(Constants.NUMBER_OF_RECORDINGS_CREATED_SHARED_PREFERENCE, currentRecordingNumber+1);
        editor.apply();
    }

    private void initializeRecordingFile() {
        currentRecordingNumber = sharedPreferences.getInt(Constants.NUMBER_OF_RECORDINGS_CREATED_SHARED_PREFERENCE, 0);
        File recordingFile = new File(getExternalFilesDir(null),
                Constants.RECORDING_FILE_NAME_PREFIX + currentRecordingNumber + Constants.RECORDING_FILE_NAME_SUFFIX);
        try {
            if(recordingFile.createNewFile()) {
                recordingFilePrintWriter = new PrintWriter(recordingFile);
                recordingFilePrintWriter.println(getFirstTwoLines());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendAccelerometerEventBroadcast(SensorData accelerometerData) {
        Intent accelerometerBroadcast = new Intent(Constants.ACCELEROMETER_EVENT_ACTION);
        accelerometerBroadcast.putExtra(Constants.TIMESTAMP_BROADCAST_KEY, accelerometerData.timeStamp);
        accelerometerBroadcast.putExtra(Constants.ACCELERATION_X_BROADCAST_KEY, accelerometerData.accelerationX);
        accelerometerBroadcast.putExtra(Constants.ACCELERATION_Y_BROADCAST_KEY, accelerometerData.accelerationY);
        accelerometerBroadcast.putExtra(Constants.ACCELERATION_Z_BROADCAST_KEY, accelerometerData.accelerationZ);
        accelerometerBroadcast.putExtra(Constants.LABEL_INTENT_EXTRA_KEY, csvData.label);
        LocalBroadcastManager.getInstance(this).sendBroadcast(accelerometerBroadcast);
    }

    private void sendGPSEventBroadcast(SensorData gpsData) {
        Intent gpsBroadcast = new Intent(Constants.GPS_EVENT_ACTION);
        gpsBroadcast.putExtra(Constants.TIMESTAMP_BROADCAST_KEY, gpsData.timeStamp);
        gpsBroadcast.putExtra(Constants.LATITUDE_BROADCAST_KEY, gpsData.latitude);
        gpsBroadcast.putExtra(Constants.LONGITUDE_BROADCAST_KEY, gpsData.longitude);
        gpsBroadcast.putExtra(Constants.LABEL_INTENT_EXTRA_KEY, csvData.label);
        LocalBroadcastManager.getInstance(this).sendBroadcast(gpsBroadcast);
    }

    private void sendCombinedEventBroadcast(SensorData combinedData) {
        Intent combinedBroadcast = new Intent(Constants.COMBINED_EVENT_ACTION);
        combinedBroadcast.putExtra(Constants.TIMESTAMP_BROADCAST_KEY, combinedData.timeStamp);
        combinedBroadcast.putExtra(Constants.ACCELERATION_X_BROADCAST_KEY, combinedData.accelerationX);
        combinedBroadcast.putExtra(Constants.ACCELERATION_Y_BROADCAST_KEY, combinedData.accelerationY);
        combinedBroadcast.putExtra(Constants.ACCELERATION_Z_BROADCAST_KEY, combinedData.accelerationZ);
        combinedBroadcast.putExtra(Constants.LATITUDE_BROADCAST_KEY, combinedData.latitude);
        combinedBroadcast.putExtra(Constants.LONGITUDE_BROADCAST_KEY, combinedData.longitude);
        combinedBroadcast.putExtra(Constants.LABEL_INTENT_EXTRA_KEY, csvData.label);
        LocalBroadcastManager.getInstance(this).sendBroadcast(combinedBroadcast);
    }

    private void addAccelerometerRowInCSVFile(SensorData accelerometerData) {
        recordingFilePrintWriter.println(accelerometerData.timeStamp
                + " :: - , - , " + accelerometerData.accelerationX
                + ", " + accelerometerData.accelerationY
                + ", " + accelerometerData.accelerationZ
                + ", " + csvData.label);
    }

    private void addGPSRowInCSVFile(SensorData GPSData) {
        recordingFilePrintWriter.println(GPSData.timeStamp
                + " :: " + GPSData.latitude
                + ", " + GPSData.longitude
                + ", - , - , - "
                + ", " + csvData.label);
    }

    private void addCombinedRowInCSVFile(SensorData combinedData) {
        recordingFilePrintWriter.println(combinedData.timeStamp
                + " :: " + combinedData.latitude
                + ", " + combinedData.longitude
                + ", " + combinedData.accelerationX
                + ", " + combinedData.accelerationY
                + ", " + combinedData.accelerationZ
                + ", " + csvData.label);
    }

    private void addNewAccelerometerSensorData() {
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) DateFormat.getDateTimeInstance();
        simpleDateFormat.applyPattern("dd-MM hh:mm:ss.SSS");
        String timeStamp = simpleDateFormat.format(new Date());
        SensorData accelerometerData = new SensorData(timeStamp, latestAccelerationX, latestAccelerationY, latestAccelerationZ);
        csvData.sensorData.add(accelerometerData);
        addAccelerometerRowInCSVFile(accelerometerData);
        sendAccelerometerEventBroadcast(accelerometerData);
        updateLatestRecordInSharedPreference(accelerometerData.timeStamp
                + " :: - , - , " + accelerometerData.accelerationX
                + ", " + accelerometerData.accelerationY
                + ", " + accelerometerData.accelerationZ
                + ", " + csvData.label);
    }

    private void addNewGPSSensorData() {
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) DateFormat.getDateTimeInstance();
        simpleDateFormat.applyPattern("dd-MM hh:mm:ss.SSS");
        String timeStamp = simpleDateFormat.format(new Date());
        SensorData gpsData = new SensorData(timeStamp, latestLatitude, latestLongitude);
        csvData.sensorData.add(gpsData);
        addGPSRowInCSVFile(gpsData);
        sendGPSEventBroadcast(gpsData);
        updateLatestRecordInSharedPreference(gpsData.timeStamp
                + " :: " + gpsData.latitude
                + ", " + gpsData.longitude
                + ", - , - , - "
                + ", " + csvData.label);
    }

    private void addNewCombinedSensorData() {
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) DateFormat.getDateTimeInstance();
        simpleDateFormat.applyPattern("dd-MM hh:mm:ss.SSS");
        String timeStamp = simpleDateFormat.format(new Date());
        SensorData combinedData = new SensorData(timeStamp, latestAccelerationX, latestAccelerationY, latestAccelerationZ,
                latestLatitude, latestLongitude);
        csvData.sensorData.add(combinedData);
        addCombinedRowInCSVFile(combinedData);
        sendCombinedEventBroadcast(combinedData);
        updateLatestRecordInSharedPreference(combinedData.timeStamp
                + " :: " + combinedData.latitude
                + ", " + combinedData.longitude
                + ", " + combinedData.accelerationX
                + ", " + combinedData.accelerationY
                + ", " + combinedData.accelerationZ
                + ", " + csvData.label);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent==null)
            return START_STICKY;
        csvData = new CSVData(intent.getStringExtra(Constants.LABEL_INTENT_EXTRA_KEY),
                intent.getBooleanExtra(Constants.ACCELEROMETER_INTENT_EXTRA_KEY, false),
                intent.getBooleanExtra(Constants.GPS_INTENT_EXTRA_KEY, false));
        sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
        initializeRecordingFile();
        updateSharedPreferences();

        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        if (csvData.isGPSRecorded && locationManager!=null) {
            try {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        SensorManager.SENSOR_DELAY_NORMAL, 0, this);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        SensorManager.SENSOR_DELAY_NORMAL, 0, this);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastKnownLocation==null)
                    lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (lastKnownLocation!=null) {
                    latestLatitude = lastKnownLocation.getLatitude();
                    latestLongitude = lastKnownLocation.getLongitude();
                }
            } catch (SecurityException e) {
                Log.d("Recorder Service", "GPS Permission denied!");
                stopSelf();
            }
        }

        if (csvData.isAccelerometerRecorded && sensorManager!=null) {
            sensorManager.registerListener(this,
                    sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        if (csvData!=null && locationManager!=null && csvData.isGPSRecorded)
            locationManager.removeUpdates(this);
        if (csvData!=null && sensorManager!=null && csvData.isAccelerometerRecorded)
            sensorManager.unregisterListener(this);
        if (sharedPreferences==null)
            sharedPreferences=getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean(Constants.SERVICE_RUNNING_SHARED_PREFERENCE_KEY, false);
        editor.apply();
        if (recordingFilePrintWriter!=null)
        recordingFilePrintWriter.close();
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent){
        Intent restartServiceTask = new Intent(getApplicationContext(),this.getClass());
        restartServiceTask.setPackage(getPackageName());
        PendingIntent restartPendingIntent =PendingIntent.getService(getApplicationContext(), 1,restartServiceTask, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager myAlarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        myAlarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartPendingIntent);

        super.onTaskRemoved(rootIntent);
    }



    @Override
    public void onProviderEnabled(String s) {
        if (s.equals(LocationManager.GPS_PROVIDER))
            Log.d("Recorder Service","GPS Enabled!");
    }

    @Override
    public void onProviderDisabled(String s) {
        if (s.equals(LocationManager.GPS_PROVIDER))
            Log.d("Recorder Service", "GPS Disabled!");
    }

    @Override
    public void onLocationChanged(Location location) {
        latestLatitude = location.getLatitude();
        latestLongitude = location.getLongitude();
        if (csvData.isAccelerometerRecorded)
            addNewCombinedSensorData();
        else
            addNewGPSSensorData();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            latestAccelerationX = sensorEvent.values[0];
            latestAccelerationY = sensorEvent.values[1];
            latestAccelerationZ = sensorEvent.values[2];
            if (csvData.isGPSRecorded)
                addNewCombinedSensorData();
            else
                addNewAccelerometerSensorData();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}
