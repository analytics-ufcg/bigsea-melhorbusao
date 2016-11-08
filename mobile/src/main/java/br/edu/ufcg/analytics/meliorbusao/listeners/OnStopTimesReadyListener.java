package br.edu.ufcg.analytics.meliorbusao.listeners;

import com.parse.ParseException;
import java.util.List;

import br.edu.ufcg.analytics.meliorbusao.models.StopHeadsign;
import br.edu.ufcg.analytics.meliorbusao.models.StopTime;

public interface OnStopTimesReadyListener {
    void onStopTimesReady(List<StopTime> stopTimes, ParseException e);
    void onStopHeadsignReady(StopHeadsign stopHeadsignObj, ParseException e);

    void onStopTimesReady(List<StopTime> stopTimes);
}

