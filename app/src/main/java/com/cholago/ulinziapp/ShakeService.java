package com.cholago.ulinziapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Random;

public class ShakeService extends Service implements SensorEventListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity
    private boolean mSendMessage = true;
    private int count = 0;
    private int maxCount = 4;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer,
                SensorManager.SENSOR_DELAY_UI, new Handler());
        return START_STICKY;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
        float delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel * 0.9f + delta; // perform low-cut filter

        if (mAccel > 50) {
            //
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            try {
                MainActivity._shakeView.setText("Device shacked sending SMS message");
                MainActivity._shakeView.setTextColor(color);
            }
            catch (Exception e){
                Log.e(TAG, "Error failed", e);
            }
            //send message
            send();
        }
    }
    public void send(){
        count +=1;
        if(mSendMessage){
            sendMessage();
            mSendMessage = false;
        }
        if(count > maxCount){
            count = 1;
            mSendMessage = true;
        }
        delay(200);
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
        SessionManager session = new SessionManager(getApplicationContext());
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
            Toast.makeText(ShakeService.this, "SMS Sent Successfully", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Log.e(TAG, "SMS send error", e);
            Toast.makeText(ShakeService.this, "SMS Failed to Send, Please try again", Toast.LENGTH_SHORT).show();
        }
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
