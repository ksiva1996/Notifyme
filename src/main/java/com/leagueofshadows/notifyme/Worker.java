package com.leagueofshadows.notifyme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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

/**
 * Created by siva on 2/26/2017.
 */

class Worker extends AsyncTask<String,String,String>
{
    String username;
    Context context;
    Worker (Context context)
    {
        this.context= context;
    }
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Communicator com =(Communicator)context;
        com.logstop();
        try
        {
            JSONObject json = new JSONObject(s);
            int x = json.getInt("success");
            if(x==1)
            {
                Intent i = new Intent(context,forgototp.class);
                i.putExtra("username",username);
                context.startActivity(i);
                Activity a = (Activity) context;
                a.finish();
            }
            else
            {
                Toast.makeText(context,"otp generated",Toast.LENGTH_SHORT).show();
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Communicator com =(Communicator)context;
        com.logstart();
    }

    @Override
    protected String doInBackground(String... params) {
        username= params[0];
        URL url;
        String result=null;
        try {
            url = new URL("http://mstmnit.com/leagueofshadows/pingme/forgot.php");
            JSONObject postDataParams = new JSONObject();
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
