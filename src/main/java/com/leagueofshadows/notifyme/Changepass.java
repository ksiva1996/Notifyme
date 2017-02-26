package com.leagueofshadows.notifyme;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

public class Changepass extends AppCompatActivity {

    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;
    FrameLayout progressBarHolder;
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepass);
        progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);
        final EditText old = (EditText)findViewById(R.id.old);
        final EditText newpass = (EditText)findViewById(R.id.newpass);
        final EditText newc = (EditText)findViewById(R.id.newconfirm);
        SharedPreferences sp = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        username = sp.getString("current_username",null);
        Button change = (Button)findViewById(R.id.change);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldp = old.getText().toString();
                String newp = newpass.getText().toString();
                String newpc = newc.getText().toString();
                if(newp.equals(newpc))
                {
                    Worker w = new Worker();
                    w.execute(oldp,newp);
                }
                else
                {
                    Toast.makeText(Changepass.this,"password and confirm password doesnot match",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void start()
    {
        inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressBarHolder.setAnimation(inAnimation);
        progressBarHolder.setVisibility(View.VISIBLE);
    }
    private void stop()
    {
        outAnimation = new AlphaAnimation(1f, 0f);
        outAnimation.setDuration(200);
        progressBarHolder.setAnimation(outAnimation);
        progressBarHolder.setVisibility(View.GONE);
    }
    class Worker extends AsyncTask<String,String,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            start();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            stop();
            if(s!=null)
            {
                try
                {
                    JSONObject json = new JSONObject(s);
                    int x = json.getInt("success");
                    if(x==1)
                    {
                        Toast.makeText(Changepass.this,"password changed successfully",Toast.LENGTH_SHORT).show();
                    }
                    else if(x==0)
                    {
                        Toast.makeText(Changepass.this,"wrong password",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(Changepass.this,"something went wrong please try again",Toast.LENGTH_SHORT).show();
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String pass = params[0];
            String newpass = params[1];
            URL url;
            String result=null;
            try {
                url = new URL("http://mstmnit.com/leagueofshadows/pingme/change.php");
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("password",pass);
                postDataParams.put("password_new",newpass);
                postDataParams.put("username", username);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));
                writer.flush();
                writer.close();
                os.close();
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    result = null;
                    StringBuilder sb = new StringBuilder();
                    while ((result = in.readLine()) != null) {
                        sb.append(result);
                    }
                    result = sb.toString();
                    in.close();
                } else {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    result = null;
                    StringBuilder sb = new StringBuilder();
                    while ((result = in.readLine()) != null) {
                        sb.append(result);
                    }
                    result = sb.toString();
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
        private String getPostDataString(JSONObject params) throws Exception {

            StringBuilder result = new StringBuilder();
            boolean first = true;

            Iterator<String> itr = params.keys();

            while (itr.hasNext()) {

                String key = itr.next();
                Object value = params.get(key);

                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(key, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(value.toString(), "UTF-8"));

            }
            return result.toString();
        }
    }
}
