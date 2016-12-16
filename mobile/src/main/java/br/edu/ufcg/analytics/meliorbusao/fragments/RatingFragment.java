package br.edu.ufcg.analytics.meliorbusao.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;

import br.edu.ufcg.analytics.meliorbusao.R;
import br.edu.ufcg.analytics.meliorbusao.models.Route;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RatingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RatingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RatingFragment extends Fragment implements View.OnClickListener {
    public static final String ROUTE_ID = "ROUTE_ID";
    public static final String MOTORISTA = "MOTORISTA";
    public static final String LOTACAO = "LOTACAO";
    public static final String VIAGEM = "VIAGEM";
    public static final String CONDITION = "CONDITION";


    private OnFragmentInteractionListener mListener;

    private Route mRoute;
    private boolean mUpdateOnInflate;

    public static RatingFragment newInstance() {
        RatingFragment fragment = new RatingFragment();
        return fragment;
    }

    public RatingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_rating, container, false);

        Button btn = (Button) root.findViewById(R.id.not_in_bus_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Sorry =(", Toast.LENGTH_LONG).show();
                System.exit(0);
            }
        });

        return root;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
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
    public void onClick(View v) {
        try {
            mListener.onRateFinished(createRateBundle());
        } catch (ParseException e) {
            e.getMessage();
        }
    }

    public Bundle createRateBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(ROUTE_ID, mRoute.getId());
        bundle.putBoolean(MOTORISTA, getMotoristaResponsa());
        bundle.putBoolean(LOTACAO, getOnibusLotado());
        bundle.putBoolean(CONDITION, getBusCondition());


        return bundle;
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onRateFinished(Bundle bundle) throws ParseException;
    }

    public void setRoute(Route route) {
        mRoute = route;
        updateUI();
    }

    private void updateUI() {
        if (getView() == null) {
            mUpdateOnInflate = true;

            return;
        }

        TextView textView = (TextView) getView().findViewById(R.id.fragment_rating_route_title);
        textView.setText(mRoute.getId());

        LinearLayout selectRota = (LinearLayout) getView().findViewById(R.id.rota_icon);
        selectRota.setBackgroundColor(Color.parseColor("#" + mRoute.getColor()));

        mUpdateOnInflate = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().findViewById(R.id.fragment_rating_rate_button).setOnClickListener(this);
        if (mUpdateOnInflate) {
            updateUI();
        }
    }

    private boolean getMotoristaResponsa() {
        CheckBox motoristaCheckBox = (CheckBox) getView().findViewById(R.id.fragment_rating_motoristaok_checkbox);

        if (motoristaCheckBox != null) {
            return motoristaCheckBox.isChecked();
        }

        return false;
    }

    private boolean getOnibusLotado() {
        CheckBox lotadoCheckBox = (CheckBox) getView().findViewById(R.id.fragment_rating_lotado_checkbox);

        if (lotadoCheckBox != null) {
            return lotadoCheckBox.isChecked();
        }

        return false;
    }

    private boolean getBusCondition() {
        CheckBox conditionCheckBox = (CheckBox) getView().findViewById(R.id.fragment_rating_conditionOK_checkbox);

        if (conditionCheckBox != null) {
            return conditionCheckBox.isChecked();
        }

        return false;
    }
}
