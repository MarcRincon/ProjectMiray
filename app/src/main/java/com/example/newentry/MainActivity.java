package com.example.newentry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_green, btn_yellow, btn_red, btn_blue, btn_options;

    private Connection conexionMySQL;
    private boolean estadoConexion;
    private String driver = "net.sourceforge.jtds.jdbc.Driver";
    // urlMySQL_head: jdbc:sqlserver   driver: com.mysql.jdbc.Driver
    private String urlMySQL_head = "jdbc:jtds:sqlserver://", urlMySQL;
    private String user = "usuario", pass = "usuariopass", db_name;

    EditText txtnomtablet, txtidtablet, txttipoalarma;
    Button btnparasave;
    DatabaseReference reff;
    DatabaseReference reffDevices;
    AlarmasMedic alarmasMedic;
    DeviceManager deviceManager;
    PlugInControlReceiver plugInControlReceiver;

    IntentFilter intentfilter;
    int deviceStatus;
    String currentBatteryStatus = "Battery Info";
    int batteryLevel;
    private Integer count = 0;


    public static boolean isPlugged(Context context) {
        boolean isPlugged = false;
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        isPlugged = plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            isPlugged = isPlugged || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS;
        }
        return isPlugged;
    }

    private BroadcastReceiver batteryInfo = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intentfilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        //Firebase stuff

        alarmasMedic = new AlarmasMedic();
        deviceManager = new DeviceManager();
        plugInControlReceiver = new PlugInControlReceiver();


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);
        btn_yellow = findViewById(R.id.btn_yellow);
        btn_blue = findViewById(R.id.btn_blue);
        btn_green = findViewById(R.id.btn_green);
        btn_options = findViewById(R.id.placeholder_icon);
        btn_red = findViewById(R.id.btn_red);

        btn_green.setOnClickListener(this);
        btn_yellow.setOnClickListener(this);
        btn_blue.setOnClickListener(this);
        btn_red.setOnClickListener(this);

        btn_options.setOnClickListener(this);


    }

    public static String timeDisplay() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss dd-MM-yyy");
        String currentDate = format.format(calendar.getTime());
        return currentDate;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = this.getSharedPreferences(
                "com.example.newentry", Context.MODE_PRIVATE);
        if (isPlugged(this)) {
            prefs.edit().putString("chargerConnected", "Conectado").apply();
        } else if (!isPlugged(this)) {
            prefs.edit().putString("chargerConnected", "Desconectado").apply();
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String urlMySQL = urlMySQL_head + sharedPreferences.getString("url_db", null) + "/";
        db_name = sharedPreferences.getString("name_db", null);
        user = sharedPreferences.getString("user_name", null);
        pass = sharedPreferences.getString("user_pass", null);

        String latestAction = sharedPreferences.getString("latestAction", null);
        String batteryConnected = prefs.getString("chargerConnected", "defaultStringIfNothingFound");
        BatteryManager bm = (BatteryManager)getSystemService(BATTERY_SERVICE);
        assert bm != null;
        int percentage = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        reffDevices = FirebaseDatabase.getInstance().getReference().child("Devices Status").child("Tablet B1");
        deviceManager.setNom_tablet("TabletB1");
        deviceManager.setID_tablet(8089);
        deviceManager.setUltima_Accion(latestAction);
        deviceManager.setApp_status("Aplicación abierta");
        deviceManager.setLast_check(timeDisplay());
        deviceManager.setBattery_lvl(percentage);
        deviceManager.setDevice_charger(batteryConnected);

        reffDevices.setValue(deviceManager);
       /* //** -- OJOestos logs se tienen que borrar OJO -- **
        Log.i("datos",urlMySQL);
        Log.i("datos",db_name);
        Log.i("datos",user);
        Log.i("datos",pass);
*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences prefs = this.getSharedPreferences(
                "com.example.newentry", Context.MODE_PRIVATE);
        if (isPlugged(this)) {
            prefs.edit().putString("chargerConnected", "Conectado").apply();
        } else if (!isPlugged(this)) {
            prefs.edit().putString("chargerConnected", "Desconectado").apply();
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String latestAction = sharedPreferences.getString("latestAction", null);
        String batteryConnected = prefs.getString("chargerConnected", "defaultStringIfNothingFound");
        BatteryManager bm = (BatteryManager)getSystemService(BATTERY_SERVICE);
        assert bm != null;
        int percentage = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        reffDevices = FirebaseDatabase.getInstance().getReference().child("Devices Status").child("Tablet B1");
        deviceManager.setNom_tablet("TabletB1");
        deviceManager.setID_tablet(8089);
        deviceManager.setUltima_Accion(latestAction);
        deviceManager.setApp_status("Aplicación pausada");
        deviceManager.setLast_check(timeDisplay());
        deviceManager.setBattery_lvl(percentage);
        deviceManager.setDevice_charger(batteryConnected);

        reffDevices.setValue(deviceManager);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        SharedPreferences prefs = this.getSharedPreferences(
                "com.example.newentry", Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String database = sharedPreferences.getString("name_db", "Database");
        int actualBattery = prefs.getInt("percentageBattery", -1);
        String batteryConnected = prefs.getString("chargerConnected", "defaultStringIfNothingFound");
        reff = FirebaseDatabase.getInstance().getReference().child(database).child("Log " + timeDisplay());
        BatteryManager bm = (BatteryManager)getSystemService(BATTERY_SERVICE);
        assert bm != null;
        int percentage = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        switch (id) {
            case R.id.btn_blue:

                alarmasMedic.setNom_tablet("TabletB1");
                alarmasMedic.setID_tablet(8089);
                alarmasMedic.setTipo_Alarma("Alarma Azul");
                alarmasMedic.setNom_user(user);
                alarmasMedic.setPassword_user(pass);
                alarmasMedic.setTime(timeDisplay());

                deviceManager.setBattery_lvl(percentage);
                deviceManager.setDevice_charger(batteryConnected);
                deviceManager.setLast_check(timeDisplay());

              /*  if (batteryCharging){
                    alarmasMedic.setDevice_charger("Cargador conectado");
                } else {
                    alarmasMedic.setDevice_charger("Cargador desconctado");
                }
*/
                reff.setValue(alarmasMedic);
                reffDevices.setValue(deviceManager);
                Toast.makeText(MainActivity.this, "Data inserted", Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_green:
                alarmasMedic.setNom_tablet("TabletB1");
                alarmasMedic.setID_tablet(8089);
                alarmasMedic.setTipo_Alarma("Alarma Verde");
                alarmasMedic.setNom_user(user);
                alarmasMedic.setPassword_user(pass);
                alarmasMedic.setTime(timeDisplay());

                deviceManager.setBattery_lvl(percentage);
                deviceManager.setDevice_charger(batteryConnected);
                deviceManager.setLast_check(timeDisplay());
              /*  if (batteryCharging){
                    alarmasMedic.setDevice_charger("Cargador conectado");
                } else {
                    alarmasMedic.setDevice_charger("Cargador desconctado");
                }
*/
                reff.setValue(alarmasMedic);
                reffDevices.setValue(deviceManager);
                Toast.makeText(MainActivity.this, "Data inserted", Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_yellow:
                alarmasMedic.setNom_tablet("TabletB1");
                alarmasMedic.setID_tablet(8089);
                alarmasMedic.setTipo_Alarma("Alarma Amarilla");
                alarmasMedic.setNom_user(user);
                alarmasMedic.setPassword_user(pass);
                alarmasMedic.setTime(timeDisplay());
                deviceManager.setBattery_lvl(percentage);
                deviceManager.setDevice_charger(batteryConnected);
                deviceManager.setLast_check(timeDisplay());

              /*  if (batteryCharging){
                    alarmasMedic.setDevice_charger("Cargador conectado");
                } else {
                    alarmasMedic.setDevice_charger("Cargador desconctado");
                }
*/
                reff.setValue(alarmasMedic);
                reffDevices.setValue(deviceManager);
                Toast.makeText(MainActivity.this, "Data inserted", Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_red:

                alarmasMedic.setNom_tablet("TabletB1");
                alarmasMedic.setID_tablet(8089);
                alarmasMedic.setTipo_Alarma("Alarma Roja");
                alarmasMedic.setNom_user(user);
                alarmasMedic.setPassword_user(pass);
                alarmasMedic.setTime(timeDisplay());
                deviceManager.setBattery_lvl(percentage);
                deviceManager.setDevice_charger(batteryConnected);
                deviceManager.setLast_check(timeDisplay());

              /*  if (batteryCharging){
                    alarmasMedic.setDevice_charger("Cargador conectado");
                } else {
                    alarmasMedic.setDevice_charger("Cargador desconctado");
                }
*/
                reff.setValue(alarmasMedic);
                reffDevices.setValue(deviceManager);
                Toast.makeText(MainActivity.this, "Data inserted", Toast.LENGTH_LONG).show();
                break;
            case R.id.placeholder_icon:
                startActivity(new Intent(this, Preferences.class));
                break;
            default:
                break;
        }
    }


    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }


    private class Conectar extends AsyncTask<Void, Integer, Boolean> {
        Connection conn;

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String url = urlMySQL + db_name;
                Log.i("url", url);

                Class.forName(driver).newInstance();

                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.10.114:3306", "boss", "123456");


                if (conn == null) {
                    return false;
                }
            } catch (NoClassDefFoundError e) {
                Log.e("catch Definicion de clase", e.getMessage());
            } catch (ClassNotFoundException e) {
                Log.e("catch Clase no encontrada", e.getMessage());
            } catch (Exception e) {
                Log.e("catch ERROR Conexion: ", e.getMessage());
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean resultado) {
            if (resultado) {
                Toast.makeText(getBaseContext(), "Conectado", Toast.LENGTH_SHORT).show();
                Log.d("LOG:", "conectado");
            } else {
                Toast.makeText(getBaseContext(), "No conectado", Toast.LENGTH_SHORT).show();
                Log.d("LOG:", "no conectado");
            }
        }
    }



/*
    public boolean conectarMySQL() {

        try {

            Class.forName(driver).newInstance();

            conexionMySQL = DriverManager.getConnection(urlMySQL + "ejemplos", user, pass);

            if (!conexionMySQL.isClosed()) {
                estadoConexion = true;
                Toast.makeText(MainActivity.this, "Conexión Establecida", Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            Toast.makeText(MainActivity.this, "Error al comprobar las credenciales:" + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
        return estadoConexion;
    }

    public void abrirConexion() throws SQLException {
        Connection conexion;
        Statement sentencia;
        //Si el valor devuelto por la función es true, pasaremos los datos de la conexión a la siguiente Activity
       if (conectarMySQL() == true) {
        Toast.makeText(this, "Los datos de conexión introducidos son correctos.", Toast.LENGTH_LONG).show();

        String insertQuery = "insert into divice values (30,'tablet4','azul')";
        try {
            Class.forName(driver).newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        conexion = DriverManager.getConnection(urlMySQL + "ejemplos", user, pass);
        sentencia = conexion.createStatement();
        sentencia.executeQuery(insertQuery);
       }
    }*/
}

