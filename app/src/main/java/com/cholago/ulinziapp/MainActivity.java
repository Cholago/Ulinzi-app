package com.cholago.ulinziapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    public static SessionManager session; //session manager
    public static TextView _shakeView;
    public static Button _logoutButton;

    private static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    String sPermission = Manifest.permission.SEND_SMS;


    // GPSTracker class
    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _shakeView = findViewById(R.id.shake);
        _logoutButton = findViewById(R.id.logout_button);
        // session manager
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            session.setLogin(false);
            // Launching the login activity
            Intent intent = new Intent(MainActivity.this, Register.class);
            startActivity(intent);
            finish();
        }

        try {
            if (ActivityCompat.checkSelfPermission(this, mPermission)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{mPermission},
                        REQUEST_CODE_PERMISSION);

                // If any permission above not allowed by user, this condition will
                //execute every time, else your else part will work
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (ActivityCompat.checkSelfPermission(this, sPermission)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{sPermission},
                        REQUEST_CODE_PERMISSION);

                // If any permission above not allowed by user, this condition will
                //execute every time, else your else part will work
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        Intent intent = new Intent(this, ShakeService.class);
        //Start Service
        startService(intent);

        _logoutButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                session.setLogin(false);
                Intent intent = new Intent(MainActivity.this, Register.class);
                startActivity(intent);
                finish();

            }
        });
    }

    public void sendMessage(){

        GPSTracker gt = new GPSTracker(getApplicationContext());
        Location l = gt.getLocation();
        String message;
        boolean gps;
        if( l == null){
            //Toast.makeText(getApplicationContext(),"GPS unable to get Value",Toast.LENGTH_SHORT).show();
            gps = false;
        }else {
            gps = true;
            double lat = l.getLatitude();
            double lon = l.getLongitude();
            //Toast.makeText(getApplicationContext(),"GPS Lat = "+lat+"\n lon = "+lon,Toast.LENGTH_SHORT).show();
        }


        String name =session.getName();
        String idNumber = session.getIdNumber();
        if(gps){
            message = "My name is " + name + " and my ID No: " + idNumber + " i am in trouble right now this is agent. My location is Lat =" + l.getLatitude() + " Long = " + l.getLongitude();

        }else {
            message = "My name is " + name + " and my ID No: " + idNumber + " i am in trouble right now this is agent. Failed to obtain gps coordinates";

        }
        //Send the message
        try{
            SmsManager smgr = SmsManager.getDefault();
            smgr.sendTextMessage(session.getPhoneNumber(),null,message,null,null);
            Toast.makeText(MainActivity.this, "SMS Sent Successfully", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Log.e(TAG, "SMS send error", e);
            Toast.makeText(MainActivity.this, "SMS Failed to Send, Please try again", Toast.LENGTH_SHORT).show();
        }
    }
}
