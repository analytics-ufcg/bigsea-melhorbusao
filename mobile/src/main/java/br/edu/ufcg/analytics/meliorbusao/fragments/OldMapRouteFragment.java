package br.edu.ufcg.analytics.meliorbusao.fragments;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FilterQueryProvider;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.cocosw.bottomsheet.BottomSheet;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import br.edu.ufcg.analytics.meliorbusao.R;
import br.edu.ufcg.analytics.meliorbusao.activities.MelhorBusaoActivity;
import br.edu.ufcg.analytics.meliorbusao.adapters.RouteArrayAdapter;
import br.edu.ufcg.analytics.meliorbusao.adapters.SearchRouteResultsAdapter;
import br.edu.ufcg.analytics.meliorbusao.adapters.StopInfoAdapter;
import br.edu.ufcg.analytics.meliorbusao.db.DBUtils;
import br.edu.ufcg.analytics.meliorbusao.listeners.FragmentTitleChangeListener;
import br.edu.ufcg.analytics.meliorbusao.listeners.OnMeliorBusaoQueryListener;
import br.edu.ufcg.analytics.meliorbusao.listeners.OnRouteSuggestionListener;
import br.edu.ufcg.analytics.meliorbusao.models.Route;
import br.edu.ufcg.analytics.meliorbusao.models.RouteShape;
import br.edu.ufcg.analytics.meliorbusao.models.Stop;
import br.edu.ufcg.analytics.meliorbusao.utils.ProgressUtils;

