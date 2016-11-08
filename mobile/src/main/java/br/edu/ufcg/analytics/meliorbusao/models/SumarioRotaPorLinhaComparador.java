package br.edu.ufcg.analytics.meliorbusao.models;

import java.util.Comparator;

public class SumarioRotaPorLinhaComparador implements Comparator<SumarioRota> {

    /**
     * Compara as rotas por linha (cor) - para agrupar as rotas por cor
     * @param sumarioRota1
     * @param sumarioRota2
     * @return
     */
    @Override
    public int compare(SumarioRota sumarioRota1, SumarioRota sumarioRota2) {
        int colorComparator = sumarioRota1.getRota().getColor().compareTo(sumarioRota2.getRota().getColor());
        if (colorComparator != 0) {
            return colorComparator;
        }else {
            return sumarioRota1.compareTo(sumarioRota2);
        }

    }
}
