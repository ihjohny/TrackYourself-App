package com.example.ihjohny.trackyourself.ui.dialog;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.ihjohny.trackyourself.data.db.DatabaseHelper;
import com.example.ihjohny.trackyourself.utils.ExtraFunction;
import com.example.ihjohny.trackyourself.R;

public class TaskDialog extends AppCompatActivity implements View.OnClickListener{

    int hour,minute,days,months,years,isCompleted;
    private Spinner spinner;
    private EditText name,description;
    private TextView clock,date,notifState;
    private DatabaseHelper databaseHelper;
    private String [] priority={"Low","Medium","High"};
    private String spinnerText;
    private TimePickerDialog timePickerDialog;
    private String section,id;
    private ImageView notifOn,notifOff,timeOn,timeOff;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        if(getIntent().getStringExtra("IntentTitle").equals("Previous Tasks"))
            getTheme().applyStyle(R.style.PreviousTasksTheme, true);
        else if(getIntent().getStringExtra("IntentTitle").equals("Upcoming Tasks"))
            getTheme().applyStyle(R.style.UpcomingTasksTheme, true);
        else
            getTheme().applyStyle(R.style.AppTheme, true);

        setContentView(R.layout.activity_task_dialog);

        setTitle(getIntent().getStringExtra("Title"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        databaseHelper=new DatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase=databaseHelper.getWritableDatabase();

        name= (EditText) findViewById(R.id.nameEditTextId);
        description= (EditText) findViewById(R.id.descriptionEditTextId);
        clock= (TextView) findViewById(R.id.clockTextViewId);
        date= (TextView) findViewById(R.id.dateTextViewId);
        spinner= (Spinner) findViewById(R.id.priority_SpinnerId);
        notifState= (TextView) findViewById(R.id.addTaskNotifText);
        notifOn= (ImageView) findViewById(R.id.addTaskNotifOn);
        notifOff= (ImageView) findViewById(R.id.addTaskNotifOff);
        timeOn= (ImageView) findViewById(R.id.addTaskTimeOnId);
        timeOff= (ImageView) findViewById(R.id.addTaskTimeOffId);

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,priority);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==2)
                {
                   spinnerText="High";
                }
                else if(i==1)
                {
                    spinnerText="Medium";
                }
                else
                {
                    spinnerText="Low";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        setTodayTimeAndDate();

        section=getIntent().getStringExtra("Title");
        if(section.equals("Edit Task"))
        {
            id=getIntent().getStringExtra("Id");
            Cursor cursor=databaseHelper.showTaskForId(id);
            if(cursor.getCount()!=0)
            {
                cursor.moveToNext();
                name.setText(cursor.getString(1));
                description.setText(cursor.getString(2));
                String string;
                isCompleted=Integer.valueOf(cursor.getString(9));
               // clock.setText(ExtraFunction.setTime(cursor.getString(4),cursor.getString(5)));
               // date.setText(ExtraFunction.setDate(getApplicationContext(),Integer.parseInt(cursor.getString(6)),Integer.parseInt(cursor.getString(7)),Integer.parseInt(cursor.getString(8))));
                if(cursor.getString(10).equals("1"))
                {
                    notifOn.setVisibility(View.VISIBLE);
                    notifOff.setVisibility(View.GONE);
                    notifState.setText("On");
                }
                else
                {
                    notifOn.setVisibility(View.GONE);
                    notifOff.setVisibility(View.VISIBLE);
                    notifState.setText("Off");
                }
/*
                if(cursor.getString(11).equals("1")){
                    dateOff.setVisibility(View.VISIBLE);
                    dateOn.setVisibility(View.GONE);
                    date.setText("Pinned List");
                }
                else{
                    dateOff.setVisibility(View.GONE);
                    dateOn.setVisibility(View.VISIBLE);
                }*/

                if(cursor.getString(4).equals("24")){
                   timeOff.setVisibility(View.VISIBLE);
                   timeOn.setVisibility(View.GONE);
                   clock.setText("Any Time");
                }
                else {
                    timeOff.setVisibility(View.GONE);
                    timeOn.setVisibility(View.VISIBLE);
                }

                if(cursor.getString(3).equals("High"))
                    spinner.setSelection(2);
                else if(cursor.getString(3).equals("Medium"))
                    spinner.setSelection(1);
                else
                    spinner.setSelection(0);

                if(!cursor.getString(4).equals("24")) {
                    hour = Integer.parseInt(cursor.getString(4));
                    minute = Integer.parseInt(cursor.getString(5));
                    clock.setText(ExtraFunction.setTime(String.valueOf(hour), String.valueOf(minute)));
                }

                if(!cursor.getString(6).equals("-1")) {
                    days = Integer.parseInt(cursor.getString(6));
                    months = Integer.parseInt(cursor.getString(7));
                    years = Integer.parseInt(cursor.getString(8));
                    date.setText(ExtraFunction.setDate(getApplicationContext(),days,months,years));
                }
                // clock.setText(ExtraFunction.setTime(cursor.getString(4),cursor.getString(5)));
                // date.setText(ExtraFunction.setDate(getApplicationContext(),Integer.parseInt(cursor.getString(6)),Integer.parseInt(cursor.getString(7)),Integer.parseInt(cursor.getString(8))));
            }
        }
        else
        {
            notifOn.setVisibility(ImageView.VISIBLE);
            notifOff.setVisibility(View.GONE);
            notifState.setText("On");
            timeOn.setVisibility(View.VISIBLE);
            timeOff.setVisibility(View.GONE);

            clock.setText(hour+" : "+minute);
            date.setText("Today");
        }

        notifOn.setOnClickListener(this);
        notifOff.setOnClickListener(this);
        timeOn.setOnClickListener(this);
        timeOff.setOnClickListener(this);
        clock.setOnClickListener(this);
        date.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.dialogmenu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.saveId)
        {
            onSaveClick();
            return true;
        }
        else if(item.getItemId()==R.id.discardId)
        {
            onBackPressed();
            return true;
        }
        else
        {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.clockTextViewId)
        {
            //call time picker
            TimePicker timePicker=new TimePicker(this);
            final int currentHour=timePicker.getCurrentHour();
            final int currentMinute=timePicker.getCurrentMinute();

            timePickerDialog=new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        clock.setText(i+" : "+i1);
                        hour=i;
                        minute=i1;
                }
            },currentHour,currentMinute,true);
            timePickerDialog.show();
        }
       else if(view.getId()==R.id.dateTextViewId)
        {
            DatePicker datePicker=new DatePicker(this);
            final int currentDay=datePicker.getDayOfMonth();
            final int currentMonth=datePicker.getMonth();
            final int currentYear=datePicker.getYear();

            DatePickerDialog datePickerPickerDialog;
            datePickerPickerDialog= new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                    i1=i1+1;
                    date.setText(ExtraFunction.setDate(getApplicationContext(),i2,i1,i));
                    days=i2;
                    months=i1;
                    years=i;
                }
            },currentYear,currentMonth,currentDay);
            datePickerPickerDialog.show();
        }

        else if(view.getId()==R.id.addTaskNotifOn)
        {
            notifOn.setVisibility(View.GONE);
            notifOff.setVisibility(View.VISIBLE);
            notifState.setText("Off");
        }

        else if(view.getId()==R.id.addTaskNotifOff)
        {
            notifOff.setVisibility(View.GONE);
            notifOn.setVisibility(View.VISIBLE);
            notifState.setText("On");
        }

        else if(view.getId()==R.id.addTaskTimeOnId)
        {
            timeOn.setVisibility(View.GONE);
            timeOff.setVisibility(View.VISIBLE);
            clock.setText("Any Time");
            //notifState.setText("Off");
        }

        else if(view.getId()==R.id.addTaskTimeOffId)
        {
            timeOff.setVisibility(View.GONE);
            timeOn.setVisibility(View.VISIBLE);
            clock.setText(hour+" : "+minute);
            //notifState.setText("On");
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishActivity(1);
    }

    private void setTodayTimeAndDate()
    {
        TimePicker timePicker=new TimePicker(this);
        int currentHour=timePicker.getCurrentHour();
        int currentMinute=timePicker.getCurrentMinute();
        hour=currentHour;
        minute=currentMinute;
      //  clock.setText(currentHour+" : "+currentMinute);

        DatePicker datePicker=new DatePicker(this);
        int currentDay=datePicker.getDayOfMonth();
        int currentMonth=datePicker.getMonth();
        int currentYear=datePicker.getYear();
        days=currentDay;
        months=currentMonth+1;
        years=currentYear;
     //   date.setText("Today");
    }

    private void onSaveClick()
    {
        String nameText = name.getText().toString();
        String descriptionText = description.getText().toString();
        String priorityText = spinnerText;
        String notif=notifState.getText().toString();
        int notifState1,pin=0;

        if(clock.getText().equals("Any Time")){
            hour=24;
            minute=0;
        }

        if(notif.equals("On")) notifState1=1;
        else notifState1=0;

        //save all data to database and goto main activity
        if (nameText.equals("")) {
            Toast.makeText(getApplicationContext(), "Enter Task Name", Toast.LENGTH_SHORT).show();
        }
        else {
            long rowNo;
            if(section.equals("Edit Task"))
            {
                rowNo = databaseHelper.updateData(id, nameText, descriptionText, priorityText, hour, minute, days, months, years, isCompleted, notifState1);

                if(notifState1==1 && hour!=-1 && years!=-1)
                    ExtraFunction.setNotif(getApplicationContext(),Integer.parseInt(id),minute,hour,days,months,years);
                else
                    ExtraFunction.cancelNotif(getApplicationContext(),Integer.parseInt(id));
            }

            else
                rowNo = databaseHelper.saveData(nameText, descriptionText, priorityText, hour, minute, days, months, years, 0,notifState1,0);

            if (rowNo > -1) {
                if(!section.equals("Edit Task"))
                        Toast.makeText(getApplicationContext(), "Task Added Successfully", Toast.LENGTH_SHORT).show();

                //do some thing
                Intent returnIntent=new Intent();
                returnIntent.putExtra("1",name.getText());

                name.setText("");
                description.setText("");
                //clock.setText("12 : 00");

                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        }
    }

    private void setEditSection()
    {
        String Section=getIntent().getStringExtra("Title");
    }
}
