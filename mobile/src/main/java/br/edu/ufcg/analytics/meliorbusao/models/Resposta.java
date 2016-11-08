package br.edu.ufcg.analytics.meliorbusao.models;


public class Resposta {
    private int categoria;
    private int valor;

    public Resposta(int categoria, int valor) {
        setCategoria(categoria);
        setValor(valor);
    }

    /**
     *
     * @return A categoria da avaliação
     */
    public int getCategoria() {
        return categoria;
    }

    /**
     * Atualiza o valor de 'categoria'
     * @param categoria
     */
    public void setCategoria(int categoria) {
        this.categoria = categoria;
    }

    /**
     *
     * @return O valor da avaliação
     */
    public int getValor() {
        return valor;
    }

    /**
     * Atualiza o valor da avaliação
     * @param valor
     */
    public void setValor(int valor) {
        this.valor = valor;
    }
}
