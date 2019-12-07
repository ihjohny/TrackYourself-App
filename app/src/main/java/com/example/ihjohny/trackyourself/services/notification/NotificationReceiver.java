package com.example.ihjohny.trackyourself.services.notification;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import com.example.ihjohny.trackyourself.data.db.DatabaseHelper;

public class NotificationReceiver extends BroadcastReceiver {

    private static DatabaseHelper databaseHelper;

    @Override
    public void onReceive(Context context, Intent intent) {

        databaseHelper=new DatabaseHelper(context);
        SQLiteDatabase sqLiteDatabase=databaseHelper.getWritableDatabase();

        int code=Integer.parseInt(intent.getStringExtra("ID"));
        //Toast.makeText(context,"this is test "+code,Toast.LENGTH_SHORT).show();
        databaseHelper.makeCompleted(code);

        NotificationManager notificationManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(code);
    }
}
