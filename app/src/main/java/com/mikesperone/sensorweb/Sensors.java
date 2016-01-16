package com.mikesperone.sensorweb;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;
import com.illposed.osc.OSCPort;
import com.illposed.osc.OSCPortIn;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import java.io.IOException;

/**
 * Created by katfox on 1/16/16.
 */

public class Sensors extends Service implements SensorEventListener {

    private static final String TAG = "SENSORWEB_LOGS";

    private String SERVER_ADDR = "68.198.36.58";
    private int SERVER_PORT = 55056;

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

    private float latitude;
    private float longitude;
    private float speed;
    private float altitude;

    private LocationManager mLocationManager = null;

    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;

    private Float temp;

    private String routeName;  //OSC routing

    public OSCPortOut sender = null;
    //private OSCPortIn receiver;  //currently not trying to receive data
    public InetAddress targetIP;

    sensorSetUp();
    locationSetUp();
    setConnection();

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }
    public void setConnection(){
        try {
            targetIP = InetAddress.getByName(SERVER_ADDR);
            //targetIP = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();        }

        try {
            sender = new OSCPortOut(targetIP, SERVER_PORT); //------set up outgoing ------
        } catch (SocketException e) {
            e.printStackTrace();
        }

        /*try {                                     //------set up incoming-------
            receiver = new OSCPortIn(4444);         //----NO CURRENT INCOMING-----
        } catch (SocketException e) {
            e.printStackTrace();
        } */

    }

    public void sensorSetUp() {
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

        // Set up sensor management
        sMgr = (SensorManager)this.getSystemService(SENSOR_SERVICE);
        pressure = sMgr.getDefaultSensor(Sensor.TYPE_PRESSURE);
        light = sMgr.getDefaultSensor(Sensor.TYPE_LIGHT);
        temperature = sMgr.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        humidity = sMgr.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        rotation = sMgr.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
        acceleration = sMgr.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
    }

    public void locationSetUp() {
        //Log.e(TAG, "onCreate");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }
    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
        sendMyOscMessage("/accuracy", (float) accuracy);
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        // Do something with this sensor data.
        Sensor sensor = event.sensor;
        switch(sensor.getType()){
            case Sensor.TYPE_PRESSURE:
                //routeName = "/pressure";
                pressureValue.setText(Float.toString(event.values[0]));
                //sendMyOscMessage(routeName, event.values[0]);
                break;
            case Sensor.TYPE_LIGHT:
                routeName = "/light";
                lightValue.setText(Float.toString(event.values[0]));
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                routeName = "/temperature";
                temperatureValue.setText(Float.toString(event.values[0]));
                sendMyOscMessage(routeName, event.values[0]);
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                routeName = "/humidity";
                humidityValue.setText(Float.toString(event.values[0]));
                break;
            case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
                routeName = "/rotation";
                rotationXValue.setText(Float.toString(event.values[0]));
                rotationYValue.setText(Float.toString(event.values[1]));
                rotationZValue.setText(Float.toString(event.values[2]));
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                routeName = "/acceleration";
                accelerationXValue.setText(Float.toString(event.values[0]));
                accelerationYValue.setText(Float.toString(event.values[1]));
                accelerationZValue.setText(Float.toString(event.values[2]));
                break;
        }
    }

    public void sendMyOscMessage(String sensor, Float val){

        OSCMessage msg = new OSCMessage();
        msg.setAddress(sensor);
        msg.addArgument(val);

        try {
            sender.send(msg);
            //System.out.println("OSC message sent!");
        } catch (Exception e) {
            System.out.println("can not send");
            //e.printStackTrace();
        }

    }

    private class LocationListener implements android.location.LocationListener
    {
        Location mLastLocation;

        public LocationListener(String provider)
        {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location)
        {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);

            latitude = (float) location.getLatitude();
            longitude = (float) location.getLongitude();
            altitude = (float) location.getAltitude();
            speed = location.getSpeed();
            sendMyOscMessage("/location/latitude", latitude);
            sendMyOscMessage("/location/longitude", longitude);
            sendMyOscMessage("/location/altitude", altitude);
            sendMyOscMessage("/location/speed", speed);
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            //Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            //Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            //Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
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
        sMgr.unregisterListener(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onDestroy()
    {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        Toast.makeText(this, "ServiceStopped", Toast.LENGTH_LONG).show();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        sMgr.registerListener(this, pressure, SensorManager.SENSOR_DELAY_NORMAL);
        sMgr.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL);
        sMgr.registerListener(this, temperature, SensorManager.SENSOR_DELAY_NORMAL);
        sMgr.registerListener(this, humidity, SensorManager.SENSOR_DELAY_NORMAL);
        sMgr.registerListener(this, rotation, SensorManager.SENSOR_DELAY_NORMAL);
        sMgr.registerListener(this, acceleration, SensorManager.SENSOR_DELAY_NORMAL);
        return START_STICKY;
    }
}
