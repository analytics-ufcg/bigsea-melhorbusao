package br.edu.ufcg.analytics.meliorbusao.models.otp;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * An Itinerary is a representation for the output from Open Trip Planner API.
 * It contains all the information of a trip that goes from one point to another.
 */
public class Itinerary implements Parcelable {
    public static final String TAG = "Itinerary";

    private List<String> busRoutes;
    private List<String> encodedPolylinePoints;
    private String departureBusStop;
    private Date departureTime;
    private Date arrivalTime;
    private int durationInSecs;

    public Itinerary(List<String> busRoutes, List<String> encodedPolylinePoints, String departureBusStop, Date departureTime, Date arrivalTime, int durationInSecs) {
        this.busRoutes = busRoutes;
        this.encodedPolylinePoints = encodedPolylinePoints;
        this.departureBusStop = departureBusStop;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.durationInSecs = durationInSecs;
    }

    protected Itinerary(Parcel in) {
        busRoutes = new ArrayList<>();
        in.readStringList(busRoutes);
        encodedPolylinePoints = new ArrayList<>();
        in.readStringList(encodedPolylinePoints);
        departureBusStop = in.readString();
        departureTime = (Date) in.readSerializable();
        arrivalTime = (Date) in.readSerializable();
        durationInSecs = in.readInt();
    }

    public static final Creator<Itinerary> CREATOR = new Creator<Itinerary>() {
        @Override
        public Itinerary createFromParcel(Parcel in) {
            return new Itinerary(in);
        }

        @Override
        public Itinerary[] newArray(int size) {
            return new Itinerary[size];
        }
    };

    public List<String> getBusRoutes() {
        return busRoutes;
    }

    public void setBusRoutes(List<String> busRoutes) {
        this.busRoutes = busRoutes;
    }

    public String getDepartureBusStop() {
        return departureBusStop;
    }

    public void setDepartureBusStop(String departureBusStop) {
        this.departureBusStop = departureBusStop;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getDurationInSecs() {
        return durationInSecs;
    }

    public void setDurationInSecs(int durationInSecs) {
        this.durationInSecs = durationInSecs;
    }

    public List<String> getEncodedPolylinePoints() {
        return encodedPolylinePoints;
    }

    public void setEncodedPolylinePoints(List<String> encodedPolylinePoints) {
        this.encodedPolylinePoints = encodedPolylinePoints;
    }

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
            itinerary = new Itinerary(busRoutes, legsPoints, depBusStop, startTime, endTime, duration);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itinerary;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Routes: ");
        for (int i = 0; i < busRoutes.size(); i++) {
            if (i != 0) sb.append(",");
            sb.append(busRoutes.get(i));
        }
        sb.append("\n");
        sb.append("Departure Bus Stop: ");
        sb.append(departureBusStop);
        sb.append("\n");
        sb.append("Start Time: ");
        sb.append(departureTime);
        sb.append("\n");
        sb.append("End Time: ");
        sb.append(arrivalTime);
        sb.append("\n");
        sb.append("Duration (in secs): ");
        sb.append(durationInSecs);
        return sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringList(busRoutes);
        parcel.writeStringList(encodedPolylinePoints);
        parcel.writeString(departureBusStop);
        parcel.writeSerializable(departureTime);
        parcel.writeSerializable(arrivalTime);
        parcel.writeInt(durationInSecs);
    }
}
