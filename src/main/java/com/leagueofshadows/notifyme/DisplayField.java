package com.leagueofshadows.notifyme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
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

public class DisplayField extends AppCompatActivity {

    int number;
    int refresh;
    String channelid;
    String apikey;
    ArrayList<FeedItem> feeds;
    CustomAdapter adap;
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;
    FrameLayout progressBarHolder;
    ListView li;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_field);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        feeds= new ArrayList<>();
        li = (ListView)findViewById(R.id.list);
        SharedPreferences sp = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);
        refresh = sp.getInt("refresh_value",120)*1000;
        Intent i = getIntent();
        number = i.getIntExtra("feedid",1);
        channelid = i.getStringExtra("channelid");
        apikey= i.getStringExtra("apikey");
        Worker w = new Worker(this);
        w.execute();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://api.thingspeak.com/channels/"+channelid+"/charts/"+Integer.toString(number)+"?days=1&dynamic=true&api_key="+apikey));
                startActivity(browserIntent);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.action_refresh:
            {
                feeds.clear();
                Worker w = new Worker(this);
                w.execute();
                return true;
            }
            default: {
                super.onOptionsItemSelected(item);
                return true;
            }
        }

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
    public void reload() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                feeds.clear();
                Worker w = new Worker(DisplayField.this);
                w.execute();
                reload();
            }
        },refresh);
    }
    class CustomAdapter extends ArrayAdapter<FeedItem>
    {
        Context context;
        CustomAdapter(Context context, int resource, ArrayList<FeedItem> objects) {
            super(context, resource, objects);
            this.context=context;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if(convertView==null)
            {
                LayoutInflater li = getLayoutInflater();
                convertView = li.inflate(R.layout.fieldfeeditem,parent,false);
                TextView value = (TextView)convertView.findViewById(R.id.value);
                TextView id = (TextView)convertView.findViewById(R.id.id);
                TextView time = (TextView)convertView.findViewById(R.id.time);
                value.setText(feeds.get(position).getValue());
                id.setText(feeds.get(position).getId());
                time.setText(feeds.get(position).getTime());
            }
            return convertView;
        }
    }
    class Worker extends AsyncTask<String,String,String>
    {
        Context context;
        Worker(Context context)
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
            stop();
            String x = "field"+Integer.toString(number);
            if(s!=null)
            {
                try
                {
                    feeds.clear();
                    JSONObject json = new JSONObject(s);
                    JSONArray array = json.getJSONArray("feeds");
                    for(int i=0;i<array.length()&&i<15;i++)
                    {
                        JSONObject j = array.getJSONObject(i);
                        String value = j.getString(x);
                        String time = j.getString("created_at");
                        String id = j.getString("entry_id");
                        FeedItem f = new FeedItem(value,id,time);
                        feeds.add(f);
                    }
                    adap = new CustomAdapter(context,R.layout.fieldfeeditem,feeds);
                    li.setAdapter(adap);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context,"no values to show",Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        protected String doInBackground(String... params) {
            URL url;
            String result=null;
            try {
                url = new URL("http://mstmnit.com/leagueofshadows/pingme/feed.php");
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("channelid",channelid);
                postDataParams.put("feed",number);
                postDataParams.put("api_key",apikey);
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
