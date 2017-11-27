package br.edu.ufcg.analytics.meliorbusao.utils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.edu.ufcg.analytics.meliorbusao.models.otp.Itinerary;

public class JsonBTRUtils {

    //TODO modify

    public static final String TAG = "Json_BTR_Utils";

    public static Itinerary fromJson(JSONObject itineraryJson) {
        Itinerary itinerary = null;
        try {
            List<String> busRoutes = new ArrayList<>();
            JSONArray legsJson = itineraryJson.getJSONArray("legs");
            JSONObject firstBusLeg = null;
            List<String> legsPoints = new ArrayList<>();

            for (int i = 0; i < legsJson.length(); i++) {
                JSONObject legJson = legsJson.getJSONObject(i);
                String mode = legJson.getString("mode");
                if (mode.equals("BUS")) {
                    if (firstBusLeg == null) firstBusLeg = legJson;
                    String route = legJson.getString("route");
                    busRoutes.add(route);
                }
                String encodedPoints = legJson.getJSONObject("legGeometry").getString("points");
                legsPoints.add(encodedPoints);
            }
            String depBusStop = firstBusLeg.getJSONObject("from").getString("name");
            Date startTime = new Date(itineraryJson.getLong("startTime"));
            Date endTime = new Date(itineraryJson.getLong("endTime"));
            int duration = itineraryJson.getInt("btr-duration");
            Log.d(TAG, String.valueOf(duration));
            //TODO Modify this
            itinerary = new Itinerary(busRoutes, legsPoints, depBusStop, startTime, endTime, duration);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itinerary;
    }



}
