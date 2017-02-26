package com.leagueofshadows.notifyme;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
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
 * Created by siva
 */

public class FirebaseInstanceID extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {

        String token = FirebaseInstanceId.getInstance().getToken();
        Log.e("TOKEN", token);
        registerToken(token);
    }

    private void registerToken(String token) {
        SharedPreferences sp = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        String username = sp.getString("active_user",null);
        WorkerClassUpdateToken wct = new WorkerClassUpdateToken();
        if(username!=null)
            wct.execute(username, token);
    }

}
class WorkerClassUpdateToken extends AsyncTask<String,String,String> {

    @Override
    protected String doInBackground(String... params) {
        String username = params[0];
        String token = params[1];
        String result = null;
        URL url;
        try {
            //TODO register token
            url = new URL("http://mstmnit.com/leagueofshadows/pingme/registertoken.php");
            JSONObject postDataParams = new JSONObject();
            postDataParams.put("username", username);
            postDataParams.put("token", token);
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

    @Override
    protected void onPostExecute(String s) {
        try {
            JSONObject json = new JSONObject(s);
            int x = json.getInt("success");
            if (x == 0) {
                Log.d("not done","notdone");
            }
            else
            {
                Log.d(" done","  done");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
