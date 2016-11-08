package br.edu.ufcg.analytics.meliorbusao;


public enum Cities {

    CAMPINA_GRANDE("Campina Grande", "-7.236425", "-35.896936", 8000),
    CURITIBA("Curitiba", "-25.4342", "-49.2714", 12000);

    private String cityName;
    private String centralPointLatitude;
    private String centralPointLongitude;
    private int cityRadius;

    Cities(String cityName, String centralPointLatitude, String centralPointLongitude, int cityRadius) {
        this.cityName = cityName;
        this.centralPointLatitude = centralPointLatitude;
        this.centralPointLongitude = centralPointLongitude;
        this.cityRadius = cityRadius;
    }

    public String getCityName() {
        return cityName;
    }

    public String getCentralPointLatitude() {
        return centralPointLatitude;
    }

    public String getCentralPointLongitude() {
        return centralPointLongitude;
    }

    public int getCityRadius() {
        return cityRadius;
    }
}
