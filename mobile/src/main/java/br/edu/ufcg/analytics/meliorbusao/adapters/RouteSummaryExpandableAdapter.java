package br.edu.ufcg.analytics.meliorbusao.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;

import java.util.List;

import br.edu.ufcg.analytics.meliorbusao.R;
import br.edu.ufcg.analytics.meliorbusao.fragments.TopBusFragment;
import br.edu.ufcg.analytics.meliorbusao.models.RouteCard;
import br.edu.ufcg.analytics.meliorbusao.models.RouteSummary;
import br.edu.ufcg.analytics.meliorbusao.views.RouteSummaryViewHolder;
import br.edu.ufcg.analytics.meliorbusao.views.RouteCardViewHolder;

public class RouteSummaryExpandableAdapter extends ExpandableRecyclerAdapter<RouteCard, RouteSummary, RouteCardViewHolder, RouteSummaryViewHolder> {

    private final TopBusFragment.OnTopBusSelectedListener mCallback;
    LayoutInflater mInflater;

    public RouteSummaryExpandableAdapter(Context context, List<RouteCard> parentItemList, TopBusFragment.OnTopBusSelectedListener mCallback) {
        super(parentItemList);
        mInflater = LayoutInflater.from(context);
        this.mCallback = mCallback;
    }

    /**
     * Inflate the view for the route summary card.
     *
     * @see ExpandableRecyclerAdapter
     * @see RouteCard
     */
    @NonNull
    @Override
    public RouteCardViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View view = mInflater.inflate(R.layout.bus_card_item, parentViewGroup, false);
        return new RouteCardViewHolder(view);
    }

    /**
     * Inflate the view for the route summary.
     *
     * @see ExpandableRecyclerAdapter
     * @see RouteSummary
     */
    @NonNull
    @Override
    public RouteSummaryViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        View view = mInflater.inflate(R.layout.bus_card_item_expanded, childViewGroup, false);
        return new RouteSummaryViewHolder(view, mCallback);
    }

    /**
     * Call {@link RouteCardViewHolder#bind(RouteCard)} passing the corresponding
     * {@link RouteCard} for binding.
     */
    @Override
    public void onBindParentViewHolder(@NonNull RouteCardViewHolder parentViewHolder, int parentPosition, @NonNull RouteCard parent) {
        parentViewHolder.bind(parent);
    }

    /**
     * Call {@link RouteSummaryViewHolder#bind(RouteSummary)} passing the corresponding
     * {@link RouteSummary} for binding.
     */
    @Override
    public void onBindChildViewHolder(@NonNull RouteSummaryViewHolder childViewHolder, int parentPosition, int childPosition, @NonNull RouteSummary child) {
        childViewHolder.bind(child);
    }
}