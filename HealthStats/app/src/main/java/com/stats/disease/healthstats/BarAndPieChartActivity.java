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

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DateFormatSymbols;
import java.util.ArrayList;

public class BarAndPieChartActivity extends Activity {

    Context context;

    LinearLayout localityParent;
    LinearLayout diseaseParent;
    LinearLayout progressBarParent;

    Spinner localitySpinner;
    Spinner diseaseSpinner;
    Spinner monthSpinner;
    Spinner daySpinner;
    Spinner yearSpinner;
    Spinner chartTypeSpinner;

    BarChart localityBarChart;
    BarChart diseaseBarChart;

    PieChart localityPieChart;
    PieChart diseasePieChart;

    AdapterView.OnItemSelectedListener localityItemSelectedListener;
    AdapterView.OnItemSelectedListener diseaseItemSelectedListener;
    AdapterView.OnItemSelectedListener monthItemSelectedListener;
    AdapterView.OnItemSelectedListener dayItemSelectedListener;
    AdapterView.OnItemSelectedListener yearItemSelectedListener;
    AdapterView.OnItemSelectedListener chartTypeItemSelectedListener;

    String chartFor;

    String[] dateStrings28;
    String[] dateStrings29;
    String[] dateStrings30;
    String[] dateStrings31;
    String[] yearStrings;

    ArrayList<String> months30;
    ArrayList<String> months31;

    ArrayAdapter<String> localityAdapter;
    ArrayAdapter<String> diseaseAdapter;
    ArrayAdapter<String> chartTypeAdapter;
    ArrayAdapter<String> monthAdapter;
    ArrayAdapter<String> dayAdapter;
    ArrayAdapter<String> yearAdapter;

    Boolean isCreationCompleted = false;

    private class LocalityBarChart extends ServerConnector{
        @Override
        protected void onPreExecute() {
            localityBarChart.setVisibility(View.GONE);
            progressBarParent.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            ArrayList<String> barChartLabels = new ArrayList<>();
            ArrayList<BarEntry> barChartData = new ArrayList<>();

            if (s.equalsIgnoreCase("false")){
                Toast.makeText(context,"No cases reported yet",Toast.LENGTH_SHORT).show();
                progressBarParent.setVisibility(View.GONE);
                localityBarChart.setVisibility(View.GONE);
                return;
            }

            String[] serverReply = s.split(" ");
            for (int i=0; i<serverReply.length; i++) {
                if (i%2==0)
                    barChartLabels.add(serverReply[i]);
                else
                    barChartData.add(new BarEntry(Float.parseFloat(serverReply[i]), i/2));
            }

            BarDataSet barDataSet = new BarDataSet(barChartData, getResources().getString(R.string.number_of_instances));
            barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);

