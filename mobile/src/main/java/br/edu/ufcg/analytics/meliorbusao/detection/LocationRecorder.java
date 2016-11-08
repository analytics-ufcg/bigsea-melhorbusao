package br.edu.ufcg.analytics.meliorbusao.detection;

import android.content.Context;
import android.location.Location;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.DetectedActivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import br.edu.ufcg.analytics.meliorbusao.Constants;
import br.edu.ufcg.analytics.meliorbusao.models.LocationData;
import br.edu.ufcg.analytics.meliorbusao.models.LocationHolder;
import br.edu.ufcg.analytics.meliorbusao.ProviderState;
import br.edu.ufcg.analytics.meliorbusao.db.DBUtils;
import br.edu.ufcg.analytics.meliorbusao.listeners.MeliorListener;
import br.edu.ufcg.analytics.meliorbusao.models.User;
import br.edu.ufcg.analytics.meliorbusao.utils.ParseUtils;
import br.edu.ufcg.analytics.meliorbusao.utils.SharedPreferencesUtils;

public class LocationRecorder implements MeliorListener {
    private String tripId;
    private boolean recording = false;
    private Context context;
    private ArrayList<LocationHolder> locationList;
    private ArrayList<ProviderState> locationStateList;
    private User user = User.getInstance();

    private long startTimestamp;

    public LocationRecorder(Context context, String tripId) {
        this.context = context;
        this.tripId = tripId;
    }

    /**
     * Inicia a gravação do percurso
     * @param timestamp
     */
    public void startRecording(long timestamp) {
        if (Constants.DEBUG_LOCATION_RECORDER) {
            Toast.makeText(context, "Começou a gravar location!", Toast.LENGTH_LONG).show();
        }

        locationList = new ArrayList<>();
        locationStateList = new ArrayList<>();
        recording = true;
        startTimestamp = timestamp;
    }

    /**
     * Para a gravaçao do percurso do usuario e grava no arquivo
     */
    public void stopRecording() {
        if (Constants.DEBUG_LOCATION_RECORDER) {
            Toast.makeText(context, "Parou de gravar location!", Toast.LENGTH_LONG).show();
        }

        recording = false;
        DBUtils.addLocation(context, gravarNoArquivo(), startTimestamp);
    }

    /**
     * Grava o percurso em um arquivo
     * @return
     */
    private ArrayList<LocationData> gravarNoArquivo() {
        File externalStorage = Environment.getExternalStorageDirectory();
        File busMonitorPath = new File(externalStorage, Constants.LOG_PATH);
        ArrayList<LocationData> todasLocations = new ArrayList();

        busMonitorPath.mkdirs();
        String horario = String.valueOf(startTimestamp);
        File arquivo = new File(busMonitorPath, "locations_" + horario);
        File arquivoGPS = new File(busMonitorPath, "locations_" + horario + "_gps");

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo));
            String header = "lat,lon,acc,speed,bear,et";
            writer.write(header + "\n");

            for (LocationHolder location : locationList) {
                ParseUtils.addUserLocation(location, user.getUserID(context), tripId);
                String output = location.getLatitude() + "," + location.getLongitude() + "," + location.getAccuracy() + "," + location.getSpeed() + "," + location.getBearing() + "," + location.getTime();

                todasLocations.add(location);

                writer.write(output + "\n");
            }

            writer.flush();
            writer.close();
            return todasLocations;
        } catch (IOException e) {
            Toast.makeText(context, "Não foi possível gravar o arquivo '" + arquivo.getName() + "'", Toast.LENGTH_LONG).show();
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(arquivoGPS));
            String header = "state,et";
            writer.write(header + "\n");

            for (ProviderState locationState : locationStateList) {
                String output = String.valueOf(locationState.getState()) + "," + locationState.getTime();
                writer.write(output + "\n");
            }

            writer.flush();
            writer.close();
        } catch (IOException e) {
            Toast.makeText(context, "Não foi possível gravar o arquivo '" + arquivo.getName() + "'", Toast.LENGTH_LONG).show();
        }

        return todasLocations;
    }

    /**
     * Verifica se está em campina e restrição de tempo
     * @param timestamp
     * @param lastLocation
     */
    @Override
    public void onReceiveMeliorLocation(long timestamp, Location lastLocation) {

        Location cityCentralPointLocation = new Location("");
        //get
        cityCentralPointLocation.setLatitude(Double.valueOf(Constants.CENTRAL_POINT_LATITUDE));
        cityCentralPointLocation.setLongitude(Double.valueOf(Constants.CENTRAL_POINT_LONGITUDE));

        //if time is negative, still on waiting time
        if ((System.currentTimeMillis() -
                (Long.valueOf(SharedPreferencesUtils.getDeactivateTime(context)) + Constants.DEACTIVATED_TIME)) < 0) {
            SharedPreferencesUtils.setDetectionActive(context, false);
            Log.d("Debug timer=", String.valueOf(System.currentTimeMillis() -
                    (Long.valueOf(SharedPreferencesUtils.getDeactivateTime(context)) + Constants.DEACTIVATED_TIME)));

        }
        //if not on waiting time but out of city limits, set new time
        //TODO: Get City and Location from BD (Parse)
        //  if location in array of cities
        else if ((lastLocation.distanceTo(cityCentralPointLocation) > Constants.CITY_RADIUS)
                && (System.currentTimeMillis() -
                (Long.valueOf(SharedPreferencesUtils.getDeactivateTime(context)) + Constants.DEACTIVATED_TIME)) >= 0) {
            SharedPreferencesUtils.setDetectionActive(context, false);
            SharedPreferencesUtils.setDeactivateTime(context, String.valueOf(System.currentTimeMillis()));
        }
        //else, if on city limits and no time restriction, record!
        else {
            SharedPreferencesUtils.setDetectionActive(context, true);
            SharedPreferencesUtils.setDeactivateTime(context, String.valueOf(0));

            if (!recording)
                return;
            if (lastLocation != null) {
                if (Constants.LOG_RECORDER_LOCATION_CHANGES) {
                    Log.d("LocationChanged", lastLocation.toString());
                }
                locationList.add(new LocationHolder(lastLocation));
            }
        }
    }

    /**
     *
     * @param timestamp
     * @param locationAvailable
     */
    @Override
    public void onMeliorLocationAvailabilityChange(long timestamp, boolean locationAvailable) {
        locationStateList.add(new ProviderState(locationAvailable));
    }

    /**
     *
     * @param timestamp
     * @param detectedActivity
     */
    @Override
    public void onReceiveMeliorActivity(long timestamp, List<DetectedActivity> detectedActivity) {
    }

}
