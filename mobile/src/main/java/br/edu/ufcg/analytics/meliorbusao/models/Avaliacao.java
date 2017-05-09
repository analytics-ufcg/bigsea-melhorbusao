package br.edu.ufcg.analytics.meliorbusao.models;


import com.parse.ParseObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Avaliacao {
    private ArrayList<Resposta> respostas;
    private long timestamp;
    private String rota;


    public Avaliacao(long timestamp, String rota) {
        setTimestamp(timestamp);
        setRota(rota);
        setRespostas(new ArrayList<>());
    }

    /**
     * Adiciona ao objeto uma resposta feita
     * @param resposta
     * @return O objeto completo Avaliação
     */
    public Avaliacao addResposta(Resposta resposta){
        getRespostas().add(resposta);
        return this;
    }


    // Alterações nessa lista refletem na Avaliação
    // Collections.unmodifiableList()

    /**
     *
     * @return As respostas da avaliação
     */
    public ArrayList<Resposta> getRespostas() {
        return respostas;
    }

    /**
     * Atualiza o array 'respostas'
     * @param respostas
     */
    private void setRespostas(ArrayList respostas) {
        this.respostas = respostas;
    }

    /**
     *
     * @return O momento da avaliação
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Atualiza o parametro 'timestamp'
     * @param timestamp
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     *
     * @return A rota a qual foi avaliada
     */
    public String getRota() {
        return rota;
    }

    /**
     * Atualiza o parametro 'rota'
     * @param rota
     */
    public void setRota(String rota) {
        this.rota = rota;
    }

    /**
     *
     * @param categoria
     * @return As respostas de uma categoria
     */
    public Resposta getRespostaByCategoria(int categoria) {
        for (Resposta r : getRespostas()) {
            if (r.getCategoria() == categoria) {
                return r;
            }
        }

        return null;
    }

    /**
     * Transforma um objeto Avaliação em um objeto do parse e escreve no bd
     * @return Um objeto do parse
     */
    public ParseObject toParseObject() {
        ParseObject parseRatingObject = new ParseObject("Rating");
        parseRatingObject.put("rota", rota);
        parseRatingObject.put("tripId", String.valueOf(timestamp));

        for (Resposta r : respostas) {
            int valor = r.getValor();

            switch (r.getCategoria()) {
                case CategoriaResposta.ID_CATEGORIA_MOTORISTA:
                    parseRatingObject.put("motorista", valor == 1);
                    break;
                case CategoriaResposta.ID_CATEGORIA_LOTACAO:
                    parseRatingObject.put("lotacao", valor == 1);
                    break;
                case CategoriaResposta.ID_CATEGORIA_VIAGEM:
                    parseRatingObject.put("nota", valor);
                    break;
                case CategoriaResposta.ID_CATEGORY_CONDITION:
                    parseRatingObject.put("condition", valor == 1);
                    break;
            }
        }

        return parseRatingObject;
    }

    public String toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("tripId", String.valueOf(timestamp));
        json.put("rota", rota);

        for (Resposta r : respostas) {
            int valor = r.getValor();

            switch (r.getCategoria()) {
                case CategoriaResposta.ID_CATEGORIA_MOTORISTA:
                    json.put("motorista", valor == 1);
                    break;
                case CategoriaResposta.ID_CATEGORIA_LOTACAO:
                    json.put("lotacao", valor == 1);
                    break;
                case CategoriaResposta.ID_CATEGORIA_VIAGEM:
                    json.put("nota", valor);
                    break;
                case CategoriaResposta.ID_CATEGORY_CONDITION:
                    json.put("condition", valor == 1);
                    break;
            }
        }

        return json.toString();
    }
}


