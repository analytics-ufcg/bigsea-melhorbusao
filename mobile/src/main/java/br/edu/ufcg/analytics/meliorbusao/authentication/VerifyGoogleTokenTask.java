package br.edu.ufcg.analytics.meliorbusao.authentication;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import br.edu.ufcg.analytics.meliorbusao.utils.SharedPreferencesUtils;

/**
 * Class that represents the task (run in background) of verifying if a Google Token is valid.
 */
public class VerifyGoogleTokenTask extends AsyncTask<Void, Void, String> {

    public static final String TAG = "VerifyGoogleTokenTask";
    private static final String ENDPOINT = "https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=";
    private String token;
    private TokenValidationListener mListener;

    public VerifyGoogleTokenTask(Context context, TokenValidationListener listener) {
        token = SharedPreferencesUtils.getUserToken(context);
        mListener = listener;
    }

    @Override
    protected String doInBackground(Void... params) {
        InputStream stream = null;
        HttpsURLConnection connection = null;
        String result = null;

        try {
            URL url = new URL(ENDPOINT + token);
            connection = (HttpsURLConnection) url.openConnection();

            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                Log.e(TAG, "HTTP error code: " + responseCode);
                return null;
            }

            stream = connection.getInputStream();
            if (stream != null) {
                result = readStream(stream);
            }
        } catch (Exception e) {
            Log.d(TAG, "doInBackground: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    Log.d(TAG, "doInBackground: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(String response) {
        if (response != null) {
            mListener.OnValidationCompleted(true);
        } else {
            mListener.OnValidationCompleted(false);
        }
    }

    /**
     * Converts the contents of an InputStream to a String.
     */
    private String readStream(InputStream stream) throws IOException {
        String line;
        String responseMessage = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        while ((line = br.readLine()) != null) {
            responseMessage += line;
        }
        return responseMessage;
    }
}
