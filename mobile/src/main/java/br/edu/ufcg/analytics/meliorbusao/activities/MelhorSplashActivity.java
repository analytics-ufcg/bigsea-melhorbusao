package br.edu.ufcg.analytics.meliorbusao.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResolvingResultCallbacks;
import com.google.android.gms.common.api.Status;

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

import br.edu.ufcg.analytics.meliorbusao.Constants;
import br.edu.ufcg.analytics.meliorbusao.MeliorBusaoApplication;
import br.edu.ufcg.analytics.meliorbusao.R;
import br.edu.ufcg.analytics.meliorbusao.utils.SharedPreferencesUtils;


public class MelhorSplashActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MelhorSplashActivity";
    private static final int GOOGLE_SIGN_IN_RC = 1;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.melior_splash);
    }

    @Override
    protected void onStart() {
        super.onStart();

        String authService = SharedPreferencesUtils.getAuthService(this);

        if (authService.equals(Constants.GOOGLE_SERVICE)) {
            mGoogleApiClient = ((MeliorBusaoApplication) getApplication()).getGoogleApiClientInstance(this);
            mGoogleApiClient.registerConnectionCallbacks(this);
            mGoogleApiClient.registerConnectionFailedListener(this);
            mGoogleApiClient.connect();
        } else if (authService.equals(Constants.BIG_SEA_SERVICE)) {
            String token = SharedPreferencesUtils.getUserToken(this);
            String username = SharedPreferencesUtils.getUsername(this);
            VerifyBigSeaTokenTask task = new VerifyBigSeaTokenTask(username, token);
            task.execute();
        } else {
            launchActivity(MelhorLoginActivity.class);
        }
    }

    private void launchActivity(Class activity) {
        Intent i = new Intent(MelhorSplashActivity.this, activity);
        startActivity(i);
        finish();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        opr.setResultCallback(new ResolvingResultCallbacks<GoogleSignInResult>(this, GOOGLE_SIGN_IN_RC) {
            @Override
            public void onSuccess(@NonNull GoogleSignInResult googleSignInResult) {
                launchActivity(MelhorBusaoActivity.class);
            }

            @Override
            public void onUnresolvableFailure(@NonNull Status status) {
                launchActivity(MelhorLoginActivity.class);
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
        launchActivity(MelhorLoginActivity.class);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        launchActivity(MelhorLoginActivity.class);
    }

    private class VerifyBigSeaTokenTask extends AsyncTask<Void, Void, String> {

        private static final String ENDPOINT_ADDRESS = "https://eubrabigsea.dei.uc.pt/engine/api/verify_token";
        private String username;
        private String token;
        private String responseMessage = "";

        public VerifyBigSeaTokenTask(String username, String token) {
            this.username = username;
            this.token = token;
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
            if (username.equals(response)) {
                launchActivity(MelhorBusaoActivity.class);
            } else {
                launchActivity(MelhorLoginActivity.class);
            }
        }
    }


}