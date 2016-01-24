package com.mikesperone.sensorweb;

import android.Manifest;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by katfox on 1/17/16.
 */
public class SendSensorService extends IntentService implements SensorEventListener {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    private static final String TAG = "SENSORWEB_LOGS";

    private SensorManager sMgr = null;

    private Sensor pressure;
    private Sensor light;
    private Sensor temperature;
    private Sensor humidity;
    private Sensor acceleration;

    private String routeName;
    public OSCPortOut sender = null;
    public InetAddress targetIP;

    private LocationManager mLocationManager = null;
    private LocationListener[] mLocationListeners;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;

    public SendSensorService() {
        super("SendSensorService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mLocationListeners = new LocationListener[]{
                new LocationListener(LocationManager.GPS_PROVIDER),
                new LocationListener(LocationManager.NETWORK_PROVIDER)
        };
    }

    @Override
    public void onHandleIntent(Intent intent) {

        setConnection(ipAddress, portAddress);
        sensorSetUp();
        locationSetUp();

    }

    public void stopSensors() {
        sMgr.unregisterListener((SensorEventListener) this);
        if (mLocationManager != null) {
            for (LocationListener mLocationListener : mLocationListeners) {
                try {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        mLocationManager.removeUpdates(mLocationListener);
                        return;
                    }
                } catch (Exception ex) {
                    Log.i(TAG, "failed to remove location listners, ignore", ex);
                }
            }
        }
    }

    public void setConnection (String ipAddress, int port) {

        try {
            targetIP = InetAddress.getByName(ipAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        try {
            sender = new OSCPortOut(targetIP, port); //------set up outgoing ------
        } catch (SocketException e) {
            e.printStackTrace();
        }

        if (sMgr != null) {
            sMgr.registerListener((SensorEventListener) this, pressure, SensorManager.SENSOR_DELAY_NORMAL);
            sMgr.registerListener((SensorEventListener) this, light, SensorManager.SENSOR_DELAY_NORMAL);
            sMgr.registerListener((SensorEventListener) this, temperature, SensorManager.SENSOR_DELAY_NORMAL);
            sMgr.registerListener((SensorEventListener) this, humidity, SensorManager.SENSOR_DELAY_NORMAL);
            sMgr.registerListener((SensorEventListener) this, acceleration, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void sensorSetUp() {
        // Set up sensor management
        sMgr = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        pressure = sMgr.getDefaultSensor(Sensor.TYPE_PRESSURE);
        light = sMgr.getDefaultSensor(Sensor.TYPE_LIGHT);
        temperature = sMgr.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        humidity = sMgr.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        acceleration = sMgr.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        sMgr.registerListener((SensorEventListener) this, pressure, SensorManager.SENSOR_DELAY_NORMAL);
        sMgr.registerListener((SensorEventListener) this, light, SensorManager.SENSOR_DELAY_NORMAL);
        sMgr.registerListener((SensorEventListener) this, temperature, SensorManager.SENSOR_DELAY_NORMAL);
        sMgr.registerListener((SensorEventListener) this, humidity, SensorManager.SENSOR_DELAY_NORMAL);
        sMgr.registerListener((SensorEventListener) this, acceleration, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void locationSetUp() {
        //Log.e(TAG, "onCreate");
        initializeLocationManager();
        try {
            //FusedLocationApi.requestLocationUpdates
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (SecurityException ex) {
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
        switch (sensor.getType()) {
            case Sensor.TYPE_PRESSURE:
                routeName = "/pressure";
                sendMyOscMessage(routeName, event.values[0]);
                break;
            case Sensor.TYPE_LIGHT:
                routeName = "/light";
                sendMyOscMessage(routeName, event.values[0]);
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                routeName = "/temperature";
                sendMyOscMessage(routeName, event.values[0]);
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                routeName = "/humidity";
                sendMyOscMessage(routeName, event.values[0]);
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                routeName = "/acceleration";
                sendMyOscMessage(routeName + "/x", event.values[0]);
                sendMyOscMessage(routeName + "/y", event.values[1]);
                sendMyOscMessage(routeName + "/z", event.values[2]);
                break;
        }
    }


    public void sendMyOscMessage(String sensor, Float val) {

        OSCMessage msg = new OSCMessage();
        msg.setAddress(sensor);
        msg.addArgument(val);

        try {
            sender.send(msg);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "can not send " + msg, Toast.LENGTH_SHORT).show();
            System.out.println("can not send");
            //e.printStackTrace();
        }

    }

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);

            float latitude = (float) location.getLatitude();
            float longitude = (float) location.getLongitude();
            float altitude = (float) location.getAltitude();
            float speed = location.getSpeed();
            sendMyOscMessage("/location/latitude", latitude);
            sendMyOscMessage("/location/longitude", longitude);
            sendMyOscMessage("/location/altitude", altitude);
            sendMyOscMessage("/location/speed", speed);
        }

        @Override
        public void onProviderDisabled(String provider) {
            //Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            //Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    }

}

