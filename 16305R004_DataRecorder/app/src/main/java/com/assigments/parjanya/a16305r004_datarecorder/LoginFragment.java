package com.assigments.parjanya.a16305r004_datarecorder;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private SubmitButtonClickedListener submitButtonClickedListener;
    FragmentActivity parentActivity;
    
    EditText firstNameEditText;
    EditText lastNameEditText;
    EditText mobileNumberEditText;
    EditText emailEditText;
    EditText ageEditText;
    RadioButton maleGenderRadioButton;
    RadioButton femaleGenderRadioButton;
    RadioButton otherGenderRadioButton;
    Button submitButton;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    boolean validate() {
        if (firstNameEditText.getText().toString().isEmpty()) {
            Toast.makeText(parentActivity, "Empty First Name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (lastNameEditText.getText().toString().isEmpty()) {
            Toast.makeText(parentActivity, "Empty Last Name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mobileNumberEditText.getText().toString().isEmpty()
                || mobileNumberEditText.getText().toString().length() < 10) {
            Toast.makeText(parentActivity, "Invalid mobile number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (emailEditText.getText().toString().isEmpty()
                || !emailEditText.getText().toString().contains("@")
                || !emailEditText.getText().toString().contains(".")) {
            Toast.makeText(parentActivity, "Invalid email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (ageEditText.getText().toString().isEmpty()
                || Integer.parseInt(ageEditText.getText().toString()) > 150) {
            Toast.makeText(parentActivity, "Invalid age", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    int getGenderFromRadioButton() {
        if (maleGenderRadioButton.isChecked())
            return Constants.MALE_GENDER_VALUE_SHARED_PREFERENCE;
        else if (femaleGenderRadioButton.isChecked())
            return Constants.FEMALE_GENDER_VALUE_SHARED_PREFERENCE;
        else if (otherGenderRadioButton.isChecked())
            return Constants.OTHER_GENDER_VALUE_SHARED_PREFERENCE;

        return -1;
    }

    void saveUserValuesInSharedPreference() {
        editor = sharedPreferences.edit();
        editor.putString(Constants.FIRST_NAME_SHARED_PREFERENCE_KEY, firstNameEditText.getText().toString());
        editor.putString(Constants.LAST_NAME_SHARED_PREFERENCE_KEY, lastNameEditText.getText().toString());
        editor.putString(Constants.MOBILE_NUMBER_SHARED_PREFERENCE_KEY, mobileNumberEditText.getText().toString());
        editor.putString(Constants.EMAIL_SHARED_PREFERENCE_KEY, emailEditText.getText().toString());
        editor.putString(Constants.AGE_SHARED_PREFERENCE_KEY, ageEditText.getText().toString());
        editor.putInt(Constants.GENDER_SHARED_PREFERENCE_KEY, getGenderFromRadioButton());
        editor.putBoolean(Constants.USER_DATA_PRESENT_SHARED_PREFERENCE, true);
        editor.apply();
    }

    void setUserValuesIfAvailable() {
        firstNameEditText.setText(sharedPreferences.getString(Constants.FIRST_NAME_SHARED_PREFERENCE_KEY,""));
        lastNameEditText.setText(sharedPreferences.getString(Constants.LAST_NAME_SHARED_PREFERENCE_KEY,""));
        mobileNumberEditText.setText(sharedPreferences.getString(Constants.MOBILE_NUMBER_SHARED_PREFERENCE_KEY,""));
        emailEditText.setText(sharedPreferences.getString(Constants.EMAIL_SHARED_PREFERENCE_KEY,""));
        ageEditText.setText(sharedPreferences.getString(Constants.AGE_SHARED_PREFERENCE_KEY,""));
        switch (sharedPreferences.getInt(Constants.GENDER_SHARED_PREFERENCE_KEY,-1)) {
            case Constants.MALE_GENDER_VALUE_SHARED_PREFERENCE:
                maleGenderRadioButton.setChecked(true);
                break;
            case Constants.FEMALE_GENDER_VALUE_SHARED_PREFERENCE:
                femaleGenderRadioButton.setChecked(true);
                break;
            case Constants.OTHER_GENDER_VALUE_SHARED_PREFERENCE:
                otherGenderRadioButton.setChecked(true);
                break;
            default:
                maleGenderRadioButton.setChecked(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentActivity = getActivity();

        View fragment = inflater.inflate(R.layout.fragment_login, container, false);
        firstNameEditText = fragment.findViewById(R.id.first_name_edit_text);
        lastNameEditText = fragment.findViewById(R.id.last_name_edit_text);
        mobileNumberEditText = fragment.findViewById(R.id.mobile_number_edit_text);
        emailEditText = fragment.findViewById(R.id.email_edit_text);
        ageEditText = fragment.findViewById(R.id.age_edit_text);
        maleGenderRadioButton = fragment.findViewById(R.id.male_radio_button);
        femaleGenderRadioButton = fragment.findViewById(R.id.female_radio_button);
        otherGenderRadioButton = fragment.findViewById(R.id.other_radio_button);
        submitButton = fragment.findViewById(R.id.submit_button);

        sharedPreferences = parentActivity.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);

        setUserValuesIfAvailable();
        submitButton.setOnClickListener(this);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            submitButtonClickedListener = (SubmitButtonClickedListener)getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()+" must implement SubmitButtonClickedListener ");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        submitButtonClickedListener = null;
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.submit_button && validate()) {
            saveUserValuesInSharedPreference();
            submitButtonClickedListener.submitButtonClicked();
        }
    }

    public interface SubmitButtonClickedListener {
        void submitButtonClicked();
    }
}
