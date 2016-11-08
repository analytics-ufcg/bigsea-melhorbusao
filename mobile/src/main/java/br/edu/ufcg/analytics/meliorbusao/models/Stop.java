package br.edu.ufcg.analytics.meliorbusao.models;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Stop implements LocationData {
    private int id;
    private String name;
    private String description;
    private double latitude;
    private double longitude;
    private LinkedList<Route> routes;

    public Stop(int id, String name, String description, double latitude, double longitude) {
        setId(id);
        setName(name);
        setDescription(description);
        setLatitude(latitude);
        setLongitude(longitude);
        setRoutes(new LinkedList<Route>());
    }

    /**
     *
     * @return Id da parada
     */
    public int getId() {
        return id;
    }

    /**
     * Modifica o parametro 'id'
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     *
     * @return O nome da parada
     */
    public String getName() {
        return name;
    }

    /**
     * Modifica o parametro 'name' da parada
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return A descrição da parada
     */
    public String getDescription() {
        return description;
    }

    /**
     * Modifica o parametro 'description' da parada
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return A latitude da parada
     */
    @Override
    public double getLatitude() {
        return latitude;
    }

    /**
     * Modifica o parametro 'latitude' da parada
     * @param latitude
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * A longitude da parada
     * @return
     */
    @Override
    public double getLongitude() {
        return longitude;
    }

    /**
     * Modifica o parametro 'longitude' da parada
     * @param longitude
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     *
     * @return as rotas que passam naquela parada
     */
    public List<Route> getRoutes() {
        return Collections.unmodifiableList(routes);
    }

    /**
     * Modifica o objeto das rotas que passam naquela parada
     * @param routes
     */
    private void setRoutes(LinkedList<Route> routes) {
        this.routes = routes;
    }

    /**
     * Adiciona uma rota que passa naquela parada
     * @param route
     * @return
     */
    public boolean addRoute(Route route) {
        return routes.add(route);
    }

    /**
     *
     * @return precisão do gps
     */
    @Override
    public float getAccuracy() {
        return 0;
    }

    /**
     *
     * @return velocidade do busu
     */
    @Override
    public float getSpeed() {
        return 0;
    }

    /**
     *
     * @return
     */
    @Override
    public float getBearing() {
        return 0;
    }


    @Override
    public long getTime() {
        return 0;
    }

    /**
     *
     * @param o
     * @return se duas paradas são iguais
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Stop)) {
            return false;
        }

        Stop other = (Stop) o;

        return this.getId() == other.getId();

    }

    @Override
    public int hashCode() {
        return getId();
    }
}