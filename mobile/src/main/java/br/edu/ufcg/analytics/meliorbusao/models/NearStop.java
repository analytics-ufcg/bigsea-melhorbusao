package br.edu.ufcg.analytics.meliorbusao.models;


public class NearStop extends Stop implements Comparable<NearStop> {
    private double distance;

    public NearStop(int id, String name, String description, double latitude, double longitude) {
        super(id, name, description, latitude, longitude);
    }

    public NearStop(int id, String name, String description, double latitude, double longitude, double distance) {
        super(id, name, description, latitude, longitude);
        setDistance(distance);
    }

    /**
     *
     * @return A distância entre paradas
     */
    public double getDistance() {
        return distance;
    }


    /**
     * Atualiza o valor da 'distance' entre paradas
     * @param distance
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }

    /**
     * Compara dois objetos de paradas próximas
     * @param another
     * @return
     */
    @Override
    public int compareTo(NearStop another) {
        return Double.compare(getDistance(), another.getDistance());
    }

    /**
     *
     * @return A descrição do objeto paradas proximas
     */
    @Override
    public String toString() {
        return getDescription();
    }


}
