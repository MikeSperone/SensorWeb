package com.mikesperone.sensorweb;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.EditText;


public class FullscreenActivity extends AppCompatActivity {

    private static final String TAG = "SENSORWEB_LOGS";

    EditText ipButton = (EditText) findViewById(R.id.ipAddr);
    String ipAddress = (String) ipButton.getText().toString();
    EditText portButton = (EditText) findViewById(R.id.port);
    String portAddress = (String) portButton.getText().toString();

    Intent mServiceIntent = new Intent(this, SendSensorService.class);
    mServiceIntent.putExtra(ipAddress, portAddress);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        setContentView(R.layout.activity_fullscreen);

    }

    public void startSensor() {
        startService(mServiceIntent);
    }
    
    @Override
    protected void onResume() {
        // Register a listener for the sensor.
        super.onResume();

    }

    @Override
    protected void onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onPause();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
    }
}