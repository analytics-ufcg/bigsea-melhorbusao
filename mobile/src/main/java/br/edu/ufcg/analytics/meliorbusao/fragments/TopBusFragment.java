package br.edu.ufcg.analytics.meliorbusao.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FilterQueryProvider;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.ParseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import br.edu.ufcg.analytics.meliorbusao.R;
import br.edu.ufcg.analytics.meliorbusao.adapters.RouteEvaluationExpandableAdapter;
import br.edu.ufcg.analytics.meliorbusao.adapters.SearchRouteResultsAdapter;
import br.edu.ufcg.analytics.meliorbusao.db.DBUtils;
import br.edu.ufcg.analytics.meliorbusao.listeners.FragmentTitleChangeListener;
import br.edu.ufcg.analytics.meliorbusao.listeners.OnFragmentInteractionListener;
import br.edu.ufcg.analytics.meliorbusao.listeners.OnMeliorBusaoQueryListener;
import br.edu.ufcg.analytics.meliorbusao.listeners.OnRouteSuggestionListener;
import br.edu.ufcg.analytics.meliorbusao.listeners.OnSumarioRotasReadyListener;
import br.edu.ufcg.analytics.meliorbusao.models.Route;
import br.edu.ufcg.analytics.meliorbusao.models.SumarioRota;
import br.edu.ufcg.analytics.meliorbusao.models.RouteSummaryCard;
import br.edu.ufcg.analytics.meliorbusao.models.SumarioRotaBasicComparator;
import br.edu.ufcg.analytics.meliorbusao.utils.ParseUtils;


