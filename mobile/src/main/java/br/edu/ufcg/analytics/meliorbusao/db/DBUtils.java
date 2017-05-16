package br.edu.ufcg.analytics.meliorbusao.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.PointF;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.osmdroid.util.GeoPoint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

import br.edu.ufcg.analytics.meliorbusao.Cities;
import br.edu.ufcg.analytics.meliorbusao.Constants;
import br.edu.ufcg.analytics.meliorbusao.models.LocationData;
import br.edu.ufcg.analytics.meliorbusao.models.Avaliacao;
import br.edu.ufcg.analytics.meliorbusao.models.NearStop;
import br.edu.ufcg.analytics.meliorbusao.models.Resposta;
import br.edu.ufcg.analytics.meliorbusao.models.Route;
import br.edu.ufcg.analytics.meliorbusao.models.RouteShape;
import br.edu.ufcg.analytics.meliorbusao.models.Stop;
import br.edu.ufcg.analytics.meliorbusao.models.StopTime;
import br.edu.ufcg.analytics.meliorbusao.models.SumarioRota;
import br.edu.ufcg.analytics.meliorbusao.utils.MathUtils;
import br.edu.ufcg.analytics.meliorbusao.utils.SharedPreferencesUtils;

import static br.edu.ufcg.analytics.meliorbusao.db.MeliorDBOpenHelper.getNonPublishedRatingsTable;

public class DBUtils {

    protected DBUtils() {
    }

    private static final String TAG = "DBUtils";
    private static String bd_path = "";


    public static String getTAG() {
        return TAG;
    }

    /**
     * Abre o banco apenas para leitura
     *
     * @param context
     * @return
     */
    private static SQLiteDatabase getReadableDatabase(Context context) {
        if (SharedPreferencesUtils.getCityNameOnDatabase(context).equals(Cities.CAMPINA_GRANDE.getCityName())) {
            bd_path = Environment.getExternalStorageDirectory() + Constants.BD_PATH + Constants.BD_CG;
        } else if (SharedPreferencesUtils.getCityNameOnDatabase(context).equals(Cities.CURITIBA.getCityName())) {
            bd_path = Environment.getExternalStorageDirectory() + Constants.BD_PATH + Constants.BD_CTBA;
        }

        Log.d("DBUtils>ReadingDB", bd_path);
//        MeliorDBOpenHelper openHelper = new MeliorDBOpenHelper(context);
//        return openHelper.getReadableDatabase();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(bd_path, null, 0);
        return db;
    }

    /**
     * Abre o banco para escrita
     *
     * @param context
     * @return
     */
    private static SQLiteDatabase getWritableDatabase(Context context) {
//        MeliorDBOpenHelper openHelper = new MeliorDBOpenHelper(context);
//        return openHelper.getWritableDatabase();

        if (SharedPreferencesUtils.getCityNameOnDatabase(context).equals(Cities.CAMPINA_GRANDE.getCityName())) {
            bd_path = Environment.getExternalStorageDirectory() + Constants.BD_PATH + Constants.BD_CG;
        } else if (SharedPreferencesUtils.getCityNameOnDatabase(context).equals(Cities.CURITIBA.getCityName())) {
            bd_path = Environment.getExternalStorageDirectory() + Constants.BD_PATH + Constants.BD_CTBA;
        }
        SQLiteDatabase db = SQLiteDatabase.openDatabase(bd_path, null, 0);
        return db;
    }

    /**
     * Adiciona uma Location(s) na tabela LocationsTable
     *
     * @param context
     * @param locations
     * @param timestamp
     * @return
     */
    public static boolean addLocation(Context context, ArrayList<LocationData> locations, long timestamp) {
        SQLiteDatabase db = getWritableDatabase(context);
        Table locationsTable = MeliorDBOpenHelper.getLocationsTable();
        boolean result = false;

        // inicia a transação no banco
        db.beginTransaction();
        try {
            for (LocationData location : locations) {

                ContentValues valoresAvaliacao = new ContentValues();
                valoresAvaliacao.put("lat", location.getLatitude());
                valoresAvaliacao.put("lon", location.getLongitude());
                valoresAvaliacao.put("acc", location.getAccuracy());
                valoresAvaliacao.put("speed", location.getSpeed());
                valoresAvaliacao.put("bear", location.getBearing());
                valoresAvaliacao.put("et", location.getTime());
                valoresAvaliacao.put("timestamp", timestamp);

                db.insert(locationsTable.getName(), null, valoresAvaliacao);
            }

            db.setTransactionSuccessful();
            result = true;
        } finally {
            db.endTransaction();
        }

        db.close();

        return result;
    }


