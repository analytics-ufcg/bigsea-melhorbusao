package br.edu.ufcg.analytics.meliorbusao.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;

import java.util.List;

import br.edu.ufcg.analytics.meliorbusao.R;
import br.edu.ufcg.analytics.meliorbusao.fragments.TopBusFragment;
import br.edu.ufcg.analytics.meliorbusao.models.CategoriaResposta;
import br.edu.ufcg.analytics.meliorbusao.models.RouteSummaryCard;
import br.edu.ufcg.analytics.meliorbusao.models.SumarioRota;
import br.edu.ufcg.analytics.meliorbusao.models.SumarioRotaDetail;
import br.edu.ufcg.analytics.meliorbusao.views.BusChildViewHolder;
import br.edu.ufcg.analytics.meliorbusao.views.BusParentViewHolder;

public class RouteEvaluationExpandableAdapter extends ExpandableRecyclerAdapter<RouteSummaryCard, SumarioRota, BusParentViewHolder, BusChildViewHolder> {

    private final TopBusFragment.OnTopBusSelectedListener mCallback;
    LayoutInflater mInflater;

    public RouteEvaluationExpandableAdapter(Context context, List<RouteSummaryCard> parentItemList, TopBusFragment.OnTopBusSelectedListener mCallback) {
        super(parentItemList);
        mInflater = LayoutInflater.from(context);
        this.mCallback = mCallback;
    }

    /**
     * Configura o clique no card - abre o card e ajusta a posição na tela
     *
     * @param position
     */
//    @Override
//    public void onParentItemClickListener(int position) {
//        super.onParentItemClickListener(position);
//        mRecyclerView.getLayoutManager().scrollToPosition(position + 1);
//    }

    /**
//     * Cria o card
//     *
//     * @param viewGroup
//     * @return
     */
//    @Override
//    public BusParentViewHolder onCreateParentViewHolder(ViewGroup viewGroup) {
//        View view = mInflater.inflate(R.layout.bus_card_item, viewGroup, false);
//        return new BusParentViewHolder(view);
//    }
//
//    /**
//     * Cria a expansão do card
//     *
//     * @param viewGroup
//     * @return
//     */
//    @Override
//    public BusChildViewHolder onCreateChildViewHolder(ViewGroup viewGroup) {
//        View view = mInflater.inflate(R.layout.bus_card_item_expanded, viewGroup, false);
//        return new BusChildViewHolder(view);
//    }
//
//    /**
//     * Monta visualização do card
//     *
//     * @param busParentViewHolder
//     * @param i
//     * @param parentObject
//     */
//    @Override
//    public void onBindParentViewHolder(BusParentViewHolder busParentViewHolder, int i, Object parentObject) {
//        final RouteSummaryCard routeSummaryCard = (RouteSummaryCard) parentObject;
//        busParentViewHolder.getmNumBusTextView().setText(routeSummaryCard.getRouteSummary().getRota().getShortName());
//
//        if (routeSummaryCard.getRouteSummary().isAvaliada()) {
//            busParentViewHolder.getmEvaluation().setVisibility(View.GONE);
//            busParentViewHolder.getmStars().setVisibility(View.VISIBLE);
//            busParentViewHolder.getmStars().setRating((float) routeSummaryCard.getRouteSummary().getSumarioGeral());
//        } else {
//            busParentViewHolder.getmStars().setVisibility(View.GONE);
//            busParentViewHolder.getmEvaluation().setVisibility(View.VISIBLE);
//        }
//
//        String[] routeMainStops = routeSummaryCard.getRouteSummary().getRota().getMainStops().split(" - ");
//        try {
//            String ida = routeMainStops[0];
//            String volta = routeMainStops[1];
//
//            busParentViewHolder.getmIdaTextView().setText("  " + ida);
//            busParentViewHolder.getmVoltaTextView().setText("  " + volta);
//            busParentViewHolder.getMcolorView().setBackgroundColor(Color.parseColor("#" + routeSummaryCard.getRouteSummary().getRota().getColor()));
//        } catch (Exception e) {
//            Log.e("RouteEvaluationExpAdap", e.getMessage());
//        }
//    }
//
//    /**
//     * Monta visualização da expansão do card em Top Busão
//     *
//     * @param busChildViewHolder
//     * @param i
//     * @param childObject
//     */
//    @Override
//    public void onBindChildViewHolder(BusChildViewHolder busChildViewHolder, int i, Object childObject) {
//        final SumarioRotaDetail sumarioRotaDetail = (SumarioRotaDetail) childObject;
//
//        double motorista = sumarioRotaDetail.getRouteSummary().getSumario(CategoriaResposta.MOTORISTA);
//        double lotacao = sumarioRotaDetail.getRouteSummary().getSumario(CategoriaResposta.LOTACAO);
//        double condition = sumarioRotaDetail.getRouteSummary().getSumario(CategoriaResposta.CONDITION);
//
//        busChildViewHolder.getmMotoristaProgressBar().setProgress((int) (motorista * 100));
//        busChildViewHolder.getmLotacaoProgressBar().setProgress((int) (lotacao * 100));
//        busChildViewHolder.getmConditionProgressBar().setProgress((int) (condition * 100));
//
//        busChildViewHolder.getmColorRoute().setBackgroundColor(Color.parseColor("#" + sumarioRotaDetail.getRouteSummary().getRota().getColor()));
//
//        busChildViewHolder.getmTakeBusButton().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mCallback.onTakeBusButtonClickListener(sumarioRotaDetail.getRouteSummary().getRota());
//
//            }
//        });
//        busChildViewHolder.getmMapaButton().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mCallback.onBusCardClickListener(sumarioRotaDetail.getRouteSummary().getRota().getShortName());
//            }
//        });
//
//    }

    @NonNull
    @Override
    public BusParentViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View view = mInflater.inflate(R.layout.bus_card_item, parentViewGroup, false);
        return new BusParentViewHolder(view);
    }

    @NonNull
    @Override
    public BusChildViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        View view = mInflater.inflate(R.layout.bus_card_item_expanded, childViewGroup, false);
        return new BusChildViewHolder(view, mCallback);
    }

    @Override
    public void onBindParentViewHolder(@NonNull BusParentViewHolder parentViewHolder, int parentPosition, @NonNull RouteSummaryCard parent) {
        parentViewHolder.bind(parent);
    }

    @Override
    public void onBindChildViewHolder(@NonNull BusChildViewHolder childViewHolder, int parentPosition, int childPosition, @NonNull SumarioRota child) {
        childViewHolder.bind(child);
    }
}