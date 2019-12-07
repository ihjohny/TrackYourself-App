package com.example.ihjohny.trackyourself.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;


import com.example.ihjohny.trackyourself.ui.MainActivity;
import com.example.ihjohny.trackyourself.services.notification.NotificationReceiver;
import com.example.ihjohny.trackyourself.R;
import com.example.ihjohny.trackyourself.data.db.DatabaseHelper;

import static com.example.ihjohny.trackyourself.App.CHANNEL_1_ID;

public class AlertReceiver extends BroadcastReceiver {

    private NotificationManagerCompat notificationManager;
    private static DatabaseHelper databaseHelper;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {

        databaseHelper=new DatabaseHelper(context);
        SQLiteDatabase sqLiteDatabase=databaseHelper.getWritableDatabase();

        String code=String.valueOf(intent.getIntExtra("requestCode",1));
        Cursor cursor=databaseHelper.showTaskForId(code);

        while (cursor.moveToNext())
        {
            if(cursor.getString(9).equals("0") && cursor.getString(10).equals("1"))
            {
            //   Toast.makeText(context, "its time for "+cursor.getString(1) , Toast.LENGTH_SHORT).show();

               notificationManager=NotificationManagerCompat.from(context);
               Uri path=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                Intent activityIntent=new Intent(context, MainActivity.class);
                PendingIntent contentIntent=PendingIntent.getActivity(context,0,activityIntent,0);

                Intent notificationReceiver=new Intent(context, NotificationReceiver.class);
                notificationReceiver.putExtra("ID",cursor.getString(0));

                PendingIntent pendingIntent=PendingIntent.getBroadcast(context,5,notificationReceiver, PendingIntent.FLAG_UPDATE_CURRENT);


                Notification notification=new NotificationCompat.Builder(context,CHANNEL_1_ID)
                        .setSmallIcon(R.drawable.ic_notifications_white_24dp)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                        .setContentTitle(cursor.getString(1))
                        .setContentText(cursor.getString(2))
                        .setSound(path)
                        .setContentIntent(contentIntent)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setColor(Color.parseColor("#4caf50"))
                        .setAutoCancel(true)
                        .addAction(R.drawable.ic_check_green_500_24dp,"Complete",pendingIntent)

                        .build();


                notificationManager.notify(Integer.parseInt(cursor.getString(0)),notification);

            }
        }
    }

}

