package br.edu.ufcg.analytics.meliorbusao.exceptions;

public class NoDataForCityException extends Exception {

    private static final String DEFAULT_MESSAGE = "There is no data available for the city";

    public NoDataForCityException() {
        super(DEFAULT_MESSAGE);
    }

    public NoDataForCityException(String message) {
        super(message);
    }

    public NoDataForCityException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoDataForCityException(Throwable cause) {
        super(cause);
    }
}
