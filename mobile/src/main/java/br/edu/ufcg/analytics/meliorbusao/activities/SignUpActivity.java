package br.edu.ufcg.analytics.meliorbusao.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

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
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

import br.edu.ufcg.analytics.meliorbusao.Constants;
import br.edu.ufcg.analytics.meliorbusao.R;

public class SignUpActivity extends AppCompatActivity {
    public static final String TAG = "SIGN_UP_ACTIVITY";

    private UserRegisterTask mAuthTask = null;

    private AutoCompleteTextView mFirstNameView;
    private AutoCompleteTextView mLastNameView;
    private AutoCompleteTextView mEmailView;
    private AutoCompleteTextView mUserNameView;
    private AutoCompleteTextView mPasswordView;
    private AutoCompleteTextView mPasswordConfirmationView;
    private Button mSignupButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mFirstNameView = (AutoCompleteTextView) findViewById(R.id.first_name);
        mLastNameView = (AutoCompleteTextView) findViewById(R.id.last_name);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mUserNameView = (AutoCompleteTextView) findViewById(R.id.username);
        mPasswordView = (AutoCompleteTextView) findViewById(R.id.password);
        mPasswordConfirmationView = (AutoCompleteTextView) findViewById(R.id.password_confirmation);

        mSignupButton = (Button) findViewById(R.id.signup_button);


        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

    }

    /**
     * Attempts to register the account specified by the isSuccessfulRegister form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual isSuccessfulRegister attempt is made.
     */

    private void attemptRegister() {

        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mFirstNameView.setError(null);
        mLastNameView.setError(null);
        mEmailView.setError(null);
        mUserNameView.setError(null);
        mPasswordView.setError(null);
        mPasswordConfirmationView.setError(null);

        boolean cancel = false;

        // There was an error; don't attempt isSuccessfulRegister and focus the first
        // form field with an error.
        if (isNameInvalid(mFirstNameView)) {
            cancel = true;
        } else if (isNameInvalid(mLastNameView)) {
            cancel = true;
        } else if (isNameInvalid(mLastNameView)) {
            cancel = true;
        } else if (isEmailInValid(mEmailView)) {
            cancel = true;
        } else if(isNameInvalid(mUserNameView)){
            cancel = true;
        } else if (isPasswordInvalid(mPasswordView, mPasswordConfirmationView)) {
            cancel = true;
        }

        if (!cancel) {
            // Show a progress spinner, and kick off a background task to
            // perform the user register attempt.
            mAuthTask = new UserRegisterTask(mFirstNameView.getText().toString(), mLastNameView.getText().toString(),
                    mEmailView.getText().toString(), mUserNameView.getText().toString(), mPasswordView.getText().toString());

            mAuthTask.execute((Void) null);
        }


    }

    private boolean isNameInvalid(AutoCompleteTextView name) {
        boolean invalid = false;

        if (name == null || name.getText().toString().equals("")) {
            if (mUserNameView != null && name.equals(mUserNameView)) {
                name.setError(getString(R.string.error_field_required) + ". " + getString(R.string.userName_restriction));
            } else {
                name.setError(getString(R.string.error_field_required));
            }
            invalid = true;
        } else if (name.getText().toString().length() < 3 || name.getText().toString().length() > 15) {
            if (mUserNameView != null && name.equals(mUserNameView)) {
                name.setError(getString(R.string.userName_restriction));
            } else {
                name.setError(name.getHint().toString() + " " + getString(R.string.name_restriction));
            }
            invalid = true;
        }
        //contain number
        else if (name.getText().toString().matches(".*\\d.*")) {
            if (mUserNameView != null && !name.equals(mUserNameView)) {
                name.setError(name.getHint().toString() + " " + getString(R.string.name_restriction_no_number));
                invalid = true;
            }
        }

        if (invalid) {
            name.requestFocus();
        }
        return invalid;
    }

    private boolean isEmailInValid(AutoCompleteTextView email) {

        boolean invalid = false;

        if (email == null || email.getText().equals("")) {
            email.setError(getString(R.string.error_field_required));
            invalid = true;
        } else if (!email.getText().toString().contains("@")) {
            //TODO: Replace this with your own logic
            email.setError(getString(R.string.email_restriction));
            invalid = true;
        }

        if (invalid) {
            email.requestFocus();
        }
        return invalid;

    }

    private boolean isPasswordInvalid(AutoCompleteTextView password, AutoCompleteTextView passwordConfirmation) {
        boolean invalid = false;

        if (password == null || password.equals("")) {
            mPasswordView.setError(getString(R.string.error_field_required));
            invalid = true;
        } else if (password.getText().length() < 5 || password.getText().length() > 25) {
            mPasswordView.setError(getString(R.string.password_error));
            //Passwords must match. Needs to be between 5 and 25 characters. Case sensitive. No special characters allowed.
            invalid = true;
        }
        //contain number - bigSea API restriction
        else if (!password.getText().toString().matches(".*\\d.*")){
            mPasswordView.setError(getString(R.string.password_restriction));
            invalid= true;
        }  else if (!password.getText().toString().equals(passwordConfirmation.getText().toString())) {
            passwordConfirmation.setError(getString(R.string.password_must_match));
            passwordConfirmation.requestFocus();
            invalid = true;
        }

        if (invalid) {
            password.requestFocus();
        }
        return invalid;
    }


    /**
     * Represents an asynchronous isSuccessfulLogin/registration task used to authenticate
     * the user.
     */
    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private static final String ENDPOINT_ADDRESS = "https://eubrabigsea.dei.uc.pt/engine/api/signup_data ";
        private final String firstName;
        private final String lastName;
        private final String email;
        private final String username;
        private final String password;

        private String responseMessage = "";
        private boolean isSuccessfulRegister;

        UserRegisterTask(String firstName, String lastName, String email, String username, String password) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.username = username;
            this.password = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            URL url;
            try {
                String parameters = "user=" + username + "&pwd=" + password + "&fname=" + firstName + "&lname=" + lastName+ "&email=" + email;
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
                    Log.d(TAG, jsonObject.toString());

                    isSuccessfulRegister = false;
                    Iterator<String> keys = jsonObject.keys();

                    while( keys.hasNext() ) {
                        String key = (String)keys.next();
                        if (key.equals("success")){
                            isSuccessfulRegister = jsonObject.getString("success").contains("success") ;
                        }
                    }

                    conn.disconnect();
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

            if (isSuccessfulRegister) {
                Toast.makeText(SignUpActivity.this.getBaseContext(), R.string.usersigned_up_with_success, Toast.LENGTH_LONG).show();
                final Intent i = new Intent(SignUpActivity.this, MelhorBusaoActivity.class);
                startActivity(i);
                View view = getCurrentFocus();
                //TODO login or
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                finish();
            } else{
                Toast.makeText(SignUpActivity.this.getBaseContext(), R.string.username_already_exists, Toast.LENGTH_LONG).show();

            }
            Log.d(TAG, responseMessage);

        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           