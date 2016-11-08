package br.edu.ufcg.analytics.meliorbusao.listeners;

import com.parse.ParseException;

import java.util.ArrayList;

import br.edu.ufcg.analytics.meliorbusao.models.SumarioRota;

public interface OnSumarioRotasReadyListener {
    void onSumarioRotasReady(ArrayList<SumarioRota> rotas, ParseException e);
}
