package com.stats.disease.healthstats;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DateFormatSymbols;
import java.util.ArrayList;

public class LineChartActivity extends Activity {

    Context context;

    LineChart lineChart;

    LinearLayout progressBarParent;

    Spinner localitySpinner;
    Spinner diseaseSpinner;
    Spinner monthSpinner;
    Spinner yearSpinner;

    AdapterView.OnItemSelectedListener localityListener;
    AdapterView.OnItemSelectedListener diseaseListener;
    AdapterView.OnItemSelectedListener monthListener;
    AdapterView.OnItemSelectedListener yearListener;

    String[] yearStrings;
    String[] monthStrings;
    String[] yearStringsWithoutAll;

    Boolean isCreationCompleted = false;

    private class LineChartDrawer extends ServerConnector{
        @Override
        protected void onPreExecute() {
            progressBarParent.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            ArrayList<String> lineChartLabels = new ArrayList<>();
            ArrayList<Entry> lineChartData = new ArrayList<>();

            if (s.equalsIgnoreCase("false")){
                Toast.makeText(context,"No cases reported yet",Toast.LENGTH_SHORT).show();
                progressBarParent.setVisibility(View.GONE);
                return;
            }

            String[] serverReply = s.split(" ");
            for (int i=0; i<serverReply.length; i++) {
                if (i%2==0) {
                    if (monthSpinner.getSelectedItem().toString().equals(getResources().getString(R.string.all))
                            && !yearSpinner.getSelectedItem().toString().equals(getResources().getString(R.string.all)))
                        lineChartLabels.add(Utils.getMonthString(Integer.parseInt(serverReply[i])).substring(0, 3));
                    else
                        lineChartLabels.add(serverReply[i]);
                }
                else
                    lineChartData.add(new Entry(Float.parseFloat(serverReply[i]), i/2));
            }

            LineDataSet lineDataSet = new LineDataSet(lineChartData, getResources().getString(R.string.number_of_instances));
            lineDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

            LineData lineData = new LineData(lineChartLabels, lineDataSet);
            lineChart.setData(lineData);
            lineChart.animateY(Constants.Y_ANIMATION_MILLIS);
            lineChart.setVisibleXRangeMaximum(3f);
            lineChart.setDescription(getResources().getString(R.string.number_of_instances));
            lineChart.notifyDataSetChanged();
            lineChart.invalidate();
            progressBarParent.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart);

        context = this;

        initializeLayouts();
        initializeYearAndMonthStringArrays();
        initializeSpinners();
        initializeOnItemSelectedListeners();
        drawLineChart();
    }

    void initializeLayouts(){
        progressBarParent = (LinearLayout)findViewById(R.id.line_progress_parent);
        lineChart = (LineChart)findViewById(R.id.line_chart);

        localitySpinner = (Spinner)findViewById(R.id.locality_line_spinner);
        diseaseSpinner = (Spinner)findViewById(R.id.disease_line_spinner);
        monthSpinner = (Spinner)findViewById(R.id.month_line_spinner);
        yearSpinner = (Spinner)findViewById(R.id.year_line_spinner);
    }

    void initializeYearAndMonthStringArrays(){
        yearStrings = new String[Constants.CURRENT_YEAR - Constants.START_YEAR + 2];
        monthStrings = new String[13];
        yearStringsWithoutAll = new String[Constants.CURRENT_YEAR - Constants.START_YEAR + 1];

        yearStrings[0] = context.getResources().getString(R.string.all);
        monthStrings[0] = context.getResources().getString(R.string.all);

        for (int i=Constants.START_YEAR; i<=Constants.CURRENT_YEAR; i++) {
            yearStrings[i - Constants.START_YEAR + 1] = Integer.toString(Constants.START_YEAR - i + Constants.CURRENT_YEAR);
            yearStringsWithoutAll[i - Constants.START_YEAR] = Integer.toString(Constants.START_YEAR - i + Constants.CURRENT_YEAR);
        }

        System.arraycopy(new DateFormatSymbols().getMonths(), 0, monthStrings, 1, 12);
    }

