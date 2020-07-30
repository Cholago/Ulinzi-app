package com.cholago.ulinziapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Register extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    SessionManager session; //session manager

    @BindView(R.id.name)  EditText _nameText;
    @BindView(R.id.phone) EditText _phoneText;
    @BindView(R.id.id_number) EditText _idNumberText;

    @BindView(R.id.register_button)  Button _registerButton;
    //scrollwiew
    @BindView(R.id.scrollview) ScrollView scrollView;

    String name;
    String phone;
    String idNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        // session manager
        session = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this, R.style.dialogStyle);

        if (session.isLoggedIn()) {
            launchMainActivity();
        }

        _registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //Validate form
               if(validate()){
                   _registerButton.setEnabled(false);
                   Log.i("Registering form", "Form validated");
                   //log in function
                   session.setUser(name,phone,idNumber);
                   session.setLogin(true);
                   pDialog.setMessage("Loading, please wait...");
                   showDialog();
                   delay(2500);
                   hideDialog();
                   launchMainActivity();

               }
               else{
                   _registerButton.setEnabled(true);
                   Log.i("Login form", "Form failed to validate");
               }
            }
        });
    }

    public void launchMainActivity(){
        // User is already logged in. Take him to main activity
        Intent intent = new Intent(Register.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    //form validation
    public boolean validate() {
        boolean valid = true;

        name = _nameText.getText().toString();
        phone = _phoneText.getText().toString();
        idNumber = _idNumberText.getText().toString();

        if (name.isEmpty() || name.length() < 4 || name.length() > 15) {
            _nameText.setError("at least 4 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (phone.isEmpty() || phone.length() < 10 || phone.length() > 15) {
            _phoneText.setError("at least 6 characters");
            valid = false;
        } else {
            _phoneText.setError(null);
        }

        if (idNumber.isEmpty() || idNumber.length() < 8 || idNumber.length() > 15) {
            _idNumberText.setError("at least 6 characters");
            valid = false;
        } else {
            _idNumberText.setError(null);
        }

        return valid;
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    //delay function
    public void delay(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Log.e(TAG, "Sleep error", e);
        }
    }
}
