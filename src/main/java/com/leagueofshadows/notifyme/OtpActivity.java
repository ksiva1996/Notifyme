package com.leagueofshadows.notifyme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class OtpActivity extends AppCompatActivity implements Communicator {

    String username;
    String otp=null;
    int userotp;
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;

    FrameLayout progressBarHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        username = bundle.getString("current_username");
        progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);
        final EditText editotp = (EditText)findViewById(R.id.otp);
        Button signup = (Button)findViewById(R.id.confirm);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userotp = Integer.parseInt(editotp.getText().toString());
                if(userotp!=0)
                {
                    worker w = new worker(OtpActivity.this);
                    w.execute();
                }
                else
                {
                    Toast.makeText(OtpActivity.this,"enter otp",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void logstart() {
        inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressBarHolder.setAnimation(inAnimation);
        progressBarHolder.setVisibility(View.VISIBLE);
    }

    @Override
    public void logstop() {
        outAnimation = new AlphaAnimation(1f, 0f);
        outAnimation.setDuration(200);
        progressBarHolder.setAnimation(outAnimation);
        progressBarHolder.setVisibility(View.GONE);
    }

    class worker extends AsyncTask<String,String,String>{
        Context context;
        worker (Context context)
        {
            this.context=context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Communicator com = (Communicator) context;
            com.logstart();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Communicator com = (Communicator) context;
            com.logstop();
            try {
                JSONObject json = new JSONObject(s);
                int x = json.getInt("success");
                if(x==1)
                {
                    Toast.makeText(context,"Verification Successful",Toast.LENGTH_SHORT).show();
                    SharedPreferences sp = context.getSharedPreferences("preferences",Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putString("current_username",username);
                    edit.apply();
                    Intent i = new Intent(context,Channel.class);
                    startActivity(i);
                    Activity a = (Activity) context;
                    a.finish();
                }
                else if(x==0)
                {
                    Toast.makeText(context,"something went wrong please try again",Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(context,"wrong otp",Toast.LENGTH_SHORT).show();
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            URL url;
            String result=null;
            try {
                url = new URL("http://mstmnit.com/leagueofshadows/pingme/otp.php");
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("otp",userotp);
                postDataParams.put("username",username);
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
