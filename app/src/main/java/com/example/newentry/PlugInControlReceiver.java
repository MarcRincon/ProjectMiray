package com.example.newentry;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.widget.Toast;

public class PlugInControlReceiver extends BroadcastReceiver {
    public PlugInControlReceiver(){

    }

    public boolean batteryCharging;
    public Integer batteryLvl;


    public void onReceive(Context context , Intent intent) {
        String action = intent.getAction();
        if(action.equals(Intent.ACTION_POWER_CONNECTED)) {

            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int batteryPct = (int) ((level / (float)scale)*100);
            setBatteryLvl(batteryPct);
            setBatteryCharging(true);

            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("battery", batteryLvl);
            Toast.makeText(context, "Cargador conectado " + batteryLvl + "%", Toast.LENGTH_LONG).show();

        }
        else if(action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int batteryPct = (int) ((level /(float)scale)*100);
            setBatteryLvl(batteryPct);
            setBatteryCharging(false);

            Toast.makeText(context, "Cargador desconectado " + batteryPct + "%", Toast.LENGTH_LONG).show();

        }
        else if (action.equals(Intent.ACTION_BATTERY_LOW)){
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int batteryPct = (int) ((level /(float)scale)*100);
            setBatteryLvl(batteryPct);
            setBatteryCharging(false);

            Toast.makeText(context, "Bateria baja " + batteryPct + "%", Toast.LENGTH_LONG).show();
        }
    }

    public boolean isBatteryCharging() {
        return batteryCharging;
    }

    public void setBatteryCharging(boolean batteryCharging) {
        this.batteryCharging = batteryCharging;
    }

    public Integer getBatteryLvl() {
        return batteryLvl;
    }

    public void setBatteryLvl(Integer batteryLvl) {
        this.batteryLvl = batteryLvl;
    }
}