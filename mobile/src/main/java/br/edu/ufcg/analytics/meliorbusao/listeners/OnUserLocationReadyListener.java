package br.edu.ufcg.analytics.meliorbusao.listeners;


import com.parse.ParseException;

import java.util.ArrayList;

import br.edu.ufcg.analytics.meliorbusao.models.LocationHolder;

public interface OnUserLocationReadyListener {
    void OnUserLocationReady(ArrayList<LocationHolder> rotas, ParseException e);

}
