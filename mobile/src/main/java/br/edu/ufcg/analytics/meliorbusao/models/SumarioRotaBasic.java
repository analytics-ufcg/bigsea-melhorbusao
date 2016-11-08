package br.edu.ufcg.analytics.meliorbusao.models;

import com.bignerdranch.expandablerecyclerview.Model.ParentObject;

import java.util.List;


public class SumarioRotaBasic implements ParentObject{

    private List<Object> mChildrenList;
    private SumarioRota routeSummary;


    public SumarioRotaBasic(SumarioRota routeSummary){
        this.routeSummary = routeSummary;
    }

    /**
     * Atualiza o objeto 'routeSummary' de SumarioRota
     * @param routeSummary
     */
    public void setRouteSummary(SumarioRota routeSummary) {
        this.routeSummary = routeSummary;
    }

    /**
     *
     * @return Um objeto sumario rota
     */
    public SumarioRota getRouteSummary() {
        return routeSummary;
    }


    @Override
    public List<Object> getChildObjectList() {
        return mChildrenList;
    }

    @Override
    public void setChildObjectList(List<Object> list) {
        mChildrenList = list;
    }


}
