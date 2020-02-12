package com.example.newentry;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import static com.example.newentry.MainActivity.timeDisplay;

public class PlugInControlReceiver extends BroadcastReceiver {
    public PlugInControlReceiver() {

    }

    public boolean batteryCharging;
    public Integer batteryLvl;
    DeviceManager deviceManager;
    DatabaseReference reffDevices;


    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_POWER_CONNECTED)) {

            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int batteryPct = (int) ((level / (float) scale) * 100);
            setBatteryLvl(batteryPct);
            setBatteryCharging(true);

            //Shared prefs
            SharedPreferences prefs = context.getSharedPreferences(
                    "com.example.newentry", Context.MODE_PRIVATE);
            prefs.edit().putInt("percentageBattery", batteryPct).apply();
            prefs.edit().putString("chargerConnected", "Conectado").apply();

            int actualBattery = prefs.getInt("percentageBattery", -1);
            Toast.makeText(context, "Cargador conectado " + actualBattery + "%", Toast.LENGTH_LONG).show();

        } else if (action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int batteryPct = (int) ((level / (float) scale) * 100);
            setBatteryLvl(batteryPct);
            setBatteryCharging(false);

            //Shared prefs
            SharedPreferences prefs = context.getSharedPreferences(
                    "com.example.newentry", Context.MODE_PRIVATE);
            prefs.edit().putInt("percentageBattery", batteryPct).apply();
            prefs.edit().putString("chargerConnected", "Desconectado").apply();

            int actualBattery = prefs.getInt("percentageBattery", -1);
            Toast.makeText(context, "Cargador desconectado " + actualBattery + "%", Toast.LENGTH_LONG).show();

        } else if (action.equals(Intent.ACTION_BATTERY_LOW)) {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int batteryPct = (int) ((level / (float) scale) * 100);
            setBatteryLvl(batteryPct);
            setBatteryCharging(false);

            Toast.makeText(context, "Bateria baja " + batteryPct + "%", Toast.LENGTH_LONG).show();

        } else if (action.equals(Intent.ACTION_SHUTDOWN)){
            //Shared prefs
            SharedPreferences prefs = context.getSharedPreferences(
                    "com.example.newentry", Context.MODE_PRIVATE);
            prefs.edit().putString("deviceStatus","Off").apply();
            prefs.edit().putString("latestAction","Shutdown").apply();

            deviceManager.setNom_tablet("TabletB1");
            deviceManager.setID_tablet(8089);
            deviceManager.setUltima_Accion("Shutdown");
            deviceManager.setApp_status("Dispositivo apagado");
            deviceManager.setLast_check(timeDisplay());

            reffDevices.push().setValue(deviceManager);

        } else if (action.equals(Intent.ACTION_REBOOT)){
            SharedPreferences prefs = context.getSharedPreferences(
                    "com.example.newentry", Context.MODE_PRIVATE);
            prefs.edit().putString("deviceStatus","Off").apply();
            prefs.edit().putString("latestAction","Reboot").apply();

            deviceManager.setNom_tablet("TabletB1");
            deviceManager.setID_tablet(8089);
            deviceManager.setUltima_Accion("Reboot");
            deviceManager.setApp_status("Dispositivo apagado");
            deviceManager.setLast_check(timeDisplay());

            reffDevices.push().setValue(deviceManager);

        } else if (action.equals(Intent.ACTION_BATTERY_CHANGED)){
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int batteryPct = (int) ((level / (float) scale) * 100);
            setBatteryLvl(batteryPct);
            setBatteryCharging(true);

            //Shared prefs
            SharedPreferences prefs = context.getSharedPreferences(
                    "com.example.newentry", Context.MODE_PRIVATE);
            prefs.edit().putInt("percentageBattery", batteryPct).apply();

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