public class OldMapRouteFragment  extends MeliorMapFragment implements GoogleMap.OnMapLoadedCallback,
        GoogleMap.OnMapClickListener, OnMeliorBusaoQueryListener, OnRouteSuggestionListener,
        SearchView.OnQueryTextListener, FilterQueryProvider, SearchView.OnSuggestionListener,
        AdapterView.OnItemSelectedListener, GoogleMap.OnCameraChangeListener {

    public static final String TAG = "MAP_ROUTE_FRAGMENT";
    private static final double DEFAULT_ZOOM_THRESHOLD = 15.0;
    private static OldMapRouteFragment instance;
    private FragmentTitleChangeListener mCallback;
    private String routeShortName;
    private Menu mMenu;
    private SearchView mSearchView;
    private Spinner mSpinner;
    private List<String> routeSuggestionList = new ArrayList<>();
    private ArrayAdapter<String> itemsAdapter;
    private float previousZoomLevel;
    private boolean isZoomingIn;
    private List<Marker> stopsMarkers;
    private BitmapDescriptor mParadaBitmap;
    private ProgressBar progressSpinner;

    public OldMapRouteFragment() {

    }

    public static OldMapRouteFragment getInstance() {
        if (instance == null) {
            OldMapRouteFragment fragment = new OldMapRouteFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            instance = fragment;
        }
        return instance;
    }

    public String getRouteShortName() {
        return routeShortName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FrameLayout viewMain = (FrameLayout) super.onCreateView(inflater, container, savedInstanceState);

        progressSpinner = ProgressUtils.buildProgressBar(this.getContext());

        viewMain.addView(progressSpinner);

        getMap().setOnMapLoadedCallback(this);
        getMap().setOnMapClickListener(this);
        getMap().setOnCameraChangeListener(this);
        getMap().setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {

            @Override
            public boolean onMyLocationButtonClick() {
                if (((MelhorBusaoActivity) getActivity()).isLocationEnabled()) {
                    requestLocationUpdates();
                    Snackbar.make(getView(), R.string.reload_location, Snackbar.LENGTH_LONG).show();
                } else {
                    ((MelhorBusaoActivity) getActivity()).buildAlertMessageNoGps();
                }
                return true;
            }
        });

        itemsAdapter =
                new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, routeSuggestionList);
        //Enable Options Menu handling
        setHasOptionsMenu(true);
        if (stopsMarkers == null) {
            stopsMarkers = new ArrayList<Marker>();
        }

        return viewMain;
    }


    private void showLinesMenu() {
        BottomSheet.Builder builder = new BottomSheet.Builder(getActivity());

        builder.title(R.string.lines);

        BottomSheet bottomSheet = builder.build();
        Menu m = bottomSheet.getMenu();

        for (String linha : DBUtils.getLinhas(getContext())) {
            m.add(linha).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    showRoutesLineMenu(item.getTitle());
                    return false;
                }
            });
        }

        bottomSheet.show();
    }

    private CameraUpdate getCameraUpdate(List<RouteShape> shapes) {
        LatLngBounds.Builder latLngBoundsBuilder = LatLngBounds.builder();

        for (RouteShape shape : shapes) {
            LatLng[] edges = shape.edges();
            for (LatLng edge : edges) {
                latLngBoundsBuilder.include(edge);
                Log.d("Edge", edge.toString());
            }
        }
        return CameraUpdateFactory.newLatLngBounds(latLngBoundsBuilder.build(), 10);

    }

    private void showRoutesLineMenu(CharSequence linhaName) {
        BottomSheet.Builder builder = new BottomSheet.Builder(getActivity());
        builder.title("Linha " + linhaName);
        BottomSheet bottomSheet = builder.build();
        Menu m = bottomSheet.getMenu();

        for (final Route route : DBUtils.getRotasPorLinha(getContext(), (String) linhaName)) {
            m.add(route.getShortName()).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    mCallback.onTitleChange(buildScreenTitle(route.getShortName()));
                    setUpMap(route);
                    return false;
                }
            });
        }

        bottomSheet.show();
    }

    private void setUpMap(Route r) {
        getMap().clear();
        drawRoute(r);
        StopInfoAdapter stopInfo = new StopInfoAdapter();
        stopInfo.setActivity(getActivity());
        getMap().setInfoWindowAdapter(stopInfo);
    }


    private void drawRoute(Route route) {
        List<RouteShape> shapes = DBUtils.getRouteShape(getContext(), route.getId());

        for (RouteShape shape : shapes) {
            PolylineOptions polygonOptions = new PolylineOptions();
            polygonOptions.visible(true);
            Log.d("OldMapRouteFragment", "#" + shape.getColor());
            polygonOptions.color(Color.parseColor("#" + shape.getColor()));
            polygonOptions.addAll(shape);
            getMap().addPolyline(polygonOptions);
        }
        inicializarParadas(route);
        getMap().animateCamera(getCameraUpdate(shapes));
        progressSpinner.setVisibility(View.GONE);
    }

    private void inicializarParadas(Route rota) {
        HashSet<Stop> paradas = DBUtils.getParadasRota(getContext(), rota);
        if (stopsMarkers == null) {
            stopsMarkers = new ArrayList<Marker>();
        } else {
            stopsMarkers.clear();
        }
        for (Stop parada : paradas) {
            stopsMarkers.add(getMap().addMarker(getMarkerOptionsFromStop(parada, getStopBitmap())));
        }
    }

    @Override
    public void onMeliorLocationAvaliable(Location result) {
        super.onMeliorLocationAvaliable(result);

        if (getRouteShortName() == null) {
            getMap().animateCamera(getCameraUpdate());
        }
    }

    public void setRoute(String routeShortName) {
        this.routeShortName = routeShortName;
        if (routeShortName == null){
            stopsMarkers = null;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (FragmentTitleChangeListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (routeShortName != null) {
            mCallback.onTitleChange(buildScreenTitle(routeShortName));
        } else {
            mCallback.onTitleChange(getResources().getString(R.string.map_routes_title));
            getMap().clear();
        }
    }

    @Override
    public void onMapLoaded() {
        if (routeShortName == null) {
            progressSpinner.setVisibility(View.GONE);
        } else {
            Route route = DBUtils.getRoute(getContext(), routeShortName);
            setUpMap(route);
//            mSpinner.setSelection(((ArrayAdapter) mSpinner.getAdapter()).getPosition(route));
            setRoute(null);
        }
    }

    private String buildScreenTitle(String routeName) {
        return getResources().getString(R.string.map_route_screen_base_title) + routeName;
    }

    @Override
    public void onMapClick(LatLng latLng) {
//        search.onActionViewCollapsed();
    }

    @Override
    public void onMeliorBusaoQueryChange(String query) {

    }

    @Override
    public boolean onMeliorBusaoQuerySubmit(String query) {
//        searchListView.setVisibility(View.INVISIBLE);
        try {
            Route searchRoute = DBUtils.getRoute(getContext(), query);
            mCallback.onTitleChange(buildScreenTitle(searchRoute.getShortName()));
            setUpMap(searchRoute);
            return true;
        } catch (Exception e) {
            Toast.makeText(getActivity(), getString(R.string.msg_unable_to_find_route), Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public boolean onRouteSuggestionClick(Route selectedRoute) {
        try {
            mCallback.onTitleChange(buildScreenTitle(selectedRoute.getShortName()));
            setUpMap(selectedRoute);
            return true;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        mMenu = menu;

        menu.findItem(R.id.action_search).setVisible(true);
        mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setQueryHint(getResources().getString(R.string.search_hint_map_route));
        mSearchView.setInputType(0x00000002);

        int autoCompleteTextViewID = getResources().getIdentifier("search_src_text", "id",
                getActivity().getPackageName());
        AutoCompleteTextView searchAutoCompleteTextView =
                (AutoCompleteTextView) mSearchView.findViewById(autoCompleteTextViewID);
        searchAutoCompleteTextView.setThreshold(0);

        SearchRouteResultsAdapter mSearchSuggestionAdapter = new SearchRouteResultsAdapter(getContext(),
                R.layout.search_suggestion_list_item, null, null, -1000);
        mSearchSuggestionAdapter.setFilterQueryProvider(this);
        mSearchView.setSuggestionsAdapter(mSearchSuggestionAdapter);
        mSearchView.setOnSuggestionListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        progressSpinner.setVisibility(View.VISIBLE);
        try {
            Route searchRoute = DBUtils.getRoute(getContext(), query);
            mCallback.onTitleChange(buildScreenTitle(searchRoute.getShortName()));
            setUpMap(searchRoute);
            mMenu.findItem(R.id.action_search).collapseActionView();
            return true;
        } catch (Exception e) {
            Toast.makeText(getActivity(), getString(R.string.msg_route_not_found), Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }

    @Override
    public Cursor runQuery(CharSequence constraint) {
        return DBUtils.getRouteCursor(getContext(), (String) constraint);
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        progressSpinner.setVisibility(View.VISIBLE);
        Cursor c = mSearchView.getSuggestionsAdapter().getCursor();
        Route selectedRoute = new Route(c.getString(0), c.getString(1),
                c.getString(2), c.getString(3));
        onRouteSuggestionClick(selectedRoute);
//        mSpinner.setSelection(((ArrayAdapter) mSpinner.getAdapter()).getPosition(selectedRoute));

        try {
            mMenu.findItem(R.id.action_search).collapseActionView();
            c.close();
            return true;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    private void populateRouteSpinner(Spinner spinner) {
        List<Route> routes = new ArrayList<Route>(DBUtils.getTodasAsRotas(getActivity()));
        Collections.sort(routes);
        RouteArrayAdapter adapter = new RouteArrayAdapter(getActivity(), routes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (routeShortName == null) {
            Route selectedRoute = (Route) parent.getItemAtPosition(position);
            onMeliorBusaoQuerySubmit(selectedRoute.getShortName());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if (previousZoomLevel != cameraPosition.zoom && stopsMarkers!=null) {
            if (cameraPosition.zoom >= DEFAULT_ZOOM_THRESHOLD) {
                if (!isZoomingIn) {
                    for (Marker stopMarker : stopsMarkers) {
                        stopMarker.setIcon(getBitmapDescriptor(R.drawable.ic_parada, 52, 40));
                    }
                }
                isZoomingIn = true;
            } else {
                if (isZoomingIn) {
                    for (Marker stopMarker : stopsMarkers) {
                        stopMarker.setIcon(getBitmapDescriptor(R.drawable.ic_bus_stop, 5, 5));
                    }
                }
                isZoomingIn = false;
            }
        }
        previousZoomLevel = cameraPosition.zoom;
    }

    private BitmapDescriptor getStopBitmap() {
        if (mParadaBitmap == null) {
            mParadaBitmap = getBitmapDescriptor(R.drawable.ic_bus_stop, 5, 5);
        }
        return mParadaBitmap;
    }
}
