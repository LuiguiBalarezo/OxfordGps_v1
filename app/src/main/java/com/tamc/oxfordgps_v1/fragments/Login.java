package com.tamc.oxfordgps_v1.fragments;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.tamc.oxfordgps_v1.MainActivity;
import com.tamc.oxfordgps_v1.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class Login extends Fragment implements Validator.ValidationListener{

    private static final String TAG = "Login";

    private String user, pass;

    @NotEmpty(sequence = 2, message = "Debe ingresar email.")
    @Email(sequence = 3, message = "Email no cuenta con formato.")
    @Order(2)
    @InjectView(R.id.Edt_user)
    EditText edt_user;

    @NotEmpty(sequence = 1, message = "Debe ingresar una Contraseña.")
    @Order(1)
    @InjectView(R.id.Edt_pass)
    EditText edt_pass;

    @InjectView(R.id.Btn_singin)
    Button btn_singin;

    Validator validator;


    ClicksLogin clicksLogin;

    public interface ClicksLogin {
        public void onClickButtonlogin(String user, String clave);
    }

    public Login() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            clicksLogin = (ClicksLogin) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() ESTAS EN LOGIN");
        validator = new Validator(this);
        validator.setValidationListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.inject(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainActivity.setShowToolBar(false, "");
//        TestService.setUseAlarm(true);
    }

    @OnClick(R.id.Btn_singin)
      public void Btn_singin() {
        Log.d(TAG, "Btn_singin()");
        validator.validate();
    }

    @Override
    public void onValidationSucceeded() {
        user = edt_user.getText().toString();
        pass = edt_pass.getText().toString();
        clicksLogin.onClickButtonlogin(user, pass);
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            if (error.getView() instanceof Checkable) {
                Toast.makeText(getActivity(), error.getCollatedErrorMessage(getActivity()), Toast.LENGTH_SHORT);
            } else if (error.getView() instanceof TextView) {
                TextView view = (TextView) error.getView();
                view.requestFocus();
                view.setError(error.getCollatedErrorMessage(getActivity()));
            }
        }
    }




}
