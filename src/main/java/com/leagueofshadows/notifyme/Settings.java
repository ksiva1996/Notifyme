package com.leagueofshadows.notifyme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        final CheckBox checkBox = (CheckBox)findViewById(R.id.checkBox);
        SharedPreferences sp = getSharedPreferences("preferences",Context.MODE_PRIVATE);
        Boolean x= sp.getBoolean("notifications",true);
        checkBox.setChecked(x);
        Button changepass = (Button)findViewById(R.id.changepass);
        Button freq = (Button)findViewById(R.id.changefreq);
        final EditText et = (EditText)findViewById(R.id.freq);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked())
                {
                    SharedPreferences sp = getSharedPreferences("preferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit =sp.edit();
                    edit.putBoolean("notifications",true);
                    edit.apply();
                }
                else
                {
                    SharedPreferences sp = getSharedPreferences("preferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit =sp.edit();
                    edit.putBoolean("notifications",false);
                    edit.apply();
                }
            }
        });
        changepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Settings.this,Changepass.class);
                startActivity(i);
            }
        });
        freq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(et.getText().toString().equals(""))) {
                    SharedPreferences sp = getSharedPreferences("preferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putInt("refresh_value",Integer.parseInt(et.getText().toString()));
                    edit.apply();
                }
                else
                {
                    Toast.makeText(Settings.this,"enter a value",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
