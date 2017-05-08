package br.edu.ufcg.analytics.meliorbusao.authentication;

/**
 * Interface For handling BigSea and google token validation.
 */
public interface TokenValidationListener {

    /**
     * Should be called once the validation is completed.
     * @param isTokenValid whether or not the token is valid.
     */
    void OnValidationCompleted(boolean isTokenValid);
}
