package br.edu.ufcg.analytics.meliorbusao.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import br.edu.ufcg.analytics.meliorbusao.Constants;
import br.edu.ufcg.analytics.meliorbusao.R;
import br.edu.ufcg.analytics.meliorbusao.utils.SharedPreferencesUtils;


/**
 * A isSuccessfulLogin screen that offers isSuccessfulLogin via email/password.
 */
public class BigseaLoginActivity extends AppCompatActivity {

    public static final String TAG = "BigSeaLoginActivity";
    // Keep track of the isSuccessfulLogin task to ensure we can cancel it if requested.
    private UserLoginTask mAuthTask = null;
    // UI references.
    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bigsea_login);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Set up the isSuccessfulLogin form.
        mUsernameView = (AutoCompleteTextView) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);

        Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    /**
     * Attempts to sign in or register the account specified by the isSuccessfulLogin form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual isSuccessfulLogin attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the isSuccessfulLogin attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;

        // There was an error; don't attempt isSuccessfulLogin and focus the first
        // form field with an error.
        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            mPasswordView.requestFocus();
            cancel = true;
        } else if(!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_incorrect_password));
            mPasswordView.requestFocus();
            cancel = true;
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            mUsernameView.requestFocus();
            cancel = true;
        } else if (!isUsernameValid(username)) {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            mUsernameView.requestFocus();
            cancel = true;
        }

        if (!cancel) {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(username, password);
            mAuthTask.execute();
        }
    }

    private boolean isUsernameValid(String username) {
        return username.length() >= 3 &&
                username.length() <= 15;
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 5 &&
                password.length() <= 25;
    }

    /**
     * Shows the progress UI and hides the isSuccessfulLogin form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    /**
     * Represents an asynchronous isSuccessfulLogin/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private static final String ENDPOINT_ADDRESS = "https://eubrabigsea.dei.uc.pt/engine/api/checkin_data";
        private final String username;
        private final String password;
        private String responseMessage = "";
        private boolean isSuccessfulLogin;
        private String token;
        private String loginServiceType;

        UserLoginTask(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            URL url;
            try {
                String parameters = "user=" + username + "&pwd=" + password;
                url = new URL(ENDPOINT_ADDRESS);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(parameters);
                writer.flush();
                writer.close();
                os.close();

                conn.connect();
                int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line = "";
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        responseMessage += line;
                    }
                    JSONObject jsonObject = new JSONObject(responseMessage);
                    isSuccessfulLogin = jsonObject.getBoolean("success");

                    if (isSuccessfulLogin){
                        JSONObject userInfo = new JSONObject(jsonObject.getString("user_info"));
                        token = userInfo.getString("user_token");
                        loginServiceType = Constants.BIG_SEA_SERVICE;
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (isSuccessfulLogin) {
                SharedPreferencesUtils.setUserToken(getApplicationContext(), username, loginServiceType, token);
                final Intent i = new Intent(BigseaLoginActivity.this, MelhorBusaoActivity.class);
                startActivity(i);
            } else {
                mUsernameView.setError(getString(R.string.error_unsuccessful_login));
                mUsernameView.requestFocus();
            }
            Log.d(TAG, responseMessage);
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
