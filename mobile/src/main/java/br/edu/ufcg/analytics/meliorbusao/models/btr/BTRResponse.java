package br.edu.ufcg.analytics.meliorbusao.models.btr;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Tarciso on 02/05/2017.
 */
public class BTRResponse {

    //{"route":"0500","date":"2016-09-01","week.day":"Thursday","trip.initial.time":"14:07:00",
    // "trip.final.time":"15:01:00","grouped.timetable":52200,"difference.previous.timetable":-1,
    // "difference.next.timetable":26,"day.type":"TUE WED THU","mean.timetable":"14:35:33",
    // "time.difference":753,"previous.timetable":"14:31:33","next.timetable":"14:39:33",
    // "passengers.number":28.5556,"trip.duration":78.2222,"is.fastest.trip":false,"is.emptiest.trip":true}

    private String busRoute;
    private Date date;
    private String weekday;
    private double passengersNum;
    private double tripDuration;

    public BTRResponse(String busRoute, Date date, String weekday, double passengersNum, double tripDuration) {
        this.busRoute = busRoute;
        this.date = date;
        this.weekday = weekday;
        this.passengersNum = passengersNum;
        this.tripDuration = tripDuration;
    }

    public String getBusRoute() {
        return busRoute;
    }

    public void setBusRoute(String busRoute) {
        this.busRoute = busRoute;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getWeekday() {
        return weekday;
    }

    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }

    public double getPassengersNum() {
        return passengersNum;
    }

    public void setPassengersNum(double passengersNum) {
        this.passengersNum = passengersNum;
    }

    public double getTripDuration() {
        return tripDuration;
    }

    public void setTripDuration(double tripDuration) {
        this.tripDuration = tripDuration;
    }

    public static BTRResponse fromJson(JSONObject tripJson) {
        BTRResponse resp = null;
        try {
            String busRoute = tripJson.getString("route");
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(tripJson.getString("date"));
            String weekday = tripJson.getString("week.day");
            double passengersNum = tripJson.getDouble("passengers.number");
            double tripDuration = tripJson.getDouble("trip.duration");

            resp = new BTRResponse(busRoute,date,weekday,passengersNum,tripDuration);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resp;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Route: ");
        sb.append(getBusRoute());
        sb.append("\n");
        sb.append("Date: ");
        sb.append(getDate());
        sb.append("\n");
        sb.append("Weekday: ");
        sb.append(getWeekday());
        sb.append("\n");
        sb.append("Passengers Number: ");
        sb.append(getPassengersNum());
        sb.append("\n");
        sb.append("Trip Duration: ");
        sb.append(getTripDuration());
        return sb.toString();
    }
}
