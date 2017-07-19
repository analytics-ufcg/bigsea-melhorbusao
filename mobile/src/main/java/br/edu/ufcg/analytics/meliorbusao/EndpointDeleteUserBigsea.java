package br.edu.ufcg.analytics.meliorbusao;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

import br.edu.ufcg.analytics.meliorbusao.listeners.BigseaLoginListener;

/**
 * Created by rafaelle on 17/07/17.
 */

public class EndpointDeleteUserBigsea  extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "ENDPOINT_DELETE_USER";

    private final String ENDPOINT_ADDRESS;
    private final String token;
    private final String userName;

    private Object mAuthTask;
    private boolean isSuccessfulRegister;

    public EndpointDeleteUserBigsea(String token, String endpoint, String userName) {
        ENDPOINT_ADDRESS = endpoint;
        this.token= token;
        this.userName = userName;
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        URL url;

        String parameters = "user=" + userName + "&token=" + token;

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

            int responseCode = conn.getResponseCode();
            Log.v(TAG , "RESPONSE_CODE" + conn.getResponseMessage());

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line = "";
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String responseMessage = "";
                while ((line = br.readLine()) != null) {
                    responseMessage += line;
                }
                JSONObject jsonObject = new JSONObject(responseMessage);
                Log.d(TAG, jsonObject.toString());

                isSuccessfulRegister = false;
                Iterator<String> keys = jsonObject.keys();
                while( keys.hasNext() ) {
                    String key = (String)keys.next();
                    if (key.equals("success")){
                        isSuccessfulRegister = jsonObject.getString("success").contains("success") ;
                    }
                }

                conn.disconnect();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onPostExecute(Boolean success) {
        Log.d(TAG, "Logout");
        //mListener.OnDeleteUser(isSuccessfulRegister);
    }

    @Override
    protected void onCancelled() {
        mAuthTask = null;
    }
}
