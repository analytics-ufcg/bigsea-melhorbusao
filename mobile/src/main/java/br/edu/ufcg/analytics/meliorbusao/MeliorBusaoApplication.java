package br.edu.ufcg.analytics.meliorbusao;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.plus.Plus;
import com.parse.Parse;
import com.testfairy.TestFairy;

import android.support.multidex.MultiDexApplication;
import android.support.v4.app.FragmentActivity;

public class MeliorBusaoApplication extends MultiDexApplication implements GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleDetectionApiClient = null;
    private GoogleApiClient mGoogleApiClient = null;


    /**
     * Adiciona as APIs necessarias para a detecção do bus
     */
    private synchronized void buildGoogleDetectionApiClient() {
        mGoogleDetectionApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     *
     * @return as APIs para a detecção do bus
     */
    public GoogleApiClient getGoogleDetectionApiClientInstance() {
        if (mGoogleDetectionApiClient == null) {
            buildGoogleDetectionApiClient();
        }

        return mGoogleDetectionApiClient;
    }

    /**
     * Adiciona as API's e escopos necessarios para o login com email google
     */
    private synchronized void buildGoogleApiClient(FragmentActivity fragmentActivity) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(fragmentActivity /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(ActivityRecognition.API)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     *
     * @return as APis para o login com email google
     */
    public GoogleApiClient getGoogleApiClientInstance(FragmentActivity fragmentActivity) {
        if (mGoogleApiClient == null) {
            buildGoogleApiClient(fragmentActivity);
        }

        return mGoogleApiClient;
    }

    /**
     * Adiciona as API's e escopos necessarios para o login com email google
     */
    private synchronized void buildGooglePlusApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Plus.API)
                .addApi(LocationServices.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.EMAIL))
                .build();
    }

    /**
     *
     * @return as APis para o login com email google
     */
    public GoogleApiClient getGooglePlusApiClientInstance() {
        if (mGoogleApiClient == null) {
            buildGooglePlusApiClient();
        }

        return mGoogleApiClient;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Parse: Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, getString(R.string.PARSE_APPLICATION_ID), getString(R.string.PARSE_CLIENT_KEY));

        TestFairy.begin(this, getString(R.string.TEST_FAIRY_API_KEY));

        //Local Parse Server
//        Parse.initialize(new Parse.Configuration.Builder(this)
//                .applicationId(getString(R.string.ParseServerApplicationID))
//                .clientKey(getString(R.string.ParseServerClientKey))
//                .server(getString(R.string.ParseServerUrl)) // The trailing slash is important.
//                .build()
//        );

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}