package br.edu.ufcg.analytics.meliorbusao.authentication;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
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

public class VerifyGoogleTokenTask extends AsyncTask<Void, Void, String> {

    private static final String ENDPOINT = "https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=";
    private String token;
    private String responseMessage = "";
    private VerifyGoogleTokenListener mListener;

    public VerifyGoogleTokenTask( Context context, VerifyGoogleTokenListener listener) {
        token = SharedPreferencesUtils.getUserToken(context);
        mListener = listener;
    }

    @Override
    protected String doInBackground(Void... params) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(ENDPOINT + token);
        //TODO: the methods above are deprecated. use HttpURlConnectionInstead
//        try {
//
//            HttpClient httpClient = new DefaultHttpClient();
//            HttpPost httpPost = new HttpPost("https://yourbackend.example.com/tokensignin");
//
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("POST");
//
//            OutputStream os = conn.getOutputStream();
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
//            writer.write(parameters);
//            writer.flush();
//            writer.close();
//            os.close();
//
//            conn.connect();
//            int responseCode = conn.getResponseCode();
//
//            if (responseCode == HttpsURLConnection.HTTP_OK) {
//                String line;
//                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                while ((line = br.readLine()) != null) {
//                    responseMessage += line;
//                }
//                JSONObject responseJSON = new JSONObject(responseMessage);
//                String response = responseJSON.getString("response");
//                return response;
//            }
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        return null;
    }

    @Override
    protected void onPostExecute(String response) {

    }

    public interface VerifyGoogleTokenListener {
        void onValidationComplete(Boolean isTokenValid);
    }
}
