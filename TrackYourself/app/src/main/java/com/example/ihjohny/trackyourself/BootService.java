package com.example.ihjohny.trackyourself;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.DatePicker;
import android.widget.TimePicker;

public class BootService extends IntentService {

    private static DatabaseHelper databaseHelper;

    public BootService() {
        super("BootService");
    }

    private void setAlarm() {
        // Set your alarm here as you do in "1. First I set an alarm in alarm manager"
    }

    private void setAlarmsFromDatabase() {
        // Set your alarms from database here
        DatePicker datePicker=new DatePicker(this);
        TimePicker timePicker=new TimePicker(this);

        int currentHour=timePicker.getCurrentHour();
        int currentMinute=timePicker.getCurrentMinute();
        int currentDay=datePicker.getDayOfMonth();
        int currentMonth=datePicker.getMonth();
        int currentYear=datePicker.getYear();


        databaseHelper=new DatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase=databaseHelper.getWritableDatabase();
        Cursor cursor=databaseHelper.showAllData();

        while (cursor.moveToNext())
        {
            int minute=Integer.parseInt(cursor.getString(5));
            int hour=Integer.parseInt(cursor.getString(4));
            int day=Integer.parseInt(cursor.getString(6));
            int month=Integer.parseInt(cursor.getString(7));
            int year=Integer.parseInt(cursor.getString(8));

            if(Integer.parseInt(cursor.getString(8)) >currentYear)
            {
                if(cursor.getString(9).equals("0") && cursor.getString(10).equals("1"))
                    ExtraFunction.setNotif(getApplicationContext(),Integer.parseInt(cursor.getString(0)),minute,hour,day,month,year);
            }
            else if(Integer.parseInt(cursor.getString(8)) ==currentYear)
            {
                if(Integer.parseInt(cursor.getString(7)) >(currentMonth+1))
                {
                    if(cursor.getString(9).equals("0") && cursor.getString(10).equals("1"))
                        ExtraFunction.setNotif(getApplicationContext(),Integer.parseInt(cursor.getString(0)),minute,hour,day,month,year);
                }
                else if(Integer.parseInt(cursor.getString(7)) ==(currentMonth+1))
                {
                    if(Integer.parseInt(cursor.getString(6)) >currentDay)
                    {
                        if(cursor.getString(9).equals("0") && cursor.getString(10).equals("1"))
                            ExtraFunction.setNotif(getApplicationContext(),Integer.parseInt(cursor.getString(0)),minute,hour,day,month,year);
                    }
                    else if(Integer.parseInt(cursor.getString(6)) ==currentDay)
                    {
                        if(Integer.parseInt(cursor.getString(4)) >currentHour)
                        {
                            if(cursor.getString(9).equals("0") && cursor.getString(10).equals("1"))
                                ExtraFunction.setNotif(getApplicationContext(),Integer.parseInt(cursor.getString(0)),minute,hour,day,month,year);
                        }
                        else if(Integer.parseInt(cursor.getString(4)) ==currentHour)
                        {
                            if(Integer.parseInt(cursor.getString(5)) >=currentMinute)
                            {
                                if(cursor.getString(9).equals("0") && cursor.getString(10).equals("1"))
                                    ExtraFunction.setNotif(getApplicationContext(),Integer.parseInt(cursor.getString(0)),minute,hour,day,month,year);
                            }
                        }
                    }
                }
            }
        }

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        setAlarm();
        setAlarmsFromDatabase(); // A nice a approach is to store alarms on a database, you may not need it
        Intent service = new Intent(this, BootService.class);
        stopService(service);
    }
}
