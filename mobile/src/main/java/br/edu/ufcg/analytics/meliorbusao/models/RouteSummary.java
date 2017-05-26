package br.edu.ufcg.analytics.meliorbusao.models;

public class RouteSummary implements Comparable<RouteSummary> {

    private Route rota;

    private CategoriaResposta[] categorias;
    private int[] soma;
    private int[] quantidadeRespostas;

    private long maiorTimestamp;

    private boolean avaliada;

    public RouteSummary(Route rota) {
        setRota(rota);

        categorias = CategoriaResposta.values();
        soma = new int[categorias.length];
        quantidadeRespostas = new int[categorias.length];
        maiorTimestamp = Long.MIN_VALUE;
    }

    /**
     * Retorna a rota avaliada
     *
     * @return
     */
    public Route getRota() {
        return rota;
    }

    /**
     * Atualiza o valor de 'rota'
     *
     * @param rota
     */
    private void setRota(Route rota) {
        this.rota = rota;
    }

    /**
     * Computa a resposta de cada item: conservação, motorista e lotação
     *
     * @param resposta
     * @param timestamp
     */
    public void computarResposta(Resposta resposta, long timestamp) {
        int indiceCategoria = categoriaIndex(resposta.getCategoria());

        if (indiceCategoria == -1) {
            return;
        }

        quantidadeRespostas[indiceCategoria]++;
        soma[indiceCategoria] += resposta.getValor();
        maiorTimestamp = Math.max(timestamp, maiorTimestamp);
    }

    /**
     * Computa a resposta de cada item: conservação, motorista e lotação - de uma avaliação específica
     *
     * @param resposta
     * @param timestamp
     */
    public void computarResposta(Resposta resposta, long timestamp, int count) {
        int indiceCategoria = categoriaIndex(resposta.getCategoria());

        if (indiceCategoria == -1) {
            return;
        }

        quantidadeRespostas[indiceCategoria] = count;
        soma[indiceCategoria] += resposta.getValor();
        maiorTimestamp = Long.MAX_VALUE;
    }

    /**
     * Pega o momento de resposta da última pergunta
     *
     * @return
     */
    public long getTimestampUltimaResposta() {
        return maiorTimestamp;
    }

    /**
     * Sumário de uma categoria especificada (categoria = conservação, motorista e lotação)
     *
     * @param categoria
     * @return
     */
    public double getSumario(CategoriaResposta categoria) {
        int indiceCategoria = categoriaIndex(categoria.idCategoria);

        if (quantidadeRespostas[indiceCategoria] == 0) {
            return 0;
        }

        return (double) soma[indiceCategoria] / (double) quantidadeRespostas[indiceCategoria];
    }

    /**
     * faz o calculo do sumario da rota
     *
     * @return o numero de estrelas das rotas
     */
    public double getSumarioGeral() {

        if (!isAvaliada()) {
            return -1;
        }
        int total = 0;
        int indiceCategoria = categoriaIndex(CategoriaResposta.ID_CATEGORIA_VIAGEM);


        for (int i = 0; i < categorias.length; i++) {
            if (i != categoriaIndex(CategoriaResposta.ID_CATEGORIA_VIAGEM)) {
                total += soma[i];
            }
        }

        if (total == 0) {
            return total;
        }

        double evalValue = total / (double) ((categorias.length - 1) * quantidadeRespostas[indiceCategoria]);
        double numStars = evalValue * 5;

        return numStars;
    }

    /**
     * Retorna o índice de uma categoria
     *
     * @param idCategoria
     * @return
     */
    private int categoriaIndex(int idCategoria) {
        int indiceCategoria = -1;

        for (int i = 0; i < categorias.length; i++) {
            if (categorias[i].idCategoria == idCategoria) {
                indiceCategoria = i;
                break;
            }
        }
        return indiceCategoria;
    }

    /**
     * @return se a rota é avaliada ou não
     */
    public boolean isAvaliada() {
        return avaliada;
    }

    /**
     * altera o valor da variavel 'avaliada'
     *
     * @param avaliada
     */
    public void setAvaliada(boolean avaliada) {
        this.avaliada = avaliada;
    }

    /**
     * compara duas dois sumarios de rota
     *
     * @param another
     * @return
     */
    @Override
    public int compareTo(RouteSummary another) {

        return this.rota.compareTo(another.getRota());
    }

}

