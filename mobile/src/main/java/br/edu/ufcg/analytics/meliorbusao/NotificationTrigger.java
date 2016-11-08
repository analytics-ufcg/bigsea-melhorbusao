package br.edu.ufcg.analytics.meliorbusao;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import br.edu.ufcg.analytics.meliorbusao.activities.RatingBusaoActivity;
import br.edu.ufcg.analytics.meliorbusao.db.DBUtils;
import br.edu.ufcg.analytics.meliorbusao.detection.RouteDetector;
import br.edu.ufcg.analytics.meliorbusao.listeners.MeliorListener;
import br.edu.ufcg.analytics.meliorbusao.models.Route;
import br.edu.ufcg.analytics.meliorbusao.utils.MathUtils;
import br.edu.ufcg.analytics.meliorbusao.utils.StringUtils;

public class NotificationTrigger implements MeliorListener {
    private static final String LOG = "NotificationTrigger";
    private Context mContext;
    private long tripTimestamp;
    private boolean mOnBus;
    private long lastNotifiedTripTimestamp;
    private RouteDetector routeDetector;
    private Set<Route> detectedRoutes;
    int numOfDetectedRoutes;
    int notificationId;

    public NotificationTrigger(Context context) {
        mContext = context;

        LocalBroadcastManager.getInstance(mContext).registerReceiver(new OnBusBroadcastReceiver(),
                new IntentFilter(Constants.BROADCAST_ACTION_ON_BUS));
    }

    /**
     * Quando detecta que está no bus notifica caso não tenha notificado na ultima viagem,
     * Atualiza o valor de rotas detectadas
     * @param timestamp
     * @param lastLocation
     */
    @Override
    public void onReceiveMeliorLocation(long timestamp, Location lastLocation) {
        double lastLocationLatitude = lastLocation.getLatitude();
        double lastLocationLongitude = lastLocation.getLongitude();

        if (mOnBus && !notifiedLastTrip()) {
            if (((Calendar.getInstance().getTimeInMillis() - tripTimestamp) >= Constants.TIME_TO_NOTIFY)
                    && hasDetectedRoute()) {
                createNotification();
            }
        }

        if (routeDetector != null) {
            if (!routeDetector.hasFirstDetection() ||
                    conditionDistanceBetweenDetections(
                    lastLocationLatitude, lastLocationLongitude)) {

                detectedRoutes = routeDetector.processLocation(
                        lastLocation.getLatitude(),
                        lastLocation.getLongitude()
                );

                if (numOfDetectedRoutes != detectedRoutes.size()) {
                    numOfDetectedRoutes = detectedRoutes.size();
                    String text = "Detected routes: " + detectedRoutes.toString();

                    Log.d(LOG, text);
                }
            }
        }
    }

    @Override
    public void onMeliorLocationAvailabilityChange(long timestamp, boolean locationAvailable) {

    }

    @Override
    public void onReceiveMeliorActivity(long timestamp, List<DetectedActivity> detectedActivity) {

    }

    /**
     * Cria notificação falsa para debug
     * @param numOfRoutes
     */
    public void fakeRatingNotification(int numOfRoutes) {
        final long timestamp = Calendar.getInstance().getTimeInMillis();

        DBUtils.createEmptyRating(mContext, timestamp);

        ArrayList<Route> rotas = new ArrayList<>(DBUtils.getTodasAsRotas(mContext));
        Collections.shuffle(rotas);

        Route[] rotasAMostrar = new Route[numOfRoutes];

        for (int i = 0; i < numOfRoutes; i++) {
            rotasAMostrar[i] = rotas.get(i);
        }

        ratingNotification(timestamp, rotasAMostrar);
    }

    /**
     * Quando está no no bus cria uma Avaliação vazia,
     * caso contrario cria uma notificação
     * 
     */
    public class OnBusBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mOnBus = intent.getExtras().getBoolean(Constants.ON_BUS_STATUS_EXTRA);
            long receivedTripTimestamp = intent.getExtras().getLong(Constants.ON_BUS_TIMESTAMP_EXTRA);

            if (mOnBus) {
                routeDetector = new RouteDetector(mContext);
                DBUtils.createEmptyRating(mContext, receivedTripTimestamp);
            } else if (!notifiedLastTrip() && hasDetectedAnyRoute()) { // && SharedPreferencesUtils.isDetectionActive(mContext)
                createNotification();
            }

            tripTimestamp = receivedTripTimestamp;
        }
    }



    /**
     * Custom Notification that asks the user to rate the bus.
     */
    public void ratingNotification(long tripTimestamp, Route[] routes) {
        PendingIntent pIntent = getNotificationPendingIntent(tripTimestamp, routes);
        Notification notification = getRatingNotification(pIntent);
        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) mContext.getSystemService(Service.NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify(notificationId++, notification);
    }

    /**
     * Constroi a notificação de avaliação
     * @param pIntent
     * @return
     */
    private Notification getRatingNotification(PendingIntent pIntent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                // Set Title
                .setContentTitle(StringUtils.getRandomString(mContext, R.array.notification_title_array))
                // Set Long Text
                .setStyle(new NotificationCompat.BigTextStyle().bigText(StringUtils.getRandomString(mContext, R.array.notification_sub_title_array)))
                // Set Icon
                .setSmallIcon(R.drawable.bus_custom)
                // Set Ticker Message
                .setTicker(mContext.getString(R.string.notificationticker))
                // Dismiss Notification
                .setAutoCancel(true)
                // Set PendingIntent into Notification
                .setContentIntent(pIntent)
                // Action button
                .addAction(R.drawable.heart, "Avalie agora!", pIntent);

        return builder.build();
    }

    /**
     * Coloca nas intents o timestamp da viagem e o array de rotas detectadas
     * @param tripTimestamp
     * @param routes
     * @return
     */
    private PendingIntent getNotificationPendingIntent(long tripTimestamp, Route[] routes) {
        Intent intent = new Intent(mContext, RatingBusaoActivity.class);
        intent.putExtra(Constants.TRIP_TIMESTAMP_EXTRA, tripTimestamp);
        intent.putExtra(Constants.TRIP_ROUTES_EXTRA, routes);
        // Open RatingActivity.java Activity
        return PendingIntent.getActivity(mContext, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Verifica se um ponto pertece a detecção anterior
     * @param latitude
     * @param longitude
     * @return se distancia entre a ultima localização da ultima detecção e a passada como parametro está
     * no intervalo de distância DISTANCE_BETWEEN_DETECTIONS
     */
    private boolean conditionDistanceBetweenDetections(double latitude, double longitude) {
        return MathUtils.getDistanceBetweenTwoPoints(
                latitude, longitude,
                routeDetector.getLastDetectedLatitude(), routeDetector.getLastDetectedLongitude())
                >= Constants.DISTANCE_BETWEEN_DETECTIONS;
    }

    /**
     *
     * @return se a ultima viagem foi notificada
     */
    private boolean notifiedLastTrip() {
        return (tripTimestamp == lastNotifiedTripTimestamp);
    }

    private void createNotification() {
        Route[] routes = detectedRoutes.toArray(new Route[detectedRoutes.size()]);
        ratingNotification(tripTimestamp, routes);
        lastNotifiedTripTimestamp = tripTimestamp;
    }

    private boolean hasDetectedAnyRoute() {
        return detectedRoutes.size() > 0;
    }

    private boolean hasDetectedRoute() {
        return detectedRoutes.size() == 1;
    }
}
