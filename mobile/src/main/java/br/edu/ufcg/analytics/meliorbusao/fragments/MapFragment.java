package br.edu.ufcg.analytics.meliorbusao.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;

import br.edu.ufcg.analytics.meliorbusao.R;


public class MapFragment extends Fragment {

    private MapView mOpenStreetMap;
    private MapController mMapController;
    private GeoPoint center;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainView = inflater.inflate(R.layout.fragment_map, container, false);

        mOpenStreetMap = (MapView) mainView.findViewById(R.id.map_view);
        mOpenStreetMap.setTileSource(TileSourceFactory.MAPNIK);
        mOpenStreetMap.setBuiltInZoomControls(true);
        mOpenStreetMap.setMultiTouchControls(true);

        mMapController = (MapController) mOpenStreetMap.getController();
        mMapController.setZoom(12);

        return mainView;
    }

    private void addMarker(GeoPoint center) {
        Marker marker = new Marker(mOpenStreetMap);
        marker.setPosition(center);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setIcon(getResources().getDrawable(R.mipmap.map_marker));

        mOpenStreetMap.getOverlays().clear();
        mOpenStreetMap.getOverlays().add(marker);
        mOpenStreetMap.invalidate();
    }

    public void setCenterPoint(GeoPoint center) {
        this.center = center;
        mMapController.animateTo(center);
        addMarker(center);
    }

}
