package br.edu.ufcg.analytics.meliorbusao.models;


import java.text.SimpleDateFormat;
import java.util.Date;

public class StopTime implements Comparable<StopTime> {
    private int id;
    private String serviceId;
    private String routeId;
    private int stopId;
    private Date arrivalTime;
    private String stopHeadsign;

    private Date arrivalTimeBefore;
    private Date arrivalTimeAfter;


    public StopTime(String serviceId, String routeId, int stopId, Date arrivalTime, String stopHeadsign) {
        this.serviceId = serviceId;
        this.routeId = routeId;
        this.stopId = stopId;
        this.arrivalTime = arrivalTime;
        this.stopHeadsign = stopHeadsign;

    }

    public StopTime(int id, String routeId, String serviceId, int stopId, Date scheduleMedia, Date scheduleBefore, Date scheduleAfter) {
        this.id = id;
        this.routeId =routeId;
        this.serviceId=serviceId;
        this.stopId=stopId;
        this.arrivalTime=scheduleMedia;
        this.arrivalTimeBefore=scheduleBefore;
        this.arrivalTimeAfter=scheduleAfter;
    }

    public StopTime(String routeId, String serviceId, int stopId, Date scheduleMedia, Date scheduleBefore, Date scheduleAfter) {
        this.routeId =routeId;
        this.serviceId=serviceId;
        this.stopId=stopId;
        this.arrivalTime=scheduleMedia;
        this.arrivalTimeBefore=scheduleBefore;
        this.arrivalTimeAfter=scheduleAfter;

    }



    /**
     *
     * @return Id do objeto stop time (parada + horario + descrição ....)
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
     * @return
     */
    public String getServiceId() {
        return serviceId;
    }

    /**
     *
     * @param serviceId
     */
    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }


    /**
     *
     * @return Id da rota
     */
    public String getRouteId() {
        return routeId;
    }

    /**
     *  Modifica o parametro 'routeId'
     * @param routeId
     */
    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    /**
     *
     * @return Id da parada
     */
    public int getStopId() {
        return stopId;
    }

    /**
     * Modifica o parametro 'stopId'
     * @param stopId
     */
    public void setStopId(int stopId) {
        this.stopId = stopId;
    }

    /**
     *
     * @return Horário médio de chegada na parada
     */
    public Date getArrivalTime() {
        return arrivalTime;
    }

    /**
     * Modifica o parametro 'arrivalTime'
     * @param arrivalTime
     */
    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    /**
     *
     * @return A descrição do objeto stop time
     */
    public String getStopHeadsign() {
        return stopHeadsign;
    }

    /**
     * Modifica o parametro 'stopHeadsign'
     * @param stopHeadsign
     */
    public void setStopHeadsign(String stopHeadsign) {
        this.stopHeadsign = stopHeadsign;
    }

    /**
     *
     * @return Horário do limite inferior de chegada na parada
     */
    public Date getArrivalTimeBefore() {
        return arrivalTimeBefore;
    }


    /**
     * Modifica o parametro 'arrivalTimeBefore'
     * @param arrivalTimeBefore
     */
    public void setArrivalTimeBefore(Date arrivalTimeBefore) {
        this.arrivalTimeBefore = arrivalTimeBefore;
    }

    /**
     *
     * @return Horário do limite superior de chegada na parada
     */
    public Date getArrivalTimeAfter() {
        return arrivalTimeAfter;
    }


    /**
     * Modifica o parametro 'arrivalTimeAfter'
     * @param arrivalTimeAfter
     */
    public void setArrivalTimeAfter(Date arrivalTimeAfter) {
        this.arrivalTimeAfter = arrivalTimeAfter;
    }

    /**
     *
     * @param another
     * @return
     */
    @Override
    public int compareTo(StopTime another) {
        return getArrivalTime().compareTo(another.getArrivalTime());
    }

    /**
     *  Verifica se os objetos stop time são iguais
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof StopTime))
            return false;

        StopTime stopTime = (StopTime) obj;

        return  (this.toString().equals(stopTime.toString()));
    }

    /**
     *
     * @return O horário de chegada convertido pra string
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm");

        sb.append(sdfDate.format(arrivalTime));

        String scheduleAfter = sdfDate.format(arrivalTimeAfter);
        String scheduleBefore = sdfDate.format(arrivalTimeBefore);

        return  /*sb.toString() + " " + */scheduleBefore + " - " + scheduleAfter;
    }
}
