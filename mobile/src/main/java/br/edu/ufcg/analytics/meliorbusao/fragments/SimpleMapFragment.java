package br.edu.ufcg.analytics.meliorbusao.fragments;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

import br.edu.ufcg.analytics.meliorbusao.Constants;
import br.edu.ufcg.analytics.meliorbusao.R;
import br.edu.ufcg.analytics.meliorbusao.activities.MelhorBusaoActivity;

public class SimpleMapFragment extends MeliorMapFragment implements GoogleMap.OnMapClickListener {

    private double lat = 0;
    private double lon = 0;
    private String address = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = super.onCreateView(inflater, container, savedInstanceState);
        getMap().setOnMapClickListener(this);
        onConnected(savedInstanceState);
        getMap().animateCamera(getCameraUpdate());
        putAddressName();

        getMap().setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {

            @Override
            public boolean onMyLocationButtonClick() {
                if (((MelhorBusaoActivity) getActivity()).isLocationEnabled()) {
                    getMap().animateCamera(getCameraUpdate());
                    updateMark(new LatLng(lat, lon));
                } else {
                    ((MelhorBusaoActivity) getActivity()).buildAlertMessageNoGps();
                }
                return true;
            }
        });

        updateMark(new LatLng(lat, lon));

        return mView;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        updateMark(latLng);
    }

    protected void updateMark(LatLng point) {
        getMap().clear();
        getMap().animateCamera(getCameraUpdate(point));
        putAddressName();
        getMap().addMarker(new MarkerOptions().position(point).title(getAddress()).
                icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_marker)));
        ((SearchScheduleFragment) getParentFragment()).setRouteAdapter();
    }

    private CameraUpdate getCameraUpdate(LatLng point) {
        setLat(point.latitude);
        setLon(point.longitude);
        return CameraUpdateFactory.newLatLngZoom(new LatLng(point.latitude, point.longitude), 15.5f);
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    @Override
    protected CameraUpdate getCameraUpdate() {
        if (mLastLocation == null) {
            setLat(Double.parseDouble(Constants.CENTRAL_POINT_LATITUDE));
            setLon(Double.parseDouble(Constants.CENTRAL_POINT_LONGITUDE));
            return CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(Constants.CENTRAL_POINT_LATITUDE), Double.parseDouble(Constants.CENTRAL_POINT_LONGITUDE)), 13f);
        } else {
            setLat(mLastLocation.getLatitude());
            setLon(mLastLocation.getLongitude());
            return CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 15.5f);
        }
    }

    protected void setAddressName(double lat, double lon) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lon, 1);
            setAddress(addresses.get(0).getAddressLine(0));

        } catch (Exception e) {
            Log.e("Erro address", e.getMessage());
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private void putAddressName() {
        setAddressName(getLat(), getLon());
        ((SearchScheduleFragment) getParentFragment()).setAddress(getAddress());
    }

}
