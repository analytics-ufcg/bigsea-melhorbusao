package br.edu.ufcg.analytics.meliorbusao.utils;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import br.edu.ufcg.analytics.meliorbusao.models.NearStop;
import br.edu.ufcg.analytics.meliorbusao.models.Route;
import br.edu.ufcg.analytics.meliorbusao.models.Stop;

public class StopRouteUtils {

    protected StopRouteUtils() {}

    /**
     * Recupera as rotas que passam em cada parada
     * @param stops
     * @return
     */
    public static Set<Route> getRoutesFromStops(TreeSet<NearStop> stops) {
        Set<Route> routeSet = new HashSet<>();

        for (Stop stop : stops) {
            for (Route route: stop.getRoutes()) {
                routeSet.add(route);
            }
        }

        return routeSet;
    }
}
