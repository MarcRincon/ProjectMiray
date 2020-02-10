package com.example.newentry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.BatteryManager;
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
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_green, btn_yellow, btn_red, btn_blue,btn_options;

    private Connection conexionMySQL;
    private boolean estadoConexion;
    private String driver = "net.sourceforge.jtds.jdbc.Driver";
    // urlMySQL_head: jdbc:sqlserver   driver: com.mysql.jdbc.Driver
    private String urlMySQL_head = "jdbc:jtds:sqlserver://",urlMySQL;
    private String user = "usuario", pass = "usuariopass",db_name;

    EditText txtnomtablet, txtidtablet, txttipoalarma;
    Button btnparasave;
    DatabaseReference reff;
    AlarmasMedic alarmasMedic;
    PlugInControlReceiver plugInControlReceiver;

    IntentFilter intentfilter;
    int deviceStatus;
    String currentBatteryStatus="Battery Info";
    int batteryLevel = 40;
    private Integer count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intentfilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        //Firebase stuff

        alarmasMedic = new AlarmasMedic();
        plugInControlReceiver = new PlugInControlReceiver();


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);
        btn_yellow = findViewById(R.id.btn_yellow);
        btn_blue = findViewById(R.id.btn_blue);
        btn_green = findViewById(R.id.btn_green);
        btn_options = findViewById(R.id.placeholder_icon);

        btn_green.setOnClickListener(this);
        btn_yellow.setOnClickListener(this);
        btn_blue.setOnClickListener(this);
        btn_green.setOnClickListener(this);
        btn_options.setOnClickListener(this);
    }

    public static String timeDisplay(){
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

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String urlMySQL = urlMySQL_head + sharedPreferences.getString("url_db", null)+"/";;
        db_name = sharedPreferences.getString("name_db", null);
        user = sharedPreferences.getString("user_name", null);
        pass = sharedPreferences.getString("user_pass", null);


       /* //** -- OJOestos logs se tienen que borrar OJO -- **
        Log.i("datos",urlMySQL);
        Log.i("datos",db_name);
        Log.i("datos",user);
        Log.i("datos",pass);
*/
    }

        @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_blue:
                Toast.makeText(MainActivity.this, "azul clicado", Toast.LENGTH_LONG).show();
                Conectar conectar = new Conectar();
                conectar.execute();

                /*try {
                    abrirConexion();
                } catch (SQLException e) {
                    e.printStackTrace();
                }*/
                break;
            case R.id.btn_green:
                reff = FirebaseDatabase.getInstance().getReference().child(db_name);

                alarmasMedic.setTabletnom("TabletB1");
                alarmasMedic.setIdtablet(8089);
                alarmasMedic.setAlarmtype("Alarma Verde");
                alarmasMedic.setNombre_user(user);
                alarmasMedic.setPassword_user(pass);
                alarmasMedic.setTime(timeDisplay());
                alarmasMedic.setBatteryLvl(batteryLevel);
                alarmasMedic.setIsBatteryCharging("Cargador dakhd");
              /*  if (batteryCharging){
                    alarmasMedic.setIsBatteryCharging("Cargador conectado");
                } else {
                    alarmasMedic.setIsBatteryCharging("Cargador desconctado");
                }
*/
                reff.push().setValue(alarmasMedic);
                Toast.makeText(MainActivity.this, "Data inserted", Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_yellow:

                String debug = "Url = " + urlMySQL + "\nDatabase Name = " + db_name + "\nUsuario = " + user + "\nContraseña = " + pass;
                Toast.makeText(MainActivity.this,debug, Toast.LENGTH_LONG).show();

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
                String url = urlMySQL+db_name;
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

