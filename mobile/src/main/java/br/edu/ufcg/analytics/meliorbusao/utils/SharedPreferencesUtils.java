package br.edu.ufcg.analytics.meliorbusao.utils;

import android.content.Context;
import android.content.SharedPreferences;

import br.edu.ufcg.analytics.meliorbusao.Constants;

public class SharedPreferencesUtils {

    protected SharedPreferencesUtils() {}

    final static String SEPARATOR = "_";

    /**
     * @param context
     * @return Se as rotas e paradas estão no BD
     */
    public static boolean isRoutesAndStopsOnDatabase(Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constants.DB_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return settings.getBoolean(Constants.STOPS_ON_DB_PREFERENCE + SEPARATOR + getCityNameOnDatabase(context), false);
    }

    /**
     * Modifica o valor (boolean) de STOPS_ON_DB_PREFERENCE
     * @param context
     * @param onDatabase
     */
    public static void setRoutesAndStopsOnDatabase(Context context, boolean onDatabase) {
        SharedPreferences settings = context.getSharedPreferences(Constants.DB_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(Constants.STOPS_ON_DB_PREFERENCE + SEPARATOR + getCityNameOnDatabase(context), onDatabase);

        editor.apply();
    }


    /**
     * @param context
     * @return Se a previsao de horario está no BD
     */
    public static boolean isScheduleOnDatabase(Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constants.DB_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return settings.getBoolean(Constants.SCHEDULE_ON_DB_PREFERENCE  + SEPARATOR + getCityNameOnDatabase(context), false);
    }

    /**
     * Modifica o valor (boolean) de STOPS_ON_DB_PREFERENCE
     * @param context
     * @param onDatabase
     */
    public static void setScheduleOnDatabase(Context context, boolean onDatabase){
        SharedPreferences settings = context.getSharedPreferences(Constants.DB_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(Constants.SCHEDULE_ON_DB_PREFERENCE  + SEPARATOR + getCityNameOnDatabase(context), onDatabase);

        editor.apply();
    }

    /**
     * @param context
     * @return Se os shapes das rotas estão no BD
     */
    public static boolean isShapesOnDatabase(Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constants.DB_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return settings.getBoolean(Constants.SHAPES_ON_DB_PREFERENCE  + SEPARATOR + getCityNameOnDatabase(context), false);
    }

    /**
     *  Modifica o valor (boolean) de SHAPES_ON_DB_PREFERENCE
     * @param context
     * @param onDatabase
     */
    public static void setShapesOnDatabase(Context context, boolean onDatabase){
        SharedPreferences settings = context.getSharedPreferences(Constants.DB_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(Constants.SHAPES_ON_DB_PREFERENCE + SEPARATOR + getCityNameOnDatabase(context), onDatabase);

        editor.apply();
    }

    /**
     * @param context
     * @return se a detecção automatica está ativada
     */
    public static boolean isBusMonitoring(Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constants.BUS_MONITORING, Context.MODE_PRIVATE);
        return settings.getBoolean(Constants.BUS_MONITORING, true);
    }

    /**
     *   Modifica o valor (boolean) de BUS_MONITORING
     * @param context
     * @param monitoring
     */
    public static void setBusMonitoring(Context context, boolean monitoring){
        SharedPreferences settings = context.getSharedPreferences(Constants.BUS_MONITORING, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(Constants.BUS_MONITORING, monitoring);

        editor.apply();
    }

    /**
     *
     * @param context
     * @return a string do timestamp do final da detecção do ônibus
     */
    public static String getDeactivateTime(Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constants.TIMER_MONITORING, Context.MODE_PRIVATE);
        return settings.getString(Constants.TIMER_MONITORING, "0");
    }

    /**
     *   Modifica o valor (String) de TIMER_MONITORING
     * @param context
     * @param monitoring
     */
    public static void setDeactivateTime(Context context, String monitoring){
        SharedPreferences settings = context.getSharedPreferences(Constants.TIMER_MONITORING, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constants.TIMER_MONITORING, monitoring);

        editor.apply();
    }

    /**
     * @param context
     * @return se a detecção está ativa
     */
    public static boolean isDetectionActive(Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constants.DETECTION, Context.MODE_PRIVATE);
        return settings.getBoolean(Constants.DETECTION, true);
    }

    /**
     *   Modifica o valor (boolean) de DETECTION
     * @param context
     * @param monitoring
     */
    public static void setDetectionActive(Context context, boolean monitoring){
        SharedPreferences settings = context.getSharedPreferences(Constants.DETECTION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(Constants.DETECTION, monitoring);

        editor.apply();
    }


    public static String getCityNameOnDatabase(Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constants.DB_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return settings.getString(Constants.CITY_ON_DB_PREFERENCE, "");
    }

    public static void setCityNameOnDatabase(Context context, String cityName){
        SharedPreferences settings = context.getSharedPreferences(Constants.DB_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constants.CITY_ON_DB_PREFERENCE, cityName);

        editor.apply();
    }

    public static void setUserToken(Context context, String serviceType, String userToken) {
        SharedPreferences settings = context.getSharedPreferences(Constants.AUTH_FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constants.AUTH_SERVICE_KEY, serviceType);
        editor.putString(Constants.USER_TOKEN_KEY, userToken);

        editor.apply();
    }

    public static String getAuthService(Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constants.AUTH_FILE_KEY, Context.MODE_PRIVATE);
        return settings.getString(Constants.AUTH_SERVICE_KEY, Constants.GOOGLE_SERVICE);
    }

    public static String getUserToken(Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constants.AUTH_FILE_KEY, Context.MODE_PRIVATE);
        return settings.getString(Constants.USER_TOKEN_KEY, "");
    }
}
