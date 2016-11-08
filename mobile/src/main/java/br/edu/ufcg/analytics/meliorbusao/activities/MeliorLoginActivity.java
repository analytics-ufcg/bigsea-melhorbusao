package br.edu.ufcg.analytics.meliorbusao.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.testfairy.TestFairy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.edu.ufcg.analytics.meliorbusao.MeliorBusaoApplication;
import br.edu.ufcg.analytics.meliorbusao.R;

public class MeliorLoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    public static final String TAG = "MeliorLoginActivity";

    private Button loginBtn;

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;
    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;
    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;
    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;

    private static final int PERMISSION_ALL = 12345;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_melior_login);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(
                    MeliorLoginActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_ALL
            );
            return;
        }


        TestFairy.begin(this, getString(R.string.TEST_FAIRY_API_KEY));

        loginBtn = (Button) findViewById(R.id.login_button);

        loginBtn.setOnClickListener(new View.OnClickListener()

                                    {
                                        @Override
                                        public void onClick(View v) {
                                            onSignInClicked();
                                        }
                                    }

        );

        mGoogleApiClient = ((MeliorBusaoApplication) getApplication()).getGooglePlusApiClientInstance();

        mGoogleApiClient.registerConnectionCallbacks(this);
        mGoogleApiClient.registerConnectionFailedListener(this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);

        if (requestCode == RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }
            mIsResolving = false;
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Could not resolve ConnectionResult.", e);
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            } else {
                // Could not resolve the connection result, show the user an error dialog.
                Toast.makeText(this, getString(R.string.msg_unable_to_connect), Toast.LENGTH_LONG);
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        // onConnected indicates that an account was selected on the device, that the selected
        // account has granted any requested permissions to our app and that we were able to
        // establish a service connection to Google Play services.
        Log.d(TAG, "onConnected:" + bundle);
        mShouldResolve = false;

        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            Intent intent = new Intent(getApplicationContext(), MeliorBusaoActivity.class);
            Log.d(TAG, "connected: loading MeliorBusaoActivity");
            startActivity(intent);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost. The GoogleApiClient will automatically
        // attempt to re-connect. Any UI elements that depend on connection to Google APIs should
        // be hidden or disabled until onConnected is called again.
        Log.w(TAG, "onConnectionSuspended:" + i);
    }

    /**
     * Serviço de login do Google
     */
    private void onSignInClicked() {
        // User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.
        Log.d(TAG, "onSignInClicked");
        mShouldResolve = true;
        mGoogleApiClient.connect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        System.out.println(requestCode + "..." + grantResults[0]);

        switch (requestCode) {

            case PERMISSION_ALL: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            Log.d(TAG, "Permission Granted!");
                        } else {
                            Log.i(TAG, "Permission denied.");
                        }
                    }
                } else {
                    Log.i(TAG, "Error on grant results");

                }
            }
        }
    }

}
