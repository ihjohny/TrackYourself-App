package com.example.ihjohny.trackyourself.utils;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.example.ihjohny.trackyourself.services.AlertReceiver;

import java.lang.reflect.Field;
import java.util.Calendar;

import static java.lang.StrictMath.abs;

public class ExtraFunction  {

    @SuppressLint("RestrictedApi")
    public static void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                //noinspection RestrictedApi
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                //noinspection RestrictedApi
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("BNVHelper", "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e("BNVHelper", "Unable to change value of shift mode", e);
        }
    }

    public static StringBuffer setTime(String hours, String minutes)
    {
        StringBuffer resultedTime = new StringBuffer();
        Integer h=Integer.parseInt(hours);
        Integer m=Integer.parseInt(minutes);

        if(h==24 && m==0){
            resultedTime.append("Any Time");
            return resultedTime;
        }
        if(h<10)
            resultedTime.append("0"+hours);
        else
            resultedTime.append(hours);

        resultedTime.append(":");

        if(m<10)
            resultedTime.append("0"+minutes);
        else
            resultedTime.append(minutes);

        return resultedTime;
    }

    public static StringBuffer setRemainingTime(Context context,String hours, String minutes)
    {
        TimePicker timePicker;
        timePicker=new TimePicker(context);
        int currentMinutes=timePicker.getCurrentMinute();
        int currentHours=timePicker.getCurrentHour();
        int givenHours=Integer.parseInt(hours);
        int givenMinutes=Integer.parseInt(minutes);
        StringBuffer resultedTime = new StringBuffer();

        if(currentHours>givenHours || (currentHours==givenHours && currentMinutes>givenMinutes))
        {
            resultedTime.append("Overdue");
            return resultedTime;
        }
        else if(givenHours==24 && givenMinutes==0){
            resultedTime.append("Any Time");
            return resultedTime;
        }
        else
        {
            if(givenMinutes==0)
            {
                givenHours=givenHours-1;
                givenMinutes=60;
                resultedTime.append("After "+String.valueOf(givenHours-currentHours)+"h "+String.valueOf(givenMinutes-currentMinutes)+"m");
            }
            else
                resultedTime.append("After "+String.valueOf(givenHours-currentHours)+"h "+String.valueOf(abs(givenMinutes-currentMinutes))+"m");

            return resultedTime;
        }
    }

    public static StringBuffer setDate(Context context, int day, int month, int year)
    {
        StringBuffer resultedDate = new StringBuffer();
        DatePicker datePicker=new DatePicker(context);
        int currentDay=datePicker.getDayOfMonth();
        int currentMonth=datePicker.getMonth();
        int currentYear=datePicker.getYear();

        if(currentDay==day && currentMonth==month-1 && currentYear==year)
            resultedDate.append("Today");
        else if(currentDay-1==day && currentMonth==month-1 && currentYear==year)
            resultedDate.append("Yesterday");
        else if(currentDay+1==day && currentMonth==month-1 && currentYear==year)
            resultedDate.append("Tomorrow");
        else
            resultedDate.append(day+"/"+month+"/"+year);

        return resultedDate;
    }

    //Notification ON
    public static void setNotif(Context context, int request_code, int minute, int hour, int day, int month, int year)
    {
        DatePicker datePicker=new DatePicker(context);
        TimePicker timePicker=new TimePicker(context);

        if(year<datePicker.getYear())
            return;
        else if(month<datePicker.getMonth()+1)
            return;
        else if(day<datePicker.getDayOfMonth())
            return;
        else if(hour<timePicker.getCurrentHour())
            return;
        else if(minute<=timePicker.getCurrentMinute())
            return;

        Calendar c=Calendar.getInstance();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,month-1);
        c.set(Calendar.DATE,day);
        c.set(Calendar.HOUR_OF_DAY,hour);
        c.set(Calendar.MINUTE,minute);
        c.set(Calendar.SECOND,0);
        AlarmManager alarmManager= (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent=new Intent(context, AlertReceiver.class);
        intent.putExtra("requestCode",request_code);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(context,request_code,intent,0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),pendingIntent);
    }

    //Notification Cancel
    public static void cancelNotif(Context context,int request_code)
    {
        AlarmManager alarmManager= (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent=new Intent(context,AlertReceiver.class);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(context,request_code,intent,0);
        alarmManager.cancel(pendingIntent);
    }

}
