package br.edu.ufcg.analytics.meliorbusao.views;


import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;

import br.edu.ufcg.analytics.meliorbusao.R;

public class BusChildViewHolder extends ChildViewHolder {

    private ProgressBar mConditionProgressBar;
    private ProgressBar mLotacaoProgressBar;
    private ProgressBar mMotoristaProgressBar;
    private Button mMapaButton;

    public Button getmTakeBusButton() {
        return mTakeBusButton;
    }

    public void setmTakeBusButton(Button mTakeBusButton) {
        this.mTakeBusButton = mTakeBusButton;
    }

    private Button mTakeBusButton;
    private View mColorRoute;

    public View getmColorRoute() {
        return mColorRoute;
    }

    public void setmColorRoute(View mColorRoute) {
        this.mColorRoute = mColorRoute;
    }

    public ProgressBar getmConditionProgressBar() {
        return mConditionProgressBar;
    }

    public void setmConditionProgressBar(ProgressBar mConditionProgressBar) {
        this.mConditionProgressBar = mConditionProgressBar;
    }

    public ProgressBar getmLotacaoProgressBar() {
        return mLotacaoProgressBar;
    }

    public void setmLotacaoProgressBar(ProgressBar mLotacaoProgressBar) {
        this.mLotacaoProgressBar = mLotacaoProgressBar;
    }

    public ProgressBar getmMotoristaProgressBar() {
        return mMotoristaProgressBar;
    }

    public void setmMotoristaProgressBar(ProgressBar mMotoristaProgressBar) {
        this.mMotoristaProgressBar = mMotoristaProgressBar;
    }

    public Button getmMapaButton() {
        return mMapaButton;
    }

    public void setmMapaButton(Button mMapaButton) {
        this.mMapaButton = mMapaButton;
    }

    public BusChildViewHolder(View itemView) {
        super(itemView);

        mConditionProgressBar = (ProgressBar) itemView.findViewById(R.id.arc_condition);
        mLotacaoProgressBar = (ProgressBar) itemView.findViewById(R.id.arc_lotacao);
        mMotoristaProgressBar = (ProgressBar) itemView.findViewById(R.id.arc_motorista);
        mColorRoute = itemView.findViewById(R.id.card_route_color_expansion);
        mMapaButton = (Button) itemView.findViewById(R.id.map_button);
        mTakeBusButton = (Button) itemView.findViewById(R.id.route_card_expanded_take_bus_button);
    }
}