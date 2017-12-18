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

    private List<Leg> legs;
    private String departureBusStop;
    private Date departureTime;
    private Date arrivalTime;
    private int durationInSecs;

    public Itinerary(List<Leg> legs,String departureBusStop, Date departureTime, Date arrivalTime,int durationInSecs) {

        this.legs = legs;
        this.departureBusStop = departureBusStop;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.durationInSecs = durationInSecs;
    }

    protected Itinerary(Parcel in) {

        legs = new ArrayList<Leg>();
        in.readList(legs,Leg.class.getClassLoader());
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


    public List<Leg> getLegs() {
        return legs;
    }

    public void setLegs(List<Leg> legs) {
        this.legs = legs;
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


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Routes: ");
        if (!legs.isEmpty()){
            for (Leg l: legs ) {
                sb.append(",");
                sb.append(l.getBusRoute());
            }
            sb.deleteCharAt(0);
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
        parcel.readList(legs, Leg.class.getClassLoader());
        parcel.writeString(departureBusStop);
        parcel.writeSerializable(departureTime);
        parcel.writeSerializable(arrivalTime);
        parcel.writeInt(durationInSecs);
    }

}
