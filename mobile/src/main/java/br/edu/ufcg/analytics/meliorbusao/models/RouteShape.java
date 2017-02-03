package br.edu.ufcg.analytics.meliorbusao.models;

import com.google.android.gms.maps.model.LatLng;

import org.osmdroid.util.GeoPoint;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;

public class RouteShape extends ArrayList<GeoPoint> {
    private String sub;
    private String routeId;
    private String color;

    private double minLat, minLng, maxLat, maxLng;

    public RouteShape(String routeId, String sub, String color) {
        super();

        setRouteId(routeId);
        setSub(sub);
        setColor(color);

        minLat = Double.POSITIVE_INFINITY;
        minLng = Double.POSITIVE_INFINITY;
        maxLat = Double.NEGATIVE_INFINITY;
        maxLng = Double.NEGATIVE_INFINITY;
    }

    public RouteShape(String routeId, String sub) {
        this(routeId, sub, null);
    }

    /**
     *
     * @param sub
     */
    public void setSub(String sub) {
        this.sub = sub;
    }

    /**
     *
     * @return
     */
    public String getSub() {
        return this.sub;
    }

    /**
     *
     * @return O id da rota
     */
    public String getRouteId() {
        return routeId;
    }

    /**
     * Atualiza o parametro 'routeId'
     * @param routeId
     */
    public void setRouteId(String routeId) {
        if (routeId == null) throw new InvalidParameterException("ID da rota não pode ser null");
        this.routeId = routeId;
    }

    /**
     *
     * @return
     */
    public boolean hasSub() {
        return sub != null;
    }

    /**
     * Avalia se os objetos Route shape são iguais
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        RouteShape latLngs = (RouteShape) o;

        if (sub != null ? !sub.equals(latLngs.sub) : latLngs.sub != null) return false;
        if (!routeId.equals(latLngs.routeId)) return false;
        return !(color != null ? !color.equals(latLngs.color) : latLngs.color != null);
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (sub != null ? sub.hashCode() : 0);
        result = 31 * result + routeId.hashCode();
        result = 31 * result + (color != null ? color.hashCode() : 0);
        return result;
    }

    /**
     *
     * @return A cor da rota
     */
    public String getColor() {
        return color;
    }


    /**
     * Atualiza o parametro 'color'
     * @param color
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Adiciona um ponto de latitude e longitude na rota
     * @param object
     * @return
     */
    @Override
    public boolean add(GeoPoint object) {
        updateEdges(object);
        return super.add(object);
    }

    /**
     * Adiciona um ponto de latitude e longitude na rota num índice especifico
     * @param index
     * @param object
     */
    @Override
    public void add(int index, GeoPoint object) {
        updateEdges(object);
        super.add(index, object);
    }

    /**
     * Adiciona uma coleção de pontos de latitude e longitude na rota
     * @param collection
     * @return
     */
    @Override
    public boolean addAll(Collection<? extends GeoPoint> collection) {
        throw new UnsupportedOperationException();
    }

    /**
     * Adiciona uma coleção de pontos de latitude e longitude na rota num índice especifico
     * @param index
     * @param collection
     * @return
     */
    @Override
    public boolean addAll(int index, Collection<? extends GeoPoint> collection) {
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @return Arestas da rota
     */
    public LatLng[] edges() {
        LatLng[] edges = {
                new LatLng(minLat, minLng),
                new LatLng(maxLat, minLng),
                new LatLng(minLat, maxLng),
                new LatLng(maxLat, maxLng)
        };

        return edges;
    }

    /**
     * atualiza as arestas da rota
     * @param object
     */
    private void updateEdges(GeoPoint object) {
        if (object.getLatitude() > maxLat) maxLat = object.getLongitude();
        if (object.getLatitude() < minLat) minLat = object.getLongitude();
        if (object.getLatitude() > maxLng) maxLng = object.getLongitude();
        if (object.getLatitude() < minLng) minLng = object.getLongitude();
    }

}
