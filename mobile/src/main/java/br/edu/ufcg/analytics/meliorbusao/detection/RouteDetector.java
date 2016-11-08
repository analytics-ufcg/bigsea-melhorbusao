package br.edu.ufcg.analytics.meliorbusao.detection;

import android.content.Context;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import br.edu.ufcg.analytics.meliorbusao.Constants;
import br.edu.ufcg.analytics.meliorbusao.db.DBUtils;
import br.edu.ufcg.analytics.meliorbusao.models.NearStop;
import br.edu.ufcg.analytics.meliorbusao.models.Route;
import br.edu.ufcg.analytics.meliorbusao.models.Stop;
import br.edu.ufcg.analytics.meliorbusao.utils.MathUtils;
import br.edu.ufcg.analytics.meliorbusao.utils.StopRouteUtils;

public class RouteDetector {
    public static final int LATITUDE = 0;
    public static final int LONGITUDE = 1;

    private Context mContext;
    private Set<Route> routeIds;
    private TreeSet<NearStop> stops;

    //detection v2
    private double[] detectedLocation;
    private boolean firstDetection;

    private Set<Route> todasRotas;
    private Map<String, Integer> rankingParadas = new TreeMap<String, Integer>();
    private int primeiroColocado = 0, segundoColocado = 1, terceiroColocado = 2, quartoColocado = 3, minimoDeParadasDetectadas = 5;
    private Set<Route> detectedRoutes;



    public RouteDetector(Context context) {
        mContext = context;
        initialize();
    }

    public void initialize() {
        firstDetection = false;
        routeIds = new HashSet<>();
        stops = new TreeSet<>();
        detectedLocation = new double[2];

        //detection v2
        todasRotas = DBUtils.getTodasAsRotas(mContext);
        for (Route rID : todasRotas)
            rankingParadas.put(rID.toString(), 0);
    }

    public Set<Route> processLocation(double latitude, double longitude) {
        setDetectedLocation(latitude, longitude);

        if (!hasFirstDetection()) {
            return initialDetection(latitude, longitude);
        }

        for (Stop stop : stops) {
            if (MathUtils.getDistanceBetweenTwoPoints(latitude, longitude, stop) < Constants.STOP_DETECTION_RADIUS) {
//                routeIds.retainAll(stop.getRoutes());

                stops = DBUtils.getNearStops(mContext, latitude, longitude,
                        Constants.NEAR_STOPS_RADIUS, routeIds);
                for (Route ro : stop.getRoutes()) {
                    if (rankingParadas.containsKey(ro.toString())) {
                        rankingParadas.put(ro.toString(), rankingParadas.get(ro.toString()) + 1);
                    }
                }
                rankingParadas = sortByComparator(rankingParadas);


                break;
            }
            //detection v2 (nao existia anteriormente aqui fora)
            stops = DBUtils.getNearStops(mContext, latitude, longitude,
                    Constants.NEAR_STOPS_RADIUS, routeIds);
        }
        //detection v2
        detectMostProbableRoute();

        return detectedRoutes;
        //return routeIds;
    }

    private Set<Route> initialDetection(double latitude, double longitude) {
        stops = DBUtils.getNearStops(mContext, latitude, longitude, Constants.NEAR_STOPS_RADIUS, null);
        firstDetection = true;

        return (routeIds = StopRouteUtils.getRoutesFromStops(stops));
    }

    private void setDetectedLocation(double latitude, double longitude) {
        detectedLocation[LATITUDE] = latitude;
        detectedLocation[LONGITUDE] = longitude;
    }

    public boolean hasFirstDetection() {
        return firstDetection;
    }

    public double getLastDetectedLatitude() {
        return detectedLocation[LATITUDE];
    }

    public double getLastDetectedLongitude() {
        return detectedLocation[LONGITUDE];
    }

    //detection v2
    private void detectMostProbableRoute() {
        detectedRoutes = routeIds;
        if (Integer.valueOf(rankingParadas.values().toArray()[primeiroColocado].toString()) > minimoDeParadasDetectadas)
            if (Integer.valueOf(rankingParadas.values().toArray()[primeiroColocado].toString()) >= Integer.valueOf(rankingParadas.values().toArray()[segundoColocado].toString()) * 2) {
                for (Route r : todasRotas)
                    if ((r.getId().toString() == rankingParadas.keySet().toArray()[primeiroColocado])) {
                        detectedRoutes.removeAll(detectedRoutes);
                        detectedRoutes.add(r);
                    }
            } else {
                detectedRoutes.removeAll(detectedRoutes);
                for (Route r : todasRotas) {
                    if ((r.getId().toString() == rankingParadas.keySet().toArray()[primeiroColocado])
                            || (r.getId().toString() == rankingParadas.keySet().toArray()[segundoColocado]
                            && Integer.valueOf(rankingParadas.values().toArray()[primeiroColocado].toString()) <= Integer.valueOf(rankingParadas.values().toArray()[segundoColocado].toString()) * 2)
                            || (r.getId().toString() == rankingParadas.keySet().toArray()[terceiroColocado]
                            && Integer.valueOf(rankingParadas.values().toArray()[primeiroColocado].toString()) <= Integer.valueOf(rankingParadas.values().toArray()[terceiroColocado].toString()) * 1.5)
                            || (r.getId().toString() == rankingParadas.keySet().toArray()[quartoColocado]
                            && Integer.valueOf(rankingParadas.values().toArray()[primeiroColocado].toString()) <= Integer.valueOf(rankingParadas.values().toArray()[quartoColocado].toString()) * 1.4))
                        detectedRoutes.add(r);
                }
            }
    }
    //ordena o mapa em ordem decrescente (e2.comparteTo(e1)), inverter pra inverter a ordem...
    private static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap) {

        // Convert Map to List
        List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

        // Sort list with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // Convert sorted map back to a Map
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext(); ) {
            Map.Entry<String, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
}
