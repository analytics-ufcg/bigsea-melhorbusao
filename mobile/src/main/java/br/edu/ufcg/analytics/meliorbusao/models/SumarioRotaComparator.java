package br.edu.ufcg.analytics.meliorbusao.models;

import java.util.Comparator;

public class SumarioRotaComparator implements Comparator<SumarioRota> {

    /**
     * Compara dois sumários de rota (total de avaliações de cada rota)
     * @param sumarioRota1
     * @param sumarioRota2
     * @return
     */
    @Override
    public int compare(SumarioRota sumarioRota1, SumarioRota sumarioRota2) {
        if ( sumarioRota1.getSumarioGeral() > sumarioRota2.getSumarioGeral()) {
            return -1;
        } if (sumarioRota1.getSumarioGeral() < sumarioRota2.getSumarioGeral())  {
            return 1;
        }
        return 0;
    }
}