            BarData barData = new BarData(barChartLabels, barDataSet);
            localityBarChart.setData(barData);
            localityBarChart.animateY(Constants.Y_ANIMATION_MILLIS);
            localityBarChart.setVisibleXRangeMaximum(3f);
            localityBarChart.setDescription(getResources().getString(R.string.number_of_instances));
            localityBarChart.notifyDataSetChanged();
            localityBarChart.invalidate();
            progressBarParent.setVisibility(View.GONE);
            localityBarChart.setVisibility(View.VISIBLE);
        }
    }

    private class DiseaseBarChart extends ServerConnector{
        @Override
        protected void onPreExecute() {
            diseaseBarChart.setVisibility(View.GONE);
            progressBarParent.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            ArrayList<String> barChartLabels = new ArrayList<>();
            ArrayList<BarEntry> barChartData = new ArrayList<>();

            if (s.equalsIgnoreCase("false")){
                Toast.makeText(context,"No cases reported yet",Toast.LENGTH_SHORT).show();
                progressBarParent.setVisibility(View.GONE);
                diseaseBarChart.setVisibility(View.GONE);
                return;
            }

            String[] serverReply = s.split(" ");
            for (int i=0; i<serverReply.length; i++) {
                if (i%2==0)
                    barChartLabels.add(serverReply[i]);
                else
                    barChartData.add(new BarEntry(Float.parseFloat(serverReply[i]), i/2));
            }

            BarDataSet barDataSet = new BarDataSet(barChartData, getResources().getString(R.string.number_of_instances));
            barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);

            BarData barData = new BarData(barChartLabels, barDataSet);
            diseaseBarChart.resetViewPortOffsets();
            diseaseBarChart.setData(barData);
            diseaseBarChart.animateY(Constants.Y_ANIMATION_MILLIS);
            diseaseBarChart.setVisibleXRangeMaximum(3f);
            diseaseBarChart.setDescription(getResources().getString(R.string.number_of_instances));
            diseaseBarChart.notifyDataSetChanged();
            diseaseBarChart.invalidate();
            progressBarParent.setVisibility(View.GONE);
            diseaseBarChart.setVisibility(View.VISIBLE);
        }
    }

    private class LocalityPieChart extends ServerConnector{
        @Override
        protected void onPreExecute() {
            localityPieChart.setVisibility(View.GONE);
            progressBarParent.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            ArrayList<String> pieChartLabels = new ArrayList<>();
            ArrayList<Entry> pieChartData = new ArrayList<>();

            if (s.equalsIgnoreCase("false")){
                Toast.makeText(context,"No cases reported yet",Toast.LENGTH_SHORT).show();
                progressBarParent.setVisibility(View.GONE);
                localityPieChart.setVisibility(View.GONE);
                return;
            }

            String[] serverReply = s.split(" ");
            for (int i=0; i<serverReply.length; i++) {
                if (i%2==0)
                    pieChartLabels.add(serverReply[i]);
                else
                    pieChartData.add(new Entry(Float.parseFloat(serverReply[i]), i/2));
            }

            PieDataSet pieDataSet = new PieDataSet(pieChartData, getResources().getString(R.string.percentage));
            pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);

            PieData pieData = new PieData(pieChartLabels, pieDataSet);
            localityPieChart.setData(pieData);
            localityPieChart.animateY(Constants.Y_ANIMATION_MILLIS);
            localityPieChart.setDrawHoleEnabled(false);
            localityPieChart.setDescription(getResources().getString(R.string.percentage));
            localityPieChart.notifyDataSetChanged();
            localityPieChart.invalidate();
            progressBarParent.setVisibility(View.GONE);
            localityPieChart.setVisibility(View.VISIBLE);
        }
    }

    private class DiseasePieChart extends ServerConnector{
        @Override
        protected void onPreExecute() {
            diseasePieChart.setVisibility(View.GONE);
            progressBarParent.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            ArrayList<String> pieChartLabels = new ArrayList<>();
            ArrayList<Entry> pieChartData = new ArrayList<>();

            if (s.equalsIgnoreCase("false")){
                Toast.makeText(context,"No cases reported yet",Toast.LENGTH_SHORT).show();
                progressBarParent.setVisibility(View.GONE);
                diseasePieChart.setVisibility(View.GONE);
                return;
            }

            String[] serverReply = s.split(" ");
            for (int i=0; i<serverReply.length; i++) {
                if (i%2==0)
                    pieChartLabels.add(serverReply[i]);
                else
                    pieChartData.add(new Entry(Float.parseFloat(serverReply[i]), i/2));
            }

            PieDataSet pieDataSet = new PieDataSet(pieChartData, getResources().getString(R.string.percentage));
            pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);

            PieData pieData = new PieData(pieChartLabels, pieDataSet);
            diseasePieChart.setData(pieData);
            diseasePieChart.animateY(Constants.Y_ANIMATION_MILLIS);
            diseasePieChart.setDrawHoleEnabled(false);
            diseasePieChart.setDescription(getResources().getString(R.string.percentage));
            diseasePieChart.notifyDataSetChanged();
            diseasePieChart.invalidate();
            progressBarParent.setVisibility(View.GONE);
            diseasePieChart.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_and_pie_chart);

        isCreationCompleted = false;

        context = this;
        chartFor = getIntent().getStringExtra(Constants.BAR_AND_PIE_CHART_TYPE);

        initializeLayouts();
        initializeDateStringArrays();
        initializeSpinners();
        initializeItemSelectedListeners();
        drawSelectedChart();
    }

    void initializeLayouts(){
        localityParent = (LinearLayout)findViewById(R.id.locality_chart_parent);
        diseaseParent = (LinearLayout)findViewById(R.id.disease_chart_parent);
        progressBarParent = (LinearLayout)findViewById(R.id.pie_bar_progress_parent);

        localitySpinner = (Spinner)findViewById(R.id.locality_chart_spinner);
        diseaseSpinner = (Spinner)findViewById(R.id.disease_chart_spinner);
        monthSpinner = (Spinner)findViewById(R.id.month_chart_spinner);
        daySpinner = (Spinner)findViewById(R.id.day_chart_spinner);
        yearSpinner = (Spinner)findViewById(R.id.year_chart_spinner);
        chartTypeSpinner = (Spinner)findViewById(R.id.type_chart_spinner);

        localityBarChart = (BarChart)findViewById(R.id.locality_bar_chart);
        diseaseBarChart = (BarChart)findViewById(R.id.disease_bar_chart);

        localityPieChart = (PieChart)findViewById(R.id.locality_pie_chart);
        diseasePieChart = (PieChart)findViewById(R.id.disease_pie_chart);
    }

    void initializeDateStringArrays(){
        String[] allMonths = new DateFormatSymbols().getMonths();
        dateStrings28 = new String[29];
        dateStrings29 = new String[30];
        dateStrings30 = new String[31];
        dateStrings31 = new String[32];
        yearStrings = new String[Constants.CURRENT_YEAR - Constants.START_YEAR + 2];

        months30 = new ArrayList<>();
        months31 = new ArrayList<>();

        dateStrings28[0] = dateStrings29[0] = dateStrings30[0] = dateStrings31[0] = context.getResources().getString(R.string.all);

        for (int i=1;i<=28;i++) {
            dateStrings28[i] = Integer.toString(i);
            dateStrings29[i] = Integer.toString(i);
            dateStrings30[i] = Integer.toString(i);
            dateStrings31[i] = Integer.toString(i);
        }

        dateStrings29[29] = "29";

        dateStrings30[29] = "29";
        dateStrings30[30] = "30";

        dateStrings31[29] = "29";
        dateStrings31[30] = "30";
        dateStrings31[31] = "31";

        yearStrings[0] = context.getResources().getString(R.string.all);
        for (int i=Constants.START_YEAR; i<=Constants.CURRENT_YEAR; i++)
            yearStrings[i-Constants.START_YEAR+1] = Integer.toString(Constants.START_YEAR - i + Constants.CURRENT_YEAR);

        months30.add(allMonths[3]);
        months30.add(allMonths[5]);
        months30.add(allMonths[8]);
        months30.add(allMonths[10]);

        months31.add(allMonths[0]);
        months31.add(allMonths[2]);
        months31.add(allMonths[4]);
        months31.add(allMonths[6]);
        months31.add(allMonths[7]);
        months31.add(allMonths[9]);
        months31.add(allMonths[11]);
    }

    void initializeSpinners(){
        String[] localityStrings = context.getResources().getStringArray(R.array.locality_array);
        String[] diseaseStrings = context.getResources().getStringArray(R.array.disease_array);
        String[] chartTypeStrings = context.getResources().getStringArray(R.array.chart_type_array);
        String[] monthStrings = new DateFormatSymbols().getMonths();

        String[] spinnerMonths = new String[13];
        spinnerMonths[0] = context.getResources().getString(R.string.all);
        System.arraycopy(monthStrings, 0, spinnerMonths, 1, monthStrings.length);

        localityAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, localityStrings);
        diseaseAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, diseaseStrings);
        chartTypeAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, chartTypeStrings);
        monthAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, spinnerMonths);
        dayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, dateStrings31);
        yearAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, yearStrings);

        localityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        diseaseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chartTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        localitySpinner.setAdapter(localityAdapter);
        diseaseSpinner.setAdapter(diseaseAdapter);
        chartTypeSpinner.setAdapter(chartTypeAdapter);
        monthSpinner.setAdapter(monthAdapter);
        daySpinner.setAdapter(dayAdapter);
        yearSpinner.setAdapter(yearAdapter);
    }

    void initializeItemSelectedListeners(){

        localityItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isCreationCompleted)
                    drawSelectedChart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        diseaseItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isCreationCompleted)
                    drawSelectedChart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        dayItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isCreationCompleted)
                    drawSelectedChart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        monthItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isCreationCompleted) {
                    String monthString = parent.getItemAtPosition(position).toString();
                    int year = 1;

                    if (!yearSpinner.getSelectedItem().toString().equals(getResources().getString(R.string.all))) {
                        year = Integer.parseInt(yearSpinner.getSelectedItem().toString());
                    }

                    if (months31.contains(monthString))
                        dayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, dateStrings31);
                    else if (months30.contains(monthString))
                        dayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, dateStrings30);
                    else if (!yearSpinner.getSelectedItem().toString().equals(getResources().getString(R.string.all)) && year % 4 != 0)
                        dayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, dateStrings28);
                    else
                        dayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, dateStrings28);

                    daySpinner.setAdapter(dayAdapter);

                    drawSelectedChart();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        yearItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isCreationCompleted) {
                    String monthString = monthSpinner.getSelectedItem().toString();
                    int year = 1;

                    if (!yearSpinner.getSelectedItem().toString().equals(getResources().getString(R.string.all))) {
                        year = Integer.parseInt(yearSpinner.getSelectedItem().toString());
                    }

                    if (!months31.contains(monthString) && !months30.contains(monthString) && !yearSpinner.getSelectedItem().toString().equals(getResources().getString(R.string.all))) {
                        if (year % 4 == 0)
                            dayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, dateStrings29);
                        else
                            dayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, dateStrings28);

                        daySpinner.setAdapter(dayAdapter);
                    }

                    drawSelectedChart();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        chartTypeItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isCreationCompleted)
                    drawSelectedChart();
                else
                    isCreationCompleted = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        localitySpinner.setOnItemSelectedListener(localityItemSelectedListener);
        diseaseSpinner.setOnItemSelectedListener(diseaseItemSelectedListener);
        daySpinner.setOnItemSelectedListener(dayItemSelectedListener);
        monthSpinner.setOnItemSelectedListener(monthItemSelectedListener);
        yearSpinner.setOnItemSelectedListener(yearItemSelectedListener);
        chartTypeSpinner.setOnItemSelectedListener(chartTypeItemSelectedListener);
    }

    void drawSelectedChart(){
        setViewVisibilities();
        if (Constants.CHART_TYPE_LOCALITY.equals(chartFor)) {
            if (chartTypeSpinner.getSelectedItem().toString().equals("Bar Chart"))
                drawLocalityBarChart();
            else
                drawLocalityPieChart();
        }
        else {
            if (chartTypeSpinner.getSelectedItem().toString().equals("Bar Chart"))
                drawDiseaseBarChart();
            else
                drawDiseasePieChart();
        }
    }

    void drawLocalityBarChart(){
        LocalityBarChart localityBarChart = new LocalityBarChart();
        localityBarChart.execute(Constants.SERVER_URL_STRING, buildLocalityBarChartQuery());
    }

    void drawDiseaseBarChart(){
        DiseaseBarChart diseaseBarChart = new DiseaseBarChart();
        diseaseBarChart.execute(Constants.SERVER_URL_STRING, buildDiseaseBarChartQuery());
    }

    void drawLocalityPieChart(){
        LocalityPieChart localityPieChart = new LocalityPieChart();
        localityPieChart.execute(Constants.SERVER_URL_STRING, buildLocalityPieChartQuery());
    }

    void drawDiseasePieChart(){
        DiseasePieChart diseasePieChart = new DiseasePieChart();
        diseasePieChart.execute(Constants.SERVER_URL_STRING, buildDiseasePieChartQuery());
    }

    String buildLocalityBarChartQuery(){
        String localityBarChartQuery = "";
        String localityString = localitySpinner.getSelectedItem().toString();
        String dayString = daySpinner.getSelectedItem().toString();
        String monthString = monthSpinner.getSelectedItem().toString();
        String yearString = yearSpinner.getSelectedItem().toString();
        String allString = getResources().getString(R.string.all);

        localityBarChartQuery += Constants.GET_ALL_DISEASE_FROM_LOCALITY_VALUES + " ";
        localityBarChartQuery += localityString.replaceAll("\\s+","") + " ";

        if (dayString.equals(allString))
            localityBarChartQuery += "% ";
        else
            localityBarChartQuery += dayString + " ";

        if (monthString.equals(allString))
            localityBarChartQuery += "% ";
        else
            localityBarChartQuery += Utils.getMonthInteger(monthString) + " ";

        if (yearString.equals(allString))
            localityBarChartQuery += "%";
        else
            localityBarChartQuery += yearString;

        return localityBarChartQuery;
    }

    String buildDiseaseBarChartQuery(){
        String diseaseBarChartQuery = "";
        String diseaseString = diseaseSpinner.getSelectedItem().toString();
        String dayString = daySpinner.getSelectedItem().toString();
        String monthString = monthSpinner.getSelectedItem().toString();
        String yearString = yearSpinner.getSelectedItem().toString();
        String allString = getResources().getString(R.string.all);

        diseaseBarChartQuery += Constants.GET_ALL_LOCALITY_FROM_DISEASE_VALUES + " ";
        diseaseBarChartQuery += diseaseString.replaceAll("\\s+","") + " ";

        if (dayString.equals(allString))
            diseaseBarChartQuery += "% ";
        else
            diseaseBarChartQuery += dayString + " ";

        if (monthString.equals(allString))
            diseaseBarChartQuery += "% ";
        else
            diseaseBarChartQuery += Utils.getMonthInteger(monthString) + " ";

        if (yearString.equals(allString))
            diseaseBarChartQuery += "%";
        else
            diseaseBarChartQuery += yearString;

        return diseaseBarChartQuery;
    }

    String buildLocalityPieChartQuery(){
        String localityPieChartQuery = "";
        String localityString = localitySpinner.getSelectedItem().toString();
        String dayString = daySpinner.getSelectedItem().toString();
        String monthString = monthSpinner.getSelectedItem().toString();
        String yearString = yearSpinner.getSelectedItem().toString();
        String allString = getResources().getString(R.string.all);

        localityPieChartQuery += Constants.GET_ALL_DISEASE_FROM_LOCALITY_PERCENT + " ";
        localityPieChartQuery += localityString.replaceAll("\\s+","") + " ";

        if (dayString.equals(allString))
            localityPieChartQuery += "% ";
        else
            localityPieChartQuery += dayString + " ";

        if (monthString.equals(allString))
            localityPieChartQuery += "% ";
        else
            localityPieChartQuery += Utils.getMonthInteger(monthString) + " ";

        if (yearString.equals(allString))
            localityPieChartQuery += "%";
        else
            localityPieChartQuery += yearString;

        return localityPieChartQuery;
    }

    String buildDiseasePieChartQuery(){
        String diseasePieChartQuery = "";
        String diseaseString = diseaseSpinner.getSelectedItem().toString();
        String dayString = daySpinner.getSelectedItem().toString();
        String monthString = monthSpinner.getSelectedItem().toString();
        String yearString = yearSpinner.getSelectedItem().toString();
        String allString = getResources().getString(R.string.all);

        diseasePieChartQuery += Constants.GET_ALL_LOCALITY_FROM_DISEASE_PERCENT + " ";
        diseasePieChartQuery += diseaseString.replaceAll("\\s+","") + " ";

        if (dayString.equals(allString))
            diseasePieChartQuery += "% ";
        else
            diseasePieChartQuery += dayString + " ";

        if (monthString.equals(allString))
            diseasePieChartQuery += "% ";
        else
            diseasePieChartQuery += Utils.getMonthInteger(monthString) + " ";

        if (yearString.equals(allString))
            diseasePieChartQuery += "%";
        else
            diseasePieChartQuery += yearString;

        return diseasePieChartQuery;
    }

    void setViewVisibilities(){
        if (Constants.CHART_TYPE_LOCALITY.equals(chartFor)) {
            localityParent.setVisibility(View.VISIBLE);
            diseaseParent.setVisibility(View.GONE);
            if (chartTypeSpinner.getSelectedItem().toString().equals("Bar Chart")) {
                localityBarChart.setVisibility(View.VISIBLE);
                localityPieChart.setVisibility(View.GONE);
            }
            else {
                localityPieChart.setVisibility(View.VISIBLE);
                localityBarChart.setVisibility(View.GONE);
            }
        }
        else {
            diseaseParent.setVisibility(View.VISIBLE);
            localityParent.setVisibility(View.GONE);
            if (chartTypeSpinner.getSelectedItem().toString().equals("Bar Chart")) {
                diseaseBarChart.setVisibility(View.VISIBLE);
                diseasePieChart.setVisibility(View.GONE);
            }
            else {
                diseasePieChart.setVisibility(View.VISIBLE);
                diseaseBarChart.setVisibility(View.GONE);
            }
        }
    }
}
