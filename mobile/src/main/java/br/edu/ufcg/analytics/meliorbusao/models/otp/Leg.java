package br.edu.ufcg.analytics.meliorbusao.models.otp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Leg implements Parcelable {

    public static final String TAG = "Leg";

    private String mode;
    private String busRoute;
    private List<String> encodedPolylinePoints;
    private String departureBusStop;
    private Date departureTime;
    private Date arrivalTime;
    //leg = new Leg(busRoute, legsPoints, depBusStop, startTime, endTime, );



    public Leg(String busRoute, List<String> encodedPolylinePoints, String departureBusStop, Date departureTime, Date arrivalTime, String mode) {
        this.busRoute = busRoute;
        this.encodedPolylinePoints = encodedPolylinePoints;
        this.departureBusStop = departureBusStop;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.mode = mode;
    }

    protected Leg(Parcel in) {
        busRoute = in.readString();
        encodedPolylinePoints = new ArrayList<>();
        in.readStringList(encodedPolylinePoints);
        departureBusStop = in.readString();
        departureTime = (Date) in.readSerializable();
        arrivalTime = (Date) in.readSerializable();
        mode = in.readString();
    }

    public static final Parcelable.Creator<Leg> CREATOR = new Parcelable.Creator<Leg>() {
        @Override
        public Leg createFromParcel(Parcel in) {
            return new Leg(in);
        }

        @Override
        public Leg[] newArray(int size) {
            return new Leg[size];
        }


    };


    public String getBusRoute() {
        return busRoute;
    }

    public void setBusRoute(String busRoute) {
        this.busRoute = busRoute;
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

    public List<String> getEncodedPolylinePoints() {
        return encodedPolylinePoints;
    }

    public void setEncodedPolylinePoints(List<String> encodedPolylinePoints) {
        this.encodedPolylinePoints = encodedPolylinePoints;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Route: ");
        sb.append(busRoute);
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
        sb.append("Mode: ");
        sb.append(mode);
        return sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(busRoute);
        parcel.writeStringList(encodedPolylinePoints);
        parcel.writeString(departureBusStop);
        parcel.writeSerializable(departureTime);
        parcel.writeSerializable(arrivalTime);
        parcel.writeString(mode);
    }
}
