package br.edu.ufcg.analytics.meliorbusao;

import android.content.Context;
import android.content.res.Resources;

import com.google.android.gms.location.DetectedActivity;

import java.io.File;

public final class Constants {
    public static final String PACKAGE_NAME = "br.edu.ufcg.analytics.meliorbusao";

    public static final boolean DEBUG_NOTIFICATION = false;
    public static final boolean DEBUG_LOCATION_RECORDER = false;

    public static final boolean LOG_EACH_ACTIVITY = false;
    public static final boolean LOG_RECORDER_LOCATION_CHANGES = false;

    public static final String DB_SHARED_PREFERENCES = "db_preferences";

    public static final String STOPS_ON_DB_PREFERENCE = "stops_on_db";

    // Regarding to Authentication
    public static final String AUTH_FILE_KEY = PACKAGE_NAME + ".AUTH_FILE_KEY";
    public static final String USER_TOKEN_KEY = "user_token_key";
    public static final String AUTH_SERVICE_KEY = "user_service";
    public static final String BIG_SEA_SERVICE = "big_sea_service";
    public static final String GOOGLE_SERVICE = "google_service";

    public static final String SHAPES_ON_DB_PREFERENCE = "shapes_on_db";
    public static final String SCHEDULE_ON_DB_PREFERENCE = "schedule_on_db";
    public static final String CITY_ON_DB_PREFERENCE = "city_on_db";
    public static final String BUS_MONITORING = "monitoring" ;

    // All distances in meters
    public static final int NEAR_STOPS_RADIUS = 500;
    public static final int STOP_DETECTION_RADIUS = 25;
    public static final int DISTANCE_BETWEEN_DETECTIONS = 10;

    private Constants() { }

    public static final String CITY = "Campina Grande";
    public static final String CENTRAL_POINT_LATITUDE = "-7.236425";
    public static final String CENTRAL_POINT_LONGITUDE = "-35.896936";
    public static final int CITY_RADIUS = 8000; //in meters
    public static final String TIMER_MONITORING = "0" ;
    public static final long DEACTIVATED_TIME = 1000 * 7200; //2h
    public static final String DETECTION = "detecting" ;

    public static final String BROADCAST_ACTION = PACKAGE_NAME + ".BROADCAST_ACTION";

    public static final String BROADCAST_ACTION_ON_BUS = PACKAGE_NAME + ".BROADCAST_ACTION_ON_BUS";
    public static final String ON_BUS_STATUS_EXTRA = PACKAGE_NAME + ".ON_BUS_STATUS_EXTRA";
    public static final String ON_BUS_TIMESTAMP_EXTRA = PACKAGE_NAME + ".ON_BUS_TIMESTAMP_EXTRA";

    public static final String TRIP_TIMESTAMP_EXTRA = PACKAGE_NAME + ".TRIP_TIMESTAMP_EXTRA";
    public static final String TRIP_ROUTES_EXTRA = PACKAGE_NAME + ".TRIP_ROUTES_EXTRA";

    public static final String ACTIVITY_EXTRA = PACKAGE_NAME + ".ACTIVITY_EXTRA";

    public static final String SHARED_PREFERENCES_NAME = PACKAGE_NAME + ".SHARED_PREFERENCES";

    public static final String ACTIVITY_UPDATES_REQUESTED_KEY = PACKAGE_NAME +
            ".ACTIVITY_UPDATES_REQUESTED";

    public static final String LOCATION_RECORDER_WAKELOCK = PACKAGE_NAME +
            "LOCATION_RECORDER_WAKELOCK";

    public static final String DETECTED_ACTIVITIES = PACKAGE_NAME + ".DETECTED_ACTIVITIES";

    public static final String LOG_PATH = "MeliorBusao/Log";
    public static final String BD_FOLDER_PATH = "/MeliorBusao/BD/";
    public static final String BD_PATH = "/MeliorBusao/BD/"; //atencao ao nome da pasta :P


    //BD ZIP
    public static final String BD_CG = "campinagrande.db";
    public static final String BD_CTBA = "curitiba.db";



    public static final long DETECTION_INTERVAL_IN_MILLISECONDS = 15;

    public static final long LOCATION_REQUEST_INTERVAL = 1000;

    public static final long TIME_TO_NOTIFY = 10 * 60 * 1000; // 10 minutes

    // Parser kinds
    public static final int KIND_ROUTESTOP = 0;
    public static final int KIND_SHAPES = 1;

//    Fetch Address Service
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String GEOPOINT_DATA_EXTRA = PACKAGE_NAME + ".GEOPOINT_DATA_EXTRA";

    protected static final int[] MONITORED_ACTIVITIES = {
            DetectedActivity.STILL,
            DetectedActivity.ON_FOOT,
            DetectedActivity.WALKING,
            DetectedActivity.RUNNING,
            DetectedActivity.ON_BICYCLE,
            DetectedActivity.IN_VEHICLE,
            DetectedActivity.TILTING,
            DetectedActivity.UNKNOWN
    };

    public static String getActivityString(Context context, int detectedActivityType) {
        Resources resources = context.getResources();
        switch(detectedActivityType) {
            case DetectedActivity.IN_VEHICLE:
                return resources.getString(R.string.in_vehicle);
            case DetectedActivity.ON_BICYCLE:
                return resources.getString(R.string.on_bicycle);
            case DetectedActivity.ON_FOOT:
                return resources.getString(R.string.on_foot);
            case DetectedActivity.RUNNING:
                return resources.getString(R.string.running);
            case DetectedActivity.STILL:
                return resources.getString(R.string.still);
            case DetectedActivity.TILTING:
                return resources.getString(R.string.tilting);
            case DetectedActivity.UNKNOWN:
                return resources.getString(R.string.unknown);
            case DetectedActivity.WALKING:
                return resources.getString(R.string.walking);
            default:
                return resources.getString(R.string.unidentifiable_activity, detectedActivityType);
        }
    }


}
