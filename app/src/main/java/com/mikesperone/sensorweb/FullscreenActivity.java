package com.mikesperone.sensorweb;

import android.annotation.SuppressLint;
//import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity implements SensorEventListener {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;

    private View mContentView;
    private View mControlsView;
    private boolean mVisible;

    private TextView pressureValue;
    private TextView lightValue;
    private TextView temperatureValue;
    private TextView humidityValue;
    private TextView rotationXValue;
    private TextView rotationYValue;
    private TextView rotationZValue;
    private TextView accelerationXValue;
    private TextView accelerationYValue;
    private TextView accelerationZValue;

    private SensorManager sMgr;

    private Sensor pressure;
    private Sensor light;
    private Sensor temperature;
    private Sensor humidity;
    private Sensor rotation;
    private Sensor acceleration;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        mVisible = true;
        //mControlsView = findViewById(R.id.fullscreen_content_controls);
        //mContentView = findViewById(R.id.fullscreen_content);

        pressureValue = (TextView)findViewById(R.id.pressureValue);
        lightValue = (TextView)findViewById(R.id.lightValue);
        temperatureValue = (TextView)findViewById(R.id.temperatureValue);
        humidityValue = (TextView)findViewById(R.id.humidityValue);
        rotationXValue = (TextView)findViewById(R.id.rotationXValue);
        rotationYValue = (TextView)findViewById(R.id.rotationYValue);
        rotationZValue = (TextView)findViewById(R.id.rotationZValue);
        accelerationXValue = (TextView)findViewById(R.id.accelerationXValue);
        accelerationYValue = (TextView)findViewById(R.id.accelerationYValue);
        accelerationZValue = (TextView)findViewById(R.id.accelerationZValue);

        // Set up the user interaction to manually show or hide the system UI.
//        mContentView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                toggle();
//            }
//        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        // Set up sensor management
        sMgr = (SensorManager)this.getSystemService(SENSOR_SERVICE);
        pressure = sMgr.getDefaultSensor(Sensor.TYPE_PRESSURE);
        light = sMgr.getDefaultSensor(Sensor.TYPE_LIGHT);
        temperature = sMgr.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        humidity = sMgr.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        rotation = sMgr.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
        acceleration = sMgr.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        // Do something with this sensor data.
        Sensor sensor = event.sensor;
        switch(sensor.getType()){
            case Sensor.TYPE_PRESSURE:
                pressureValue.setText(Float.toString(event.values[0]));
                break;
            case Sensor.TYPE_LIGHT:
                lightValue.setText(Float.toString(event.values[0]));
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                temperatureValue.setText(Float.toString(event.values[0]));
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                humidityValue.setText(Float.toString(event.values[0]));
                break;
            case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
                rotationXValue.setText(Float.toString(event.values[0]));
                rotationYValue.setText(Float.toString(event.values[1]));
                rotationZValue.setText(Float.toString(event.values[2]));
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                accelerationXValue.setText(Float.toString(event.values[0]));
                accelerationYValue.setText(Float.toString(event.values[1]));
                accelerationZValue.setText(Float.toString(event.values[2]));
                break;
        }
    }

    @Override
    protected void onResume() {
        // Register a listener for the sensor.
        super.onResume();
        sMgr.registerListener(this, pressure, SensorManager.SENSOR_DELAY_NORMAL);
        sMgr.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL);
        sMgr.registerListener(this, temperature, SensorManager.SENSOR_DELAY_NORMAL);
        sMgr.registerListener(this, humidity, SensorManager.SENSOR_DELAY_NORMAL);
        sMgr.registerListener(this, rotation, SensorManager.SENSOR_DELAY_NORMAL);
        sMgr.registerListener(this, acceleration, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onPause();
        sMgr.unregisterListener(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        //delayedHide(100);
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
//    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
//        @Override
//        public boolean onTouch(View view, MotionEvent motionEvent) {
//            if (AUTO_HIDE) {
//                delayedHide(AUTO_HIDE_DELAY_MILLIS);
//            }
//            return false;
//        }
//    };
//
//    private void toggle() {
//        if (mVisible) {
//            hide();
//        } else {
//            show();
//        }
//    }

//    private void hide() {
//        // Hide UI first
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.hide();
//        }
//        mControlsView.setVisibility(View.GONE);
//        mVisible = false;
//
//        // Schedule a runnable to remove the status and navigation bar after a delay
//        mHideHandler.removeCallbacks(mShowPart2Runnable);
//        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
//    }

//    private final Runnable mHidePart2Runnable = new Runnable() {
//        @SuppressLint("InlinedApi")
//        @Override
//        public void run() {
//            // Delayed removal of status and navigation bar
//
//            // Note that some of these constants are new as of API 16 (Jelly Bean)
//            // and API 19 (KitKat). It is safe to use them, as they are inlined
//            // at compile-time and do nothing on earlier devices.
//            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//        }
//    };
//
//    @SuppressLint("InlinedApi")
//    private void show() {
//        // Show the system bar
//        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
//        mVisible = true;
//
//        // Schedule a runnable to display UI elements after a delay
//        mHideHandler.removeCallbacks(mHidePart2Runnable);
//        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
//    }
//
//    private final Runnable mShowPart2Runnable = new Runnable() {
//        @Override
//        public void run() {
//            // Delayed display of UI elements
//            ActionBar actionBar = getSupportActionBar();
//            if (actionBar != null) {
//                actionBar.show();
//            }
//            mControlsView.setVisibility(View.VISIBLE);
//        }
//    };
//
//    private final Handler mHideHandler = new Handler();
//    private final Runnable mHideRunnable = new Runnable() {
//        @Override
//        public void run() {
//            hide();
//        }
//    };
//
//    /**
//     * Schedules a call to hide() in [delay] milliseconds, canceling any
//     * previously scheduled calls.
//     */
//    private void delayedHide(int delayMillis) {
//        mHideHandler.removeCallbacks(mHideRunnable);
//        mHideHandler.postDelayed(mHideRunnable, delayMillis);
//    }
}