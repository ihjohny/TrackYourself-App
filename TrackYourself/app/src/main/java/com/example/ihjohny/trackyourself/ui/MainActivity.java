package com.example.ihjohny.trackyourself.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.ihjohny.trackyourself.R;
import com.example.ihjohny.trackyourself.data.db.DatabaseHelper;
import com.example.ihjohny.trackyourself.ui.home.Home;
import com.example.ihjohny.trackyourself.ui.dialog.TaskDialog;
import com.example.ihjohny.trackyourself.ui.today.Today;
import com.example.ihjohny.trackyourself.utils.ExtraFunction;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private static DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper=new DatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase=databaseHelper.getWritableDatabase();

        bottomNavigationView= findViewById(R.id.buttomNavigationId);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,new Home()).commit();
        bottomNavigationView.setOnNavigationItemSelectedListener(buttomNavListener);
       // ExtraFunction.disableShiftMode(bottomNavigationView);


    }



    //BottomNavigation
    private BottomNavigationView.OnNavigationItemSelectedListener buttomNavListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int flag=0;
                    Fragment selectedFragment = new Home();
                    switch (item.getItemId()) {
                        case R.id.homeId:
                            selectedFragment = new Home();
                            setTitle("Track YourSelf");
                            break;
                        case R.id.addId:
                            String title="Add New Task";
                            Intent intent=new Intent(getApplicationContext(), TaskDialog.class);
                            intent.putExtra("Title",title);
                            intent.putExtra("IntentTitle","Home");
                            startActivityForResult(intent,7);
                            flag=1;
                            break;
                        case R.id.todayId:
                            selectedFragment = new Today();
                            setTitle("Today");
                            break;
/*                        case R.id.completedId:
                            selectedFragment = new Calendar();
                            setTitle("Completed");
                            break;*/
                    }
                    if(flag==0)
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,selectedFragment).commit();
                    return true;
                }
            };


/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_layout,menu);

        return super.onCreateOptionsMenu(menu);
    }*/


/*    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.shareId)
        {
            //  Toast.makeText(CompletedActivity.this,"Share",Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String shareBody="Track YourSelf";
            String shareSub="“Focus on being productive instead of busy.” ~ Tim Ferriss";
            intent.putExtra(Intent.EXTRA_SUBJECT,shareSub);
            intent.putExtra(Intent.EXTRA_TEXT,shareBody);
            startActivity(Intent.createChooser(intent,"Share Using"));
            return true;
        }
        if(item.getItemId()==R.id.aboutId)
        {
            //   Toast.makeText(CompletedActivity.this,"about",Toast.LENGTH_SHORT).show();
            AlertDialog.Builder about=new AlertDialog.Builder(this);
            View view= getLayoutInflater().inflate(R.layout.about_dialog,null);
            about.setView(view);
            AlertDialog dialog=about.create();
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/


    //setNotifToLastAddedTask
    public void setNotifToLastAddedTask()
    {
        int lastTaskId,lastTaskMinute,lastTaskHour,lastTaskDay,lastTaskMonth,lastTaskYear;
        Cursor cursor=databaseHelper.getLastRecord();
        while(cursor.moveToNext())
        {
            lastTaskId=Integer.parseInt(cursor.getString(0));
            lastTaskMinute=Integer.parseInt(cursor.getString(5));
            lastTaskHour=Integer.parseInt(cursor.getString(4));
            lastTaskDay=Integer.parseInt(cursor.getString(6));
            lastTaskMonth=Integer.parseInt(cursor.getString(7));
            lastTaskYear=Integer.parseInt(cursor.getString(8));;

            // ExtraFunction extraFunction=new ExtraFunction();
            if(cursor.getString(9).equals("0") && cursor.getString(10).equals("1") && lastTaskHour!=-1 && lastTaskYear!=-1)
                ExtraFunction.setNotif(getApplicationContext(),lastTaskId,lastTaskMinute,lastTaskHour,lastTaskDay,lastTaskMonth,lastTaskYear);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==7)
        {
            if(resultCode== Activity.RESULT_OK)
            {
                setNotifToLastAddedTask();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,new Today()).commit();
            }
        }
    }

}
