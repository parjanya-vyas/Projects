package com.assigments.parjanya.a16305r004_datarecorder;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

public class ContainerActivity extends FragmentActivity implements View.OnClickListener, LoginFragment.SubmitButtonClickedListener {

    ViewPager mainViewPager;
    TabLayout mainTabLayout;
    DataRecorderPagerAdapter mainPagerAdapter;

    Toolbar toolbar;
    ImageButton logoButton;
    ImageButton startRecordingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        mainViewPager = findViewById(R.id.main_view_pager);
        mainTabLayout = findViewById(R.id.main_tab_layout);
        mainPagerAdapter = new DataRecorderPagerAdapter(getSupportFragmentManager(), this);
        mainViewPager.setAdapter(mainPagerAdapter);
        mainTabLayout.setupWithViewPager(mainViewPager);

        toolbar = findViewById(R.id.app_bar);
        logoButton = toolbar.findViewById(R.id.app_logo_button);
        startRecordingButton = toolbar.findViewById(R.id.start_recording_button);

        logoButton.setOnClickListener(this);
        startRecordingButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.app_logo_button:
                mainViewPager.setCurrentItem(Constants.LOGIN_PAGE_INDEX, true);
                break;
            case R.id.start_recording_button:
                mainPagerAdapter.setStartRecordingSwitchChecked(true);
                mainViewPager.setCurrentItem(Constants.RECORDINGS_PAGE_INDEX, false);
                mainPagerAdapter.setStartRecordingSwitchChecked(false);
        }
    }

    @Override
    public void submitButtonClicked() {
        mainViewPager.setCurrentItem(Constants.SENSORS_PAGE_INDEX, true);
    }
}