    void initializeSpinners(){
        String[] localityStrings = context.getResources().getStringArray(R.array.locality_array);
        String[] diseaseStrings = context.getResources().getStringArray(R.array.disease_array);

        String[] spinnerLocalities = new String[localityStrings.length + 1];
        spinnerLocalities[0] = context.getResources().getString(R.string.all);
        System.arraycopy(localityStrings, 0, spinnerLocalities, 1, localityStrings.length);

        String[] spinnerDiseases = new String[diseaseStrings.length + 1];
        spinnerDiseases[0] = context.getResources().getString(R.string.all);
        System.arraycopy(diseaseStrings, 0, spinnerDiseases, 1, diseaseStrings.length);

        ArrayAdapter<String> localityAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, spinnerLocalities);
        ArrayAdapter<String> diseaseAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, spinnerDiseases);
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, monthStrings);
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, yearStrings);

        localityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        diseaseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        localitySpinner.setAdapter(localityAdapter);
        diseaseSpinner.setAdapter(diseaseAdapter);
        monthSpinner.setAdapter(monthAdapter);
        yearSpinner.setAdapter(yearAdapter);

        yearSpinner.setSelection(1);
    }

    void initializeOnItemSelectedListeners(){
        localityListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isCreationCompleted)
                    drawLineChart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        diseaseListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isCreationCompleted)
                    drawLineChart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        monthListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isCreationCompleted) {
                    String selectedMonth = parent.getItemAtPosition(position).toString();
                    if (!getResources().getString(R.string.all).equals(selectedMonth)){
                        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, yearStringsWithoutAll);
                        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        yearSpinner.setAdapter(yearAdapter);
                    } else {
                        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, yearStrings);
                        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        yearSpinner.setAdapter(yearAdapter);
                    }
                    drawLineChart();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        yearListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isCreationCompleted)
                    drawLineChart();
                else
                    isCreationCompleted = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        localitySpinner.setOnItemSelectedListener(localityListener);
        diseaseSpinner.setOnItemSelectedListener(diseaseListener);
        monthSpinner.setOnItemSelectedListener(monthListener);
        yearSpinner.setOnItemSelectedListener(yearListener);
    }

    void drawLineChart(){
        LineChartDrawer lineChartDrawer = new LineChartDrawer();
        lineChartDrawer.execute(Constants.SERVER_URL_STRING, buildLineChartQuery());
    }

    String buildLineChartQuery(){
        String lineChartQuery = "";
        if (yearSpinner.getSelectedItem().toString().equals(getResources().getString(R.string.all))){
            lineChartQuery += Constants.GET_INSTANCES_YEARS_WIDE + " " +
                    diseaseSpinner.getSelectedItem().toString().replaceAll("\\s+","") + " " +
                    localitySpinner.getSelectedItem().toString().replaceAll("\\s+","");
        }
        else if (monthSpinner.getSelectedItem().toString().equals(getResources().getString(R.string.all))){
            lineChartQuery += Constants.GET_INSTANCES_MONTHS_WIDE + " " +
                    yearSpinner.getSelectedItem().toString() + " " +
                    diseaseSpinner.getSelectedItem().toString().replaceAll("\\s+","") + " " +
                    localitySpinner.getSelectedItem().toString().replaceAll("\\s+","") + " ";
        } else {
            lineChartQuery += Constants.GET_INSTANCES_DAYS_WIDE + " " +
                    Utils.getMonthInteger(monthSpinner.getSelectedItem().toString()) + " " +
                    yearSpinner.getSelectedItem().toString() + " " +
                    diseaseSpinner.getSelectedItem().toString().replaceAll("\\s+","") + " " +
                    localitySpinner.getSelectedItem().toString().replaceAll("\\s+","");
        }

        return lineChartQuery;
    }
}
