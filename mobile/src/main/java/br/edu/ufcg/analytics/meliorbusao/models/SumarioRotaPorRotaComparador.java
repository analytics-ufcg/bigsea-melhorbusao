package br.edu.ufcg.analytics.meliorbusao.models;

import java.util.Comparator;

public class SumarioRotaPorRotaComparador implements Comparator<SumarioRota> {

    /**
     *
     * @param sumarioRota1
     * @param sumarioRota2
     * @return A comparação dos sumários pela avaliação geral
     */
    @Override
    public int compare(SumarioRota sumarioRota1, SumarioRota sumarioRota2) {
        return sumarioRota1.compareTo(sumarioRota2);
    }
}
