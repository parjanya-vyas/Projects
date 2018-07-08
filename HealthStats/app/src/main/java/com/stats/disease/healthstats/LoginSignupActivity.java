package com.stats.disease.healthstats;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class LoginSignupActivity extends Activity {

    LinearLayout ageGroupParent;
    LinearLayout genderParent;
    LinearLayout localityParent;
    LinearLayout userIdParent;
    LinearLayout checkAvailabilityParent;
    LinearLayout passwordParent;
    LinearLayout confirmPasswordParent;
    LinearLayout loginButtonsParent;

    Button checkAvailabilityButton;
    Button mainLoginButton;
    Button mainSignupButton;
    Button fakeSignupButton;

    EditText userIdInput;
    EditText passwordInput;
    EditText confirmPasswordInput;

    ProgressBar checkAvailabilityBar;
    ProgressBar mainProgressBar;

    RadioButton maleRadio;
    RadioButton femaleRadio;
    RadioButton otherRadio;

    Spinner ageGroupSpinner;
    Spinner localitySpinner;

    TextView checkAvailabilityOutput;

    View.OnClickListener checkAvailabilityListener;
    View.OnClickListener loginListener;
    View.OnClickListener fakeSignupListener;
    View.OnClickListener actualSignupListener;

    String[] age_group_array;
    String[] locality_array;

    String userId;
    String password;
    String ageGroup;
    String locality;
    String gender;

    Context context;

    private class AvailabilityChecker extends ServerConnector{
        @Override
        protected void onPreExecute() {
            checkAvailabilityBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equalsIgnoreCase("true"))
                checkAvailabilityOutput.setText(context.getResources().getString(R.string.userid_available));
            else
                checkAvailabilityOutput.setText(context.getResources().getString(R.string.userid_not_available));
            checkAvailabilityBar.setVisibility(View.GONE);
        }
    }

    private class LoginInitializer extends ServerConnector{
        @Override
        protected void onPreExecute() {
            mainProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            if (!s.equalsIgnoreCase("false")){
                String serverReply[] = s.split(" ");
                Utils.loginUser(context, serverReply[0], serverReply[1], serverReply[2], serverReply[3]);
                Intent welcomeAgainIntent = new Intent(context, WelcomeActivity.class);
                startActivity(welcomeAgainIntent);
                finish();
            }
            else {
                Toast.makeText(context,"Login Failed",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class UserLogin extends ServerConnector{
        @Override
        protected void onPreExecute() {
            mainProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equalsIgnoreCase("true")){
                LoginInitializer loginInitializer = new LoginInitializer();
                loginInitializer.execute(Constants.SERVER_URL_STRING, Constants.GET_USER_FROM_ID_QUERY + " " +userIdInput.getText().toString());
            } else {
                Toast.makeText(context,"Login Failed",Toast.LENGTH_SHORT).show();
            }
            mainProgressBar.setVisibility(View.GONE);
        }
    }

    private class UserSignup extends ServerConnector{
        @Override
        protected void onPreExecute() {
            mainProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equalsIgnoreCase("true")){
                Utils.loginUser(context, userId, ageGroup, gender, locality);
                Intent welcomeAgainIntent = new Intent(context, WelcomeActivity.class);
                startActivity(welcomeAgainIntent);
                finish();
            } else {
                Toast.makeText(context,"Signing Up Failed",Toast.LENGTH_SHORT).show();
            }
            mainProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup);

        context = this;

        initializeLayouts();
        initializeSpinners();
        initializeClickListeners();
    }

    void initializeLayouts(){
        ageGroupParent = (LinearLayout)findViewById(R.id.age_group_parent);
        genderParent = (LinearLayout)findViewById(R.id.gender_parent);
        localityParent = (LinearLayout)findViewById(R.id.locality_parent);
        userIdParent = (LinearLayout)findViewById(R.id.user_id_parent);
        checkAvailabilityParent = (LinearLayout)findViewById(R.id.check_availability_parent);
        passwordParent = (LinearLayout)findViewById(R.id.password_parent);
        confirmPasswordParent = (LinearLayout)findViewById(R.id.confirm_pass_parent);
        loginButtonsParent = (LinearLayout)findViewById(R.id.login_buttons_parent);

        checkAvailabilityButton = (Button)findViewById(R.id.check_availability_button);
        GradientDrawable bgColor = (GradientDrawable) checkAvailabilityButton.getBackground();
        bgColor.setColor(Color.BLACK);
        mainLoginButton = (Button)findViewById(R.id.login_main_button);
        bgColor = (GradientDrawable) mainLoginButton.getBackground();
        bgColor.setColor(Color.BLACK);
        mainSignupButton = (Button)findViewById(R.id.main_signup_button);
        bgColor = (GradientDrawable) mainSignupButton.getBackground();
        bgColor.setColor(Color.BLACK);
        fakeSignupButton = (Button)findViewById(R.id.signup_fake_button);
        bgColor = (GradientDrawable) fakeSignupButton.getBackground();
        bgColor.setColor(Color.BLACK);

        userIdInput = (EditText)findViewById(R.id.user_id_input);
        passwordInput = (EditText)findViewById(R.id.password_input);
        confirmPasswordInput = (EditText)findViewById(R.id.confirm_password_input);

        checkAvailabilityBar = (ProgressBar)findViewById(R.id.check_availability_progress_bar);
        mainProgressBar = (ProgressBar)findViewById(R.id.main_progress_bar);

        maleRadio = (RadioButton)findViewById(R.id.gender_male_radio);
        femaleRadio = (RadioButton)findViewById(R.id.gender_female_radio);
        otherRadio = (RadioButton)findViewById(R.id.gender_other_radio);

        ageGroupSpinner = (Spinner)findViewById(R.id.age_group_spinner);
        localitySpinner = (Spinner)findViewById(R.id.locality_spinner);

        checkAvailabilityOutput = (TextView)findViewById(R.id.check_availability_output);
    }

    void initializeSpinners(){
        age_group_array = context.getResources().getStringArray(R.array.age_group_array);
        locality_array = context.getResources().getStringArray(R.array.locality_array);

        ArrayAdapter<String> ageGroupAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, age_group_array);
        ArrayAdapter<String> localityAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, locality_array);

        ageGroupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        localityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ageGroupSpinner.setAdapter(ageGroupAdapter);
        localitySpinner.setAdapter(localityAdapter);
    }

    void initializeClickListeners(){

        checkAvailabilityListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validate(userIdInput.getText().toString()))
                    return;
                AvailabilityChecker checkAvailabilityAsyncTask = new AvailabilityChecker();
                String checkAvailabilityQueryString = Constants.GET_USERS_QUERY + " " + userIdInput.getText().toString();
                checkAvailabilityAsyncTask.execute(Constants.SERVER_URL_STRING, checkAvailabilityQueryString);
            }
        };

        loginListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userId = userIdInput.getText().toString();
                password = passwordInput.getText().toString();
                if(!validate(userId, password))
                    return;
                UserLogin loginAsyncTask = new UserLogin();
                String loginAuthQuery = Constants.LOGIN_AUTHENTICATE_QUERY + " " + userId + " " + password;
                loginAsyncTask.execute(Constants.SERVER_URL_STRING, loginAuthQuery);
            }
        };

        fakeSignupListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ageGroupParent.setVisibility(View.VISIBLE);
                genderParent.setVisibility(View.VISIBLE);
                localityParent.setVisibility(View.VISIBLE);
                checkAvailabilityParent.setVisibility(View.VISIBLE);
                confirmPasswordParent.setVisibility(View.VISIBLE);
                mainSignupButton.setVisibility(View.VISIBLE);
                loginButtonsParent.setVisibility(View.GONE);
            }
        };

        actualSignupListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userId = userIdInput.getText().toString();
                password = passwordInput.getText().toString();
                String confirmPassword = confirmPasswordInput.getText().toString();
                ageGroup = ageGroupSpinner.getSelectedItem().toString();
                locality = localitySpinner.getSelectedItem().toString().replaceAll("\\s+","");
                gender = getGender();
                if (!validate(userId, password, confirmPassword, ageGroup, gender, locality))
                    return;
                UserSignup signupAsyncTask = new UserSignup();
                String signupQuery = Constants.SIGNUP_QUERY + " " +
                        userId + " " +
                        password + " " +
                        ageGroup + " " +
                        gender + " " +
                        locality;
                signupAsyncTask.execute(Constants.SERVER_URL_STRING, signupQuery);
            }
        };

        checkAvailabilityButton.setOnClickListener(checkAvailabilityListener);
        mainLoginButton.setOnClickListener(loginListener);
        fakeSignupButton.setOnClickListener(fakeSignupListener);
        mainSignupButton.setOnClickListener(actualSignupListener);
    }

    boolean validate(String userId){
        if (userId.isEmpty()){
            Toast.makeText(context,"Please enter UserID",Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    boolean validate(String userId, String password){
        if (userId.isEmpty()){
            Toast.makeText(context,"Please enter UserID",Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.isEmpty()){
            Toast.makeText(context,"Please enter Password",Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    boolean validate(String userId, String password, String confirmPassword, String ageGroup, String gender, String locality){
        if (userId.isEmpty()){
            Toast.makeText(context,"Please enter UserID",Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.isEmpty()){
            Toast.makeText(context,"Please enter Password",Toast.LENGTH_SHORT).show();
            return false;
        } else if (confirmPassword.isEmpty()){
            Toast.makeText(context,"Please enter Confirm Password",Toast.LENGTH_SHORT).show();
            return false;
        } else if (ageGroup.isEmpty()){
            Toast.makeText(context,"Please enter Age Group",Toast.LENGTH_SHORT).show();
            return false;
        } else if (gender.isEmpty()){
            Toast.makeText(context,"Please enter Gender",Toast.LENGTH_SHORT).show();
            return false;
        } else if (locality.isEmpty()){
            Toast.makeText(context,"Please enter Locality",Toast.LENGTH_SHORT).show();
            return false;
        } else if (!password.equals(confirmPassword)) {
            Toast.makeText(context,"Passwords do not match",Toast.LENGTH_SHORT).show();
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
}
