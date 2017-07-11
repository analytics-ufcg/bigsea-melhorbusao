package br.edu.ufcg.analytics.meliorbusao.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
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

import br.edu.ufcg.analytics.meliorbusao.R;
import br.edu.ufcg.analytics.meliorbusao.activities.MelhorBusaoActivity;
import br.edu.ufcg.analytics.meliorbusao.activities.SignUpActivity;
import br.edu.ufcg.analytics.meliorbusao.utils.ProgressUtils;
import br.edu.ufcg.analytics.meliorbusao.utils.SharedPreferencesUtils;


public class ChangePasswordFragment extends Fragment {

    public static final String TAG = "ChangePasswordFragment";

    private AutoCompleteTextView oldPasswordView;
    private AutoCompleteTextView newPasswordView;
    private AutoCompleteTextView confirmNewPasswordView;
    private Button saveChangesButton;
    private ChangePasswordTask mChangePasswordTask;
    private OnPasswordChangedListener mCallback;
    private ProgressBar mProgressBar;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * @return A new instance of fragment ChangePasswordFragment.
     */
    public static ChangePasswordFragment newInstance() {
        ChangePasswordFragment fragment = new ChangePasswordFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_change_password, container, false);

        ((MelhorBusaoActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.change_password_fragment_title));

        oldPasswordView = (AutoCompleteTextView) mainView.findViewById(R.id.old_password_textview);
        newPasswordView = (AutoCompleteTextView) mainView.findViewById(R.id.new_password_textview);
        confirmNewPasswordView = (AutoCompleteTextView) mainView.findViewById(R.id.confirm_new_password_textview);
        saveChangesButton = (Button) mainView.findViewById(R.id.save_changes_button);
        mProgressBar = (ProgressBar) mainView.findViewById(R.id.change_pwd_progress_bar);

        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSave();
            }
        });

        return mainView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnPasswordChangedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnPasswordChangedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    private void attemptSave() {

        if (mChangePasswordTask != null) {
            return;
        }

        oldPasswordView.setError(null);
        newPasswordView.setError(null);
        confirmNewPasswordView.setError(null);

        if (!isPasswordInvalid(newPasswordView, confirmNewPasswordView)) {

            String username = SharedPreferencesUtils.getUsername(getContext());
            String oldPassword = oldPasswordView.getText().toString();
            String newPassword = newPasswordView.getText().toString();
            String token = SharedPreferencesUtils.getUserToken(getContext());

            mChangePasswordTask = new ChangePasswordTask(username, oldPassword, newPassword, token);
            mChangePasswordTask.execute((Void) null);
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private boolean isPasswordInvalid(AutoCompleteTextView newPassword, AutoCompleteTextView passwordConfirmation) {
        boolean invalid = false;

        if (newPassword == null || newPassword.equals("")) {
            newPasswordView.setError(getString(R.string.error_field_required));
            invalid = true;
        } else if (newPassword.getText().length() < 5 || newPassword.getText().length() > 25) {
            newPasswordView.setError(getString(R.string.password_error));
            //Passwords must match. Needs to be between 5 and 25 characters. Case sensitive. No special characters allowed.
            invalid = true;
        }
        //contain number - bigSea API restriction
        else if (!newPassword.getText().toString().matches(".*\\d.*")){
            newPasswordView.setError(getString(R.string.password_restriction));
            invalid= true;
        }  else if (!newPassword.getText().toString().equals(passwordConfirmation.getText().toString())) {
            passwordConfirmation.setError(getString(R.string.password_must_match));
            passwordConfirmation.requestFocus();
            invalid = true;
        }

        if (invalid) {
            newPasswordView.requestFocus();
        }

        return invalid;
    }

    public class ChangePasswordTask extends AsyncTask<Void, Void, Boolean> {

        private final String ENDPOINT_ADDRESS = "https://eubrabigsea.dei.uc.pt/engine/api/change_password";
        private String username;
        private String oldPassword;
        private String newPassword;
        private String token;
        private String responseMessage = "";

        public ChangePasswordTask(String username, String oldPassword, String newPassword, String token) {
            this.username = username;
            this.oldPassword = oldPassword;
            this.newPassword = newPassword;
            this.token = token;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            URL url;
            boolean success = false;
            try {
                String parameters = "user=" + username + "&oldpwd=" + oldPassword + "&newpwd=" + newPassword + "&token=" + token;
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
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        responseMessage += line;
                    }
                    JSONObject jsonObject = new JSONObject(responseMessage);

                    if (jsonObject.has("success")) {
                        success = true;
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
            return success;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mChangePasswordTask = null;
            mProgressBar.setVisibility(View.GONE);
            if (success) {
                if (mCallback != null) {
                    mCallback.onPasswordChanged();
                }
                Toast.makeText(getContext(), "Sua senha foi modificada.", Toast.LENGTH_SHORT).show();
            } else {
                oldPasswordView.setError("Ocorreu um erro. Verifique se a sua senha atual está correta ou tente sair e entrar no aplicativo novamente.");
            }
        }

        @Override
        protected void onCancelled() {
            mChangePasswordTask = null;
        }
    }

    public interface OnPasswordChangedListener {
        void onPasswordChanged();
    }

}
