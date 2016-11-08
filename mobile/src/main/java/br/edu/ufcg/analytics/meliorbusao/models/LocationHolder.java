package br.edu.ufcg.analytics.meliorbusao.models;

import android.location.Location;

import com.parse.ParseObject;

import java.util.Calendar;

import br.edu.ufcg.analytics.meliorbusao.models.LocationData;

public class LocationHolder implements LocationData {
    private long time;
    private Location location;
    private String tripId;

    public LocationHolder(Location location) {
        this.location = location;
        this.time = Calendar.getInstance().getTimeInMillis();
    }

    /**
     *
     * @return O objeto localização
     */
    public Location getLocation() {
        return location;
    }

    /**
     *
     * @return A latitude da location
     */
    @Override
    public double getLatitude() {
        return getLocation().getLatitude();
    }

    /**
     *
     * @return A longitude da location
     */
    @Override
    public double getLongitude() {
        return getLocation().getLongitude();
    }

    /**
     *
     * @return A acurácia do gps
     */
    @Override
    public float getAccuracy() {
        return getLocation().getAccuracy();
    }

    /**
     *
     * @return A velocidade do bus
     */
    @Override
    public float getSpeed() {
        return getLocation().getSpeed();
    }

    /**
     *
     * @return
     */
    @Override
    public float getBearing() {
        return getLocation().getBearing();
    }

    /**
     *
     * @return Timestamp da coleta de latitude e longitude
     */
    @Override
    public long getTime() {
        return time;
    }

    /**
     *
     * @return O id da viagem realizada
     */
    public String getTripId() {
        return tripId;
    }

    /**
     * Atualiza o parametro 'tripId'
     * @param tripId
     */
    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    /**
     * Transforma em objeto do Parse e insere no bd
     * @param userId
     * @param tripId
     * @return
     */
    public ParseObject toParseObject(String userId, String tripId) {
        ParseObject parseLocationObject = new ParseObject("UserLocation");
        parseLocationObject.put("latitude", getLatitude());
        parseLocationObject.put("longitude", getLongitude());
        parseLocationObject.put("accuracy", getAccuracy());
        parseLocationObject.put("speed", getSpeed());
        parseLocationObject.put("bearing", getBearing());
        parseLocationObject.put("time", getTime());
        parseLocationObject.put("userId", userId);
        parseLocationObject.put("tripId", tripId);

        return parseLocationObject;
    }


}
