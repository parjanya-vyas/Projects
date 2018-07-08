package com.stats.disease.healthstats;

import java.util.Calendar;

/**
 * Created by parjanya on 22/10/16.
 */

class Constants {
    static final String LOGIN_PREFERENCE = "login_preference";
    static final String LOGIN_KEY = "login_key";
    static final String USER_ID_KEY = "user_id_key";
    static final String AGE_GROUP_KEY = "age_group_key";
    static final String GENDER_KEY = "gender_key";
    static final String LOCALITY_KEY = "locality_key";
    static final String LAST_REPORTED_DISEASE_KEY = "last_reported_disease_key";

    static final String SERVER_QUERY_POST_KEY = "query";
    static String SERVER_URL_STRING = "http://10.42.0.1/HealthStats/server.php";

    static final String GET_USERS_QUERY = "getUsersWithId";
    static final String GET_USER_FROM_ID_QUERY = "getUserFromId";
    static final String LOGIN_AUTHENTICATE_QUERY = "userLoginAuth";
    static final String SIGNUP_QUERY = "userSignup";
    static final String REPORT_CASE_QUERY = "reportCase";
    static final String SIMILAR_CHECK_QUERY = "isSimilarCase";
    static final String GET_CASE_QUERY = "getCaseFromId";
    static final String GET_ALL_CASES_FROM_USER_QUERY = "getAllCasesFromUserId";

    static final String GET_ALL_DISEASE_FROM_LOCALITY_PERCENT = "getAllDiseaseFromLocalityPercent";
    static final String GET_ALL_LOCALITY_FROM_DISEASE_PERCENT = "getAllLocalityFromDiseasePercent";
    static final String GET_ALL_DISEASE_FROM_LOCALITY_VALUES = "getAllDiseaseFromLocalityValues";
    static final String GET_ALL_LOCALITY_FROM_DISEASE_VALUES = "getAllLocalityFromDiseaseValues";

    static final String GET_INSTANCES_DAYS_WIDE = "getInstancesDaysWide";
    static final String GET_INSTANCES_MONTHS_WIDE = "getInstancesMonthsWide";
    static final String GET_INSTANCES_YEARS_WIDE = "getInstancesYearsWide";

    static final String BAR_AND_PIE_CHART_TYPE = "chartType";
    static final String CHART_TYPE_LOCALITY = "locality";
    static final String CHART_TYPE_DISEASE = "disease";

    static final String SERVER_INVALID_QUERY_STRING = "Invalid Query";

    static final String IITB_ADDRESS_STRING = "IIT Bombay, Powai, Mumbai, Maharashtra, India";

    static final int START_YEAR = 1945;
    static final int CURRENT_YEAR = Calendar.getInstance().get(Calendar.YEAR);

    static final int Y_ANIMATION_MILLIS = 1500;

    static final double hostel1Lat=19.137400;
    static final double hostel1Lang=72.913900;

    static final double hostel2Lat=19.137000;
    static final double hostel2Lang=72.912800;

    static final double hostel3Lat=19.136700;
    static final double hostel3Lang=72.911400;

    static final double hostel4Lat=19.136700;
    static final double hostel4Lang=72.910200;

    static final double hostel5Lat=19.134900;
    static final double hostel5Lang=72.909700;

    static final double hostel6Lat=19.135600;
    static final double hostel6Lang=72.907200;

    static final double hostel7Lat=19.134300;
    static final double hostel7Lang=72.908100;

    static final double hostel8Lat=19.133200;
    static final double hostel8Lang=72.911000;

    static final double hostel9Lat=19.135400;
    static final double hostel9Lang=72.908100;

    static final double hostel10Lat=19.128400;
    static final double hostel10Lang=72.916100;

    static final double hostel11Lat=19.133400;
    static final double hostel11Lang=72.912000;

    static final double hostel12Lat=19.135300;
    static final double hostel12Lang=72.905000;

    static final double hostel13Lat=19.134100;
    static final double hostel13Lang=72.904800;

    static final double hostel14Lat=19.134200;
    static final double hostel14Lang=72.906000;

    static final double hostel15Lat=19.137800;
    static final double hostel15Lang=72.913900;

    static final double hostel16Lat=19.137900;
    static final double hostel16Lang=72.912900;
}
