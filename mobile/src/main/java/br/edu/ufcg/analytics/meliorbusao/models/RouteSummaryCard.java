package br.edu.ufcg.analytics.meliorbusao.models;

import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.ArrayList;
import java.util.List;

import br.edu.ufcg.analytics.meliorbusao.fragments.MapFragment;


public class RouteSummaryCard implements Parent<SumarioRota> {

    private SumarioRota routeSummary;

    public RouteSummaryCard(SumarioRota routeSummary){
        this.routeSummary = routeSummary;
    }

    /**
     * @return Um objeto sumario rota
     */
    public SumarioRota getRouteSummary() {
        return routeSummary;
    }
    
    @Override
    public List<SumarioRota> getChildList() {
        List<SumarioRota> list = new ArrayList<>();
        list.add(routeSummary);
        return list;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }

    public void setChildObjectList(ArrayList<SumarioRota> list) {
        if (list != null && !list.isEmpty()) {
            routeSummary = list.get(0);
        }
    }
}
