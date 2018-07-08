package com.stats.disease.healthstats;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.github.mikephil.charting.data.LineDataSet;

public class WelcomeActivity extends Activity {

    Context context;

    AlertDialog changeServerAlertDialog;
    AlertDialog serverNotReachableDialog;

    LinearLayout welcomeProgressBarParent;
    ScrollView welcomeScrollviewParent;

    Button loginSignupButton;
    Button reportInstanceButton;
    Button previousCasesButton;
    Button reportAsGuestButton;
    Button viewStatsButton;
    Button logoutButton;
    Button changeServerButton;

    EditText changeServerURLEditText;

    View.OnClickListener loginSignupListener;
    View.OnClickListener reportInstanceListener;
    View.OnClickListener previousCasesListener;
    View.OnClickListener reportAsGuestListener;
    View.OnClickListener viewStatsListener;
    View.OnClickListener logoutListener;
    View.OnClickListener changeServerListener;

    private class ServerAvailabilityChecker extends ServerConnector{
        @Override
        protected void onPreExecute() {
            welcomeScrollviewParent.setVisibility(View.GONE);
            welcomeProgressBarParent.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            if (s!=null && s.equalsIgnoreCase(Constants.SERVER_INVALID_QUERY_STRING)){
                welcomeProgressBarParent.setVisibility(View.GONE);
                welcomeScrollviewParent.setVisibility(View.VISIBLE);
            }
            else {
                showServerNotReachableDialog();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        context = this;
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializeUI();
        initializeClickListeners();
        assignClickListeners();
        checkServerAvailability();
    }

    void initializeUI(){
        loginSignupButton = (Button)findViewById(R.id.login_signup_button);
        GradientDrawable bgcolor = (GradientDrawable) loginSignupButton.getBackground();
        bgcolor.setColor(Color.rgb(0, 118, 236));
        reportInstanceButton = (Button) findViewById(R.id.report_instance_button);
        bgcolor = (GradientDrawable) reportInstanceButton.getBackground();
        bgcolor.setColor(Color.rgb(54, 13, 9));
        previousCasesButton = (Button)findViewById(R.id.prev_case_id);
        bgcolor = (GradientDrawable) previousCasesButton.getBackground();
        bgcolor.setColor(Color.rgb(190, 190, 190));
        reportAsGuestButton = (Button)findViewById(R.id.report_as_guest_button);
        bgcolor = (GradientDrawable) reportAsGuestButton.getBackground();
        bgcolor.setColor(Color.rgb(255, 0, 0));
        viewStatsButton = (Button)findViewById(R.id.view_stats_button);
        bgcolor = (GradientDrawable) viewStatsButton.getBackground();
        bgcolor.setColor(Color.rgb(121, 121, 121));
        logoutButton = (Button)findViewById(R.id.logout_button);
        bgcolor = (GradientDrawable) logoutButton.getBackground();
        bgcolor.setColor(Color.rgb(68, 72, 169));
        changeServerButton = (Button)findViewById(R.id.change_server);
        bgcolor = (GradientDrawable) changeServerButton.getBackground();
        bgcolor.setColor(Color.rgb(200, 200, 110));

        welcomeProgressBarParent = (LinearLayout)findViewById(R.id.welcome_progressbar_parent);
        welcomeScrollviewParent = (ScrollView)findViewById(R.id.welcome_parent_scrollview);

        if (Utils.isUserLoggedIn(context)){
            loginSignupButton.setVisibility(View.GONE);
            reportAsGuestButton.setVisibility(View.GONE);
            reportInstanceButton.setVisibility(View.VISIBLE);
            previousCasesButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.VISIBLE);
        } else {
            reportInstanceButton.setVisibility(View.GONE);
            previousCasesButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.GONE);
            loginSignupButton.setVisibility(View.VISIBLE);
            reportAsGuestButton.setVisibility(View.VISIBLE);
        }
    }

    void initializeClickListeners(){
        loginSignupListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginSignupIntent = new Intent(context, LoginSignupActivity.class);
                startActivity(loginSignupIntent);
            }
        };

        reportInstanceListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reportInstanceIntent = new Intent(context, ReportInstanceActivity.class);
                startActivity(reportInstanceIntent);
            }
        };

        reportAsGuestListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reportAsGuestIntent = new Intent(context, ReportInstanceActivity.class);
                startActivity(reportAsGuestIntent);
            }
        };

        viewStatsListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewStatsIntent = new Intent(context, ViewStatsActivity.class);
                startActivity(viewStatsIntent);
            }
        };

        previousCasesListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent previousCasesIntent = new Intent(context, PreviousCasesActivity.class);
                startActivity(previousCasesIntent);
            }
        } ;

        logoutListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.logoutUser(context);
                Intent welcomeIntent = new Intent(context, WelcomeActivity.class);
                finish();
                startActivity(welcomeIntent);
            }
        };

        changeServerListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayServerChangeDialog();
            }
        };
    }

    void assignClickListeners(){
        loginSignupButton.setOnClickListener(loginSignupListener);
        reportInstanceButton.setOnClickListener(reportInstanceListener);
        previousCasesButton.setOnClickListener(previousCasesListener);
        reportAsGuestButton.setOnClickListener(reportAsGuestListener);
        viewStatsButton.setOnClickListener(viewStatsListener);
        logoutButton.setOnClickListener(logoutListener);
        changeServerButton.setOnClickListener(changeServerListener);
    }

    void checkServerAvailability(){
        ServerAvailabilityChecker serverAvailabilityChecker = new ServerAvailabilityChecker();
        serverAvailabilityChecker.execute(Constants.SERVER_URL_STRING," ");
    }

    void displayServerChangeDialog(){
        final AlertDialog.Builder changeServerDialog = new AlertDialog.Builder(context);
        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        changeServerURLEditText = new EditText(context);

        changeServerURLEditText.setLayoutParams(editTextParams);
        changeServerURLEditText.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        changeServerURLEditText.setText(Constants.SERVER_URL_STRING);

        changeServerDialog.setMessage(getResources().getString(R.string.insert_new_url_message));
        changeServerDialog.setView(changeServerURLEditText);

        changeServerDialog.setNegativeButton(getResources().getString(R.string.cancel_string), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeServerAlertDialog.dismiss();
            }
        });

        changeServerDialog.setPositiveButton(getResources().getString(R.string.ok_string), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Constants.SERVER_URL_STRING = changeServerURLEditText.getText().toString();
                changeServerAlertDialog.dismiss();
                checkServerAvailability();
            }
        });

        changeServerAlertDialog = changeServerDialog.create();
        changeServerAlertDialog.show();
    }

    void showServerNotReachableDialog(){
        AlertDialog.Builder serverNotReachableBuilder = new AlertDialog.Builder(context);
        serverNotReachableBuilder.setMessage(getResources().getString(R.string.server_not_reachable_message));
        serverNotReachableBuilder.setCancelable(false);
        serverNotReachableBuilder.setPositiveButton(getResources().getString(R.string.ok_string), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        serverNotReachableDialog = serverNotReachableBuilder.create();
        serverNotReachableDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                serverNotReachableDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(false);
            }
        });
        serverNotReachableDialog.show();
    }
}
