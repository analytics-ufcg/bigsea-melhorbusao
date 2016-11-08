package br.edu.ufcg.analytics.meliorbusao;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.edu.ufcg.analytics.meliorbusao.db.DBUtils;
import br.edu.ufcg.analytics.meliorbusao.listeners.OnFinishedParseListener;
import br.edu.ufcg.analytics.meliorbusao.listeners.OnStopTimesReadyListener;
import br.edu.ufcg.analytics.meliorbusao.models.StopTime;
import br.edu.ufcg.analytics.meliorbusao.utils.ParseUtils;
import br.edu.ufcg.analytics.meliorbusao.utils.SharedPreferencesUtils;

public class SchedulesProbableParser extends AsyncTask<Void, Void, Void> {

    private static String LOG_TAG = "SchedulesProbableFileParser";

    private static Context mContext;
    private OnFinishedParseListener mListener;

    public SchedulesProbableParser(Context context, OnFinishedParseListener listener) {
        super();

        mContext = context;
        mListener = listener;
    }


    private static final int ROUTE_ID_IDX = 0;
    private static final int DAY_TYPE = 1;
    private static final int STOP_ID = 2;
    private static final int SCHEDULE_MEDIA = 3;
    private static final int SCHEDULE_BEFORE = 4;
    private static final int SCHEDULE_AFTER = 5;

    // CSV
    final static String SCHEDULES_CAMPINA_CSV = "melhor_busao_schedules_campina.csv";
    final static String SCHEDULES_CURITIBA_CSV = "melhor_busao_schedules_curitiba.csv";

    final static String DELIMITER = ",";

    /**
     * A partir do csv constroi o schedule e adiciona no bd
     */
    private void parse() {

        String schedulesDataFile = "";

        if (SharedPreferencesUtils.getCityNameOnDatabase(mContext).equals(Cities.CAMPINA_GRANDE.getCityName())) {
            schedulesDataFile = SCHEDULES_CAMPINA_CSV;
        } else if (SharedPreferencesUtils.getCityNameOnDatabase(mContext).equals(Cities.CURITIBA.getCityName())) {
            schedulesDataFile = SCHEDULES_CURITIBA_CSV;
        }

        long startTime = Calendar.getInstance().getTimeInMillis();

        Log.d("PARSING SCHEDULE", "Começou o parse do schedule...");

        ArrayList<StopTime> stopTimesList = new ArrayList<>();
        AssetManager assetManager = mContext.getAssets();
        InputStream is = null;
        StopTime stopTime = null;

        try  {
            is = assetManager.open(schedulesDataFile);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        String route;
        String dayType;
        int stopId;
        String scheduleMedia;
        String scheduleBefore;
        String scheduleAfter;

        String[] values;

        try {
            // Consome header:
            String line = reader.readLine();
            boolean firstLine = true;
            int id = 1;
            do {

                values = line.split(DELIMITER);

                line = reader.readLine();


                if (!firstLine){
                    route = values[ROUTE_ID_IDX];
                    dayType = values[DAY_TYPE];
                    stopId = Integer.parseInt(values[STOP_ID]);
                    scheduleMedia = values[SCHEDULE_MEDIA];
                    scheduleBefore = values[SCHEDULE_BEFORE];
                    scheduleAfter = values[SCHEDULE_AFTER];

                    SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss");

                    // create a schedule
                    try {

                        stopTime = new StopTime(id,route, dayType,stopId,(Date)sdfDate.parse(scheduleMedia),(Date)sdfDate.parse(scheduleBefore),(Date)sdfDate.parse(scheduleAfter));


                    } catch (ParseException e) {
                        Log.d("erro", "Deu erro");
                        e.printStackTrace();
                    }



                    stopTimesList.add(stopTime);
                    if (stopTimesList.size() >= 10000 || line==null){
                        DBUtils.addSchedule(mContext, stopTimesList);
                        stopTimesList.clear();

                    }

                    id++;

                }

                firstLine = false;



            } while (line != null);

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            is.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        SharedPreferencesUtils.setScheduleOnDatabase(mContext,true);

        Log.d("PARSING SCHEDULE", "Terminou o parse do schedule... Duração: " +
                String.valueOf(Calendar.getInstance().getTimeInMillis() - startTime)
        );
    }

    /**
     *
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    /**
     *
     * @param params
     * @return
     */
    @Override
    protected Void doInBackground(Void... params) {
        parse();
        return null;
    }

    /**
     *
     * @param aVoid
     */
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mListener.finishedParse(Constants.KIND_ROUTESTOP);
    }



    /**
     * Retorna os horarios de uma rota em uma parada
     * @param stopId
     * @param routeId
     * @param listener
     */
    public static void getRouteSchedules(Context context,final int stopId, final String routeId, final OnStopTimesReadyListener listener) {
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_WEEK);
        final String dayS;

        switch (day) {
            case 1:
                dayS = "SUN";
                break;
            case 2:
                dayS = "MON";
                break;
            case 6:
                dayS = "FRI";
                break;
            case 7:
                dayS = "SAT";
                break;
            default:
                dayS = "TUE WED THU";
                break;
        }

        listener.onStopTimesReady(DBUtils.getRouteSchedule(context, routeId, stopId, dayS));

//        final List<StopTime> stopTimes = ParseUtils.getRouteScheduleCuritiba(context, routeId, stopId);
//        Log.d(LOG_TAG, Integer.toString(stopTimes.size()));
//        listener.onStopTimesReady(stopTimes);

    }
}
