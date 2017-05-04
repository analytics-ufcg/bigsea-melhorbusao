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
import br.edu.ufcg.analytics.meliorbusao.utils.VerifyBigSeaTokenTask;


public class MelhorSplashActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        VerifyBigSeaTokenTask.VerifyBigSeaTokenInterface {

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
            VerifyBigSeaTokenTask task = new VerifyBigSeaTokenTask(this, this);
            task.execute();
        } else {
            launchActivity(MelhorLoginActivity.class);
        }
    }

    private void launchActivity(Class activity) {
        Intent i = new Intent(MelhorSplashActivity.this, activity);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

    @Override
    public void onValidationDone(Boolean isTokenValid) {
        if (isTokenValid) {
            launchActivity(MelhorBusaoActivity.class);
        } else {
            launchActivity(MelhorLoginActivity.class);
        }
    }
}