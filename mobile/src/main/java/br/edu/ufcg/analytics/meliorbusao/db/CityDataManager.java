package br.edu.ufcg.analytics.meliorbusao.db;

import android.content.Context;
import android.content.res.AssetManager;
import android.location.Location;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import br.edu.ufcg.analytics.meliorbusao.exceptions.NoDataForCityException;
import br.edu.ufcg.analytics.meliorbusao.listeners.OnFinishedParseListener;
import br.edu.ufcg.analytics.meliorbusao.utils.CitiesPropertiesHelper;
import br.edu.ufcg.analytics.meliorbusao.utils.SharedPreferencesUtils;

public class CityDataManager {

    private static final String TAG = "CityDataManager";
    private static final String FOLDER_NAME = "citiesProperties";

    private CityDataManager() {};

    public static String checkCity(AssetManager assetManager, Location lastLocation) throws NoDataForCityException {
        try {
            Log.d(TAG, "checkCity: Reading files in assets/citiesProperties folder");

            String[] configFiles = assetManager.list(FOLDER_NAME);

            for (String filename: configFiles) {

                InputStream propertiesFile = assetManager.open(FOLDER_NAME + "/" + filename);
                CitiesPropertiesHelper cpHelper = new CitiesPropertiesHelper(propertiesFile);

                Location cityCentralPointLocation = new Location("");
                cityCentralPointLocation.setLatitude(Double.parseDouble(cpHelper.getCentralPointLatitude()));
                cityCentralPointLocation.setLongitude(Double.parseDouble(cpHelper.getCentralPointLongitude()));

                int cityRadius = Integer.parseInt(cpHelper.getCityRadius());

                if (lastLocation.distanceTo(cityCentralPointLocation) <= cityRadius) {
                    return cpHelper.getCityName();
                }
            }
        } catch (IOException e) {
            Log.d(TAG, "checkCity: Unable to read files in assets/citiesProperties folder");
            e.printStackTrace();
        }

        throw new NoDataForCityException();
    }

    public static void downloadDB(Context context, OnFinishedParseListener listener, AssetManager assetManager) {
        String file_url = "";

        try {
            Log.d(TAG, "downloadDB: Reading files in assets/citiesProperties folder");
            String[] configFiles = assetManager.list(FOLDER_NAME);

            for (String filename: configFiles) {
                InputStream propertiesFile = assetManager.open(FOLDER_NAME + "/" + filename);
                CitiesPropertiesHelper cpHelper = new CitiesPropertiesHelper(propertiesFile);

                if (SharedPreferencesUtils.getCityNameOnDatabase(context).equals(cpHelper.getCityName())) {
                    file_url = cpHelper.getDbBaseUrl();
                }
            }

            new DownloadFileFromURL(context, listener).execute(file_url);
        } catch (IOException e) {
            Log.d(TAG, "downloadDB: Unable to read files in assets/citiesProperties folder");
            e.printStackTrace();
        }

    }

}
