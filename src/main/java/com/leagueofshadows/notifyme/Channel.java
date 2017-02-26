package com.leagueofshadows.notifyme;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

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

public class Channel extends AppCompatActivity {

    ListView li;
    ArrayList<ChannelItem> channels;
    CustomAdapter adap;
    String username;
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;
    FrameLayout progressBarHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);
        //for animation
        progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);
        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        li = (ListView) findViewById(R.id.list);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        SharedPreferences sp = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        //getting the username ie email of the user
        username = sp.getString("current_username",null);
        //if it is the first time the user is being logged in then get the channels of user from server
        //else get the channels from local database
        boolean first = sp.getBoolean("first_time",true);
        WorkerClassUpdateToken workerClassUpdateToken = new WorkerClassUpdateToken();
        workerClassUpdateToken.execute(username, FirebaseInstanceId.getInstance().getToken());
        if(first) {
            channels = new ArrayList<>();
            SharedPreferences.Editor edit = sp.edit();
            edit.putBoolean("first_time",false);
            edit.apply();
            worker w = new worker(this);
            w.execute(username);
        }
        else
        {
            channeldb db = new channeldb(this);
            channels = db.getChannels();
            adap = new CustomAdapter(this,R.layout.channelitem,channels);
            li.setAdapter(adap);
        }
        //FAB for adding channel
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Channel.this);
                LayoutInflater li = getLayoutInflater();
                View v = li.inflate(R.layout.addchanneldialog,null);
                final EditText channelid =  (EditText)v.findViewById(R.id.channelid);
                builder.setView(v);
                builder.setTitle("ADD CHANNEL");
                builder.setCancelable(true);
                builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String chanid= channelid.getText().toString();
                        Boolean x=false;
                        // checking if the channel is already added by the user
                        for(int i=0;i<channels.size();i++)
                        {
                            if(channels.get(i).getId().equals(chanid))
                            {
                                x=true;
                            }
                        }
                        if(!x) {
                            workeraddchannel wac = new workeraddchannel(Channel.this);
                            wac.execute(chanid);
                        }
                        else
                        {
                            Toast.makeText(Channel.this,"this channel already exists",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //showing the dialog
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_channel, menu);
        return true;
    }
    // creating the menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            //settings
            case R.id.action_settings:
            {
                Intent i = new Intent(this,Settings.class);
                startActivity(i);
                return true;
            }
            //logout
            case R.id.action_logout:
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("LOGOUT");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sp = getSharedPreferences("preferences",Context.MODE_PRIVATE);
                        SharedPreferences.Editor edit = sp.edit();
                        edit.putString("current_username",null);
                        edit.putBoolean("first_time",true);
                        edit.apply();
                        channeldb db = new channeldb(Channel.this);
                        //drop the local databse
                        db.drop();
                        //goto the login screen
                        Intent i = new Intent(Channel.this,MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                });
                builder.setCancelable(true);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
            }
            case R.id.action_refresh:
            {
                //refresh the channels this time from the server
                channels.clear();
                channeldb db = new channeldb(this);
                db.drop();
                worker w = new worker(this);
                w.execute();
                return true;
            }
            default: {
                super.onOptionsItemSelected(item);
                return true;
            }
        }

    }
    //start animation
     private void start()
     {
         inAnimation = new AlphaAnimation(0f, 1f);
         inAnimation.setDuration(200);
         progressBarHolder.setAnimation(inAnimation);
         progressBarHolder.setVisibility(View.VISIBLE);
     }
    //stop animation
     private void stop()
     {
         outAnimation = new AlphaAnimation(1f, 0f);
         outAnimation.setDuration(200);
         progressBarHolder.setAnimation(outAnimation);
         progressBarHolder.setVisibility(View.GONE);
     }
     // worker class to get the channels from the server
     class worker extends AsyncTask<String,String,String>
     {
         Context context;
         worker(Context context)
         {
             this.context=context;
         }
         @Override
         protected void onPreExecute() {
             super.onPreExecute();
             //start animation
             start();
         }

         @Override
         protected void onPostExecute(String s) {
             super.onPostExecute(s);
             //stop animation
             stop();
             if(s!=null) {
                 try {
                     // reading json encoded data from the network
                     JSONObject json = new JSONObject(s);
                     JSONArray array = json.getJSONArray("channels");
                     for (int i = 0; i < array.length(); i++) {
                         JSONObject chanjson = array.getJSONObject(i);
                         String name = chanjson.getString("name");
                         String id = chanjson.getString("id");
                         String apikey = chanjson.getString("api");
                         ChannelItem channel = new ChannelItem(name, id, null, null,apikey);
                         channels.add(channel);
                     }
                     // displaying the channels through a list view
                     adap = new CustomAdapter(context,R.layout.channelitem,channels);
                     li.setAdapter(adap);
                     //adding channels to local databse
                     channeldb db = new channeldb(Channel.this);
                     db.addChannels(channels);
                 } catch (JSONException e) {
                     e.printStackTrace();
                 }
             }
             else
             {
                 Toast.makeText(context,"No internet connection",Toast.LENGTH_SHORT).show();
             }
         }
         // background task
         @Override
         protected String doInBackground(String... params) {
             URL url;
             String result=null;
             try {
                 url = new URL("http://mstmnit.com/leagueofshadows/pingme/channels.php");
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
     }
    // custom adapter to populate listview
    class CustomAdapter extends ArrayAdapter<ChannelItem>
    {
        Context context;
        CustomAdapter(Context context, int textViewResourceId, ArrayList<ChannelItem> objects) {
            super(context, textViewResourceId, objects);
            this.context = context;
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
            if(convertView==null) {
                LayoutInflater li = getLayoutInflater();
                convertView = li.inflate(R.layout.channelitem,parent,false);
                TextView name = (TextView)convertView.findViewById(R.id.channelname);
                name.setText(channels.get(position).getName());
                // onclick listener for channel item
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Channel.this,ChannelDisplay.class);
                        i.putExtra("channel",channels.get(position).getName());
                        i.putExtra("channelid",channels.get(position).getId());
                        i.putExtra("apikey",channels.get(position).getApikey());
                        startActivity(i);
                    }
                });
                // long click listener for chanel item to display options for editing and adding channel
                convertView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        LayoutInflater li = getLayoutInflater();
                        View v = li.inflate(R.layout.channeloptions,null);
                        TextView del= (TextView)v.findViewById(R.id.delete);
                        TextView edit = (TextView)v.findViewById(R.id.edit);

                        del.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                workerdeletechannel wdc = new workerdeletechannel(context);
                                wdc.execute(channels.get(position).getId());

                            }
                        });
                        edit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(context,"  ",Toast.LENGTH_SHORT).show();
                            }
                        });
                        builder.setView(v);
                        AlertDialog alert=builder.create();
                        alert.show();
                        return false;
                    }
                });
            }
            return convertView;
        }
    }
    //worker class to add a chanel to the server so that the user can get the channels he added when he logins a different device
    class workeraddchannel extends AsyncTask<String,String,String>
    {
        Context context;
        String channelid;
        workeraddchannel(Context context)
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
            if(s!=null) {
                try {
                    JSONObject json = new JSONObject(s);
                    int success = json.getInt("success");
                    if(success==1) {
                        String channelname = json.getString("channelname");
                        String apikey = json.getString("apikey");
                        channeldb db = new channeldb(context);
                        ChannelItem channelItem = new ChannelItem(channelname, channelid, null, null, apikey);
                        db.addchannel(channelItem);
                        channels.add(channelItem);
                        adap.notifyDataSetChanged();
                        Toast.makeText(context,"Channel added successfully",Toast.LENGTH_SHORT).show();
                    }
                    else if(success==-1)
                    {
                        Toast.makeText(context,"Channel does not exist",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(context,"Something went wrong please try again",Toast.LENGTH_SHORT).show();
                    }
                }  catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                Toast.makeText(context,"No internet connection",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            channelid = params[0];
            URL url;
            String result=null;
            try {
                url = new URL("http://mstmnit.com/leagueofshadows/pingme/channel.php");
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("channelid", channelid);
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
    }
    class workerdeletechannel extends AsyncTask<String,String,String>
    {
        Context context;
        String channelid;
        workerdeletechannel(Context context)
        {
            this.context = context;
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
            if(s!=null)
            {
                try {
                    JSONObject json = new JSONObject(s);
                    int success = json.getInt("success");
                    if (success == 1) {
                        channeldb db = new channeldb(context);
                        db.deletechannel(channelid);
                        for(int i =0;i<channels.size();i++)
                        {
                            if(channels.get(i).getId().equals(channelid))
                            {
                                channels.remove(i);
                                break;
                            }
                        }
                        adap.notifyDataSetChanged();
                        Toast.makeText(context, "Channel deleted successfully", Toast.LENGTH_SHORT).show();
                    } else if (success == -1) {
                        Toast.makeText(context, "Channel does not exist in your subscription", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Something went wrong please try again", Toast.LENGTH_SHORT).show();
                    }
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
            channelid=params[0];
            URL url;
            String result=null;
            try {
                url = new URL("http://mstmnit.com/leagueofshadows/pingme/delete.php");
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("channelid", channelid);
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
