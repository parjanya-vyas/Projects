package com.assigments.parjanya.a16305r004_datarecorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class RecordingsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener{

    private SharedPreferences sharedPreferences;

    private Switch startRecordingSwitch;
    private TextView recordingTextView1;
    private TextView recordingTextView2;
    private TextView recordingTextView3;
    private TextView recordingTextView4;
    private TextView recordingTextView5;

    private int numberOfRecords = 0;

    private Spinner labelSpinner;
    private ArrayAdapter<String> labelArrayAdapter;

    private Bundle parentArgs;

    private BroadcastReceiver accelerometerBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            addNewAccelerometerEventRow(intent.getStringExtra(Constants.TIMESTAMP_BROADCAST_KEY),
                    intent.getFloatExtra(Constants.ACCELERATION_X_BROADCAST_KEY,0),
                    intent.getFloatExtra(Constants.ACCELERATION_Y_BROADCAST_KEY,0),
                    intent.getFloatExtra(Constants.ACCELERATION_Z_BROADCAST_KEY,0),
                    intent.getStringExtra(Constants.LABEL_INTENT_EXTRA_KEY));
        }
    };

    private BroadcastReceiver gpsBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            addNewGPSEventRow(intent.getStringExtra(Constants.TIMESTAMP_BROADCAST_KEY),
                    intent.getDoubleExtra(Constants.LATITUDE_BROADCAST_KEY,0),
                    intent.getDoubleExtra(Constants.LONGITUDE_BROADCAST_KEY,0),
                    intent.getStringExtra(Constants.LABEL_INTENT_EXTRA_KEY));
        }
    };

    private BroadcastReceiver combinedBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            addNewCombinedEventRow(intent.getStringExtra(Constants.TIMESTAMP_BROADCAST_KEY),
                    intent.getFloatExtra(Constants.ACCELERATION_X_BROADCAST_KEY,0),
                    intent.getFloatExtra(Constants.ACCELERATION_Y_BROADCAST_KEY,0),
                    intent.getFloatExtra(Constants.ACCELERATION_Z_BROADCAST_KEY,0),
                    intent.getDoubleExtra(Constants.LATITUDE_BROADCAST_KEY,0),
                    intent.getDoubleExtra(Constants.LONGITUDE_BROADCAST_KEY,0),
                    intent.getStringExtra(Constants.LABEL_INTENT_EXTRA_KEY));
        }
    };

    private void addNewRowInReadings(String rowText) {
        switch (numberOfRecords) {
            case 0:
                recordingTextView1.setText(rowText);
                numberOfRecords++;
                break;
            case 1:
                recordingTextView2.setText(rowText);
                numberOfRecords++;
                break;
            case 2:
                recordingTextView3.setText(rowText);
                numberOfRecords++;
                break;
            case 3:
                recordingTextView4.setText(rowText);
                numberOfRecords++;
                break;
            case 4:
                recordingTextView5.setText(rowText);
                numberOfRecords++;
                break;
            default:
                recordingTextView1.setText(recordingTextView2.getText().toString());
                recordingTextView2.setText(recordingTextView3.getText().toString());
                recordingTextView3.setText(recordingTextView4.getText().toString());
                recordingTextView4.setText(recordingTextView5.getText().toString());
                recordingTextView5.setText(rowText);
        }
    }

    private void addNewAccelerometerEventRow(String timeStamp, float accelerationX, float accelerationY, float accelerationZ, String label) {
        addNewRowInReadings(timeStamp + " :: - , - , " + accelerationX + ", " + accelerationY + ", " + accelerationZ + ", " + label);
    }

    private void addNewGPSEventRow(String timeStamp, double latitude, double longitude, String label) {
        addNewRowInReadings(timeStamp + " :: " + latitude + ", " + longitude + ", - , - , - , " + label);
    }

    private void addNewCombinedEventRow(String timeStamp,
                                        float accelerationX, float accelerationY, float accelerationZ,
                                        double latitude, double longitude,
                                        String label) {
        addNewRowInReadings(timeStamp + " :: " + latitude + ", " + longitude
                + ", " + accelerationX + ", " + accelerationY + ", " + accelerationZ
                + ", " + label);
    }

    boolean allDataPresent() {
        if (!sharedPreferences.getBoolean(Constants.USER_DATA_PRESENT_SHARED_PREFERENCE, false)) {
            Toast.makeText(getActivity(), "User Data Not Submitted Yet!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!sharedPreferences.getBoolean(Constants.GPS_SHARED_PREFERENCE_KEY, false)
                && !sharedPreferences.getBoolean(Constants.ACCELEROMETER_SHARED_PREFERENCE_KEY, false)) {
            Toast.makeText(getActivity(), "No Sensors Selected!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    void initializeViewsIfAvailable() {
        numberOfRecords = 0;
        if (parentArgs.containsKey(Constants.RECORDING_SWITCH_STATE_BUNDLE_KEY)
                && parentArgs.getBoolean(Constants.RECORDING_SWITCH_STATE_BUNDLE_KEY)
                && startRecordingSwitch!=null
                && !startRecordingSwitch.isChecked())
            startRecordingSwitch.setChecked(true);
        recordingTextView1.setText(
                sharedPreferences.getString(Constants.RECORDING_ROW_1_SHARED_PREFERENCE_KEY,
                        getString(R.string.empty)));
        recordingTextView2.setText(
                sharedPreferences.getString(Constants.RECORDING_ROW_2_SHARED_PREFERENCE_KEY,
                        getString(R.string.empty)));
        recordingTextView3.setText(
                sharedPreferences.getString(Constants.RECORDING_ROW_3_SHARED_PREFERENCE_KEY,
                        getString(R.string.empty)));
        recordingTextView4.setText(
                sharedPreferences.getString(Constants.RECORDING_ROW_4_SHARED_PREFERENCE_KEY,
                        getString(R.string.empty)));
        recordingTextView5.setText(
                sharedPreferences.getString(Constants.RECORDING_ROW_5_SHARED_PREFERENCE_KEY,
                        getString(R.string.empty)));
        if (sharedPreferences.contains(Constants.RECORDING_ROW_1_SHARED_PREFERENCE_KEY))
            numberOfRecords++;
        if (sharedPreferences.contains(Constants.RECORDING_ROW_2_SHARED_PREFERENCE_KEY))
            numberOfRecords++;
        if (sharedPreferences.contains(Constants.RECORDING_ROW_3_SHARED_PREFERENCE_KEY))
            numberOfRecords++;
        if (sharedPreferences.contains(Constants.RECORDING_ROW_4_SHARED_PREFERENCE_KEY))
            numberOfRecords++;
        if (sharedPreferences.contains(Constants.RECORDING_ROW_5_SHARED_PREFERENCE_KEY))
            numberOfRecords++;
    }

    void startRecordingSensors() {
        Intent recorderServiceIntent = new Intent(getActivity(), SensorRecorderService.class);
        recorderServiceIntent.putExtra(Constants.LABEL_INTENT_EXTRA_KEY, labelArrayAdapter.getItem(labelSpinner.getSelectedItemPosition()));
        recorderServiceIntent.putExtra(Constants.ACCELEROMETER_INTENT_EXTRA_KEY,
                sharedPreferences.getBoolean(Constants.ACCELEROMETER_SHARED_PREFERENCE_KEY, false));
        recorderServiceIntent.putExtra(Constants.GPS_INTENT_EXTRA_KEY,
                sharedPreferences.getBoolean(Constants.GPS_SHARED_PREFERENCE_KEY, false));
        getActivity().startService(recorderServiceIntent);
    }

    void stopRecordingSensors() {
        Intent recorderServiceIntent = new Intent(getActivity(), SensorRecorderService.class);
        getActivity().stopService(recorderServiceIntent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_recordings, container, false);

        sharedPreferences = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);

        startRecordingSwitch = fragment.findViewById(R.id.start_recording_switch);
        recordingTextView1 = fragment.findViewById(R.id.recording1_text_view);
        recordingTextView2 = fragment.findViewById(R.id.recording2_text_view);
        recordingTextView3 = fragment.findViewById(R.id.recording3_text_view);
        recordingTextView4 = fragment.findViewById(R.id.recording4_text_view);
        recordingTextView5 = fragment.findViewById(R.id.recording5_text_view);

        startRecordingSwitch.setChecked(sharedPreferences.getBoolean(Constants.SERVICE_RUNNING_SHARED_PREFERENCE_KEY, false));

        labelSpinner = fragment.findViewById(R.id.label_spinner);
        labelArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.label_spinner_items));
        labelArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        labelSpinner.setAdapter(labelArrayAdapter);

        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        startRecordingSwitch.setOnCheckedChangeListener(this);
        parentArgs = getArguments();
        initializeViewsIfAvailable();
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(accelerometerBroadcast,
                new IntentFilter(Constants.ACCELEROMETER_EVENT_ACTION));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(gpsBroadcast,
                new IntentFilter(Constants.GPS_EVENT_ACTION));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(combinedBroadcast,
                new IntentFilter(Constants.COMBINED_EVENT_ACTION));
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(accelerometerBroadcast);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(gpsBroadcast);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(combinedBroadcast);
        super.onPause();
    }

    @Override
    public void onStop() {
        startRecordingSwitch.setOnCheckedChangeListener(null);
        super.onStop();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        if (checked && allDataPresent() && !sharedPreferences.getBoolean(Constants.SERVICE_RUNNING_SHARED_PREFERENCE_KEY, false))
            startRecordingSensors();
        if (!checked && sharedPreferences.getBoolean(Constants.SERVICE_RUNNING_SHARED_PREFERENCE_KEY, false))
            stopRecordingSensors();
    }
}
