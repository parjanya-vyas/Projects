package com.stats.disease.healthstats;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormatSymbols;

/**
 * Created by parjanya on 22/10/16.
 */

class Utils {

    static boolean isUserLoggedIn(Context context){
        SharedPreferences loginPreference = context.getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        return loginPreference.getBoolean(Constants.LOGIN_KEY, false);
    }

    static void loginUser(Context context, String userId, String ageGroup, String gender, String locality){
        SharedPreferences loginPreference = context.getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor loginEditor = loginPreference.edit();
        loginEditor.putBoolean(Constants.LOGIN_KEY, true);
        loginEditor.putString(Constants.USER_ID_KEY, userId);
        loginEditor.putString(Constants.AGE_GROUP_KEY, ageGroup);
        loginEditor.putString(Constants.GENDER_KEY, gender);
        loginEditor.putString(Constants.LOCALITY_KEY, locality);
        loginEditor.apply();
    }

    static void logoutUser(Context context){
        SharedPreferences loginPreference = context.getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor loginEditor = loginPreference.edit();
        loginEditor.remove(Constants.LOGIN_KEY);
        loginEditor.apply();
    }

    static String getUserId(Context context){
        if (!isUserLoggedIn(context))
            return null;
        SharedPreferences loginPreference = context.getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        return loginPreference.getString(Constants.USER_ID_KEY, null);
    }

    static String getAgeGroup(Context context){
        if (!isUserLoggedIn(context))
            return null;
        SharedPreferences loginPreference = context.getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        return loginPreference.getString(Constants.AGE_GROUP_KEY, null);
    }

    static String getGender(Context context){
        if (!isUserLoggedIn(context))
            return null;
        SharedPreferences loginPreference = context.getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        return loginPreference.getString(Constants.GENDER_KEY, null);
    }

    static String getLocality(Context context){
        if (!isUserLoggedIn(context))
            return null;
        SharedPreferences loginPreference = context.getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        return loginPreference.getString(Constants.LOCALITY_KEY, null);
    }

    static String getLastReportedDisease(Context context){
        if (!isUserLoggedIn(context))
            return null;
        SharedPreferences loginPreference = context.getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        return loginPreference.getString(Constants.LAST_REPORTED_DISEASE_KEY, null);
    }

    static void setLastReportedDisease(Context context, String diseaseString){
        SharedPreferences loginPreference = context.getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor loginEditor = loginPreference.edit();
        loginEditor.putString(Constants.LAST_REPORTED_DISEASE_KEY, diseaseString);
        loginEditor.apply();
    }

    static int getMonthInteger(String month){
        String[] allMonths = new DateFormatSymbols().getMonths();
        for (int i=0;i<12;i++){
            if (allMonths[i].equals(month))
                return (i+1);
        }

        return  -1;
    }

    static String getMonthString(int month){
        String[] allMonths = new DateFormatSymbols().getMonths();
        return allMonths[month-1];
    }
}