    /**
     * Adiciona Avaliação na tabela AvaliacaoTable
     *
     * @param context
     * @param avaliacao
     * @return
     */
    public static boolean addAvaliacao(Context context, Avaliacao avaliacao) {
        SQLiteDatabase db = getWritableDatabase(context);
        Table avaliacaoTable = MeliorDBOpenHelper.getAvaliacaoTable();
        boolean result = false;

        // inicia a transação no banco
        db.beginTransaction();
        try {
            ContentValues valoresAvaliacao = new ContentValues();

            valoresAvaliacao.put("timestamp", avaliacao.getTimestamp());
            valoresAvaliacao.put("rota", avaliacao.getRota());

            db.insert(avaliacaoTable.getName(), null, valoresAvaliacao);

            for (Resposta resposta : avaliacao.getRespostas()) {
                // adiciona todas as respostas da avaliação na tabela RespostasTable (addRespota)
                addResposta(db, avaliacao.getTimestamp(), resposta);
            }

            db.setTransactionSuccessful();
            result = true;
        } finally {
            db.endTransaction();
        }

        db.close();

        return result;
    }

    /**
     * Adiciona Resposta na tabela RespostaTable
     *
     * @param db
     * @param timestampAvalicao
     * @param resposta
     */
    private static void addResposta(SQLiteDatabase db, long timestampAvalicao, Resposta resposta) {
        Table respostaTable = MeliorDBOpenHelper.getRespostaTable();

        ContentValues valoresResposta = new ContentValues();

        valoresResposta.put("categoria", resposta.getCategoria());
        valoresResposta.put("timestamp", timestampAvalicao);
        valoresResposta.put("valor", resposta.getValor());

        db.insert(respostaTable.getName(), null, valoresResposta);
    }

    /**
     * Retorna todas as avaliações da tabela AvaliacaoTable
     *
     * @param context
     * @return
     */
    public static ArrayList<Avaliacao> getTodasAsAvaliacoes(Context context) {
        SQLiteDatabase db = getReadableDatabase(context);
        Table avaliacaoTable = MeliorDBOpenHelper.getAvaliacaoTable();
        Table respostasTable = MeliorDBOpenHelper.getRespostaTable();

        ArrayList<Avaliacao> avaliacoes = new ArrayList<>();

        //Pega apenas avaliações preenchidas
        String[] queryArgs = {String.valueOf(1)};
        Cursor c = db.query(avaliacaoTable.getName(), null, "preenchida=?", queryArgs, null, null, null, null);

        c.moveToFirst();
        while (!c.isAfterLast()) {
            String rota = c.getString(c.getColumnIndex("rota"));
            long timestamp = c.getLong(c.getColumnIndex("timestamp"));
            // cria o obejto Avaliação
            Avaliacao av = new Avaliacao(timestamp, rota);
            String[] args = {String.valueOf(timestamp)};
            Cursor respostaCursor = db.query(respostasTable.getName(), null, "timestamp=?", args, null, null, null, null);
            respostaCursor.moveToFirst();
            // Recupera do banco todas as respostas de cada avaliação
            while (!respostaCursor.isAfterLast()) {
                int categoria = respostaCursor.getInt(respostaCursor.getColumnIndex("categoria"));
                int valor = respostaCursor.getInt(respostaCursor.getColumnIndex("valor"));
                av.addResposta(new Resposta(categoria, valor));

                respostaCursor.moveToNext();
            }

            respostaCursor.close();
            avaliacoes.add(av);

            c.moveToNext();
        }

        c.close();
        db.close();

        return avaliacoes;
    }

    /**
     * Retorna todas as rotas avaliadas
     *
     * @param context
     * @return
     */
    public static HashSet<Route> getRotasAvaliadas(Context context) {
        SQLiteDatabase db = getReadableDatabase(context);

        HashSet<Route> rotas = new HashSet<>();

        String query = "SELECT avaliacao.preenchida, avaliacao.rota, " +
                "route.id, route.short_name, route.long_name, route.color, route.line_name " +
                "FROM avaliacao, route WHERE avaliacao.rota = route.id AND preenchida=1";

        Cursor c = db.rawQuery(query, null);

        c.moveToFirst();
        while (!c.isAfterLast()) {
            String id = c.getString(c.getColumnIndex("id"));
            String shortName = c.getString(c.getColumnIndex("short_name"));
            String longName = c.getString(c.getColumnIndex("long_name"));
            String color = c.getString(c.getColumnIndex("color"));
            String lineName = c.getString(c.getColumnIndex("line_name"));

            Route route = new Route(id, shortName, longName, color, lineName);
            rotas.add(route);

            c.moveToNext();
        }

        c.close();
        db.close();


        return rotas;
    }

