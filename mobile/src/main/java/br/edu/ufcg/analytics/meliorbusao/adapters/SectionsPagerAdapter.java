package br.edu.ufcg.analytics.meliorbusao.adapters;


import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import br.edu.ufcg.analytics.meliorbusao.R;
import br.edu.ufcg.analytics.meliorbusao.fragments.RatingFragment;
import br.edu.ufcg.analytics.meliorbusao.fragments.RouteSelectionFragment;
import br.edu.ufcg.analytics.meliorbusao.models.Route;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
    public final static int ROUTE_SELECTION_FRAGMENT_INDEX = 0;
    public final static int ROUTE_RATING_FRAGMENT_INDEX = 1;
    private final Resources resources;
    private final Route[] mRoutes;

    private RouteSelectionFragment mRouteSelectionFragment;
    private RatingFragment mRatingFragment;

    /**
     * Este metodo pode introduzir exposição no código.
     * Se necessario migra-lo para a classe onde estava, inicialmente como private
     * @param fm
     * @param resources
     * @param mRoutes
     */
    public SectionsPagerAdapter(FragmentManager fm, Resources resources, Route[] mRoutes) {
        super(fm);
        this.resources = resources;
        this.mRoutes = mRoutes;
    }

    /**
     * Retorna o fragment da rota ou avaliação
     * @param position
     * @return
     */
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case ROUTE_SELECTION_FRAGMENT_INDEX:
                return getRouteSelectionFragment();
            case ROUTE_RATING_FRAGMENT_INDEX:
                return getRatingFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    /**
     * Muda o titulo da pagina atual
     * @param position
     * @return
     */
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case ROUTE_SELECTION_FRAGMENT_INDEX:
                return resources.getString(R.string.route_selection_title);
            case ROUTE_RATING_FRAGMENT_INDEX:
                return resources.getString(R.string.route_rating_title);
        }
        return null;
    }

    /**
     * Retorna o fragment da rota
     * @return
     */
    public RouteSelectionFragment getRouteSelectionFragment() {
        if (mRouteSelectionFragment == null) {
            mRouteSelectionFragment = RouteSelectionFragment.newInstance(mRoutes);
        }

        return mRouteSelectionFragment;
    }

    /**
     * Retorna o fragment da avaliação
     * @return
     */
    public RatingFragment getRatingFragment() {
        if (mRatingFragment == null) {
            mRatingFragment = RatingFragment.newInstance();
        }
        return mRatingFragment;
    }
}