package br.edu.ufcg.analytics.meliorbusao.models;

public enum CategoriaResposta {
    MOTORISTA(1,"0,1"),LOTACAO(2,"0,1"),VIAGEM(3,"0...5"),CONDITION(4,"0,1");

    public static final int ID_CATEGORIA_MOTORISTA = 1;
    public static final int ID_CATEGORIA_LOTACAO = 2;
    public static final int ID_CATEGORIA_VIAGEM = 3;
    public static final int ID_CATEGORY_CONDITION = 4;

    public int idCategoria;
    public String range;

    CategoriaResposta(int idCategoria, String range) {
        this.idCategoria = idCategoria;
        this.range = range;
    }

    /**
     *
     * @return O id da categoria a ser avaliada
     */
    public int getIdCategoria() {
        return idCategoria;
    }
}