    /**
     * Retorna todas as rotas
     *
     * @param context
     * @return
     */
    public static HashSet<Route> getTodasAsRotas(Context context) {
        SQLiteDatabase db = getReadableDatabase(context);
        Table rotasTable = MeliorDBOpenHelper.getRouteTable();

        HashSet<Route> rotas = new HashSet<>();

        String[] columns = {"id", "short_name", "long_name", "color", "line_name", "main_stops"};
        Cursor c = db.query(true, rotasTable.getName(), columns, null, null, null, null, null, null);

        int idIndex = c.getColumnIndex("id");
        int shortNameIndex = c.getColumnIndex("short_name");
        int longNameIndex = c.getColumnIndex("long_name");
        int colorIndex = c.getColumnIndex("color");
        int lineNameIndex = c.getColumnIndex("line_name");
        int mainStopsIndex = c.getColumnIndex("main_stops");

        c.moveToFirst();
        while (!c.isAfterLast()) {
            rotas.add(new Route(c.getString(idIndex), c.getString(shortNameIndex),
                    c.getString(longNameIndex), c.getString(colorIndex), c.getString(lineNameIndex),
                    c.getString(mainStopsIndex)));

            c.moveToNext();
        }

        c.close();
        db.close();

        return rotas;
    }


    /**
     * Retorna as linhas dos ônibus (linha azul, linha laranja, linha cinza...)
     *
     * @param context
     * @return
     */
    public static HashSet<String> getLinhas(Context context) {
        SQLiteDatabase db = getReadableDatabase(context);
        Table rotasTable = MeliorDBOpenHelper.getRouteTable();

        HashSet<String> linhas = new HashSet<>();

        String[] columns = {"line_name"};
        Cursor c = db.query(true, rotasTable.getName(), columns, null, null, null, null, "line_name ASC", null);

        int lineNameIndex = c.getColumnIndex("line_name");

        c.moveToFirst();
        while (!c.isAfterLast()) {
            linhas.add(c.getString(lineNameIndex));

            c.moveToNext();
        }

        c.close();
        db.close();

        return linhas;
    }

    /**
     * Retorna todas a rota de uma linha
     *
     * @param context
     * @param nameLine
     * @return
     */
    public static HashSet<Route> getRotasPorLinha(Context context, String nameLine) {
        SQLiteDatabase db = getReadableDatabase(context);
        HashSet<Route> rotas = new HashSet<>();
        String[] args = {nameLine};

        Cursor c = db.rawQuery("SELECT route.id, route.short_name, route.long_name, route.color " +
                "FROM route " + "WHERE route.line_name = ?", args);

        c.moveToFirst();
        while (!c.isAfterLast()) {
            String id = c.getString(c.getColumnIndex("id"));
            String shortName = c.getString(c.getColumnIndex("short_name"));
            String longName = c.getString(c.getColumnIndex("long_name"));
            String color = c.getString(c.getColumnIndex("color"));

            Route route = new Route(id, shortName, longName, color);
            rotas.add(route);

            c.moveToNext();
        }

        c.close();
        db.close();
        return rotas;
    }

    /**
     * Seleciona a rota a partir do nome da rota
     *
     * @param context
     * @param routeShortName
     * @return
     */
    public static Route getRoute(Context context, String routeShortName) {
        SQLiteDatabase db = getReadableDatabase(context);
        Route route = null;
        String[] args = {routeShortName};

        Cursor c = db.rawQuery("SELECT route.id, route.short_name, route.long_name, route.color, " +
                "route.line_name, route.main_stops FROM route " + "WHERE route.short_name = ?", args);

        if (c.isAfterLast()) {
            c.close();
            db.close();
            return route;
        }

        c.moveToFirst();

        String id = c.getString(c.getColumnIndex("id"));
        String shortName = c.getString(c.getColumnIndex("short_name"));
        String longName = c.getString(c.getColumnIndex("long_name"));
        String color = c.getString(c.getColumnIndex("color"));
        String lineName = c.getString(c.getColumnIndex("line_name"));
        String mainStops = c.getString(c.getColumnIndex("main_stops"));

        route = new Route(id, shortName, longName, color, lineName, mainStops);

        c.close();
        db.close();
        return route;
    }

    /**
     * A partir de uma pesquisa, retorna a posição do cursor
     *
     * @param context
     * @param query
     * @return
     */
    public static Cursor getRouteCursor(Context context, String query) {
        Cursor c;

        if (query == null) {
            return new MatrixCursor(
                    new String[]{"_id", "short_name", "long_name", "color"});
        } else if (query.equals("")) {
            String[] args = new String[]{};
            SQLiteDatabase db = getReadableDatabase(context);
            c = db.rawQuery("SELECT route.id _id, route.short_name, route.long_name, route.color, " +
                    "route.line_name, route.main_stops FROM route ORDER BY route.short_name", args);
        } else {
            String[] args = new String[]{"%" + query + "%"};
            SQLiteDatabase db = getReadableDatabase(context);
            c = db.rawQuery("SELECT route.id _id, route.short_name, route.long_name, route.color, " +
                    "route.line_name, route.main_stops FROM route " + "WHERE route.short_name LIKE ? " +
                    "ORDER BY route.short_name", args);
        }
        return c;
    }

