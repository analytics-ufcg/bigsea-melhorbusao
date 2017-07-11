package br.edu.ufcg.analytics.meliorbusao;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import br.edu.ufcg.analytics.meliorbusao.activities.BigseaLoginActivity;
import br.edu.ufcg.analytics.meliorbusao.activities.MelhorBusaoActivity;
import br.edu.ufcg.analytics.meliorbusao.listeners.BigseaLoginListener;
import br.edu.ufcg.analytics.meliorbusao.utils.SharedPreferencesUtils;

/**
 * Created by Rafaelle on 04/07/2017.
 */

public class EndpointCheckoutDataBigsea extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = "ENDPOINT_CHECKOUT_DATA";

    private final String ENDPOINT_ADDRESS;
    private final String token;
    private BigseaLoginListener mListener;

    private Object mAuthTask;
    private boolean logout;

    public EndpointCheckoutDataBigsea(BigseaLoginListener mListener, String token) {
        ENDPOINT_ADDRESS = "https://eubrabigsea.dei.uc.pt/engine/api/checkout_data";
        this.token= token;
        this.mListener = mListener;
    }

    @SuppressLint("LongLogTag")
    @Override
    protected Boolean doInBackground(Void... params) {

        URL url;

        String parameters = "token=" + token;

        try {
            url = new URL(ENDPOINT_ADDRESS);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(parameters);
            writer.flush();
            writer.close();
            os.close();
            conn.connect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logout = true;
        return true;
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onPostExecute(Boolean success) {
        Log.d(TAG, "Logout");
        mListener.OnCheckoutData(logout);
    }

    @Override
    protected void onCancelled() {
        mAuthTask = null;
    }

}
