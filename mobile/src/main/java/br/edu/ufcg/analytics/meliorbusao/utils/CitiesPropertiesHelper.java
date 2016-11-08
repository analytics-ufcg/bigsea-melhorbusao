package br.edu.ufcg.analytics.meliorbusao.utils;

import android.content.res.Resources;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CitiesPropertiesHelper {

    private static final String TAG = "CitiesPropertiesHelper";

    private static final String CITY_NAME = "city_name";
    private static final String CENTRAL_POINT_LAT = "central_point_lat";
    private static final String CENTRAL_POINT_LONG = "central_point_long";
    private static final String CITY_RADIUS = "city_radius";
    private static final String DB_BASE_URL = "db_base_url";

    private Properties properties;

    public CitiesPropertiesHelper(InputStream configFile) {
        try {
            properties = new Properties();
            properties.load(configFile);
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Unable to find the config file: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "Failed to open config file.");
        }

    }

    public String getDbBaseUrl() {
        return properties.getProperty(DB_BASE_URL);
    }

    public String getCityName() {
        return properties.getProperty(CITY_NAME);
    }

    public String getCentralPointLatitude() {
        return properties.getProperty(CENTRAL_POINT_LAT);
    }

    public String getCentralPointLongitude() {
        return properties.getProperty(CENTRAL_POINT_LONG);
    }

    public String getCityRadius() {
        return properties.getProperty(CITY_RADIUS);
    }
}