    /**
     * Retorna a avaliação geral e quantidade de votos de uma rota
     *
     * @param context
     * @param rota
     * @return
     */
    public static SumarioRota getSumarioRota(Context context, Route rota) {
        SQLiteDatabase db = getReadableDatabase(context);

        SumarioRota sumarioRota = new SumarioRota(rota);

        String[] args = {rota.getId()};
        Cursor c = db.rawQuery("SELECT resposta.categoria AS categoria, resposta.valor AS valor," +
                " resposta.timestamp AS timestamp" +
                " FROM resposta, avaliacao " +
                " WHERE resposta.timestamp = avaliacao.timestamp AND avaliacao.rota = ?", args);

        c.moveToFirst();
        while (!c.isAfterLast()) {
            sumarioRota.setAvaliada(true);
            int categoria = c.getInt(c.getColumnIndex("categoria"));
            int valor = c.getInt(c.getColumnIndex("valor"));
            long timestamp = c.getLong(c.getColumnIndex("timestamp"));

            Resposta r = new Resposta(categoria, valor);
            sumarioRota.computarResposta(r, timestamp);

            c.moveToNext();
        }

        c.close();
        db.close();

        return sumarioRota;
    }

    /**
     * Retorna a avaliação geral e quantidade de votos de todas as rotas avaliadas
     *
     * @param context
     * @return
     */
    public static ArrayList<SumarioRota> getSumarioRotasAvaliadas(Context context) {
        HashSet<Route> rotasAvaliadas = getRotasAvaliadas(context);
        ArrayList<SumarioRota> sumarioRotas = new ArrayList<>();

        for (Route rota : rotasAvaliadas) {
            sumarioRotas.add(getSumarioRota(context, rota));
        }

        return sumarioRotas;
    }

    /**
     * a avaliação geral e quantidade de votos de TODAS as rotas
     *
     * @param context
     * @return
     */
    public static ArrayList<SumarioRota> getSumarioTodasAsRotas(Context context) {
        HashSet<Route> rotasAvaliadas = getTodasAsRotas(context);
        ArrayList<SumarioRota> sumarioRotas = new ArrayList<>();

        for (Route rota : rotasAvaliadas) {
            sumarioRotas.add(getSumarioRota(context, rota));
        }

        return sumarioRotas;
    }

    /**
     * Adiciona no banco de dados (apenas na tabela parada) uma parada a uma rota
     *
     * @param db
     * @param stops
     * @return
     */
    public static boolean addStop(SQLiteDatabase db, HashMap<Integer, Stop> stops) {
        Table stopTable = MeliorDBOpenHelper.getStopTable();
        boolean result = true;

        for (Stop stop : stops.values()) {
            ContentValues stopData = new ContentValues();

            stopData.put("id", stop.getId());
            stopData.put("name", stop.getName());
            stopData.put("desc", stop.getDescription());
            stopData.put("lat", stop.getLatitude());
            stopData.put("lon", stop.getLongitude());

            try {
                db.insert(stopTable.getName(), null, stopData);
            } catch (Exception e) {
                result = false;
            }
        }

        return result;
    }

    /**
     * Adiciona no banco de dados (na tabela parada e rota) uma parada a uma rota
     *
     * @param db
     * @param routeStops
     * @return
     */
    public static boolean addRouteStop(SQLiteDatabase db, HashMap<String, ArrayList<Integer>> routeStops) {
        Table routeStopTable = MeliorDBOpenHelper.getRouteStopTable();
        boolean result = true;

        for (String routeId : routeStops.keySet()) {
            ArrayList<Integer> stopIds = routeStops.get(routeId);

            for (int i = 0; i < stopIds.size(); i++) {
                int stopId = stopIds.get(i);

                ContentValues routeStopData = new ContentValues();

                int nextStopIndex = (i + 1 == stopIds.size()) ? 0 : i + 1;
                int nextStopId = stopIds.get(nextStopIndex);

                routeStopData.put("id_route", routeId);
                routeStopData.put("id_stop", stopId);
                routeStopData.put("next_stop", nextStopId);

                try {
                    db.insert(routeStopTable.getName(), null, routeStopData);
                } catch (Exception e) {
                    result = false;
                }
            }
        }

        return result;
    }

    /**
     * Confirma se a adição do dado ao banco de dados foi feita com sucesso
     *
     * @param context
     * @param stops
     * @param routes
     * @param routeStops
     */
    public static void addStopRouteData(Context context, HashMap<Integer, Stop> stops,
                                        HashMap<String, Route> routes, HashMap<String, ArrayList<Integer>> routeStops) {
        SQLiteDatabase db = DBUtils.getWritableDatabase(context);
        db.beginTransaction();

        boolean success = addStop(db, stops) && addRoute(db, routes) && addRouteStop(db, routeStops);

        if (success) {
            db.setTransactionSuccessful();
        }

        db.endTransaction();
        db.close();

        SharedPreferencesUtils.setRoutesAndStopsOnDatabase(context, success);
    }

