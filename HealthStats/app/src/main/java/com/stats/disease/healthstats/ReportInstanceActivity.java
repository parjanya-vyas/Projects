package com.stats.disease.healthstats;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ReportInstanceActivity extends Activity {

    Context context;

    Button reportCaseButton;

    EditText caseDate;

    ProgressBar reportCaseBar;

    RadioButton maleRadio;
    RadioButton femaleRadio;
    RadioButton otherRadio;

    Spinner ageGroupSpinner;
    Spinner localitySpinner;
    Spinner diseaseSpinner;

    String age_group_array[];
    String locality_array[];
    String disease_array[];

    String currentUserId;
    String ageGroupInput;
    String genderInput;
    String localityInput;
    String dateInput;
    String diseaseInput;

    View.OnClickListener reportCaseListener;
    View.OnClickListener dateListener;

    AlertDialog similarCaseAlertDialog;

    private class CaseReporter extends ServerConnector{

        @Override
        protected void onPreExecute() {
            reportCaseBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            reportCaseBar.setVisibility(View.GONE);
            if (s.equals("true")) {
                Toast.makeText(context, "Case Reported Successfully", Toast.LENGTH_SHORT).show();
                Utils.setLastReportedDisease(context, diseaseInput);
                Intent welcomeAgainIntent = new Intent(context, WelcomeActivity.class);
                startActivity(welcomeAgainIntent);
                finish();
            }
            else
                Toast.makeText(context, "Reporting Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private class SimilarChecker extends ServerConnector{
        @Override
        protected void onPreExecute() {
            reportCaseBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            reportCaseBar.setVisibility(View.GONE);
            if (s.equals("true"))
                showSimilarCaseDialog();
            else
                reportCase();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_instance);

        context = this;

        initializeLayouts();
        initializeSpinners();
        initializeClickListeners();
        if (Utils.isUserLoggedIn(context))
            setDefaultValues();
    }

    void initializeLayouts(){

        reportCaseButton = (Button)findViewById(R.id.report_case);
        GradientDrawable bgColor = (GradientDrawable) reportCaseButton.getBackground();
        bgColor.setColor(Color.BLACK);

        caseDate = (EditText) findViewById(R.id.report_instance_date);

        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        caseDate.setText(currentDateFormat.format(new Date()));

        reportCaseBar = (ProgressBar)findViewById(R.id.report_case_progress_bar);

        maleRadio = (RadioButton)findViewById(R.id.report_instance_gender_male_radio);
        femaleRadio = (RadioButton)findViewById(R.id.report_instance_gender_female_radio);
        otherRadio = (RadioButton)findViewById(R.id.report_instance_gender_other_radio);

        ageGroupSpinner = (Spinner)findViewById(R.id.report_instance_age_group_spinner);
        localitySpinner = (Spinner)findViewById(R.id.report_instance_locality_spinner);
        diseaseSpinner = (Spinner)findViewById(R.id.report_instance_disease);
    }

    void initializeSpinners(){
        age_group_array = context.getResources().getStringArray(R.array.age_group_array);
        locality_array = context.getResources().getStringArray(R.array.locality_array);
        disease_array = context.getResources().getStringArray(R.array.disease_array);

        ArrayAdapter<String> ageGroupAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, age_group_array);
        ArrayAdapter<String> localityAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, locality_array);
        ArrayAdapter<String> diseaseAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, disease_array);

        ageGroupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        localityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        diseaseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ageGroupSpinner.setAdapter(ageGroupAdapter);
        localitySpinner.setAdapter(localityAdapter);
        diseaseSpinner.setAdapter(diseaseAdapter);
    }

    void initializeClickListeners(){

        final Calendar currentDate = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                currentDate.set(Calendar.YEAR, year);
                currentDate.set(Calendar.MONTH, month);
                currentDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                caseDate.setText(currentDateFormat.format(currentDate.getTime()));
            }
        };

        dateListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(context,
                        dateSetListener,
                        currentDate.get(Calendar.YEAR),
                        currentDate.get(Calendar.MONTH),
                        currentDate.get(Calendar.DAY_OF_MONTH)).show();
            }
        };

        reportCaseListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeStringValues();
                if (!validate())
                    return;

                SimilarChecker currentCaseSimilarChecker = new SimilarChecker();
                String similarQueryString = buildSimilarQueryString();
                currentCaseSimilarChecker.execute(Constants.SERVER_URL_STRING, similarQueryString);
            }
        };

        caseDate.setOnClickListener(dateListener);
        reportCaseButton.setOnClickListener(reportCaseListener);
    }

    void initializeStringValues(){
        if (Utils.isUserLoggedIn(context))
            currentUserId = Utils.getUserId(context);
        else
            currentUserId = "null";
        ageGroupInput = ageGroupSpinner.getSelectedItem().toString();
        genderInput = getGender();
        localityInput = localitySpinner.getSelectedItem().toString();
        dateInput = caseDate.getText().toString();
        diseaseInput = diseaseSpinner.getSelectedItem().toString();
    }

    void setDefaultValues(){
        ageGroupSpinner.setSelection(Arrays.asList(age_group_array).indexOf(Utils.getAgeGroup(context)));
        maleRadio.setChecked(Utils.getGender(context).equalsIgnoreCase("Male"));
        femaleRadio.setChecked(Utils.getGender(context).equalsIgnoreCase("Female"));
        otherRadio.setChecked(Utils.getGender(context).equalsIgnoreCase("Other"));
        localitySpinner.setSelection(Arrays.asList(locality_array).indexOf(Utils.getLocality(context)));

        if (Utils.getLastReportedDisease(context)!=null){
            diseaseSpinner.setSelection(Arrays.asList(disease_array).indexOf(Utils.getLastReportedDisease(context)));
        }
    }

    boolean validate(){
        if (ageGroupInput.isEmpty()){
            Toast.makeText(context,"Please enter Age Group",Toast.LENGTH_SHORT).show();
            return false;
        } else if (genderInput.isEmpty()){
            Toast.makeText(context,"Please enter Gender",Toast.LENGTH_SHORT).show();
            return false;
        } else if (localityInput.isEmpty()){
            Toast.makeText(context,"Please enter Locality",Toast.LENGTH_SHORT).show();
            return false;
        } else if (dateInput.isEmpty()){
            Toast.makeText(context,"Please enter Date",Toast.LENGTH_SHORT).show();
            return false;
        } else if (diseaseInput.isEmpty()){
            Toast.makeText(context,"Please enter Disease",Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    String getGender(){
        if (maleRadio.isChecked())
            return maleRadio.getText().toString();
        else if (femaleRadio.isChecked())
            return femaleRadio.getText().toString();
        else if (otherRadio.isChecked())
            return otherRadio.getText().toString();

        return "";
    }

    String buildQueryString(){
        return Constants.REPORT_CASE_QUERY + " " +
                currentUserId + " " +
                ageGroupInput.replaceAll("\\s+","") + " " +
                genderInput + " " +
                localityInput.replaceAll("\\s+","") + " " +
                dateInput + " " +
                diseaseInput.replaceAll("\\s+","");
    }

    String buildSimilarQueryString(){
        return Constants.SIMILAR_CHECK_QUERY + " " +
                ageGroupInput.replaceAll("\\s+","") + " " +
                genderInput + " " +
                localityInput.replaceAll("\\s+","") + " " +
                dateInput + " " +
                diseaseInput.replaceAll("\\s+","");
    }

    void showSimilarCaseDialog(){
        AlertDialog.Builder similarCaseDialog = new AlertDialog.Builder(context);
        similarCaseDialog.setMessage(getResources().getString(R.string.similar_case_dialog));
        similarCaseDialog.setCancelable(false);

        similarCaseDialog.setPositiveButton(getResources().getString(R.string.ok_string), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                similarCaseAlertDialog.dismiss();
                reportCase();
            }
        });

        similarCaseDialog.setNegativeButton(getResources().getString(R.string.cancel_string), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                similarCaseAlertDialog.dismiss();
            }
        });

        similarCaseAlertDialog = similarCaseDialog.create();
        similarCaseAlertDialog.show();
    }

    void reportCase(){
        String caseQueryString = buildQueryString();
        CaseReporter currentCaseReport = new CaseReporter();
        currentCaseReport.execute(Constants.SERVER_URL_STRING, caseQueryString);
    }
}