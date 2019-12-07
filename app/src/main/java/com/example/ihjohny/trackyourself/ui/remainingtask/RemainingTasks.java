package com.example.ihjohny.trackyourself.ui.remainingtask;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import com.example.ihjohny.trackyourself.data.db.DatabaseHelper;
import com.example.ihjohny.trackyourself.ui.taskdetails.TaskDetailsBottomSheet;
import com.example.ihjohny.trackyourself.ui.adapter.TaskRecycleAdapter;
import com.example.ihjohny.trackyourself.ui.dialog.TaskDialog;
import com.example.ihjohny.trackyourself.utils.ExtraFunction;
import com.example.ihjohny.trackyourself.R;
import com.example.ihjohny.trackyourself.ui.adapter.SimpleSectionedRecyclerViewAdapter;
import com.example.ihjohny.trackyourself.data.db.model.Task;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RemainingTasks extends AppCompatActivity {

    private String intentTitle;

    private DatabaseHelper databaseHelper;
    private RecyclerView remainingTaskRecyclerView;
    private TaskRecycleAdapter remainingTaskAdapter;
    private SimpleSectionedRecyclerViewAdapter sectionedAdapter;

    private TimePicker timePicker;
    private DatePicker datePicker;
    private LinearLayout noPreviousTaskView,noUpcommingTaskView,noPinnedList;
    private ArrayList<Task> dataList=new ArrayList<>();
    private ArrayList<Task> dataList1=new ArrayList<>();
    private ArrayList<Task> dataList2=new ArrayList<>();
    private List<SimpleSectionedRecyclerViewAdapter.Section> sections =
            new ArrayList<SimpleSectionedRecyclerViewAdapter.Section>();
    ArrayList<Task>  remainingTaskDataList=new ArrayList<>();
    TaskDetailsBottomSheet taskDetailsBottomSheet =new TaskDetailsBottomSheet();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        intentTitle=getIntent().getStringExtra("Title");

        if(intentTitle.equals("Previous Tasks"))
            getTheme().applyStyle(R.style.PreviousTasksTheme, true);
        else if(intentTitle.equals("Upcoming Tasks"))
            getTheme().applyStyle(R.style.UpcomingTasksTheme, true);
        else
            getTheme().applyStyle(R.style.AppTheme, true);

        setContentView(R.layout.remaining_tasks);

        databaseHelper=new DatabaseHelper(getApplicationContext());
        SQLiteDatabase sqLiteDatabase=databaseHelper.getWritableDatabase();
        noPreviousTaskView= (LinearLayout) findViewById(R.id.noPreviousTaskViewId);
        noUpcommingTaskView=findViewById(R.id.noUpcomingTaskViewId);
        noPinnedList=findViewById(R.id.noPinnedListViewId);
        remainingTaskRecyclerView= (RecyclerView) findViewById(R.id.previousTaskRecyclerViewId);



        setTitle(intentTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setRemainingTasksView();

    }

    private void setRemainingTasksView() {
        loadRemainingTasksData();

        remainingTaskRecyclerView.setHasFixedSize(true);
        remainingTaskRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        remainingTaskAdapter=new TaskRecycleAdapter(getApplicationContext(),dataList,intentTitle);

        sectionedAdapter = new SimpleSectionedRecyclerViewAdapter(getApplicationContext(),R.layout.section,R.id.section_text,remainingTaskAdapter);
        setSection();

        remainingTaskRecyclerView.setAdapter(sectionedAdapter);

        remainingTaskAdapter.setOnItemClickListener(new TaskRecycleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String id;
                if(intentTitle.equals("Pinned Tasks"))
                    id = dataList1.get(position).getId();
                else
                    id = dataList2.get(position).getId();
                Bundle bundle=new Bundle();
                bundle.putString("IntentTitle",intentTitle);
                taskDetailsBottomSheet.setArguments(bundle);
                taskDetailsBottomSheet.show(getSupportFragmentManager(),id);
                bottomSheetButton();
            }

            @Override
            public void onChecked(int position) {
                {
                    databaseHelper.makeCompleted(Integer.parseInt(dataList2.get(position).getId()));
                    loadRemainingTasksData();
                    setSection();
                    remainingTaskAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNotifOnClick(int position) {
                if(intentTitle.equals("Pinned Tasks"))
                    databaseHelper.makeNotif(Integer.parseInt(dataList1.get(position).getId()), 0);
                else
                    databaseHelper.makeNotif(Integer.parseInt(dataList2.get(position).getId()), 0);
                loadRemainingTasksData();
                remainingTaskAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNotifOffClick(int position) {
                if(intentTitle.equals("Pinned Tasks"))
                    databaseHelper.makeNotif(Integer.parseInt(dataList1.get(position).getId()), 1);
                else
                    databaseHelper.makeNotif(Integer.parseInt(dataList2.get(position).getId()), 1);
                loadRemainingTasksData();
                remainingTaskAdapter.notifyDataSetChanged();
            }
        });
    }

    //loadRemainingTasksData
    private void loadRemainingTasksData()
    {
        datePicker=new DatePicker(getApplicationContext());
        String currentDay= String.valueOf(datePicker.getDayOfMonth());
        String currentMonth= String.valueOf(datePicker.getMonth()+1);
        String currentYear= String.valueOf(datePicker.getYear());
        Cursor cursor;

        dataList.clear();
        dataList1.clear();
        dataList2.clear();

        int showCompleted=readSettings("showCompleted","1");
        int hideCompleted=readSettings("hideCompleted","0");

        if(intentTitle.equals("Previous Tasks")) {

            cursor = databaseHelper.showPreviousTasksDec(hideCompleted,showCompleted,currentMonth, currentYear);  //00 for Remaining 11 for Completed 01 for both
            dataList.clear();
            if(cursor.getCount()==0)
            {
                noPreviousTaskView.setVisibility(LinearLayout.VISIBLE);
                noUpcommingTaskView.setVisibility(LinearLayout.GONE);
                remainingTaskRecyclerView.setVisibility(LinearLayout.GONE);
            }

            String date=new String("-1");
            while(cursor.moveToNext())
            {
                if(Integer.parseInt(cursor.getString(8)) >=Integer.parseInt(currentYear) && Integer.parseInt(cursor.getString(7)) >=Integer.parseInt(currentMonth) && Integer.parseInt(cursor.getString(6)) >=Integer.parseInt(currentDay))
                    continue;
          //      if(cursor.getString(9).equals("1") )
                {
                    //remainingTaskDataList.add(new Task(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8),cursor.getString(10)));
                    Task taskForDate =new Task(cursor.getString(0),cursor.getString(1),cursor.getString(2),"date",cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8),cursor.getString(9),cursor.getString(10));
                    Task task =new Task(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8),cursor.getString(9),cursor.getString(10));
                    if(!date.equals(cursor.getString(6)+cursor.getString(7)+cursor.getString(8)))
                    {
                        dataList2.add(taskForDate);
                        dataList1.add(taskForDate);
                        date=cursor.getString(6)+cursor.getString(7)+cursor.getString(8);
                    }
                    else
                        dataList1.add(task);
                    dataList2.add(taskForDate);
                    dataList.add(task);

                }
            }

            if(dataList.isEmpty())
            {
                noPreviousTaskView.setVisibility(LinearLayout.VISIBLE);
                noUpcommingTaskView.setVisibility(LinearLayout.GONE);
                remainingTaskRecyclerView.setVisibility(LinearLayout.GONE);
            }
            else
            {
                noPreviousTaskView.setVisibility(LinearLayout.GONE);
                noUpcommingTaskView.setVisibility(LinearLayout.GONE);
                remainingTaskRecyclerView.setVisibility(LinearLayout.VISIBLE);
            }
        }
        else if(intentTitle.equals("Upcoming Tasks")) {
            cursor = databaseHelper.showUpcomingTaskDec(hideCompleted,showCompleted,currentMonth, currentYear);  //00 for Remaining 11 for Completed 01 for both
            remainingTaskDataList.clear();
            if(cursor.getCount()==0)
            {
                noPreviousTaskView.setVisibility(LinearLayout.GONE);
                noUpcommingTaskView.setVisibility(LinearLayout.VISIBLE);
                remainingTaskRecyclerView.setVisibility(LinearLayout.GONE);
            }

            String date=new String("-1");
            while(cursor.moveToNext())
            {
                if(Integer.parseInt(cursor.getString(8)) <=Integer.parseInt(currentYear) && Integer.parseInt(cursor.getString(7)) <=Integer.parseInt(currentMonth) && Integer.parseInt(cursor.getString(6)) <=Integer.parseInt(currentDay))
                    continue;

                    //remainingTaskDataList.add(new Task(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8),cursor.getString(10)));
                    Task taskForDate =new Task(cursor.getString(0),cursor.getString(1),cursor.getString(2),"date",cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8),cursor.getString(9),cursor.getString(10));
                    Task task =new Task(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8),cursor.getString(9),cursor.getString(10));
                    if(!date.equals(cursor.getString(6)+cursor.getString(7)+cursor.getString(8)))
                    {
                        dataList2.add(taskForDate);
                        dataList1.add(taskForDate);
                        date=cursor.getString(6)+cursor.getString(7)+cursor.getString(8);
                    }
                    else
                        dataList1.add(task);
                    dataList2.add(taskForDate);
                    dataList.add(task);
            }

            if(dataList.isEmpty())
            {
                noPreviousTaskView.setVisibility(LinearLayout.GONE);
                noUpcommingTaskView.setVisibility(LinearLayout.VISIBLE);
                remainingTaskRecyclerView.setVisibility(LinearLayout.GONE);
            }
            else
            {
                noPreviousTaskView.setVisibility(LinearLayout.GONE);
                noUpcommingTaskView.setVisibility(LinearLayout.GONE);
                remainingTaskRecyclerView.setVisibility(LinearLayout.VISIBLE);
            }
        }
        else if(intentTitle.equals("Pinned Tasks")){
            cursor = databaseHelper.showPinnedTaskDec();
            remainingTaskDataList.clear();
            if(cursor.getCount()==0)
            {
                noPreviousTaskView.setVisibility(LinearLayout.GONE);
                noUpcommingTaskView.setVisibility(LinearLayout.GONE);
                noPinnedList.setVisibility(View.VISIBLE);
                remainingTaskRecyclerView.setVisibility(LinearLayout.GONE);
            }
            String date=new String("-1");
            while(cursor.moveToNext()) {
                Task taskForDate =new Task(cursor.getString(0),cursor.getString(1),cursor.getString(2),"date",cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8),cursor.getString(9),cursor.getString(10));
                Task task =new Task(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8),cursor.getString(9),cursor.getString(10));
                if(!date.equals(cursor.getString(6)+cursor.getString(7)+cursor.getString(8)))
                {
                    dataList2.add(taskForDate);
                    dataList1.add(taskForDate);
                    date=cursor.getString(6)+cursor.getString(7)+cursor.getString(8);
                }
                else
                    dataList1.add(task);
                dataList2.add(taskForDate);
                dataList.add(task);
            }
            remainingTaskRecyclerView.setVisibility(LinearLayout.VISIBLE);
        }
    }

    //setSection
    private void setSection()
    {
        sections.clear();
        for(int i=0;i<dataList.size();i++)
        {
            if(dataList1.get(i).getPriority().equals("date") && !intentTitle.equals("Pinned Tasks"))
            {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR,Integer.parseInt(dataList1.get(i).getYears()));
                calendar.set(Calendar.MONTH,Integer.parseInt(dataList1.get(i).getMonths())-1);
                calendar.set(Calendar.DATE,Integer.parseInt(dataList1.get(i).getDays()));
                String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
                sections.add(new SimpleSectionedRecyclerViewAdapter.Section(i,currentDate));
            }
        }
        SimpleSectionedRecyclerViewAdapter.Section[] s=new SimpleSectionedRecyclerViewAdapter.Section[sections.size()];
        sectionedAdapter.setSections(sections.toArray(s));
    }

    public void bottomSheetButton()
    {
        taskDetailsBottomSheet.setOnButtonClickListener(new TaskDetailsBottomSheet.TaskDetailsBottomShitListener() {

            @Override
            public void onPinOn(int id) {
                databaseHelper.makePin(Integer.parseInt(String.valueOf(id)),1);
                loadRemainingTasksData();
                setSection();
                remainingTaskAdapter.notifyDataSetChanged();
             //   Toast.makeText(getApplicationContext(),"this is test pin on clicked "+id ,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPinOff(int id) {
                databaseHelper.makePin(Integer.parseInt(String.valueOf(id)),0);
                loadRemainingTasksData();
                setSection();
                remainingTaskAdapter.notifyDataSetChanged();
             //   Toast.makeText(getApplicationContext(),"this is test pin off clicked "+id ,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteClick(int id) {
                databaseHelper.deleteData(String.valueOf(id));
                loadRemainingTasksData();
                setSection();
                remainingTaskAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCompletedClick(int id) {
                databaseHelper.makeCompleted(Integer.parseInt(String.valueOf(id)));
                loadRemainingTasksData();
                setSection();
                remainingTaskAdapter.notifyDataSetChanged();
            }

            @Override
            public void onUndoClick(int id) {
                databaseHelper.makeInCompleted(Integer.parseInt(String.valueOf(id)));
                loadRemainingTasksData();
                setSection();
                remainingTaskAdapter.notifyDataSetChanged();
            }

            @Override
            public void onInitClick(int id) {
                datePicker=new DatePicker(getApplicationContext());
                int currentDay= datePicker.getDayOfMonth();
                int currentMonth= datePicker.getMonth()+1;
                int currentYear= datePicker.getYear();

                Cursor cursor=databaseHelper.showTaskForId(String.valueOf(id));
                cursor.moveToNext();
                databaseHelper.saveData(cursor.getString(1),cursor.getString(2), cursor.getString(3), Integer.valueOf(cursor.getString(4)), Integer.valueOf(cursor.getString(5)),currentDay,currentMonth,currentYear, 0,Integer.valueOf(cursor.getString(10)),0);
                if(cursor.getString(10).equals("1"))
                    ExtraFunction.setNotif(getApplicationContext(),Integer.parseInt(cursor.getString(0)),Integer.valueOf(cursor.getString(5)), Integer.valueOf(cursor.getString(4)),currentDay,currentMonth,currentYear);
            }

            @Override
            public void onEditClick(int id) {
                String title="Edit Task";
                String idd=String.valueOf(id);
                Intent intent=new Intent(getApplicationContext(), TaskDialog.class);
                intent.putExtra("IntentTitle",intentTitle);
                intent.putExtra("Title",title);
                intent.putExtra("Id",idd);
                startActivityForResult(intent,101);
            }
        });
    }

    public void writeSettings(String key,String value){
        SharedPreferences sharedPreferences=getSharedPreferences("Settings",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(key,value);
        editor.apply();
    }
    public int readSettings(String key,String defValue){
        SharedPreferences sharedPreferences=getSharedPreferences("Settings",MODE_PRIVATE);
        return Integer.valueOf(sharedPreferences.getString(key,defValue));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        if(!intentTitle.equals("Pinned Tasks"))
            inflater.inflate(R.menu.filter_tasks_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.showCompleted)
        {

            writeSettings("showCompleted","1");
            writeSettings("hideCompleted","0");
 //           Toast.makeText(getApplicationContext(),String.valueOf(readSettings("showCompleted","0")),Toast.LENGTH_SHORT).show();
 //           Toast.makeText(getApplicationContext(),String.valueOf(readSettings("hideCompleted","1")),Toast.LENGTH_SHORT).show();
            setRemainingTasksView();
            return true;
        }
        if(item.getItemId()==R.id.hideCompleted)
        {
            writeSettings("showCompleted","0");
            writeSettings("hideCompleted","0");
            setRemainingTasksView();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101)
        {
            if(resultCode== Activity.RESULT_OK)
            {
                loadRemainingTasksData();
                remainingTaskAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishActivity(1);
    }
}