    /**
     * Confirma se a adição do dado ao banco de dados foi feita com sucesso
     *
     * @param context
     * @param stops
     * @param routes
     * @param routeStops
     */
    public static void addStopRouteData(Context context, List<Stop> stops, List<Route> routes,
                                        HashMap<String, ArrayList<Integer>> routeStops) {
        SQLiteDatabase db = DBUtils.getWritableDatabase(context);
        db.beginTransaction();

        boolean success = (addStopData(db, stops) && addRouteData(db, routes)) &&
                (addRouteStop(db, routeStops));

        if (success) {
            db.setTransactionSuccessful();
        }

        db.endTransaction();
        db.close();

        SharedPreferencesUtils.setRoutesAndStopsOnDatabase(context, success);
    }

    /**
     * Adiciona no banco de dados uma nova rota
     *
     * @param db
     * @param routes
     * @return
     */
    public static boolean addRoute(SQLiteDatabase db, HashMap<String, Route> routes) {
        Table routeTable = MeliorDBOpenHelper.getRouteTable();
        boolean result = true;

        for (Route route : routes.values()) {
            ContentValues routeData = new ContentValues();

            routeData.put("id", route.getId());
            routeData.put("short_name", route.getShortName());
            routeData.put("long_name", route.getLongName());
            routeData.put("color", route.getColor());
            routeData.put("line_name", route.getLineName());
            routeData.put("main_stops", route.getMainStops());

            try {
                db.insert(routeTable.getName(), null, routeData);
            } catch (Exception e) {
                result = false;
            }
        }

        return result;
    }

    /**
     * Adiciona no banco de dados uma nova rota
     *
     * @param db
     * @param routes
     * @return
     */
    public static boolean addRouteData(SQLiteDatabase db, List<Route> routes) {
        Table routeTable = MeliorDBOpenHelper.getRouteTable();

        boolean success = true;

        for (Route route : routes) {
            ContentValues routeData = new ContentValues();

            routeData.put("id", route.getId());
            routeData.put("short_name", route.getShortName());
            routeData.put("long_name", route.getLongName());
            routeData.put("color", route.getColor());
            routeData.put("line_name", route.getLineName());
            routeData.put("main_stops", route.getMainStops());

            try {
                db.insert(routeTable.getName(), null, routeData);
            } catch (Exception e) {
                Log.e(DBUtils.TAG, "Error on inserting new route: " + e.getMessage());
                success = false;
            }
        }

        return success;
    }

    /**
     * Adiciona no banco de dados (apenas na tabela parada) uma parada a uma rota
     *
     * @param db
     * @param stops
     * @return
     */
    public static boolean addStopData(SQLiteDatabase db, List<Stop> stops) {
        Table stopTable = MeliorDBOpenHelper.getStopTable();
        boolean success = true;

        for (Stop stop : stops) {
            ContentValues stopData = new ContentValues();

            stopData.put("id", stop.getId());
            stopData.put("name", stop.getName());
            stopData.put("desc", stop.getDescription());
            stopData.put("lat", stop.getLatitude());
            stopData.put("lon", stop.getLongitude());

            try {
                db.insert(stopTable.getName(), null, stopData);
            } catch (Exception e) {
                success = false;
            }
        }

        return success;
    }

    /**
     * Retorna as paradas de uma rota
     *
     * @param context
     * @param route
     * @return
     */
    public static HashSet<Stop> getParadasRota(Context context, Route route) {
        HashSet<Stop> stops = new HashSet<>();
        SQLiteDatabase db = getReadableDatabase(context);

        String[] args = {route.getId()};

        Cursor c = db.rawQuery("SELECT stop.id AS stop_id, stop.name AS stop_name, stop.desc AS stop_desc, " +
                "stop.lat AS stop_lat, stop.lon AS stop_lon " +
                "FROM stop, route, route_stop WHERE " +
                "route.id = ? AND route_stop.id_route = route.id AND route_stop.id_stop = stop_id", args);

        c.moveToFirst();
        while (!c.isAfterLast()) {
            int id = c.getInt(c.getColumnIndex("stop_id"));
            String name = c.getString(c.getColumnIndex("stop_name"));
            String desc = c.getString(c.getColumnIndex("stop_desc"));
            Double lat = c.getDouble(c.getColumnIndex("stop_lat"));
            Double lon = c.getDouble(c.getColumnIndex("stop_lon"));

            Stop stop = new Stop(id, name, desc, lat, lon);
            stops.add(stop);
            c.moveToNext();
        }

        c.close();
        db.close();

        return stops;
    }

