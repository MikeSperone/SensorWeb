package com.mikesperone.sensorweb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

/**
 * Created by katfox on 1/17/16.
 */
public class PowerConnectionReceiver extends BroadcastReceiver {
    float charging;
    float chargeType;
    @Override
    public void onReceive(Context context, Intent intent) {
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
        if (isCharging) { charging = 1; } else { charging = 0; }

        int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
        if (usbCharge) {chargeType = 0;} else { chargeType = 2; }
        if (acCharge) {chargeType = 1;}
        //sendMyOscMessage("/charging", charging);
        //sendMyOscMessage("/chargeType", chargeType);
        //sendMyOscMessage("/battery", (intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) / (float)intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)));

    }
}