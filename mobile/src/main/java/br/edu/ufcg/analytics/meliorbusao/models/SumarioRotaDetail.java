package br.edu.ufcg.analytics.meliorbusao.models;


public class SumarioRotaDetail {

    private SumarioRota routeSummary;


    public SumarioRotaDetail(SumarioRota routeSummary){
        this.routeSummary = routeSummary;
    }

    /**
     *
     * @param routeSummary
     */
    public void setRouteSummary(SumarioRota routeSummary) {

        this.routeSummary = routeSummary;
    }

    /**
     *
     * @return o sumário da rota
     */
    public SumarioRota getRouteSummary() {

        return routeSummary;
    }

}
