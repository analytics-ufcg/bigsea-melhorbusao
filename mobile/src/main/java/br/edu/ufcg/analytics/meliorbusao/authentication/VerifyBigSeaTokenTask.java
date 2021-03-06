package br.edu.ufcg.analytics.meliorbusao.authentication;

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

import br.edu.ufcg.analytics.meliorbusao.R;
import br.edu.ufcg.analytics.meliorbusao.utils.SharedPreferencesUtils;

/**
 * Class that represents the task (run in background) of verifying if a BigSea Token is valid.
 */
public class VerifyBigSeaTokenTask extends AsyncTask<Void, Void, String> {

    private String endpoint_address;
    private String username;
    private String token;
    private String responseMessage = "";
    private TokenValidationListener mListener;

    public VerifyBigSeaTokenTask(Context context, TokenValidationListener listener) {
        username = SharedPreferencesUtils.getUsername(context);
        token = SharedPreferencesUtils.getUserToken(context);
        mListener = listener;
        if (context !=  null) {
            endpoint_address = context.getResources().getString(R.string.BIG_SEA_AUTH_VERIFY_TOKEN_ENDPOINT);
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            String parameters = "token=" + token;
            URL url = new URL(endpoint_address);

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
        if (mListener != null) {
            if (username != null && username.equals(response)) {
                mListener.OnValidationCompleted(true);
            } else {
                mListener.OnValidationCompleted(false);
            }
        }
    }
}
