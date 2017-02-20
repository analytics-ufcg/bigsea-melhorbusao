package br.edu.ufcg.analytics.meliorbusao.utils;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.edu.ufcg.analytics.meliorbusao.models.LocationHolder;
import br.edu.ufcg.analytics.meliorbusao.db.DBUtils;
import br.edu.ufcg.analytics.meliorbusao.listeners.OnStopTimesReadyListener;
import br.edu.ufcg.analytics.meliorbusao.listeners.OnSumarioRotasReadyListener;
import br.edu.ufcg.analytics.meliorbusao.listeners.OnUserLocationReadyListener;
import br.edu.ufcg.analytics.meliorbusao.models.Avaliacao;
import br.edu.ufcg.analytics.meliorbusao.models.CategoriaResposta;
import br.edu.ufcg.analytics.meliorbusao.models.NearStop;
import br.edu.ufcg.analytics.meliorbusao.models.Resposta;
import br.edu.ufcg.analytics.meliorbusao.models.Route;
import br.edu.ufcg.analytics.meliorbusao.models.StopHeadsign;
import br.edu.ufcg.analytics.meliorbusao.models.StopTime;
import br.edu.ufcg.analytics.meliorbusao.models.SumarioRota;

public class ParseUtils {
    public static final String TAG = "ParseUtils";
    private static Context mContext;

    public static final String RATINGS_TABLE = "Rating";
    private static List<ParseObject> allRatings = new ArrayList<>();
    private static final int QUERY_MAX_LIMIT = 1000;

    private static void getAllRatingsFromServer() {
        final ParseQuery ratingQuery = new ParseQuery(RATINGS_TABLE);
        ratingQuery.setLimit(QUERY_MAX_LIMIT);
        int skip = 0;
        ratingQuery.findInBackground(getAllRatings(skip));
    }

