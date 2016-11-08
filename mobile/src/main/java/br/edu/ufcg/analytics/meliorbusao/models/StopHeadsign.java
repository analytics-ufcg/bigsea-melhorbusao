package br.edu.ufcg.analytics.meliorbusao.models;

public class StopHeadsign {

    private Route route;
    StopTime stopTime;
    NearStop nearStops;

    /**
     * As informações dessa classe existem separadamente, entretanto nessa classe
     * concentramos essas informações
     * @param route
     * @param stopTime
     * @param nearStops
     */
    public StopHeadsign(Route route, StopTime stopTime, NearStop nearStops){
        this.nearStops = nearStops;
        this.stopTime = stopTime;
        this.route = route;
    }


    /**
     *
     * @return A rota
     */
    public Route getRoute() {
        return route;
    }

    /**
     *
     * @return O objeto stop time
     */
    public StopTime getStopTime() {
        return stopTime;
    }

    /**
     * Modifica o parametro 'stopTime'
     * @param stopTime
     */
    public void setStopTime(StopTime stopTime) {
        this.stopTime = stopTime;
    }

    /**
     *
     * @return O objeto paradas próximas
     */
    public NearStop getNearStops() {
        return nearStops;
    }

    /**
     * Modifica o objeto 'stopTime'
     * @param nearStops
     */
    public void setNearStops(NearStop nearStops) {
        this.nearStops = nearStops;
    }
}
