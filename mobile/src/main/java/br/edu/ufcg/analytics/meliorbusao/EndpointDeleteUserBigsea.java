package br.edu.ufcg.analytics.meliorbusao;

import android.os.AsyncTask;

import br.edu.ufcg.analytics.meliorbusao.listeners.BigseaLoginListener;

/**
 * Created by rafaelle on 17/07/17.
 */

public class EndpointDeleteUserBigsea  extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "ENDPOINT_DELETE_USER";

    private final String ENDPOINT_ADDRESS;
    private final String token;

    private Object mAuthTask;
    private boolean logout;

    public EndpointDeleteUserBigsea(String token, String endpoint) {
        ENDPOINT_ADDRESS = endpoint;
        this.token= token;
    }


    @Override
    protected Boolean doInBackground(Void... voids) {
        return null;
    }
}
