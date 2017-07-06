package br.edu.ufcg.analytics.meliorbusao.listeners;

/**
 * Created by rafaelle on 06/07/17.
 */

public interface BigseaLoginListener {
    void OnCheckoutData(boolean finished);
    void OnLogged(boolean logged);
}
