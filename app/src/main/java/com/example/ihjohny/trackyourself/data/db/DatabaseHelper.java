package com.example.ihjohny.trackyourself.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.CompoundButton;
import android.widget.Toast;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="TASK.db";
    private static final String TABLE_NAME="task_details";
    private static final String ID="id";
    private static final String NAME="name";
    private static final String DESCRIPTION="description";
    private static final String PRIORITY="priority";
    private static final String HOURS="hours";
    private static final String MINUTES="minutes";
    private static final String DAYS="days";
    private static final String MONTHS="months";
    private static final String YEARS="years";
    private static final String COMPLETED="completed";
    private static final String PIN="pinned";
    private static final String NOTIF="notification";
    private static final int VERSION_NUMBER=2;
    private static final String CREATE_TABLE="CREATE TABLE "+TABLE_NAME+" ("+ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+NAME+" VARCHAR(50), "+DESCRIPTION+" TEXT, "+PRIORITY+" TEXT, "+HOURS+" INTEGER, "+MINUTES+" INTEGER,"+DAYS+" INTEGER ,"+MONTHS+" INTEGER,"+YEARS+" INTEGER,"+COMPLETED+" INTEGER ,"+NOTIF+" INTEGER,"+PIN+" INTEGER);";
    private Context context;
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME,null,VERSION_NUMBER);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try{
            sqLiteDatabase.execSQL(CREATE_TABLE);
        }
        catch (Exception e)
        {
            Toast.makeText(context,"Exception"+e,Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        try{
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
            onCreate(sqLiteDatabase);
        }
        catch (Exception e)
        {
            Toast.makeText(context,"Exception"+e,Toast.LENGTH_SHORT).show();
        }

    }

    public long saveData(String name,String description,String priority,int hours,int minutes,int days,int months,int years,int completed,int notif,int pin)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();

        contentValues.put(NAME,name);
        contentValues.put(DESCRIPTION,description);
        contentValues.put(PRIORITY,priority);
        contentValues.put(HOURS,hours);
        contentValues.put(MINUTES,minutes);
        contentValues.put(DAYS,days);
        contentValues.put(MONTHS,months);
        contentValues.put(YEARS,years);
        contentValues.put(COMPLETED,completed);
        contentValues.put(NOTIF,notif);
        contentValues.put(PIN,pin);
        long rowNo=sqLiteDatabase.insert(TABLE_NAME,null,contentValues);
        return rowNo;
    }

    public long updateData(String id,String name,String description,String priority,int hours,int minutes,int days,int months,int years,int completed,int notif)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();

        contentValues.put(NAME,name);
        contentValues.put(DESCRIPTION,description);
        contentValues.put(PRIORITY,priority);
        contentValues.put(HOURS,hours);
        contentValues.put(MINUTES,minutes);
        contentValues.put(DAYS,days);
        contentValues.put(MONTHS,months);
        contentValues.put(YEARS,years);
        contentValues.put(COMPLETED,completed);
        contentValues.put(NOTIF,notif);
        long rowNo=sqLiteDatabase.update(TABLE_NAME,contentValues,ID+"="+id, null);
        return rowNo;
    }

    public Cursor showAllData()
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery("SELECT * FROM "+TABLE_NAME,null);
        return cursor;
    }
    public Cursor showAllDataDecOrder()
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery("SELECT * FROM "+TABLE_NAME+" ORDER BY "+ID+" DESC",null);
        return cursor;
    }

    public Cursor showAllCompletedDataDecOrder()
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
       // Cursor cursor=sqLiteDatabase.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+COMPLETED+" = 1 ORDER BY "+ID+" DESC",null);
        Cursor cursor=sqLiteDatabase.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+COMPLETED+" = 1 ORDER BY days+(months*30)+(years*365) DESC",null);
        return cursor;
    }

    public void makeCompleted(int id)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(COMPLETED, 1);
        sqLiteDatabase.update(TABLE_NAME, contentValues, ID+"="+id, null);
    }

    public void makeInCompleted(int id)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(COMPLETED, 0);
        sqLiteDatabase.update(TABLE_NAME, contentValues, ID+"="+id, null);
    }

    public int deleteData(String id)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        return sqLiteDatabase.delete(TABLE_NAME,ID+" = ?",new String[]{id});
    }

    public Cursor showDataForDateAndUpTime(String hour,String day,String month,String year)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+DAYS+" = "+day+" AND "+MONTHS+" = "+month+" AND "+YEARS+" = "+year+" AND "+HOURS+" >= "+hour ,null);
        return cursor;
    }

    public Cursor showTotalTaskForDay(String day,String month,String year)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+DAYS+" = "+day+" AND "+MONTHS+" = "+month+" AND "+YEARS+" = "+year,null);
        return cursor;
    }

    public Cursor showCompletedTaskForDay(String day,String month,String year)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+DAYS+" = "+day+" AND "+MONTHS+" = "+month+" AND "+YEARS+" = "+year+" AND "+COMPLETED+" = 1" ,null);
        return cursor;
    }

    public Cursor showTaskForDayAndPriority(String p,String day,String month,String year)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+DAYS+" = "+day+" AND "+MONTHS+" = "+month+" AND "+YEARS+" = "+year+" AND "+PRIORITY+" = '"+p+"' ",null);
        return cursor;
    }

    public Cursor showPreviousTasksDec(int choice0,int choice1,String month,String year)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+MONTHS+" <= "+month+" AND "+YEARS+" = "+year+" AND ("+COMPLETED+" = "+choice0+" OR "+COMPLETED+" = "+choice1+" ) ORDER BY days+(months*30)+(years*365) DESC",null);
        return cursor;
    }
    public Cursor showUpcomingTaskDec(int choice0,int choice1,String month,String year)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+MONTHS+" >= "+month+" AND "+YEARS+" >= "+year+" AND ("+COMPLETED+" = "+choice0+" OR "+COMPLETED+" = "+choice1+" )ORDER BY days+(months*30)+(years*365) ASC",null);
        return cursor;
    }
    public Cursor showDataForDate(int choice0,int choice1,String day,String month,String year,int sort)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        Cursor cursor;
        if(sort==1)
            cursor=sqLiteDatabase.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+DAYS+" = "+day+" AND "+MONTHS+" = "+month+" AND "+YEARS+" = "+year+" AND ("+COMPLETED+" = "+choice0+" OR "+COMPLETED+" = "+choice1+" ) ORDER BY (hours*60)+minutes ASC",null);
        else if(sort==2)
            cursor=sqLiteDatabase.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+DAYS+" = "+day+" AND "+MONTHS+" = "+month+" AND "+YEARS+" = "+year+" AND ("+COMPLETED+" = "+choice0+" OR "+COMPLETED+" = "+choice1+" ) ORDER BY "+NAME+" ASC",null);
        else
            cursor=sqLiteDatabase.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+DAYS+" = "+day+" AND "+MONTHS+" = "+month+" AND "+YEARS+" = "+year+" AND ("+COMPLETED+" = "+choice0+" OR "+COMPLETED+" = "+choice1+" ) ORDER BY "+ID+" ASC",null);

        return cursor;
    }
    public Cursor showTaskForId(String id)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+ID+" = "+id,null);
        return cursor;
    }

    public void makeNotif(int id,int state)  //for enable and disable notification on task
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(NOTIF, state);
        sqLiteDatabase.update(TABLE_NAME, contentValues, ID+"="+id, null);
    }

    public Cursor getLastRecord()
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE ID = (SELECT MAX (ID) FROM "+TABLE_NAME+")",null);
        return cursor;
    }

    public Cursor showTaskPriorityForDateRange(String p,String dayStart,String dayEnd,String month,String year)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery("SELECT "+ID+" FROM "+TABLE_NAME+" WHERE "+DAYS+" >= "+dayStart+" AND "+DAYS+" <="+dayEnd+" AND "+MONTHS+" = "+month+" AND "+YEARS+" = "+year+" AND "+PRIORITY+" = '"+p+"' ",null);
        return cursor;
    }

    public Cursor showTotalTaskForDateRange(String dayStart,String dayEnd,String month,String year)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery("SELECT "+ID+" FROM "+TABLE_NAME+" WHERE "+DAYS+" >= "+dayStart+" AND "+DAYS+" <="+dayEnd+" AND "+MONTHS+" = "+month+" AND "+YEARS+" = "+year,null);
        return cursor;
    }

    public Cursor showTaskDataForDateRange(String dayStart,String dayEnd,String month,String year)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+DAYS+" >= "+dayStart+" AND "+DAYS+" <="+dayEnd+" AND "+MONTHS+" = "+month+" AND "+YEARS+" = "+year+" ORDER BY "+NAME+" ASC",null);
        return cursor;
    }

    public Cursor showCompletedTaskForDateRange(String dayStart,String dayEnd,String month,String year)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery("SELECT "+ID+" FROM "+TABLE_NAME+" WHERE "+DAYS+" >= "+dayStart+" AND "+DAYS+" <="+dayEnd+" AND "+MONTHS+" = "+month+" AND "+YEARS+" = "+year+" AND "+COMPLETED+" = 1" ,null);
        return cursor;
    }

    public Cursor showDueTaskForDateRange(String minute,String hour,String day,String dayStart,String dayEnd,String month,String year)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery("SELECT "+ID+" FROM "+TABLE_NAME+" WHERE ("+COMPLETED+" = 0"+") AND ("+DAYS+" >= "+dayStart+" AND "+DAYS+" <="+dayEnd+")  AND (("+DAYS+" > "+day+" AND "+MONTHS+" = "+month+" AND "+YEARS+" = "+year+") OR ("+HOURS+" > "+hour+" AND "+DAYS+" = "+day+" AND "+MONTHS+" = "+month+" AND "+YEARS+" = "+year+" ) OR ("+MINUTES+" > "+minute+" AND "+HOURS+" = "+hour+" AND "+DAYS+" = "+day+" AND "+MONTHS+" = "+month+" AND "+YEARS+" = "+year+" ) )"  ,null);
        return cursor;
    }

    public Cursor showPinnedTaskDec()
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+PIN+" = 1"+" ORDER BY "+ID+" DESC",null);
        return cursor;
    }

    public void makePin(int id,int state)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(PIN, state);
        sqLiteDatabase.update(TABLE_NAME, contentValues, ID+"="+id, null);
    }
}
