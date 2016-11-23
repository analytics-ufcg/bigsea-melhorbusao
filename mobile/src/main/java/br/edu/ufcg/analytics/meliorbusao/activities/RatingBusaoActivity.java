package br.edu.ufcg.analytics.meliorbusao.activities;

import android.os.Bundle;
import android.os.Parcelable;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.parse.ParseException;
import java.util.Arrays;

import br.edu.ufcg.analytics.meliorbusao.Constants;
import br.edu.ufcg.analytics.meliorbusao.R;
import br.edu.ufcg.analytics.meliorbusao.adapters.SectionsPagerAdapter;
import br.edu.ufcg.analytics.meliorbusao.db.DBUtils;
import br.edu.ufcg.analytics.meliorbusao.fragments.RatingFragment;
import br.edu.ufcg.analytics.meliorbusao.fragments.RouteSelectionFragment;
import br.edu.ufcg.analytics.meliorbusao.models.Avaliacao;
import br.edu.ufcg.analytics.meliorbusao.models.CategoriaResposta;
import br.edu.ufcg.analytics.meliorbusao.models.Resposta;
import br.edu.ufcg.analytics.meliorbusao.models.Route;
import br.edu.ufcg.analytics.meliorbusao.utils.ParseUtils;

public class RatingBusaoActivity extends AppCompatActivity implements
        RouteSelectionFragment.OnRouteSelectedListener, RatingFragment.OnFragmentInteractionListener, ViewPager.OnPageChangeListener {

    private static final int NONE_ROUTE = 0;
    private static final int ONLY_ONE_ROUTE = 1;
    private static final int MANY_ROUTES = 2;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private Toolbar mToolbar;

    private Route[] mRoutes;
    private long mTimestamp;

    /**
     * Método para iniciar os fragments,a própria a aplicação
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extractDataFromIntent();

        setContentView(R.layout.activity_rating_busao);

        setSupportActionBar(getToolbar());
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), getResources(), mRoutes);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.toolbar_text_color));
    }

    /**
     * Coleta informações sobre a avaliaçao realizada pelo usuário
     */
    private void extractDataFromIntent() {
        mRoutes = new Route[0];
        Parcelable[] routeParcelables = getIntent().getParcelableArrayExtra(Constants.TRIP_ROUTES_EXTRA);

        if (routeParcelables != null) {
            mRoutes = Arrays.copyOf(routeParcelables, routeParcelables.length, Route[].class);
        }

        mTimestamp = getIntent().getLongExtra(Constants.TRIP_TIMESTAMP_EXTRA, -1);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareRatingUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_rating_busao, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == android.R.id.home) {
            mViewPager.setCurrentItem(SectionsPagerAdapter.ROUTE_SELECTION_FRAGMENT_INDEX, true);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Associa a rota a avalição
     * @param route
     */
    @Override
    public void onRouteSelected(Route route) {
        Log.d("RatingBusaoActivity", route.getId());
        mSectionsPagerAdapter.getRatingFragment().setRoute(route);
        mViewPager.setCurrentItem(SectionsPagerAdapter.ROUTE_RATING_FRAGMENT_INDEX, true);
    }

    /**
     * Após a realizaçã da avaliação, salva os dados fornecidos pelo usuário
     * @param bundle
     * @throws ParseException
     */
    @Override
    public void onRateFinished(Bundle bundle) throws ParseException {
        String routeId = bundle.getString(RatingFragment.ROUTE_ID);
        boolean driver = bundle.getBoolean(RatingFragment.MOTORISTA);
        boolean crowded = bundle.getBoolean(RatingFragment.LOTACAO);
        int noteTrip = 0; //TODO Decide whether or not to use the trip rating
        boolean condition = bundle.getBoolean(RatingFragment.CONDITION);

        Avaliacao avaliacao = new Avaliacao(mTimestamp, routeId)
                .addResposta(new Resposta(CategoriaResposta.MOTORISTA.idCategoria, driver ? 1 : 0))
                .addResposta(new Resposta(CategoriaResposta.LOTACAO.idCategoria, crowded ? 1 : 0))
                .addResposta(new Resposta(CategoriaResposta.VIAGEM.idCategoria, noteTrip))
                .addResposta(new Resposta(CategoriaResposta.CONDITION.idCategoria, condition ? 1 : 0));

        Log.d("RatingBusaoActivity", String.valueOf(bundle));

        if (DBUtils.fillRating(this, avaliacao)) {

            ParseUtils.insereAvaliacao(avaliacao, String.valueOf(mTimestamp));

            Toast.makeText(RatingBusaoActivity.this, getString(R.string.msg_thanks_answer), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(RatingBusaoActivity.this, getString(R.string.msg_error_rating), Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    /**
     *
     * @param position
     */
    @Override
    public void onPageSelected(int position) {
        if (position == SectionsPagerAdapter.ROUTE_RATING_FRAGMENT_INDEX &&
                mRoutes.length > 1) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        getSupportActionBar().setTitle(mSectionsPagerAdapter.getPageTitle(position));
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    /**
     *
     * @return
     */
    private Toolbar getToolbar() {
        if (mToolbar == null) {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
        }
        return mToolbar;
    }

    /**
     *
     */
    private void prepareRatingUI() {
        switch (mRoutes.length) {
            case NONE_ROUTE:
                finish();
                break;

            case ONLY_ONE_ROUTE:
                showSingleRouteUI();
                break;

            default:
                showRouteSelectionUI();
                break;
        }
    }

    /**
     * Sobrescreve a função do botão voltar
     */
    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() == SectionsPagerAdapter.ROUTE_RATING_FRAGMENT_INDEX
                && mRoutes.length > 1) {
            mViewPager.setCurrentItem(SectionsPagerAdapter.ROUTE_SELECTION_FRAGMENT_INDEX);
            return;
        }

        super.onBackPressed();
    }

    /**
     * View para escolher em que onibus entrou
     */
    private void showRouteSelectionUI() {
        getSupportActionBar().setTitle(R.string.route_selection_title);
    }

    /**
     *
     */
    private void showSingleRouteUI() {
        mSectionsPagerAdapter.getRatingFragment().setRoute(mRoutes[0]);
        mViewPager.setCurrentItem(SectionsPagerAdapter.ROUTE_RATING_FRAGMENT_INDEX, false);
    }
}
