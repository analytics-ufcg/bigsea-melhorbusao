package br.edu.ufcg.analytics.meliorbusao.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.Arrays;

import br.edu.ufcg.analytics.meliorbusao.R;
import br.edu.ufcg.analytics.meliorbusao.models.Route;

public class RouteSelectionFragment extends Fragment implements AbsListView.OnItemClickListener {

    private static final String ROUTES_PARAM = "routes";

    private Route[] mRoutes;
    private OnRouteSelectedListener mListener;

    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    public static RouteSelectionFragment newInstance(Route[] routes) {
        RouteSelectionFragment fragment = new RouteSelectionFragment();

        Bundle args = new Bundle();
        args.putParcelableArray(ROUTES_PARAM, routes);
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RouteSelectionFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Parcelable[] routes = getArguments().getParcelableArray(ROUTES_PARAM);
            mRoutes = Arrays.copyOf(routes, routes.length, Route[].class);
        }
    }

    public void updateListView() {
        mAdapter = new RotaArrayAdapter<Route>(
                getContext(), // O contexto atual
                R.layout.route_select_item, // O arquivo de layout de cada item
                R.id.numero_rota, // O ID do campo a ser preenchido
                mRoutes // A fonte dos dados
        );

        mListView.setAdapter(mAdapter);
    }

    private static class RotaArrayAdapter<T extends Route> extends ArrayAdapter<T> {
        public RotaArrayAdapter(Context context, int layout, int field, T[] av) {
            super(context, layout, field, av);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);

            Route av = (Route) getItem(position);

            TextView titulo = (TextView) v.findViewById(R.id.numero_rota);
            titulo.setText(av.getId());

            ImageView selectRota = (ImageView) v.findViewById(R.id.bus_icon);
            selectRota.setColorFilter(Color.parseColor("#" + av.getColor()));

            return v;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnRouteSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onRouteSelected(mRoutes[position]);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        updateListView();
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnRouteSelectedListener {
        void onRouteSelected(Route route);
    }

}
