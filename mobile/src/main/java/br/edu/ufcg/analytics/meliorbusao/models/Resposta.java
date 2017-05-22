package br.edu.ufcg.analytics.meliorbusao.models;


import android.os.Parcel;
import android.os.Parcelable;

public class Resposta implements Parcelable {
    private int categoria;
    private int valor;

    public Resposta(int categoria, int valor) {
        setCategoria(categoria);
        setValor(valor);
    }

    protected Resposta(Parcel in) {
        categoria = in.readInt();
        valor = in.readInt();
    }

    public static final Creator<Resposta> CREATOR = new Creator<Resposta>() {
        @Override
        public Resposta createFromParcel(Parcel in) {
            return new Resposta(in);
        }

        @Override
        public Resposta[] newArray(int size) {
            return new Resposta[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(categoria);
        dest.writeInt(valor);
    }
}
