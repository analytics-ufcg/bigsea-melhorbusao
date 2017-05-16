package br.edu.ufcg.analytics.meliorbusao.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.edu.ufcg.analytics.meliorbusao.Constants;
import br.edu.ufcg.analytics.meliorbusao.db.DBUtils;
import br.edu.ufcg.analytics.meliorbusao.models.Avaliacao;
import br.edu.ufcg.analytics.meliorbusao.utils.ParseUtils;
import br.edu.ufcg.analytics.meliorbusao.utils.SharedPreferencesUtils;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * This Service is responsable for sending {@link Avaliacao} objects (user opinions on bus trips)
 * to a Parse Server
 *
 * @see ParseUtils
 */
public class RatingsService extends IntentService {

    public static final String TAG = "RatingsService";
    private static final String ACTION_SEND_NEW_RATING = Constants.SERVICE_ACTION + ".SEND_NEW_RATING";
    private static final String ACTION_SEND_LOCAL_RATINGS = Constants.SERVICE_ACTION + ".SEND_LOCAL_RATINGS";
    private static final String EXTRA_RATINGS = Constants.SERVICE_EXTRA + ".RATINGS";
    private static final String TOKEN = "token";
    private static final String USERNAME = "username";
    private static final String AUTHENTICATION_PROVIDER = "authenticationProvider";
    private static final String RATINGS = "ratings";

    public RatingsService() {
        super(TAG);
    }

    /**
     * Starts this service to send a single new {@link Avaliacao} to server.
     * If the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void sendNewRating(Context context, Avaliacao rating) {
        Intent intent = new Intent(context, RatingsService.class);
        intent.setAction(ACTION_SEND_NEW_RATING);
        ArrayList<Avaliacao> container = new ArrayList<>();
        container.add(rating);
        intent.putParcelableArrayListExtra(EXTRA_RATINGS, container);
        context.startService(intent);
    }

    /**
     * Starts this service to send to server the locally-only saved {@link Avaliacao} objects.
     * If the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void sendLocalRatings(Context context) {
        Intent intent = new Intent(context, RatingsService.class);
        intent.setAction(ACTION_SEND_LOCAL_RATINGS);
        ArrayList<Avaliacao> ratings = DBUtils.getNonPublishedRatings(context);
        intent.putParcelableArrayListExtra(EXTRA_RATINGS, ratings);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SEND_NEW_RATING.equals(action)) {
                ArrayList<Avaliacao> singleRating = intent.getParcelableArrayListExtra(EXTRA_RATINGS);
                handleActionSendNewRating(singleRating);
            } else if (ACTION_SEND_LOCAL_RATINGS.equals(action)) {
                ArrayList<Avaliacao> localRatings = intent.getParcelableArrayListExtra(EXTRA_RATINGS);
                handleActionSendLocalRatings(localRatings);
            }
        }
    }

    /**
     * Send a single new {@link Avaliacao} to the server.
     *
     * @see SharedPreferencesUtils
     * @see DBUtils
     */
    private void handleActionSendNewRating(final ArrayList<Avaliacao> rating) {
        if (rating != null) {
            final Context context = getApplicationContext();
            Map<String, Object> params = new HashMap<>();
            params.put(TOKEN, SharedPreferencesUtils.getUserToken(context));
            params.put(USERNAME, SharedPreferencesUtils.getUsername(context));
            params.put(AUTHENTICATION_PROVIDER, SharedPreferencesUtils.getAuthService(context));
            params.put(RATINGS, rating.toString());
            Log.d(TAG, "handleActionSendNewRating: " + rating.toString());

            ParseCloud.callFunctionInBackground("insertRating", params, new FunctionCallback<Object>() {
                public void done(Object response, ParseException e) {
                    if (e == null) {
                        Log.i(TAG, "Rating saved in server: " + response.toString());
                    } else {
                        DBUtils.addNonPublishedRating(context, rating.get(0));
                        Log.e(TAG, "Could not save evaluation in server: " + e.getMessage());
                        Log.i(TAG, "Rating saved locally.");
                    }
                }
            });
        } else {
            Log.d(TAG, "Cannot save a null Rating");
        }
    }

    /**
     * Send all locally-only saved {@link Avaliacao} to the server.
     *
     * @see SharedPreferencesUtils
     * @see DBUtils
     */
    private void handleActionSendLocalRatings(ArrayList<Avaliacao> ratings) {
        if (ratings != null && !ratings.isEmpty()) {
            final Context context = getApplicationContext();
            Map<String, Object> params = new HashMap<>();
            params.put(TOKEN, SharedPreferencesUtils.getUserToken(context));
            params.put(USERNAME, SharedPreferencesUtils.getUsername(context));
            params.put(AUTHENTICATION_PROVIDER, SharedPreferencesUtils.getAuthService(context));
            params.put(RATINGS, ratings.toString());

            ParseCloud.callFunctionInBackground("insertRating", params, new FunctionCallback<Object>() {
                public void done(Object response, ParseException e) {
                    if (e == null) {
                        DBUtils.deleteAllNonPublishedRatings(context);
                        Log.i(TAG, "Ratings saved in server: " + response.toString());
                    } else {
                        Log.e(TAG, "Could not save evaluation in server: " + e.getMessage());
                    }
                }
            });
        } else {
            Log.d(TAG, "Cannot save a null Rating");
        }
    }
}