    /**
     * Retorna as paradas proximas a uma dada localização
     *
     * @param context
     * @param latitude
     * @param longitude
     * @param radius
     * @param routes
     * @return
     */
    public static TreeSet<NearStop> getNearStops(Context context, double latitude, double longitude,
                                                 double radius, Collection<Route> routes) {
        TreeSet<NearStop> stops = new TreeSet<>();
        SQLiteDatabase db = getReadableDatabase(context);

        PointF center = new PointF((float) latitude, (float) longitude);
        final double mult = 1; // mult = 1.1; is more reliable
        PointF p1 = MathUtils.calculateDerivedPosition(center, mult * radius, 0);
        PointF p2 = MathUtils.calculateDerivedPosition(center, mult * radius, 90);
        PointF p3 = MathUtils.calculateDerivedPosition(center, mult * radius, 180);
        PointF p4 = MathUtils.calculateDerivedPosition(center, mult * radius, 270);

        StringBuilder routesExtraBuilder = new StringBuilder();

        if (routes != null) {
            routesExtraBuilder.append("AND (");
            for (Route route : routes) {
                routesExtraBuilder.append("route.id = '").append(route.getId()).append("' OR ");
            }
            routesExtraBuilder.setLength(routesExtraBuilder.length() - 4);
            routesExtraBuilder.append(") ");
        }

        String routesExtra = routesExtraBuilder.toString();

        String query = "SELECT stop.id AS stop_id, stop.name AS stop_name, stop.desc AS stop_desc, " +
                "stop.lat AS stop_lat, stop.lon AS stop_lon, " +
                "GROUP_CONCAT(route.id, ',') AS route_ids, " +
                "GROUP_CONCAT(route.short_name, ',') AS route_short_names, " +
                "GROUP_CONCAT(route.long_name, ',') AS route_long_names, " +
                "GROUP_CONCAT(route.color, ',') AS route_color, " +
                "GROUP_CONCAT(route.line_name, ',') AS route_line_name, " +
                "GROUP_CONCAT(route.main_stops, ',') AS route_main_stops " +
                "FROM stop, route, route_stop WHERE " +
                "stop_lat > " + String.valueOf(p3.x) + " AND " +
                "stop_lat < " + String.valueOf(p1.x) + " AND " +
                "stop_lon < " + String.valueOf(p2.y) + " AND " +
                "stop_lon > " + String.valueOf(p4.y) + " AND " +
                "route_stop.id_route = route.id AND route_stop.id_stop = stop_id " + routesExtra +
                "GROUP BY stop_id";


        Cursor c = db.rawQuery(query, null);
        int idIndex = c.getColumnIndex("stop_id");
        int nameIndex = c.getColumnIndex("stop_name");
        int descIndex = c.getColumnIndex("stop_desc");
        int latIndex = c.getColumnIndex("stop_lat");
        int lonIndex = c.getColumnIndex("stop_lon");
        int routesIndex = c.getColumnIndex("route_ids");
        int routesShortNamesIndex = c.getColumnIndex("route_short_names");
        int routesLongNamesIndex = c.getColumnIndex("route_long_names");
        int routeColorIndex = c.getColumnIndex("route_color");
        int routeLineNameIndex = c.getColumnIndex("route_line_name");
        int routeMainStopsIndex = c.getColumnIndex("route_main_stops");

        c.moveToFirst();
        double distance;
        while (!c.isAfterLast()) {
            double stopLatitude = c.getDouble(latIndex);
            double stopLongitude = c.getDouble(lonIndex);
            PointF point = new PointF((float) stopLatitude, (float) stopLongitude);

            if ((distance = MathUtils.getDistanceBetweenTwoPoints(point, center)) <= radius) {

                int stopId = c.getInt(idIndex);
                String stopName = c.getString(nameIndex);
                String stopDescription = c.getString(descIndex);

                String routeIds = c.getString(routesIndex);
                String routesShortNames = c.getString(routesShortNamesIndex);
                String routesLongNames = c.getString(routesLongNamesIndex);
                String routeColorNames = c.getString(routeColorIndex);
                String routeLineNames = c.getString(routeLineNameIndex);
                String routeMainStops = c.getString(routeMainStopsIndex);

                NearStop stop = new NearStop(stopId, stopName, stopDescription, stopLatitude, stopLongitude, distance);

                String[] routeIdsArray = routeIds.split(",");
                String[] routesShortNamesArray = routesShortNames.split(",");
                String[] routesLongNamesArray = routesLongNames.split(",");
                String[] routeColorArray = routeColorNames.split(",");
                String[] routeMainStopsArray = routeMainStops.split(",");


                for (int i = 0; i < routeIdsArray.length; i++) {
                    Route route = new Route(routeIdsArray[i], routesShortNamesArray[i],
                            routesLongNamesArray[i], routeColorArray[i], "", routeMainStopsArray[i]);
                    stop.addRoute(route);
                }

                stops.add(stop);

            }

            c.moveToNext();
        }

        c.close();
        db.close();

        return stops;
    }

    /**
     * Cria uma avaliação vazia
     *
     * @param context
     * @param tripTimestamp
     * @return
     */
    public static boolean createEmptyRating(Context context, long tripTimestamp) {
        SQLiteDatabase db = getWritableDatabase(context);
        Table avaliacaoTable = MeliorDBOpenHelper.getAvaliacaoTable();
        boolean result = false;

        db.beginTransaction();
        try {

            ContentValues valoresAvaliacao = new ContentValues();

            valoresAvaliacao.put("timestamp", tripTimestamp);

            db.insert(avaliacaoTable.getName(), null, valoresAvaliacao);

            db.setTransactionSuccessful();
            result = true;
        } finally {
            db.endTransaction();
        }

        db.close();

        return result;
    }

