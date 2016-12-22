package br.edu.ufcg.analytics.meliorbusao.utils;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.edu.ufcg.analytics.meliorbusao.R;
import br.edu.ufcg.analytics.meliorbusao.activities.MelhorBusaoActivity;
import br.edu.ufcg.analytics.meliorbusao.models.StopTime;

public class BestTripRecommenderUtils {
    public static List<StopTime> getBestTripRecommenderData(String route, int busStopId, Context context) {
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date currDate = new Date();

        // This is a temporary solution!! It'll be resolved when correct the prediction data file!!!
        if (( (MelhorBusaoActivity) context).getCityName().equals("Campina Grande") && route.length() == 3) {
            route = "0" + route;
        }

        return getBestTripRecommenderData(context.getString(R.string.BEST_TRIP_RECOMMENDER_URL), route, timeFormat.format(currDate), dateFormat.format(currDate), busStopId);
    }

    public static List<StopTime> getBestTripRecommenderData(String API_URL, String route, String time, String date, int busStopId) {
        try {
            android.os.StrictMode.ThreadPolicy policy = new android.os.StrictMode.ThreadPolicy.Builder().permitAll().build();
            android.os.StrictMode.setThreadPolicy(policy);

            URL url = new URL(API_URL + "/get_best_trips?route=" + route + "&time=" + time + "&date=" + date + "&bus_stop_id=" + busStopId + "&closest_trip_type=next_hour");
            Log.d("", url.toString());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }

                bufferedReader.close();
                String requestedData = stringBuilder.toString();

                JSONArray jsonArray = (JSONArray) new JSONTokener(requestedData).nextValue();
                ArrayList<StopTime> stopTimes = new ArrayList<StopTime>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonStopTime = jsonArray.getJSONObject(i);

                    double numberOfPassengers = Double.parseDouble(jsonStopTime.getString("passengers.number"));
                    double tripDuration = Double.parseDouble(jsonStopTime.getString("trip.duration"));
                    String meanSchedule = jsonStopTime.getString("mean.timetable");
                    String lowerScheduleConfidenceInterval = jsonStopTime.getString("previous.timetable");
                    String higherScheduleConfidenceInterval = jsonStopTime.getString("next.timetable");
                    boolean isFastestTrip = Boolean.parseBoolean(jsonStopTime.getString("is.fastest.trip"));
                    boolean isEmptiestTrip = Boolean.parseBoolean(jsonStopTime.getString("is.emptiest.trip"));

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd HH:mm:ss");
                    Date departure = sdf.parse(date + " " + meanSchedule);
                    Date lowerScheduleConfidenceIntervalDate = sdf.parse(date + " " + lowerScheduleConfidenceInterval);
                    Date higherScheduleConfidenceIntervalDate = sdf.parse(date + " " + higherScheduleConfidenceInterval);

                    stopTimes.add(new StopTime(route, busStopId, departure, lowerScheduleConfidenceIntervalDate,
                            higherScheduleConfidenceIntervalDate, numberOfPassengers, tripDuration, isEmptiestTrip, isFastestTrip));
                }

                return stopTimes;
            } finally{
                urlConnection.disconnect();
            }
        } catch(Exception e) {
            Log.e("ERROR", e.getMessage(), e);
            return null;
        }
    }
}