    private static FindCallback getAllRatings(int skip){
        final int newSkip =  skip + QUERY_MAX_LIMIT;
        FindCallback callback = new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "done: entrou aqui 1");
                    ParseUtils.allRatings.addAll(objects);
                    Log.d(TAG, "   " + allRatings.size() + "  "+ objects.size());
                    if (objects.size() == QUERY_MAX_LIMIT) {
                        Log.d(TAG, "done: entrou aqui 2");
                        ParseQuery query = new ParseQuery(RATINGS_TABLE);
                        query.setSkip(newSkip);
                        query.setLimit(QUERY_MAX_LIMIT);
                        query.findInBackground(getAllRatings(newSkip));
                    }
                    //We have a full PokeDex
                    else {
                        Log.d(TAG, "done: entrou aqui 3");
                        //USE FULL DATA AS INTENDED
                    }
                }
            }
        };
        Log.d(TAG, " xixiixixiix  " + allRatings.size());
        return callback;
    }

    public static List<ParseObject> getRatings() {
        getAllRatingsFromServer();
        if (allRatings.isEmpty()) {
            Log.d(TAG, "getRatings: vazio");
        }
        for (ParseObject po : allRatings) {
            Log.d("$$$$$$$$$", "getRatings: " + po.getString("rota"));
        }
        return allRatings;
    }



    /**
     * Transforma a avaliação num objeto do parse e insere no bd
     *
     * @param avaliacao
     * @param tripId
     */
    public static void insereAvaliacao(Avaliacao avaliacao, String tripId) {
        avaliacao.toParseObject(tripId).saveEventually();
    }

    /**
     * Transforma a localização do usuario num objeto do parse e insere no bd
     *
     * @param location
     * @param userId
     * @param tripId
     */
    public static void addUserLocation(LocationHolder location, String userId, String tripId) {
        location.toParseObject(String.valueOf(userId), tripId).saveEventually();
    }


    /**
     * Pega o sumário de uma rota, preenche o sumario criando o objeto e insere no bd
     *
     * @param id
     * @param media
     * @param lotacao
     * @param motorista
     * @param condition
     */
    private static void preencheSumario(final String id, final double media, final double lotacao, final double motorista, final double condition) {
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Sumario");
        query.orderByDescending("_created_at");
        try {
            query.whereEqualTo("rota", id);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> listaObjetos, ParseException e) {
                    if (e == null) {
                        final ParseObject parseRatingObject = new ParseObject("Sumario");
                        if (listaObjetos.size() == 0) {
                            try {
                                parseRatingObject.put("rota", id);
                                parseRatingObject.put("media", media);
                                parseRatingObject.put("motorista", motorista);
                                parseRatingObject.put("lotacao", lotacao);
                                parseRatingObject.put("condition", condition);
                                parseRatingObject.saveEventually();
                            } catch (Exception ex) {
                                Log.d(TAG, "Erro ao gravar sumario " + ex.getMessage());
                            }
                        } else {
                            ParseQuery<ParseObject> queryUpdate = ParseQuery.getQuery("Sumario");
                            // Retrieve the object by id
                            queryUpdate.getInBackground(listaObjetos.get(0).getObjectId(), new GetCallback<ParseObject>() {
                                public void done(ParseObject parseRatingObject, ParseException e) {
                                    if (e == null) {
                                        parseRatingObject.put("media", media);
                                        parseRatingObject.put("motorista", motorista);
                                        parseRatingObject.put("lotacao", lotacao);
                                        parseRatingObject.put("conditon", condition);
                                        parseRatingObject.saveEventually();
                                    }
                                }
                            });
                        }
                    }
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "ERRO::::::: " + id + " " + e.getMessage());
        }
    }

    /**
     * Salva cada localização do usuario no bd
     *
     * @param lat
     * @param lon
     * @param acc
     * @param speed
     * @param bearing
     * @param time
     * @param userId
     * @param tripId
     */
    private static void preencheLocalizacao(final double lat, final double lon, final float acc, final float speed, final float bearing, final double time, final String userId, final String tripId) {
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Localizacao");
        query.orderByDescending("_created_at");
        try {
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> listaObjetos, ParseException e) {
                    if (e == null) {
                        final ParseObject parseLocationObject = new ParseObject("Localizacao");
                        try {
                            parseLocationObject.put("latitude", lat);
                            parseLocationObject.put("latitude", lon);
                            parseLocationObject.put("precisao", acc);
                            parseLocationObject.put("velocidade", speed);
                            parseLocationObject.put("rolamento", bearing);
                            parseLocationObject.put("horario", time);
                            parseLocationObject.put("userId", userId);
                            parseLocationObject.put("tripId", tripId);
                            parseLocationObject.saveEventually();
                        } catch (Exception ex) {
                            Log.d(TAG, "Erro ao gravar sumario " + ex.getMessage());
                        }
                    }
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "ERRO::::::: " + e.getMessage());
        }
    }

    /**
     * Calcula o sumario de todas as rotas
     *
     * @param context
     */
    public static void calculaSumario(Context context) {
        Set<Route> todasRotas = DBUtils.getTodasAsRotas(context);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Rating");
        query.orderByDescending("_created_at");
        for (final Route rID : todasRotas) {
            try {
                query = ParseQuery.getQuery("Rating");

                query.whereEqualTo("rota", rID.getId());
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> scoreList, ParseException e) {
                        if (e == null) {
                            if (scoreList.size() > 0) {
                                double media = 0;
                                double totalLotacao = 0, totalMotorista = 0, totalCondition = 0;
                                for (ParseObject i : scoreList) {
                                    media += Integer.valueOf(i.get("nota").toString());
                                    if (i.getBoolean("motorista"))
                                        totalMotorista += 1;
                                    if (i.getBoolean("lotacao"))
                                        totalLotacao += 1;

                                }
                                preencheSumario(rID.getId(), media / scoreList.size(), 100 * (totalLotacao / scoreList.size()), 100 * (totalMotorista / scoreList.size()), totalCondition);
                            }
                        } else {
                            Log.d(TAG, "score: Error: " + rID.getId() + " " + e.getMessage());
                        }

                    }
                });
            } catch (Exception e) {
                Log.d(TAG, "ERRO::::::: " + rID.getId() + " " + e.getMessage());

            }
        }
    }

    /**
     * Recupera do banco as informações de localização do usuario
     *
     * @param userID
     * @param listener
     */
    public static void getUserLocation(String userID, final OnUserLocationReadyListener listener) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserLocation");
        query.whereEqualTo("userId", userID);
        final ArrayList<LocationHolder> locations = new ArrayList<>();

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> locationList, ParseException e) {
                Log.d("PARSE", String.valueOf(locationList.size()));


                if (e == null) {
                    for (ParseObject obj : locationList) {

                        Location loc = new Location("");

                        loc.setLatitude((Double) obj.get("latitude"));
                        loc.setLongitude((Double) obj.get("longitude"));
                        String acc = String.valueOf(obj.get("accuracy"));
                        loc.setAccuracy(Float.parseFloat(acc));
                        String speed = String.valueOf(obj.get("speed"));
                        loc.setSpeed(Float.parseFloat(speed));
                        String bear = String.valueOf(obj.get("bearing"));
                        loc.setBearing(Float.parseFloat(bear));
                        loc.setTime((Long) obj.get("time"));

                        LocationHolder locationHolder = new LocationHolder(loc);
                        locationHolder.setTripId((String) obj.get("tripId"));

                        locations.add(locationHolder);

                        Log.d("PARSE", String.valueOf(locationHolder));


                    }
                }

                listener.OnUserLocationReady(locations, e);

            }
        });
    }

    /**
     * Recupera do banco as informações de localização do usuario em uma determinada viagem
     *
     * @param userID
     * @param tripId
     * @param listener
     */
    public static void getUserLocation(String userID, final String tripId, final OnUserLocationReadyListener listener) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserLocation");
        query.whereEqualTo("userId", userID);
        query.whereEqualTo("tripId", tripId);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> locationList, ParseException e) {
                ArrayList<LocationHolder> locations = new ArrayList<>();
                if (e == null) {
                    for (ParseObject obj : locationList) {

                        Location loc = new Location("");

                        loc.setLatitude((Double) obj.get("latitude"));
                        loc.setLongitude((Double) obj.get("longitude"));
                        loc.setAccuracy((Float) obj.get("accuracy"));
                        loc.setSpeed((Float) obj.get("speed"));
                        loc.setBearing((Float) obj.get("bearing"));
                        loc.setTime((Long) obj.get("time"));

                        LocationHolder locationHolder = new LocationHolder(loc);
                        locationHolder.setTripId(tripId);

                        locations.add(locationHolder);

                    }
                }

                listener.OnUserLocationReady(locations, e);
            }
        });
    }

    /**
     * Retorna o sumario de todas as rotas
     *
     * @param context
     * @param listener
     */
    public static void getSumario(Context context, final OnSumarioRotasReadyListener listener) {
        try {
            final HashSet<Route> rotas = DBUtils.getTodasAsRotas(context);
            getSummaryRoutes(listener, rotas);
        } catch (Exception e) {

        }
    }


    /**
     * Retorna o sumario de todas as rotas
     *  @param listener
     * @param rotas
     */
    public static void getSummaryRoutes(final OnSumarioRotasReadyListener listener, final HashSet<Route> rotas) {
        ParseCloud.callFunctionInBackground("getAllSummaries", new HashMap<String, Object>(), new FunctionCallback<Map<String, Map<String, Integer>>>() {
            @Override
            public void done(Map<String, Map<String, Integer>> object, ParseException e) {
                if (e == null) {
                    ArrayList<SumarioRota> sumarios = new ArrayList<>();

                    for (Route route : rotas) {
                        SumarioRota sumarioRota = new SumarioRota(route);

                        if (object.keySet().contains(String.valueOf(route))){

                            Map<String, Integer> resultado = object.get(String.valueOf(route));


                            int count = resultado.get("count");

                            if (count > 0) {
                                sumarioRota.setAvaliada(true);
                                sumarioRota.computarResposta(new Resposta(CategoriaResposta.ID_CATEGORIA_VIAGEM, resultado.get("media")), Long.MAX_VALUE, count);
                                //Lotação é uma variavel cujo valor representa a quantidade de avaliações onde o onibus não estava lotado
                                sumarioRota.computarResposta(new Resposta(CategoriaResposta.ID_CATEGORIA_LOTACAO, resultado.get("totalLotacao")), Long.MAX_VALUE, count);
                                sumarioRota.computarResposta(new Resposta(CategoriaResposta.ID_CATEGORIA_MOTORISTA, resultado.get("totalMotorista")), Long.MAX_VALUE, count);
                                sumarioRota.computarResposta(new Resposta(CategoriaResposta.ID_CATEGORY_CONDITION, resultado.get("totalCondition")), Long.MAX_VALUE, count);
                            }
                        }
                        sumarios.add(sumarioRota);
                    }

                    listener.onSumarioRotasReady(sumarios, null);
                } else {
                    listener.onSumarioRotasReady(null, e);
                    Log.d(TAG, e.getMessage());
                }
            }
        });

    }

    /**
     * Retorna os horarios de uma rota em uma parada
     *
     * @param stopId
     * @param routeId
     * @param listener
     */
    public static void getRouteSchedules(final int stopId, final String routeId, final OnStopTimesReadyListener listener) {
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_WEEK);
        final String diaS;
        int hour = c.get(Calendar.HOUR_OF_DAY);
        final int minutes = c.get(Calendar.MINUTE);

        final SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");

        switch (day) {
            case 1:
                diaS = "domingo";
                break;
            case 7:
                diaS = "sabado";
                break;
            default:
                diaS = "diasUteis";
                break;
        }

        Calendar time = Calendar.getInstance();
        time.set(1993, 8, 30);

        HashMap<String, Object> routeSchedule = new HashMap<String, Object>();
        routeSchedule.put("stopId", stopId);
        routeSchedule.put("diaS", diaS);
        routeSchedule.put("from", time.getTime());
        time.add(Calendar.HOUR_OF_DAY, 1);
        routeSchedule.put("to", time.getTime());
        routeSchedule.put("routeId", routeId);


        ParseCloud.callFunctionInBackground("horarios", routeSchedule, new FunctionCallback<ArrayList<Date>>() {
            @Override
            public void done(ArrayList<Date> object, ParseException e) {
                List<StopTime> stopTimes = new ArrayList<>();
                if (e == null) {
                    for (Date arrivalTime : object) {
                        StopTime stopTime = new StopTime(diaS, routeId, stopId, arrivalTime, "");
                        stopTimes.add(stopTime);
                    }
                } else {
                    Log.d(TAG, e.getMessage());
                }

                List<StopTime> uniquesStopTime = new ArrayList<>();
                for (StopTime element : stopTimes) {
                    if (!uniquesStopTime.contains(element)) {
                        uniquesStopTime.add(element);
                    }
                }

                Collections.sort(uniquesStopTime);

                listener.onStopTimesReady(uniquesStopTime, e);
            }

        });


    }

    /**
     * Retorna o objeto StopTime de uma rota especifica
     */
    public static void getStopTime(final Route route, final NearStop nearStop, final OnStopTimesReadyListener listener) {
          ParseQuery<ParseObject> query = ParseQuery.getQuery("StopTime");
        query.whereEqualTo("routeId", route.getId());
        query.whereEqualTo("stopId", nearStop.getId());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject stopTime, ParseException e) {
                StopHeadsign stopHeadsignObj = null;
                if (e == null) {
                    String serviceId = String.valueOf(stopTime.get("serviceId"));
                    Date arrivalTime = (Date) stopTime.get("arrivalTime");
                    String stopHeadsign = String.valueOf(stopTime.get("stopHeadsign"));

                    StopTime st = new StopTime(serviceId, route.getId(), nearStop.getId(), arrivalTime, stopHeadsign);
                    stopHeadsignObj = new StopHeadsign(route, st, nearStop);

                }
                listener.onStopHeadsignReady(stopHeadsignObj, e);
            }

        });
    }

    public static List<StopTime> getRouteScheduleCuritiba(Context context, final String routeId, final int stopId) {

        final List<StopTime> stopTimes = new ArrayList<>(100);

        final Calendar now = Calendar.getInstance();
        final int hour = now.get(Calendar.HOUR_OF_DAY);
        final int minutes = now.get(Calendar.MINUTE);

        final int day = now.get(Calendar.DAY_OF_WEEK);
        final String dayS;


        switch (day) {
            case 1:
                dayS = "3";
                break;
            case 7:
                dayS = "2";
                break;
            default:
                dayS = "1";
                break;
        }

        ParseQuery<ParseObject> query = ParseQuery.getQuery("StopTime");
        query.whereEqualTo("routeId", routeId);
        query.whereEqualTo("stopId", stopId);
        query.whereEqualTo("serviceId", dayS);

//        for (ParseObject stopTime: query) {
//
//            String serviceId = dayS;
//            Date arrivalTime = (Date) stopTime.get("arrivalTime");
//            //String stopHeadsign = String.valueOf(stopTime.get("stopHeadsign"));
//
//            Long timeStop = arrivalTime.getTime();
//
//            long secondsStop = timeStop / 1000;
//            long minutesStop = secondsStop / 60;
//            long hoursStop = minutesStop / 60;
//
//            if (minutes <= minutesStop || (minutes >= minutesStop && hour + 1 == hoursStop)) {
//                stopTimes.add(new StopTime(serviceId, routeId, stopId, arrivalTime, ""));
//            }
//        }

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> stopTimeList, ParseException e) {
                if (e == null) {
                    for (ParseObject stopTime : stopTimeList) {
                        String serviceId = dayS;
                        Date arrivalTime = (Date) stopTime.get("arrivalTime");
                        //String stopHeadsign = String.valueOf(stopTime.get("stopHeadsign"));

                        Long timeStop = arrivalTime.getTime();

                        long secondsStop = timeStop / 1000;
                        long minutesStop = secondsStop / 60;
                        long hoursStop = minutesStop / 60;

                        if (minutes <= minutesStop || (minutes >= minutesStop && hour + 1 == hoursStop)) {
                            stopTimes.add(new StopTime(serviceId, routeId, stopId, arrivalTime, ""));
                        }

                    }

                } else {
                    Log.d("product", "Error: " + e.getMessage());
                }

            }
        });

        return stopTimes;

    }


}
