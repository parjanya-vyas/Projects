package com.assigments.parjanya.a16305r004_datarecorder;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by parjanya on 14/2/18.
 */

public class DataRecorderPagerAdapter extends FragmentStatePagerAdapter {

    private Context context;
    private boolean startRecordingSwitchChecked = false;

    DataRecorderPagerAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        this.context = context;
    }

    void setStartRecordingSwitchChecked(boolean startRecordingSwitchChecked) {
        this.startRecordingSwitchChecked = startRecordingSwitchChecked;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case Constants.LOGIN_PAGE_INDEX:
                return new LoginFragment();
            case Constants.SENSORS_PAGE_INDEX:
                return new SensorsFragment();
            case Constants.RECORDINGS_PAGE_INDEX:
                Fragment recordingsFragment = new RecordingsFragment();
                Bundle recordingSwitchState = new Bundle();
                recordingSwitchState.putBoolean(Constants.RECORDING_SWITCH_STATE_BUNDLE_KEY, startRecordingSwitchChecked);
                recordingsFragment.setArguments(recordingSwitchState);
                return recordingsFragment;
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return context.getResources().getStringArray(R.array.navigation_items)[position];
    }

    @Override
    public int getCount() {
        return Constants.NUMBER_OF_PAGES;
    }
}
