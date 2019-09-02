package com.assigments.parjanya.a16305r004_datarecorder;

/**
 * Created by parjanya on 15/2/18.
 */

class Constants {
    static final String RECORDING_SWITCH_STATE_BUNDLE_KEY = "recording_switch_state";

    static final String SHARED_PREFERENCE_NAME = "data_recorder_shared_preference";
    static final String USER_DATA_PRESENT_SHARED_PREFERENCE = "user_data_present_shared_preference";
    static final String FIRST_NAME_SHARED_PREFERENCE_KEY = "first_name_shared_preference";
    static final String LAST_NAME_SHARED_PREFERENCE_KEY = "last_name_shared_preference";
    static final String EMAIL_SHARED_PREFERENCE_KEY = "email_shared_preference";
    static final String MOBILE_NUMBER_SHARED_PREFERENCE_KEY = "mobile_number_shared_preference";
    static final String GENDER_SHARED_PREFERENCE_KEY = "gender_shared_preference";
    static final String AGE_SHARED_PREFERENCE_KEY = "age_shared_preference";
    static final int MALE_GENDER_VALUE_SHARED_PREFERENCE = 0;
    static final int FEMALE_GENDER_VALUE_SHARED_PREFERENCE = 1;
    static final int OTHER_GENDER_VALUE_SHARED_PREFERENCE = 2;

    static final String ACCELEROMETER_SHARED_PREFERENCE_KEY = "accelerometer_shared_preference";
    static final String GPS_SHARED_PREFERENCE_KEY = "gps_shared_preference";

    static final String RECORDING_ROW_1_SHARED_PREFERENCE_KEY = "recording_row_1_shared_preference";
    static final String RECORDING_ROW_2_SHARED_PREFERENCE_KEY = "recording_row_2_shared_preference";
    static final String RECORDING_ROW_3_SHARED_PREFERENCE_KEY = "recording_row_3_shared_preference";
    static final String RECORDING_ROW_4_SHARED_PREFERENCE_KEY = "recording_row_4_shared_preference";
    static final String RECORDING_ROW_5_SHARED_PREFERENCE_KEY = "recording_row_5_shared_preference";

    static final String SERVICE_RUNNING_SHARED_PREFERENCE_KEY = "service_running_shared_preference";

    static final String NUMBER_OF_RECORDINGS_CREATED_SHARED_PREFERENCE = "number_of_recordings_created_shared_preference";

    static final String LABEL_INTENT_EXTRA_KEY = "label_intent_extra_key";
    static final String ACCELEROMETER_INTENT_EXTRA_KEY = "accelerometer_intent_extra_key";
    static final String GPS_INTENT_EXTRA_KEY = "gps_intent_extra_key";

    static final String ACCELEROMETER_EVENT_ACTION = "accelerometer_event_action";
    static final String GPS_EVENT_ACTION = "gps_event_action";
    static final String COMBINED_EVENT_ACTION = "combined_event_action";

    static final String TIMESTAMP_BROADCAST_KEY = "timestamp_broadcast_key";
    static final String ACCELERATION_X_BROADCAST_KEY = "acceleration_x_broadcast_key";
    static final String ACCELERATION_Y_BROADCAST_KEY = "acceleration_y_broadcast_key";
    static final String ACCELERATION_Z_BROADCAST_KEY = "acceleration_z_broadcast_key";
    static final String LATITUDE_BROADCAST_KEY = "latitude_broadcast_key";
    static final String LONGITUDE_BROADCAST_KEY = "longitude_broadcast_key";

    static final String RECORDING_FILE_NAME_PREFIX = "Recording_";
    static final String RECORDING_FILE_NAME_SUFFIX = ".csv";

    static final int NUMBER_OF_PAGES = 3;
    static final int LOGIN_PAGE_INDEX = 0;
    static final int SENSORS_PAGE_INDEX = 1;
    static final int RECORDINGS_PAGE_INDEX = 2;
}
