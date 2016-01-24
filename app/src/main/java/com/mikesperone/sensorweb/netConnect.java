package com.mikesperone.sensorweb;

import android.widget.Toast;

import com.illposed.osc.OSCMessage;

/**
 * Created by katfox on 1/23/16.
 */
public class netConnect {
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
}
