package br.edu.ufcg.analytics.meliorbusao.utils;

import android.content.Context;
import android.os.AsyncTask;

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

import javax.net.ssl.HttpsURLConnection;

import br.edu.ufcg.analytics.meliorbusao.activities.MelhorBusaoActivity;
import br.edu.ufcg.analytics.meliorbusao.activities.MelhorLoginActivity;

public class VerifyBigSeaTokenTask extends AsyncTask<Void, Void, String> {

    private static final String ENDPOINT_ADDRESS = "https://eubrabigsea.dei.uc.pt/engine/api/verify_token";
    private String username;
    private String token;
    private String responseMessage = "";
    private VerifyBigSeaTokenInterface verifyBigSeaTokenInterface;

    public VerifyBigSeaTokenTask(VerifyBigSeaTokenInterface verifyBigSeaTokenInterface, Context context) {
        this.username = SharedPreferencesUtils.getUsername(context);
        this.token = SharedPreferencesUtils.getUserToken(context);
        this.verifyBigSeaTokenInterface = verifyBigSeaTokenInterface;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            String parameters = "token=" + token;
            URL url = new URL(ENDPOINT_ADDRESS);

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

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    responseMessage += line;
                }
                JSONObject responseJSON = new JSONObject(responseMessage);
                String response = responseJSON.getString("response");
                return response;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String response) {
        if (verifyBigSeaTokenInterface != null) {
            if (username != null && username.equals(response)) {
                verifyBigSeaTokenInterface.onValidationDone(true);
            } else {
                verifyBigSeaTokenInterface.onValidationDone(false);
            }
        }
    }

    public interface VerifyBigSeaTokenInterface {
        void onValidationDone(Boolean isTokenValid);
    }
}
