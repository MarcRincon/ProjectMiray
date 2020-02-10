package com.example.newentry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    String Url,Database,Usuario,Password;
    EditText EdUrl,EdDatabase,EdUsuario,EdPassword;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);



    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setContentView(R.layout.activity_main);
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

    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.refresh:
                Toast.makeText(SettingsActivity.this, "azul clicado", Toast.LENGTH_LONG).show();

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                Url = sharedPreferences.getString("url_db", null)+"/";
                Database = sharedPreferences.getString("name_db", null);
                Usuario = sharedPreferences.getString("user_name", null);
                Password = sharedPreferences.getString("user_pass", null);
                TextView Test = (TextView)findViewById(R.id.test);
                Test.setText("Debug");

                EdUrl = (EditText)findViewById(R.id.config_url);
                EdDatabase = (EditText)findViewById(R.id.config_database);
                EdUsuario = (EditText)findViewById(R.id.config_usuario);
                EdPassword = (EditText)findViewById(R.id.config_password);

                EdUrl.setText(Url);
                EdDatabase.setText(Database);
                EdUsuario.setText(Usuario);
                EdPassword.setText(Password);
                break;
            default:
                break;
        }
    }
}
