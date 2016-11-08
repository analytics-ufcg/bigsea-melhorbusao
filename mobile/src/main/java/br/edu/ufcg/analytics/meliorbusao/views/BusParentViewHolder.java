package br.edu.ufcg.analytics.meliorbusao.views;


import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;

import br.edu.ufcg.analytics.meliorbusao.R;

public class BusParentViewHolder extends ParentViewHolder {

    private TextView mIdaTextView;
    private TextView mVoltaTextView;
    private TextView mNumBusTextView;
    private TextView mEvaluation;
    private View mcolorView;
    public TextView getmEvaluation() {
        return mEvaluation;
    }

    public void setmEvaluation(TextView mEvaluation) {
        this.mEvaluation = mEvaluation;
    }

    private RatingBar mStars;

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

    public TextView getmNumBusTextView() {
        return mNumBusTextView;
    }

    public void setmNumBusTextView(TextView mNumBusTextView) {
        this.mNumBusTextView = mNumBusTextView;
    }

    public RatingBar getmStars() {
        return mStars;
    }

    public void setmStars(RatingBar mStars) {
        this.mStars = mStars;
    }

    public View getMcolorView() {
        return mcolorView;
    }

    public void setMcolorView(View mcolorView) {
        this.mcolorView = mcolorView;
    }


    public BusParentViewHolder(View itemView) {
        super(itemView);

        mNumBusTextView = (TextView) itemView.findViewById(R.id.numRota);
        mIdaTextView = (TextView) itemView.findViewById(R.id.route_long_name_text_view2);
        mVoltaTextView = (TextView) itemView.findViewById(R.id.route_long_name_text_view_volta2);
        mStars = (RatingBar) itemView.findViewById(R.id.nota_ratingbar_card);
        mEvaluation = (TextView) itemView.findViewById(R.id.not_rating);
        mcolorView = itemView.findViewById(R.id.card_route_color);

    }
}