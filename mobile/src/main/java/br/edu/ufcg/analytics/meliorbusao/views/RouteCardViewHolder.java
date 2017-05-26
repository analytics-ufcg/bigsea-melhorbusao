package br.edu.ufcg.analytics.meliorbusao.views;


import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ParentViewHolder;

import br.edu.ufcg.analytics.meliorbusao.R;
import br.edu.ufcg.analytics.meliorbusao.models.RouteCard;

/**
 * Container that adds the corresponding information to each route card in
 * {@link br.edu.ufcg.analytics.meliorbusao.fragments.TopBusFragment}
 */
public class RouteCardViewHolder extends ParentViewHolder {

    private TextView mIdaTextView;
    private TextView mVoltaTextView;
    private TextView mRouteIdTextView;
    private TextView mEvaluation;
    private View mRouteColorView;
    private RatingBar mStars;

    public RouteCardViewHolder(View itemView) {
        super(itemView);
        mRouteIdTextView = (TextView) itemView.findViewById(R.id.numRota);
        mIdaTextView = (TextView) itemView.findViewById(R.id.route_long_name_text_view2);
        mVoltaTextView = (TextView) itemView.findViewById(R.id.route_long_name_text_view_volta2);
        mStars = (RatingBar) itemView.findViewById(R.id.nota_ratingbar_card);
        mEvaluation = (TextView) itemView.findViewById(R.id.not_rating);
        mRouteColorView = itemView.findViewById(R.id.card_route_color);
    }

    public TextView getmEvaluation() {
        return mEvaluation;
    }

    public void setmEvaluation(TextView mEvaluation) {
        this.mEvaluation = mEvaluation;
    }

    public TextView getmIdaTextView() {
        return mIdaTextView;
    }

    public void setmIdaTextView(TextView mIdaTextView) {
        this.mIdaTextView = mIdaTextView;
    }

    public TextView getmVoltaTextView() {
        return mVoltaTextView;
    }

    public void setmVoltaTextView(TextView mVoltaTextView) {
        this.mVoltaTextView = mVoltaTextView;
    }

    public TextView getmRouteIdTextView() {
        return mRouteIdTextView;
    }

    public void setmRouteIdTextView(TextView mRouteIdTextView) {
        this.mRouteIdTextView = mRouteIdTextView;
    }

    public RatingBar getmStars() {
        return mStars;
    }

    public void setmStars(RatingBar mStars) {
        this.mStars = mStars;
    }

    public View getmRouteColorView() {
        return mRouteColorView;
    }

    public void setmRouteColorView(View mRouteColorView) {
        this.mRouteColorView = mRouteColorView;
    }

    public void bind(RouteCard routeCard) {
        mRouteIdTextView.setText(routeCard.getRouteSummary().getRota().getShortName());

        if (routeCard.getRouteSummary().isAvaliada()) {
            mEvaluation.setVisibility(View.GONE);
            mStars.setVisibility(View.VISIBLE);
            mStars.setRating((float) routeCard.getRouteSummary().getSumarioGeral());
        } else {
            mStars.setVisibility(View.GONE);
            mEvaluation.setVisibility(View.VISIBLE);
        }

        String[] routeMainStops = routeCard.getRouteSummary().getRota().getMainStops().split(" - ");
        try {
            String ida = routeMainStops[0];
            String volta = routeMainStops[1];

            mIdaTextView.setText("  " + ida);
            mVoltaTextView.setText("  " + volta);
            mRouteColorView.setBackgroundColor(Color.parseColor("#" + routeCard.getRouteSummary().getRota().getColor()));
        } catch (Exception e) {
            Log.e("RouteEvaluationExpAdap", e.getMessage());
        }
    }
}