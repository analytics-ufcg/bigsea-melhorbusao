package br.edu.ufcg.analytics.meliorbusao.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import br.edu.ufcg.analytics.meliorbusao.R;
import br.edu.ufcg.analytics.meliorbusao.activities.MelhorBusaoActivity;
import br.edu.ufcg.analytics.meliorbusao.adapters.ItinerariesAdapter;
import br.edu.ufcg.analytics.meliorbusao.models.otp.Itinerary;


public class ItinerariesListFragment extends Fragment implements ItinerariesAdapter.OnItineraryClickListener {

    public static final String TAG = "ItinerariesListFragment";
    private static ItinerariesListFragment instance;
    private View mView;
    private RecyclerView itineraryRecyclerView;
    private List<Itinerary> itineraries;
    private OnItinerarySelectedListener mCallback;
    private ItinerariesAdapter mAdapter;

    public ItinerariesListFragment() {
    }

    public static ItinerariesListFragment getInstance() {
        if (instance == null) {
            ItinerariesListFragment fragment = new ItinerariesListFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            instance = fragment;
        }
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_itineraries_list, container, false);

        ////////ITINERARIES/////////
        if(itineraries.isEmpty()){
            mView.findViewById(R.id.error).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.itineraries_list).setVisibility(View.GONE);

        } else {
            mAdapter = new ItinerariesAdapter(itineraries);
            mAdapter.setOnItineraryClickListener(this);
            itineraryRecyclerView = (RecyclerView) mView.findViewById(R.id.itineraries_list);

            itineraryRecyclerView.setVisibility(View.VISIBLE);
            mView.findViewById(R.id.error).setVisibility(View.GONE);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            itineraryRecyclerView.setLayoutManager(layoutManager);
            itineraryRecyclerView.setAdapter(mAdapter);
        }

        return mView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnItinerarySelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnItinerarySelectedListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MelhorBusaoActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.itineraries_list_title));
    }

    public void setItinerariesList(List<Itinerary> itineraries) {
        this.itineraries = itineraries;
    }

    @Override
    public void onClick(Itinerary itinerary) {
        if (mCallback != null) {
            mCallback.onItinerarySelected(itinerary);
        }
    }

    public interface OnItinerarySelectedListener {
        void onItinerarySelected(Itinerary itinerary);
    }
}
