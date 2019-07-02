package com.example.ihjohny.trackyourself;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

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
