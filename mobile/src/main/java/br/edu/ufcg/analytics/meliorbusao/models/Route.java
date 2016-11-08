package br.edu.ufcg.analytics.meliorbusao.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Route implements Parcelable, Comparable {
    private String id;
    private String shortName;
    private String longName;
    private String color;
    private String lineName;
    private String mainStops;

    public Route(String id, String shortName, String longName) {
        setId(id);
        setShortName(shortName);
        setLongName(longName);
        setColor("");
    }

    public Route(String id, String shortName, String longName, String color) {
        this(id, shortName, longName);
        setColor(color);
    }

    public Route(String id, String shortName, String longName, String color, String lineName) {
        this(id, shortName, longName, color);
        setLineName(lineName);
    }

    public Route(String id, String shortName, String longName, String color, String lineName,
                 String mainStops) {
        this(id, shortName, longName, color);
        setLineName(lineName);
        setMainStops(mainStops);
    }

    public Route(Parcel in) {
        this.id = in.readString();
        this.shortName = in.readString();
        this.longName = in.readString();
        this.color = in.readString();
    }

    /**
     * Para enviar um objeto de uma classe para outra
     */
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Route createFromParcel(Parcel source) {
            return new Route(source);
        }

        @Override
        public Route[] newArray(int size) {
            return new Route[size];
        }
    };

    /**
     *
     * @return
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Constroi o objeto desconstruido na passagem de uma classe pra outra
     * @param dest
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(shortName);
        dest.writeString(longName);
        dest.writeString(color);
    }

    /**
     *
     * @return O id da rota
     */
    public String getId() {
        return id;
    }

    /**
     * Atualiza variavel 'id'
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return O short name da rota
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Atualiza variavel 'shortName'
     * @param shortName
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     *
     * @return O long name da rota
     */
    public String getLongName() {
        return longName;
    }

    /**
     * Atualiza variavel 'longName'
     * @param longName
     */
    public void setLongName(String longName) {
        this.longName = longName;
    }

    /**
     *
     * @return A cor da rota
     */
    public String getColor() {
        return color;
    }

    /**
     * Atualiza variavel 'color'
     * @param color
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     *
     * @return O nome da linha
     */
    public String getLineName() {
        return lineName;
    }

    /**
     * Atualiza variavel 'lineName'
     * @param lineName
     */
    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    /**
     *
     * @return As paradas principais da rota
     */
    public String getMainStops() {
        return mainStops;
    }

    /**
     * Atualiza as paradas principais da rota
     * @param mainStops
     */
    public void setMainStops(String mainStops) {
        this.mainStops = mainStops;
    }

    /**
     * Verifica se uma rota é igual a outra
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Route route = (Route) o;

        return getId().equals(route.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    /**
     *
     * @return O id da rota
     */
    @Override
    public String toString() {
        return getId();
    }

    /**
     * Verifica se uma rota é igual a outra
     * @param another
     * @return
     */
    @Override
    public int compareTo(Object another) {
        if (another instanceof Route) {
            Route anotherRoute = (Route)another;
            return this.getShortName().compareTo(anotherRoute.getShortName());
        }
        return 0;
    }
}
