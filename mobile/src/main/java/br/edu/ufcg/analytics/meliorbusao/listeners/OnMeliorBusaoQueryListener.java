package br.edu.ufcg.analytics.meliorbusao.listeners;

/**
 * Created by tarciso on 03/02/16.
 */
public interface OnMeliorBusaoQueryListener {

    public void onMeliorBusaoQueryChange(String query);
    public boolean onMeliorBusaoQuerySubmit(String query);

}
