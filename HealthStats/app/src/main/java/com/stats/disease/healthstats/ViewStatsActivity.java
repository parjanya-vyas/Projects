package com.stats.disease.healthstats;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ViewStatsActivity extends Activity {

    Context context;

    Button localityChartButton;
    Button diseaseChartButton;
    Button timeChartButton;
    Button diseaseMapButton;

    View.OnClickListener localityChartListener;
    View.OnClickListener diseaseChartListener;
    View.OnClickListener timeChartListener;
    View.OnClickListener diseaseMapClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_stats);

        context = this;

        initializeLayouts();
        initializeClickListeners();
    }

    void initializeLayouts(){
        localityChartButton = (Button)findViewById(R.id.locality_chart);
        GradientDrawable bgColor = (GradientDrawable) localityChartButton.getBackground();
        bgColor.setColor(Color.rgb(207, 176, 155));
        diseaseChartButton = (Button)findViewById(R.id.disease_chart);
        bgColor = (GradientDrawable) diseaseChartButton.getBackground();
        bgColor.setColor(Color.rgb(29, 86, 101));
        timeChartButton = (Button)findViewById(R.id.time_chart);
        bgColor = (GradientDrawable) timeChartButton.getBackground();
        bgColor.setColor(Color.rgb(38, 38, 38));
        diseaseMapButton = (Button) findViewById(R.id.disease_map);
        bgColor = (GradientDrawable) diseaseMapButton.getBackground();
        bgColor.setColor(Color.rgb(200, 200, 110));
    }

    void initializeClickListeners(){
        localityChartListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent localityChartActivity = new Intent(context,BarAndPieChartActivity.class);
                localityChartActivity.putExtra(Constants.BAR_AND_PIE_CHART_TYPE,Constants.CHART_TYPE_LOCALITY);
                startActivity(localityChartActivity);
            }
        };

        diseaseChartListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent diseaseChartActivity = new Intent(context,BarAndPieChartActivity.class);
                diseaseChartActivity.putExtra(Constants.BAR_AND_PIE_CHART_TYPE,Constants.CHART_TYPE_DISEASE);
                startActivity(diseaseChartActivity);
            }
        };

        timeChartListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent timeChartActivity = new Intent(context,LineChartActivity.class);
                startActivity(timeChartActivity);
            }
        };
        diseaseMapClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent diseaseMapActivity = new Intent(context,DiseaseMapActivity.class);
                startActivity(diseaseMapActivity);
            }
        };

        localityChartButton.setOnClickListener(localityChartListener);
        diseaseChartButton.setOnClickListener(diseaseChartListener);
        timeChartButton.setOnClickListener(timeChartListener);
        diseaseMapButton.setOnClickListener(diseaseMapClickListener);
    }
}