public class TopBusFragment extends Fragment implements AbsListView.OnItemClickListener,
        OnSumarioRotasReadyListener, SwipeRefreshLayout.OnRefreshListener,
        OnMeliorBusaoQueryListener, OnRouteSuggestionListener, SearchView.OnQueryTextListener,
        FilterQueryProvider, SearchView.OnSuggestionListener, AdapterView.OnItemSelectedListener {

    private static TopBusFragment instance;

    private OnTopBusSelectedListener mCallback;
    private OnFragmentInteractionListener mListener;

    private Menu mMenu;
    private SearchView mSearchView;
    private RecyclerView mRecyclerView;
    private RouteEvaluationExpandableAdapter mBusEvalExpandableAdapter;

    public static final String TAG = "TOP_BUS_FRAGMENT";

    private HashSet<Route> routesToDisplay;

    private String stopName;
    private int typeOfOrder = 0;

    List<SumarioRota> listRouteSummary;


    public static TopBusFragment getInstance() {
        if (instance == null) {
            TopBusFragment fragment = new TopBusFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            instance = fragment;
        }
        return instance;
    }

    public TopBusFragment() {
    }

    public HashSet<Route> getRoutesToDisplay() {
        return routesToDisplay;
    }

    public void setRoutesToDisplay(HashSet<Route> routesToDisplay) {
        this.routesToDisplay = routesToDisplay;
        this.stopName = null;
    }

    public void setStopRoutesToDisplay(String stopName, HashSet<Route> routesToDisplay) {
        this.stopName = stopName;
        this.routesToDisplay = routesToDisplay;
    }

    public String getStopName() {
        return stopName;
    }

    public void updateListView() {
        mBusEvalExpandableAdapter = new RouteEvaluationExpandableAdapter(getActivity(), generateRouteSummaries(), mCallback);
        mRecyclerView.setAdapter(mBusEvalExpandableAdapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_topbus, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.route_list_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Enable Options Menu handling
        setHasOptionsMenu(true);

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        String screenTitle = "";

        if (getRoutesToDisplay() != null) {
            ParseUtils.getSummaryRoutes(this, getRoutesToDisplay());
            if (getStopName() != null) {
                //Showing Stop Top Buses
                screenTitle = getResources().getString(R.string.top_busao_title) +
                        " - " + getStopName();
            } else {
                //Showing single bus evaluation
                Route r = getRoutesToDisplay().iterator().next();
                screenTitle = buildScreenTitle(r.getShortName());
            }
        } else {
            ParseUtils.getSumario(getContext(), this);
            screenTitle = getResources().getString(R.string.top_busao_title);
        }
        mCallback.onTitleChange(screenTitle);
        // colocar pra listview ficar esperando as rotas - tipo rodinha rodando
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final int normal = 0;
        if (mCallback != null) {
            SumarioRota routeSummary = (SumarioRota) parent.getItemAtPosition(position);
            mCallback.onBusCardClickListener(routeSummary.getRota().getShortName());
        }
    }

    @Override
    public void onSumarioRotasReady(ArrayList<SumarioRota> rotas, ParseException e) {
        if (getContext() == null) {
            return;
        } else if (e != null) {
            Toast.makeText(getContext(), getString(R.string.msg_connection_with_server_unavailable)
                    + e.getMessage(), Toast.LENGTH_LONG).show();
            this.listRouteSummary = DBUtils.getSumarioTodasAsRotas(getContext());
            updateListView();
        } else {
            this.listRouteSummary = rotas;
            updateListView();
        }
    }

    @Override
    public void onRefresh() {
        ParseUtils.getSumario(getContext(), this);
        mCallback.onTitleChange(getResources().getString(R.string.top_busao_title));
    }


    public interface OnTopBusSelectedListener extends FragmentTitleChangeListener {
        void onBusCardClickListener(String routeShortName);

        void onTakeBusButtonClickListener(Route selectedRoute);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnTopBusSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    public void resetTopBusList() {
        setRoutesToDisplay(null);
        ParseUtils.getSumario(getContext(), this);
        mCallback.onTitleChange(getResources().getString(R.string.top_busao_title));
    }

    @Override
    public void onMeliorBusaoQueryChange(String query) {

    }

    @Override
    public boolean onMeliorBusaoQuerySubmit(String query) {
        Route searchRoute = DBUtils.getRoute(getContext(), query);
        if (searchRoute != null) {
            HashSet<Route> routes = new HashSet<Route>();
            routes.add(searchRoute);
            ParseUtils.getSummaryRoutes(this, routes);
            mCallback.onTitleChange(buildScreenTitle(searchRoute.getShortName()));
            return true;
        }
        Toast.makeText(getActivity(), getString(R.string.msg_unable_to_find_route), Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public boolean onRouteSuggestionClick(Route selectedRoute) {
        try {
            HashSet<Route> routes = new HashSet<Route>();
            routes.add(selectedRoute);

            setRoutesToDisplay(routes);
            ParseUtils.getSummaryRoutes(this, routes);
            mCallback.onTitleChange(buildScreenTitle(selectedRoute.getShortName()));
            return true;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    private String buildScreenTitle(String routeName) {
        return getResources().getString(R.string.top_bus_screen_base_title) + routeName;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        mMenu = menu;

        menu.findItem(R.id.list_routes_btn).setVisible(false);
        //TODO: Decide whether or not to use ordering feature
//        menu.findItem(R.id.expand_button).setVisible(true);
//        populateSpinner();
        menu.findItem(R.id.action_search).setVisible(true);

        mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setQueryHint(getResources().getString(R.string.search_hint_top_bus));
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
        Route searchRoute = DBUtils.getRoute(getContext(), query);
        if (searchRoute != null) {
            HashSet<Route> routes = new HashSet<Route>();
            routes.add(searchRoute);
            ParseUtils.getSummaryRoutes(this, routes);
            mCallback.onTitleChange(buildScreenTitle(searchRoute.getShortName()));
            mMenu.findItem(R.id.action_search).collapseActionView();
            return true;
        }
        Toast.makeText(getActivity(), getString(R.string.msg_unable_to_find_route), Toast.LENGTH_SHORT).show();
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
        Cursor c = mSearchView.getSuggestionsAdapter().getCursor();
        Route selectedRoute = new Route(c.getString(0), c.getString(1),
                c.getString(2), c.getString(3), c.getString(4), c.getString(5));
        onRouteSuggestionClick(selectedRoute);

        try {
            mMenu.findItem(R.id.action_search).collapseActionView();
            c.close();
            return true;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    private void populateSpinner() {
        Spinner spinner = (Spinner) mMenu.findItem(R.id.expand_button).getActionView();
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.order_routes_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (getRoutesToDisplay() == null) {
            typeOfOrder = position;
            ParseUtils.getSumario(getContext(), this);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private ArrayList<RouteSummaryCard> generateRouteSummaries() {
        ArrayList<RouteSummaryCard> parentObjects = new ArrayList<>();
        for (SumarioRota routeSummary : listRouteSummary) {
            ArrayList<SumarioRota> childList = new ArrayList<>();
            childList.add(routeSummary);
            RouteSummaryCard routeSummaryCard = new RouteSummaryCard(routeSummary);
            routeSummaryCard.setChildObjectList(childList);
            parentObjects.add(routeSummaryCard);
        }

        Comparator comparator = new SumarioRotaBasicComparator();
        Collections.sort(parentObjects, comparator);

        return parentObjects;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //((RouteEvaluationExpandableAdapter) mRecyclerView.getAdapter()).onSaveInstanceState(outState);
    }


}