    /**
     * Preenche a avaliação criada
     *
     * @param context
     * @param avaliacao
     * @return
     */
    public static boolean fillRating(Context context, Avaliacao avaliacao) {
        SQLiteDatabase db = getWritableDatabase(context);
        Table avaliacaoTable = MeliorDBOpenHelper.getAvaliacaoTable();
        boolean result = false;

        db.beginTransaction();
        try {

            ContentValues valoresAvaliacao = new ContentValues();
            valoresAvaliacao.put("rota", avaliacao.getRota());
            valoresAvaliacao.put("preenchida", 1);

            long tripTimestamp = avaliacao.getTimestamp();
            String[] updateArgs = {String.valueOf(tripTimestamp)};

            db.update(avaliacaoTable.getName(), valoresAvaliacao, "timestamp=?", updateArgs);

            db.insert(avaliacaoTable.getName(), null, valoresAvaliacao);

            for (Resposta resposta : avaliacao.getRespostas()) {
                addResposta(db, avaliacao.getTimestamp(), resposta);
            }

            db.setTransactionSuccessful();
            result = true;
        } finally {
            db.endTransaction();
        }

        db.close();

        return result;
    }

    /**
     * Adiciona os pontos da rota (caminho que a rota faz)
     *
     * @param mContext
     * @param shapes
     * @return
     */
    public static boolean addShapes(Context mContext, HashMap<String, ArrayList<RouteShape>> shapes) {
        SQLiteDatabase db = DBUtils.getWritableDatabase(mContext);
        db.beginTransaction();

        Table shapesTable = MeliorDBOpenHelper.getShapesTable();
        boolean success = true;

        GeoPoint latLngObject;

        for (ArrayList<RouteShape> shapeList : shapes.values()) {
            for (RouteShape shape : shapeList) {
                ContentValues shapeEntry = new ContentValues();

                shapeEntry.put("id_route", shape.getRouteId());
                shapeEntry.put("sub", shape.getSub() == null ? "" : shape.getSub());

                for (int i = 1; i <= shape.size(); i++) {
                    latLngObject = shape.get(i - 1);

                    shapeEntry.put("order_num", i);
                    shapeEntry.put("lat", latLngObject.getLatitude());
                    shapeEntry.put("lon", latLngObject.getLongitude());

                    try {
                        db.insert(shapesTable.getName(), null, shapeEntry);
                    } catch (Exception e) {
                        success = false;
                    }
                }
            }
        }

        if (success) {
            db.setTransactionSuccessful();
        }

        db.endTransaction();
        db.close();

        SharedPreferencesUtils.setShapesOnDatabase(mContext, success);

        return success;
    }

    /**
     * Retorna os pontos da rota
     *
     * @param context
     * @param rota
     * @return
     */
    public static List<RouteShape> getRouteShape(Context context, Route rota) {
        return getRouteShape(context, rota.getId());
    }

    public static List<RouteShape> getRouteShape(Context context, String routeId) {
        SQLiteDatabase db = getReadableDatabase(context);

        String[] args = {routeId};
        List<RouteShape> routeShapes = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT shapes.id_route, shapes.lat, shapes.lon, shapes.sub, " +
                        "route.color FROM shapes, route WHERE shapes.id_route = route.id AND route.id = ?" +
                        " ORDER BY shapes.sub ASC, shapes.order_num ASC",
                args);

        c.moveToFirst();

        int subIdx = c.getColumnIndex("sub");
        int latIdx = c.getColumnIndex("lat");
        int lonIdx = c.getColumnIndex("lon");
        int colorIdx = c.getColumnIndex("color");


        String sub = null;
        RouteShape shape = null;
        String color;

        while (!c.isAfterLast()) {

            String cursorSub = c.getString(subIdx);

            if (!cursorSub.equals(sub)) {
                if (shape != null) routeShapes.add(shape);
                sub = cursorSub;
                color = c.getString(colorIdx);
                shape = new RouteShape(routeId, sub, color);
            }

            double latitude = c.getDouble(latIdx);
            double longitude = c.getDouble(lonIdx);

            shape.add(new GeoPoint(latitude, longitude));

            c.moveToNext();
        }

        routeShapes.add(shape);

        c.close();
        db.close();

