package br.edu.ufcg.analytics.meliorbusao.views;

import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;

import br.edu.ufcg.analytics.meliorbusao.R;
import br.edu.ufcg.analytics.meliorbusao.fragments.TopBusFragment;
import br.edu.ufcg.analytics.meliorbusao.models.CategoriaResposta;
import br.edu.ufcg.analytics.meliorbusao.models.RouteSummary;

/**
 * Container that shows information from {@link RouteSummary} to the corresponding routa card when
 * the card is clicked.
 */
public class RouteSummaryViewHolder extends ChildViewHolder {

    private ProgressBar mBusMaintenanceProgressBar;
    private ProgressBar mBusCrowdingProgressBar;
    private ProgressBar mBusDriverProgressBar;
    private Button mSeeMapButton;
    private TopBusFragment.OnTopBusSelectedListener mCallback;
    private Button mTakeBusButton;
    private View mRouteColor;

    public RouteSummaryViewHolder(View itemView, TopBusFragment.OnTopBusSelectedListener callback) {
        super(itemView);

        mBusMaintenanceProgressBar = (ProgressBar) itemView.findViewById(R.id.arc_condition);
        mBusCrowdingProgressBar = (ProgressBar) itemView.findViewById(R.id.arc_lotacao);
        mBusDriverProgressBar = (ProgressBar) itemView.findViewById(R.id.arc_motorista);
        mRouteColor = itemView.findViewById(R.id.card_route_color_expansion);
        mSeeMapButton = (Button) itemView.findViewById(R.id.map_button);
        mTakeBusButton = (Button) itemView.findViewById(R.id.route_card_expanded_take_bus_button);
        mCallback = callback;
    }

    public Button getmTakeBusButton() {
        return mTakeBusButton;
    }

    public void setmTakeBusButton(Button mTakeBusButton) {
        this.mTakeBusButton = mTakeBusButton;
    }

    public View getmRouteColor() {
        return mRouteColor;
    }

    public void setmRouteColor(View mRouteColor) {
        this.mRouteColor = mRouteColor;
    }

    public ProgressBar getmBusMaintenanceProgressBar() {
        return mBusMaintenanceProgressBar;
    }

    public void setmBusMaintenanceProgressBar(ProgressBar mBusMaintenanceProgressBar) {
        this.mBusMaintenanceProgressBar = mBusMaintenanceProgressBar;
    }

    public ProgressBar getmBusCrowdingProgressBar() {
        return mBusCrowdingProgressBar;
    }

    public void setmBusCrowdingProgressBar(ProgressBar mBusCrowdingProgressBar) {
        this.mBusCrowdingProgressBar = mBusCrowdingProgressBar;
    }

    public ProgressBar getmBusDriverProgressBar() {
        return mBusDriverProgressBar;
    }

    public void setmBusDriverProgressBar(ProgressBar mBusDriverProgressBar) {
        this.mBusDriverProgressBar = mBusDriverProgressBar;
    }

    public Button getmSeeMapButton() {
        return mSeeMapButton;
    }

    public void setmSeeMapButton(Button mSeeMapButton) {
        this.mSeeMapButton = mSeeMapButton;
    }

    public void bind(final RouteSummary routeSummary) {
        double motorista = routeSummary.getSumario(CategoriaResposta.MOTORISTA);
        double lotacao = routeSummary.getSumario(CategoriaResposta.LOTACAO);
        double condition = routeSummary.getSumario(CategoriaResposta.CONDITION);

        mBusDriverProgressBar.setProgress((int) (motorista * 100));
        mBusCrowdingProgressBar.setProgress((int) (lotacao * 100));
        mBusMaintenanceProgressBar.setProgress((int) (condition * 100));

        mRouteColor.setBackgroundColor(Color.parseColor("#" + routeSummary.getRota().getColor()));

        mTakeBusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onTakeBusButtonClickListener(routeSummary.getRota());

            }
        });

        mSeeMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onBusCardClickListener(routeSummary.getRota().getShortName());
            }
        });

    }
}