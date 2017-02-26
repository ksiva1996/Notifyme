package com.leagueofshadows.notifyme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.Iterator;

public class ChannelDisplay extends AppCompatActivity {


    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;
    FrameLayout progressBarHolder;
    String channelname;
    String channelid;
    String apikey;
    ArrayList<FieldItem> fields = new ArrayList<>();
    CustomAdapter adap;
    ListView li;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_display);
        li = (ListView)findViewById(R.id.list);
        progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);
        Intent i = getIntent();
        channelname = i.getStringExtra("channel");
        channelid= i.getStringExtra("channelid");
        apikey = i.getStringExtra("apikey");
       // Log.e("api_key",apikey);
        workerfield wf = new workerfield(this);
        wf.execute();
        workerfieldlast wfl = new workerfieldlast(this);
        wfl.execute();
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

    class workerfieldlast extends AsyncTask<String,String,String>
    {
        Context context;
        workerfieldlast(Context context)
        {
            this.context=context;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            stop();
            //Log.e("idhi jarigindhi ",s);
            if(s!=null)
            {
                if (s.equals("-1")) {
                    adap = new CustomAdapter(context, R.layout.fielditem, fields);
                    li.setAdapter(adap);
                }
                if (s != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        String field = "field";
                        int j = 1;
                        while (true) {

                            if (jsonObject.has(field + Integer.toString(j))) {
                                fields.get(j - 1).setlast(jsonObject.getString(field + Integer.toString(j)));
                            } else
                                break;
                            j++;
                        }
                        adap = new CustomAdapter(context, R.layout.fielditem, fields);
                        li.setAdapter(adap);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            else
             {
                adap = new CustomAdapter(context, R.layout.fielditem, fields);
                li.setAdapter(adap);
                Toast.makeText(context, "No Internet Connection please refresh", Toast.LENGTH_SHORT).show();
             }
        }

        @Override
        protected String doInBackground(String... params) {
            URL url;
            String result=null;
            try {
                String s ="http://mstmnit.com/leagueofshadows/pingme/getfeed.php";
               // Log.e("idhi url",s);
                url = new URL(s);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("api_key", apikey);
                postDataParams.put("channelid",channelid);
                conn.setReadTimeout(30000);
                conn.setConnectTimeout(30000);
                conn.setRequestMethod("GET");
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
    }
    class CustomAdapter extends ArrayAdapter<FieldItem>
    {

        CustomAdapter(Context context, int textViewResourceId, ArrayList<FieldItem> objects) {
            super(context, textViewResourceId, objects);
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
            if(convertView==null) {
                LayoutInflater li = getLayoutInflater();
                convertView = li.inflate(R.layout.fielditem,parent,false);
                TextView name = (TextView)convertView.findViewById(R.id.name);
                TextView average = (TextView)convertView.findViewById(R.id.average);
                TextView last = (TextView)convertView.findViewById(R.id.last);
                name.setText(fields.get(position).getName());
                average.setText(fields.get(position).getAverage());
                last.setText(fields.get(position).getLast());
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(ChannelDisplay.this,DisplayField.class);
                        i.putExtra("channelid",channelid);
                        i.putExtra("feedid",position+1);
                        i.putExtra("apikey",apikey);
                        startActivity(i);
                    }
                });
            }
            return convertView;
        }
    }
    class workerfield extends AsyncTask<String,String,String>
    {
        Context context;
        workerfield(Context context)
        {
            this.context=context;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            start();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //stop();
           // Log.e("idhi jarigindhi ",s);
            if(s!=null)
            {
                try {
                    JSONObject json = new JSONObject(s);
                    JSONObject jsonObject = json.getJSONObject("channel");
                    String field = "field";
                    int j=1;
                    while(true)
                    {

                        if(jsonObject.has(field+Integer.toString(j)))
                        {
                            FieldItem fieldItem = new FieldItem(jsonObject.getString(field+Integer.toString(j)),"","");
                            fields.add(fieldItem);
                        }
                        else
                            break;
                        j++;
                    }
                    adap = new CustomAdapter(context, R.layout.fielditem, fields);
                    li.setAdapter(adap);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else
                Toast.makeText(context,"No internet Connection",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params) {
            URL url;
            String result=null;
            try {
                String s ="http://mstmnit.com/leagueofshadows/pingme/getchannel.php";
                url = new URL(s);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("api_key", apikey);
                postDataParams.put("channelid",channelid);
                conn.setReadTimeout(30000);
                conn.setConnectTimeout(30000);
                conn.setRequestMethod("GET");
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