        return routeShapes;
    }


    /**
     * Adiciona no banco de dados (na tabela horariosProvaveisTable) as paradas dos arquivo
     *
     * @param mContext
     * @param stopTimesList
     * @return
     */
    public static boolean addSchedule(Context mContext, ArrayList<StopTime> stopTimesList) {
        SQLiteDatabase db = DBUtils.getWritableDatabase(mContext);
        db.beginTransaction();

        Table scheduleTable = MeliorDBOpenHelper.getHorariosProvaveisTable();
        boolean success = true;
        SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss");

        for (StopTime stopTime : stopTimesList) {

            ContentValues scheduleEntry = new ContentValues();

            scheduleEntry.put("rota", stopTime.getRouteId());
            scheduleEntry.put("tipo_dia", stopTime.getServiceId());
            scheduleEntry.put("id_parada", stopTime.getStopId());

            scheduleEntry.put("horario_medio", sdfDate.format(stopTime.getArrivalTime()));
            scheduleEntry.put("horario_anterior", sdfDate.format(stopTime.getArrivalTimeBefore()));
            scheduleEntry.put("horario_posterior", sdfDate.format(stopTime.getArrivalTimeAfter()));

            try {
                db.insert(scheduleTable.getName(), null, scheduleEntry);
            } catch (Exception e) {
                success = false;
            }
        }

        if (success) {
            db.setTransactionSuccessful();
        }

        db.endTransaction();
        db.close();


        return success;
    }


    /**
     * Retorna os proximos horarios da rota
     *
     * @param context
     * @param routeId
     * @param stopId
     * @param dayS
     * @return
     */
    public static List<StopTime> getRouteSchedule(Context context, String routeId, int stopId, String dayS) {
        SQLiteDatabase db = getReadableDatabase(context);

        String[] args = {routeId, dayS, String.valueOf(stopId)};
        List<StopTime> stopTimes = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT horario_medio,horario_anterior,horario_posterior FROM horariosProvaveisTable WHERE rota = ? AND tipo_dia = ? AND id_parada = ? ORDER BY horario_anterior", args);

        c.moveToFirst();

        int hourMedIdx = c.getColumnIndex("horario_medio");
        int hourBefIdx = c.getColumnIndex("horario_anterior");
        int hourAftIdx = c.getColumnIndex("horario_posterior");

        while (!c.isAfterLast()) {
            SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss");

            try {
                Date hourMed = df.parse(c.getString(hourMedIdx));
                Date hourBef = df.parse(c.getString(hourBefIdx));
                Date hourAft = df.parse(c.getString(hourAftIdx));

                Calendar now = Calendar.getInstance();

                int hour = now.get(Calendar.HOUR_OF_DAY);
                int minutes = now.get(Calendar.MINUTE);

                if ((minutes <= hourMed.getMinutes() && hour == hourMed.getHours()) || (minutes >= hourMed.getMinutes() && hour + 1 == hourMed.getHours())) {
                    stopTimes.add(new StopTime(routeId, dayS, stopId, hourMed, hourBef, hourAft));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            c.moveToNext();
        }

        c.close();
        db.close();

        return stopTimes;
    }

    /**
     * Add the non published rating
     *
     * @param context
     * @param avaliacao
     */
    public static void addNonPublishedRating(Context context, Avaliacao avaliacao) {
        if (avaliacao != null) {
            SQLiteDatabase db = getWritableDatabase(context);

            Table nonPublishedRatingTable = getNonPublishedRatingsTable();

            ContentValues ratingValues = new ContentValues();
            ratingValues.put("id_rating", String.valueOf(avaliacao.getTimestamp()));

            db.insert(nonPublishedRatingTable.getName(), null, ratingValues);
        }
    }

    /**
     * @param context
     * @return non published ratings
     */
    public static ArrayList<Avaliacao> getNonPublishedRatings(Context context) {
        SQLiteDatabase db = getReadableDatabase(context);

        ArrayList<Avaliacao> nonPublishedRatings = new ArrayList<>();

        String query = "SELECT * FROM non_published_ratings npr, avaliacao av, resposta res " +
                "WHERE av.timestamp = npr.id_rating AND av.timestamp = res.timestamp " +
                "ORDER BY av.timestamp";

        Cursor c = db.rawQuery(query, null);

        c.moveToFirst();

        int timestampIdx = c.getColumnIndex("timestamp");
        int rotaIdx = c.getColumnIndex("rota");
        int categoriaIdx = c.getColumnIndex("categoria");
        int valorIdx = c.getColumnIndex("valor");

        if (c.getCount() > 0) {

            String prevTimestamp = c.getString(timestampIdx);
            Avaliacao avaliacao = new Avaliacao(c.getLong(timestampIdx), c.getString(rotaIdx));
            while (!c.isAfterLast()) {
                if (!c.getString(timestampIdx).equals(prevTimestamp)) {
                    nonPublishedRatings.add(avaliacao);
                    avaliacao = new Avaliacao(c.getLong(timestampIdx), c.getString(rotaIdx));
                }

                avaliacao.addResposta(new Resposta(c.getInt(categoriaIdx), c.getInt(valorIdx)));

                prevTimestamp = c.getString(timestampIdx);
                c.moveToNext();
            }
            nonPublishedRatings.add(avaliacao);
        }

        Log.d(TAG, String.valueOf(c.getCount()) + ": " + nonPublishedRatings.toString());

        c.close();
        db.close();

        return nonPublishedRatings;
    }

    public static void deleteAllNonPublishedRatings(Context context){
        SQLiteDatabase db = getWritableDatabase(context);

        db.delete(getNonPublishedRatingsTable().getName(), null, null);
    }
}