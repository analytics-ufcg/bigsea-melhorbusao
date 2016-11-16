package br.edu.ufcg.analytics.meliorbusao.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;

import br.edu.ufcg.analytics.meliorbusao.MeliorBusaoApplication;
import br.edu.ufcg.analytics.meliorbusao.R;


public class MeliorSplashActivity extends AppCompatActivity {

    /**
     * The splash time out.
     */
    private static final int SPLASH_TIME_OUT = 2500;

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.melior_splash);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isLocationEnabled()) {

            mGoogleApiClient = ((MeliorBusaoApplication) getApplication()).getGoogleApiClientInstance(this);
            mGoogleApiClient.connect();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    final Intent i = new Intent(MeliorSplashActivity.this,
                            getLaunchingActivity());
                    startActivity(i);
                    finish();
                }
            }, SPLASH_TIME_OUT);
        } else {
            buildAlertMessageNoGps();
        }
    }

    public Class getLaunchingActivity(){
        Class activityToLauch = MelhorLoginActivity.class;

        if (mGoogleApiClient.isConnected()){
            OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
            if (opr.isDone()){
                GoogleSignInResult result = opr.get();
                if (result.getSignInAccount() != null){
                    activityToLauch = MeliorBusaoActivity.class;
                }

            }
        }

        return activityToLauch;
    }
    /*
    public Class getLaunchingActivity() {
        Class activityToLaunch = MelhorLoginActivity.class;
        if (mGoogleApiClient.isConnected() && Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            activityToLaunch = MeliorBusaoActivity.class;
        }
        return activityToLaunch;
    } */

    public boolean isLocationEnabled() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                mlocManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    protected void buildAlertMessageNoGps() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MeliorSplashActivity.this);
        builder.setMessage(getString(R.string.msg_gps_disabled))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.msg_yes), new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(getString(R.string.msg_no), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final android.app.AlertDialog alert = builder.create();
        alert.show();
    }

}