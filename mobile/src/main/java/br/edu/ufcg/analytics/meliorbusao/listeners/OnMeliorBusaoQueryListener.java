package br.edu.ufcg.analytics.meliorbusao.listeners;

public interface OnMeliorBusaoQueryListener {

    public void onMeliorBusaoQueryChange(String query);
    public boolean onMeliorBusaoQuerySubmit(String query);

}
