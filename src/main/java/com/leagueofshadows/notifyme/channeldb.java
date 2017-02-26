package com.leagueofshadows.notifyme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

class channeldb extends SQLiteOpenHelper {
    private static String DATABASE_NAME = "notifyme.db";
    private static String TABLE_NAME = "channels";
    channeldb(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table channels " +
                "(id integer primary key , name varchar not null, channelid varchar not null , apikey varchar not null )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS channels");
        onCreate(db);
    }
    void addChannels(final ArrayList<ChannelItem> channels)
    {
        final SQLiteDatabase db = getWritableDatabase();
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<channels.size();i++)
                {
                    ChannelItem channel = channels.get(i);
                    ContentValues con = new ContentValues();
                    con.put("name",channel.getName());
                    con.put("channelid",channel.getId());
                    con.put("apikey",channel.getApikey());
                    db.insert(TABLE_NAME,null,con);
                }
            }
        });
        th.start();
    }
    ArrayList<ChannelItem> getChannels()
    {
        final ArrayList<ChannelItem> channels = new ArrayList<>();
        final SQLiteDatabase db = getWritableDatabase();
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor cr = db.rawQuery(" SELECT * FROM channels ",null);
                cr.moveToFirst();
                int length = cr.getCount();
                if(length!=0)
                while(true)
                {
                    ChannelItem  channelItem = new ChannelItem(cr.getString(1),cr.getString(2),null,null,cr.getString(3));
                    channels.add(channelItem);
                    length--;
                    if(length==0)
                        break;
                    cr.moveToNext();
                }
                cr.close();
            }
        });
        th.start();
        return channels;
    }
    void addchannel(ChannelItem channelItem)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues con = new ContentValues();
        con.put("name",channelItem.getName());
        con.put("channelid",channelItem.getId());
        con.put("apikey",channelItem.getApikey());
        db.insert(TABLE_NAME,null,con);
    }
    void deletechannel(String channelid)
    {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME,"channelid = '"+ channelid +"'",null);
        //db.rawQuery(" DELETE FROM channels WHERE channelid = ?", new String[]{channelid});
    }
    void drop()
    {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS channels");
        onCreate(db);
    }
}
