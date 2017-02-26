package com.leagueofshadows.notifyme;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by siva
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService{


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        SharedPreferences sp= getSharedPreferences("preferences", Context.MODE_PRIVATE);
        Boolean x= sp.getBoolean("notifications",true);
        if(x)
        showNotification(remoteMessage.getData().get("message"),remoteMessage.getData().get("username"));
    }

    private void showNotification(final String message, final String username) {
        //TODO show notification 
        Intent i = new Intent(this,MainActivity.class);
        {
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setAutoCancel(true)
                    .setContentTitle(username)
                    .setContentText(message)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pendingIntent);
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder.setSound(alarmSound);
            builder.setLights(Color.GREEN,1000,1000);
            long[] pattern = {500,500,500};
            builder.setVibrate(pattern);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(0, builder.build());
        }
    }
}

