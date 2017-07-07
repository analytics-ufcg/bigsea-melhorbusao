package br.edu.ufcg.analytics.meliorbusao.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import br.edu.ufcg.analytics.meliorbusao.R;
import br.edu.ufcg.analytics.meliorbusao.activities.MelhorBusaoActivity;


public class ChangePasswordFragment extends Fragment {

    public static final String TAG = "ChangePasswordFragment";

    private AutoCompleteTextView oldPasswordView;
    private AutoCompleteTextView newPasswordView;
    private AutoCompleteTextView confirmNewPasswordView;
    private Button saveChangesButton;
    private ChangePasswordTask mChangePasswordTask;

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

        return mainView;
    }

    private void attemptSave() {

        if (mChangePasswordTask != null) {
            return;
        }

        oldPasswordView.setError(null);
        newPasswordView.setError(null);

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
            newPassword.requestFocus();
        }

        return invalid;
    }

    public class ChangePasswordTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            return null;
        }
    }

}
