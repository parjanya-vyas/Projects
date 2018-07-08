package com.stats.disease.healthstats;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PreviousCasesActivity extends Activity {

    Context context;

    ListView previousCasesList;

    LinearLayout listProgressBar;
    LinearLayout caseParent;

    TextView caseIdText;
    TextView caseAgeGroupText;
    TextView caseGenderText;
    TextView caseLocalityText;
    TextView caseDateText;
    TextView caseDiseaseText;

    AdapterView.OnItemClickListener listViewItemClickListener;

    private class UserCaseGetter extends ServerConnector{
        @Override
        protected void onPreExecute() {
            listProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {

            if (s.equalsIgnoreCase("false")){
                Toast.makeText(context,"No cases reported yet",Toast.LENGTH_SHORT).show();
                listProgressBar.setVisibility(View.GONE);
                previousCasesList.setVisibility(View.GONE);
                return;
            }

            String[] caseIds = s.split(" ");

            for (int i=0;i<caseIds.length;i++)
                caseIds[i] = "Case ID: " + caseIds[i];

            ArrayAdapter listViewAdapter = new ArrayAdapter<>(context, R.layout.list_previous_cases, caseIds);
            previousCasesList.setAdapter(listViewAdapter);

            listProgressBar.setVisibility(View.GONE);
            previousCasesList.setVisibility(View.VISIBLE);
        }
    }

    private class CaseGetter extends ServerConnector{
        @Override
        protected void onPreExecute() {
            listProgressBar.setVisibility(View.VISIBLE);
            previousCasesList.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(String s) {

            String[] caseParams = s.split(" ");

            caseIdText.setText(caseParams[0]);
            caseAgeGroupText.setText(caseParams[1]);
            caseGenderText.setText(caseParams[2]);
            caseLocalityText.setText(caseParams[3]);
            caseDateText.setText(caseParams[4]);
            caseDiseaseText.setText(caseParams[5]);

            caseParent.setVisibility(View.VISIBLE);
            listProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_cases);

        context = this;

        initializeLayout();
        initializeListView();
        populateListView();
    }

    void initializeLayout(){

        previousCasesList = (ListView)findViewById(R.id.previous_cases_list);

        listProgressBar = (LinearLayout)findViewById(R.id.list_progress_bar_parent);
        caseParent = (LinearLayout)findViewById(R.id.single_case_parent);

        caseIdText = (TextView)findViewById(R.id.single_case_id);
        caseAgeGroupText = (TextView)findViewById(R.id.single_case_age_group);
        caseGenderText = (TextView)findViewById(R.id.single_case_gender);
        caseLocalityText = (TextView)findViewById(R.id.single_case_locality);
        caseDateText = (TextView)findViewById(R.id.single_case_date);
        caseDiseaseText = (TextView)findViewById(R.id.single_case_disease);
    }

    void initializeListView(){

        listViewItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView clickedItem = (TextView)view.findViewById(R.id.case_id_list);
                CaseGetter currentCaseGetter = new CaseGetter();
                String queryString = Constants.GET_CASE_QUERY + " " + clickedItem.getText().toString().substring(9);
                currentCaseGetter.execute(Constants.SERVER_URL_STRING, queryString);
            }
        };

        previousCasesList.setOnItemClickListener(listViewItemClickListener);
    }

    void populateListView(){
        UserCaseGetter allCurrentUserCases = new UserCaseGetter();
        String queryString = Constants.GET_ALL_CASES_FROM_USER_QUERY + " " + Utils.getUserId(context);
        allCurrentUserCases.execute(Constants.SERVER_URL_STRING, queryString);
    }

    @Override
    public void onBackPressed() {
        if (previousCasesList.getVisibility()==View.VISIBLE)
            super.onBackPressed();
        else {
            caseParent.setVisibility(View.GONE);
            previousCasesList.setVisibility(View.VISIBLE);
        }
    }
}
