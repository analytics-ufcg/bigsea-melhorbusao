package br.edu.ufcg.analytics.meliorbusao;

import android.app.Application;

import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.plus.Plus;
import com.parse.Parse;
import com.testfairy.TestFairy;

import android.support.multidex.MultiDexApplication;

public class MeliorBusaoApplication extends MultiDexApplication   {

    private GoogleApiClient mGoogleDetectionApiClient = null;
    private GoogleApiClient mGooglePlusApiClient = null;


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
    private synchronized void buildGooglePlusApiClient() {
        mGooglePlusApiClient = new GoogleApiClient.Builder(this)
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
        if (mGooglePlusApiClient == null) {
            buildGooglePlusApiClient();
        }

        return mGooglePlusApiClient;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Parse: Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, getString(R.string.PARSE_APPLICATION_ID), getString(R.string.PARSE_CLIENT_KEY));
        TestFairy.begin(this, getString(R.string.TEST_FAIRY_API_KEY));
    }
}