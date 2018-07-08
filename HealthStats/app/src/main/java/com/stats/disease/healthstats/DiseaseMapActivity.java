package com.stats.disease.healthstats;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.maps.GeoPoint;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;

public class DiseaseMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private Context context;

    Spinner diseaseSpinner;
    Spinner monthSpinner;
    Spinner daySpinner;
    Spinner yearSpinner;

    AdapterView.OnItemSelectedListener diseaseItemSelectedListener;
    AdapterView.OnItemSelectedListener monthItemSelectedListener;
    AdapterView.OnItemSelectedListener dayItemSelectedListener;
    AdapterView.OnItemSelectedListener yearItemSelectedListener;

    String[] dateStrings28;
    String[] dateStrings29;
    String[] dateStrings30;
    String[] dateStrings31;
    String[] yearStrings;

    ArrayList<String> months30;
    ArrayList<String> months31;

    ArrayAdapter<String> diseaseAdapter;
    ArrayAdapter<String> monthAdapter;
    ArrayAdapter<String> dayAdapter;
    ArrayAdapter<String> yearAdapter;

    private GoogleMap mMap;
    private TileOverlay mOverlay;

    private ArrayList<LatLng> diseaseLatLngList = new ArrayList<>();

    private LatLng hostel1;
    private LatLng hostel2;
    private LatLng hostel3;
    private LatLng hostel4;
    private LatLng hostel5;
    private LatLng hostel6;
    private LatLng hostel7;
    private LatLng hostel8;
    private LatLng hostel9;
    private LatLng hostel10;
    private LatLng hostel11;
    private LatLng hostel12;
    private LatLng hostel13;
    private LatLng hostel14;
    private LatLng hostel15;
    private LatLng hostel16;
    
    Boolean isCreationCompleted = false;

    private class DiseaseMapDrawer extends ServerConnector{

        @Override
        protected void onPostExecute(String s) {

            if (s.equalsIgnoreCase("false")){
                Toast.makeText(context,"No cases reported yet",Toast.LENGTH_SHORT).show();
                clearMap();
                return;
            }

            String[] serverReply = s.split(" ");
            for (int i=0; i<serverReply.length; i+=2) {
                if (serverReply[i].equalsIgnoreCase("Hostel1")){
                    for (int j=0;j<Integer.parseInt(serverReply[i+1]);j++)
                        diseaseLatLngList.add(hostel1);
                } else if (serverReply[i].equalsIgnoreCase("Hostel2")){
                    for (int j=0;j<Integer.parseInt(serverReply[i+1]);j++)
                        diseaseLatLngList.add(hostel2);
                } else if (serverReply[i].equalsIgnoreCase("Hostel3")){
                    for (int j=0;j<Integer.parseInt(serverReply[i+1]);j++)
                        diseaseLatLngList.add(hostel3);
                } else if (serverReply[i].equalsIgnoreCase("Hostel4")){
                    for (int j=0;j<Integer.parseInt(serverReply[i+1]);j++)
                        diseaseLatLngList.add(hostel4);
                } else if (serverReply[i].equalsIgnoreCase("Hostel5")){
                    for (int j=0;j<Integer.parseInt(serverReply[i+1]);j++)
                        diseaseLatLngList.add(hostel5);
                } else if (serverReply[i].equalsIgnoreCase("Hostel6")){
                    for (int j=0;j<Integer.parseInt(serverReply[i+1]);j++)
                        diseaseLatLngList.add(hostel6);
                } else if (serverReply[i].equalsIgnoreCase("Hostel7")){
                    for (int j=0;j<Integer.parseInt(serverReply[i+1]);j++)
                        diseaseLatLngList.add(hostel7);
                } else if (serverReply[i].equalsIgnoreCase("Hostel8")){
                    for (int j=0;j<Integer.parseInt(serverReply[i+1]);j++)
                        diseaseLatLngList.add(hostel8);
                } else if (serverReply[i].equalsIgnoreCase("Hostel9")){
                    for (int j=0;j<Integer.parseInt(serverReply[i+1]);j++)
                        diseaseLatLngList.add(hostel9);
                } else if (serverReply[i].equalsIgnoreCase("Hostel10")){
                    for (int j=0;j<Integer.parseInt(serverReply[i+1]);j++)
                        diseaseLatLngList.add(hostel10);
                } else if (serverReply[i].equalsIgnoreCase("Hostel11")){
                    for (int j=0;j<Integer.parseInt(serverReply[i+1]);j++)
                        diseaseLatLngList.add(hostel11);
                } else if (serverReply[i].equalsIgnoreCase("Hostel12")){
                    for (int j=0;j<Integer.parseInt(serverReply[i+1]);j++)
                        diseaseLatLngList.add(hostel12);
                } else if (serverReply[i].equalsIgnoreCase("Hostel13")){
                    for (int j=0;j<Integer.parseInt(serverReply[i+1]);j++)
                        diseaseLatLngList.add(hostel13);
                } else if (serverReply[i].equalsIgnoreCase("Hostel14")){
                    for (int j=0;j<Integer.parseInt(serverReply[i+1]);j++)
                        diseaseLatLngList.add(hostel14);
                } else if (serverReply[i].equalsIgnoreCase("Hostel15")){
                    for (int j=0;j<Integer.parseInt(serverReply[i+1]);j++)
                        diseaseLatLngList.add(hostel15);
                } else if (serverReply[i].equalsIgnoreCase("Hostel16")){
                    for (int j=0;j<Integer.parseInt(serverReply[i+1]);j++)
                        diseaseLatLngList.add(hostel16);
                }
            }

            HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                    .data(diseaseLatLngList)
                    .build();
            TileOverlayOptions mOptions = new TileOverlayOptions().tileProvider(mProvider);
            if (mOverlay!=null) {
                clearMap();
                mOverlay = mMap.addTileOverlay(mOptions);
                mOverlay.clearTileCache();
            } else {
                mOverlay = mMap.addTileOverlay(mOptions);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_disease_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        initializeHostelLatLng();
        initializeLayouts();
        initializeDateStringArrays();
        initializeSpinners();
        initializeItemSelectedListeners();
        drawMap();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        GeoPoint iitbLocation = getLocationFromAddress(Constants.IITB_ADDRESS_STRING);
        LatLng iitbLatLng = new LatLng(convertLatLangToDouble(iitbLocation.getLatitudeE6()),
                convertLatLangToDouble(iitbLocation.getLongitudeE6()));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(iitbLatLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(iitbLatLng, 14.0f));
    }

    private void initializeHostelLatLng(){
        hostel1 = new LatLng(Constants.hostel1Lat, Constants.hostel1Lang);
        hostel2 = new LatLng(Constants.hostel2Lat, Constants.hostel2Lang);
        hostel3 = new LatLng(Constants.hostel3Lat, Constants.hostel3Lang);
        hostel4 = new LatLng(Constants.hostel4Lat, Constants.hostel4Lang);
        hostel5 = new LatLng(Constants.hostel5Lat, Constants.hostel5Lang);
        hostel6 = new LatLng(Constants.hostel6Lat, Constants.hostel6Lang);
        hostel7 = new LatLng(Constants.hostel7Lat, Constants.hostel7Lang);
        hostel8 = new LatLng(Constants.hostel8Lat, Constants.hostel8Lang);
        hostel9 = new LatLng(Constants.hostel9Lat, Constants.hostel9Lang);
        hostel10 = new LatLng(Constants.hostel10Lat, Constants.hostel10Lang);
        hostel11 = new LatLng(Constants.hostel11Lat, Constants.hostel11Lang);
        hostel12 = new LatLng(Constants.hostel12Lat, Constants.hostel12Lang);
        hostel13 = new LatLng(Constants.hostel13Lat, Constants.hostel13Lang);
        hostel14 = new LatLng(Constants.hostel14Lat, Constants.hostel14Lang);
        hostel15 = new LatLng(Constants.hostel15Lat, Constants.hostel15Lang);
        hostel16 = new LatLng(Constants.hostel16Lat, Constants.hostel16Lang);
    }

    void initializeLayouts(){
        diseaseSpinner = (Spinner)findViewById(R.id.disease_map_spinner);
        monthSpinner = (Spinner)findViewById(R.id.month_map_spinner);
        daySpinner = (Spinner)findViewById(R.id.day_map_spinner);
        yearSpinner = (Spinner)findViewById(R.id.year_map_spinner);
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
        String[] diseaseStrings = context.getResources().getStringArray(R.array.disease_array);
        String[] monthStrings = new DateFormatSymbols().getMonths();

        String[] spinnerMonths = new String[13];
        spinnerMonths[0] = context.getResources().getString(R.string.all);
        System.arraycopy(monthStrings, 0, spinnerMonths, 1, monthStrings.length);
        
        diseaseAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, diseaseStrings);
        monthAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, spinnerMonths);
        dayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, dateStrings31);
        yearAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, yearStrings);
        
        diseaseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        diseaseSpinner.setAdapter(diseaseAdapter);
        monthSpinner.setAdapter(monthAdapter);
        daySpinner.setAdapter(dayAdapter);
        yearSpinner.setAdapter(yearAdapter);
    }

    void initializeItemSelectedListeners(){

        diseaseItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isCreationCompleted)
                    drawMap();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        dayItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isCreationCompleted)
                    drawMap();
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

                    drawMap();
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

                    drawMap();
                } else {
                    isCreationCompleted = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        diseaseSpinner.setOnItemSelectedListener(diseaseItemSelectedListener);
        daySpinner.setOnItemSelectedListener(dayItemSelectedListener);
        monthSpinner.setOnItemSelectedListener(monthItemSelectedListener);
        yearSpinner.setOnItemSelectedListener(yearItemSelectedListener);
    }

    void drawMap(){
        DiseaseMapDrawer diseaseMapDrawer = new DiseaseMapDrawer();
        diseaseMapDrawer.execute(Constants.SERVER_URL_STRING, buildDiseaseMapQuery());
    }

    void clearMap(){
        if (mOverlay!=null){
            mOverlay.remove();
        }
    }

    private double convertLatLangToDouble(int latLang){
        return latLang/1E6;
    }

    private GeoPoint getLocationFromAddress(String strAddress){

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        GeoPoint p1 = null;

        try {
            address = coder.getFromLocationName(strAddress,5);
            if (address==null) {
                return null;
            }
            Address location=address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new GeoPoint((int) (location.getLatitude() * 1E6),
                    (int) (location.getLongitude() * 1E6));

            return p1;
        } catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }

    String buildDiseaseMapQuery(){
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
}
