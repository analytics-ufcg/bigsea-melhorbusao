package br.edu.ufcg.analytics.meliorbusao;


import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class ConnectionGoogleApi implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    public static ConnectionGoogleApi newInstance() {
        ConnectionGoogleApi connectionGoogleApi = new ConnectionGoogleApi();
        return connectionGoogleApi;
    }

    public ConnectionGoogleApi() {
        // Required empty public constructor
    }


    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
