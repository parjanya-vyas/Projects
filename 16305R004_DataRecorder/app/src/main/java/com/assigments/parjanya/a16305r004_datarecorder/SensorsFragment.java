package com.assigments.parjanya.a16305r004_datarecorder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class SensorsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener{

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    CheckBox accelerometerCheckBox;
    CheckBox gpsCheckBox;

    private boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager!=null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void buildEnableGPSDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Enable GPS");
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu to enable GPS?");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                getActivity().startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    private void initializeCheckBoxesIfAvailable() {
        accelerometerCheckBox.setChecked(sharedPreferences.getBoolean(Constants.ACCELEROMETER_SHARED_PREFERENCE_KEY, false));
        gpsCheckBox.setChecked(sharedPreferences.getBoolean(Constants.GPS_SHARED_PREFERENCE_KEY, false));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View sensorFragment = inflater.inflate(R.layout.fragment_sensors, container, false);
        accelerometerCheckBox = sensorFragment.findViewById(R.id.accelerometer_checkbox);
        gpsCheckBox = sensorFragment.findViewById(R.id.gps_checkbox);

        sharedPreferences = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);

        accelerometerCheckBox.setOnCheckedChangeListener(this);
        gpsCheckBox.setOnCheckedChangeListener(this);

        initializeCheckBoxesIfAvailable();

        return sensorFragment;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        editor = sharedPreferences.edit();
        switch (compoundButton.getId()) {
            case R.id.accelerometer_checkbox:
                editor.putBoolean(Constants.ACCELEROMETER_SHARED_PREFERENCE_KEY, checked);
                break;
            case R.id.gps_checkbox:
                if (checked && !isGPSEnabled()) {
                    buildEnableGPSDialog();
                    gpsCheckBox.setChecked(false);
                    return;
                }
                editor.putBoolean(Constants.GPS_SHARED_PREFERENCE_KEY, checked);
                break;
        }
        editor.apply();
    }
}
