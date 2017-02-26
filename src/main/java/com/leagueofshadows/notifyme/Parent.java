package com.leagueofshadows.notifyme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.transition.ChangeBounds;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Parent extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);
        //Getting if a user is already logged in
        SharedPreferences sp = getSharedPreferences("preferences",Context.MODE_PRIVATE);
        String username = sp.getString("current_username",null);
        if(username==null)
        {
            //if not logged in then go to login / register screen
            Intent i = new Intent(this,MainActivity.class);
            startActivity(i);
            finish();
        }
        else
        {
            //if logged in then go to Channels
            Intent i = new Intent(this,Channel.class);
            startActivity(i);
            finish();
        }
    }
}
