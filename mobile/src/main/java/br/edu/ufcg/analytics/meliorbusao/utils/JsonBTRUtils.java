package br.edu.ufcg.analytics.meliorbusao.utils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.edu.ufcg.analytics.meliorbusao.models.otp.Itinerary;
import br.edu.ufcg.analytics.meliorbusao.models.otp.Leg;

public class JsonBTRUtils {

    public static final String TAG = "Json_BTR_Utils";

    private static Leg legFromJson(JSONObject legJson){
        Leg leg = null;
        String busRoute = "";
        List<String> legsPoints = new ArrayList<>();
        String mode = null;
        String color = null;
        try {
            mode = legJson.getString("mode");

            if (mode.equals("BUS")) {
                busRoute = legJson.getString("route");
                color = legJson.getString("routeColor");
            }
            String encodedPoints = legJson.getJSONObject("legGeometry").getString("points");
            legsPoints.add(encodedPoints);

            String depBusStop = legJson.getJSONObject("from").getString("name");
            Date startTime = new Date(legJson.getLong("startTime"));
            Date endTime = new Date(legJson.getLong("endTime"));

            leg = new Leg(busRoute, legsPoints, depBusStop, startTime, endTime, mode, color==null?"633307":color);

        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        return leg;
    }

    private static Itinerary itineraryFromJson(JSONObject itineraryJson) {
        Itinerary itinerary = null;
        try {

            List<Leg> legs = new ArrayList<>();
            List<String> legsPoints = new ArrayList<>();

            JSONArray legsJson = itineraryJson.getJSONArray("legs");

            String depBusStop = null;

            for (int i = 0; i < legsJson.length(); i++) {
                JSONObject legJson = legsJson.getJSONObject(i);
                Leg leg = legFromJson(legJson);
                legs.add(leg);
                if (leg.getMode().equals("BUS") && depBusStop==null ){
                    depBusStop = leg.getDepartureBusStop();
                }
            }

            Date startTime = new Date(itineraryJson.getLong("startTime"));
            Date endTime = new Date(itineraryJson.getLong("endTime"));
            int duration = itineraryJson.getInt("btr-duration");
            Log.d(TAG, String.valueOf(duration));

            itinerary = new Itinerary(legs, depBusStop, startTime, endTime, duration);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itinerary;
    }

    public static List<Itinerary> itinerariesFromJson(JSONObject response){
        List<Itinerary> itineraries = new ArrayList<>();
        JSONObject plan = null;
        try {
            plan = response.getJSONObject("plan");


            JSONArray itinerariesJson = plan.getJSONArray("itineraries");

            for (int i = 0; i < itinerariesJson.length(); i++) {
                itineraries.add(itineraryFromJson(itinerariesJson.getJSONObject(i)));
            }

            Log.d(TAG, "Number of itineraries: " + String.valueOf(itinerariesJson.length()));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  itineraries;

    }

}
