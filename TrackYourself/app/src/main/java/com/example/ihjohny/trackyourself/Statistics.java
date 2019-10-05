package com.example.ihjohny.trackyourself;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.service.autofill.Dataset;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Statistics extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private DatabaseHelper databaseHelper;
    private String intentTitle;
    private LinearLayout selectDate, last7DaysView;
    private TextView selectMonth, last7Days;
    private DatePicker datePicker;
    private int thisMinute, thisHour, thisDay, thisMonth, thisYear, statisticsForMonth, statisticsForYear;
    private PieChart taskCompletedPieChart, taskPriorityPieChart;
    private LineChart statisticsLineChart;
    private BarChart statisticsBarChart;
    private RecyclerView statisticsTaskItemRecylerView;
    private StatisticsTaskItemAdapter statisticsTaskItemAdapter;
    private RadioButton radioButton;
    private RadioGroup radioGroup;
    private String statisticsTitle;
    private int[] totalTasksDataCount = new int[32];
    private int[] completedTasksDataCount = new int[32];
    private ArrayList<StatisticsTaskItem> statisticsTaskItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        intentTitle = getIntent().getStringExtra("Title");
        setContentView(R.layout.activity_statistics);

        setTitle(intentTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        databaseHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        selectDate = findViewById(R.id.selectDateId);
        selectMonth = findViewById(R.id.selectMonthId);
        last7Days = findViewById(R.id.last7daysId);
        last7DaysView = findViewById(R.id.last7daysViewId);
        taskCompletedPieChart = findViewById(R.id.taskCompletedPieChartId);
        taskPriorityPieChart = findViewById(R.id.taskPriorityPieChartId);
        statisticsLineChart = findViewById(R.id.statisticsLineChartId);
        statisticsBarChart = findViewById(R.id.statisticsBarChartId);
        statisticsTaskItemRecylerView = findViewById(R.id.statisticsRecyclerViewId);
        radioGroup = findViewById(R.id.radioGroupId);


        radioGroup.setOnCheckedChangeListener(this);
        selectDate.setOnClickListener(this);
        // monthRadio.
        // statisticsTitle="Month";
        datePicker = new DatePicker(this);
        thisDay = datePicker.getDayOfMonth();
        thisMonth = datePicker.getMonth() + 1;
        thisYear = datePicker.getYear();
        statisticsForMonth = datePicker.getMonth() + 1;
        statisticsForYear = datePicker.getYear();


        int startDay = thisDay - 6;
        int endDay = thisDay;
        if (startDay < 1) {
            startDay = 1;
            endDay = 7;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, startDay);
        calendar.set(Calendar.YEAR, statisticsForYear);
        calendar.set(Calendar.MONTH, statisticsForMonth - 1);
        //  SimpleDateFormat month_date = new SimpleDateFormat("MMMM yyyy");
        // String startDayText=month_date.format(calendar.getTime());
        String startDayText = DateFormat.getDateInstance(DateFormat.DEFAULT).format(calendar.getTime());

        calendar.set(Calendar.DATE, endDay);
        calendar.set(Calendar.YEAR, statisticsForYear);
        calendar.set(Calendar.MONTH, statisticsForMonth - 1);
        //  String endDayText=month_date.format(calendar.getTime());
        String endDayText = DateFormat.getDateInstance(DateFormat.DEFAULT).format(calendar.getTime());
        calendar.set(Calendar.DATE, thisDay);
        SimpleDateFormat month_date = new SimpleDateFormat("MMMM yyyy");
        String currentMonth = month_date.format(calendar.getTime());
        last7Days.setText(startDayText + " To " + endDayText);
        selectMonth.setText(currentMonth);
        selectDate.setVisibility(View.GONE);
        last7DaysView.setVisibility(View.VISIBLE);

        loadTaskData(7, startDay, endDay, thisMonth, thisYear);
        setPieChart(7, startDay, endDay, thisMonth, thisYear);
        setLineChart(7, startDay, endDay, totalTasksDataCount, completedTasksDataCount);
        setBarChart(7, startDay, endDay, totalTasksDataCount, completedTasksDataCount);

        setStatisticsRecyclerView(statisticsTaskItems);
    }

    private void setPieChart(int day, int dayStart, int dayEnd, int statisticsMonth, int statisticsYear) {

        int percentage = 0, completed = 0, due = 0, overdue = 0, high = 0, medium = 0, low = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, statisticsYear);
        calendar.set(Calendar.MONTH, statisticsMonth - 1);
        SimpleDateFormat month_date = new SimpleDateFormat("MMMM yyyy");

        Cursor totalCursor = databaseHelper.showTotalTaskForDateRange(String.valueOf(dayStart), String.valueOf(dayEnd), String.valueOf(statisticsMonth), String.valueOf(statisticsYear));
        Cursor completedCursor = databaseHelper.showCompletedTaskForDateRange(String.valueOf(dayStart), String.valueOf(dayEnd), String.valueOf(statisticsMonth), String.valueOf(statisticsYear));
        Cursor highCursor = databaseHelper.showTaskPriorityForDateRange("High", String.valueOf(dayStart), String.valueOf(dayEnd), String.valueOf(statisticsMonth), String.valueOf(statisticsYear));
        Cursor mediumCursor = databaseHelper.showTaskPriorityForDateRange("Medium", String.valueOf(dayStart), String.valueOf(dayEnd), String.valueOf(statisticsMonth), String.valueOf(statisticsYear));
        Cursor lowCursor = databaseHelper.showTaskPriorityForDateRange("Low", String.valueOf(dayStart), String.valueOf(dayEnd), String.valueOf(statisticsMonth), String.valueOf(statisticsYear));
        Cursor dueCursor = databaseHelper.showDueTaskForDateRange(String.valueOf(thisMinute), String.valueOf(thisHour), String.valueOf(thisDay), String.valueOf(dayStart), String.valueOf(dayEnd), String.valueOf(thisMonth), String.valueOf(thisYear));

        if (totalCursor.getCount() != 0)
            percentage = (100 * completedCursor.getCount()) / totalCursor.getCount();

        completed = completedCursor.getCount();
        if (statisticsMonth == thisMonth && statisticsYear == thisYear) {
            due = dueCursor.getCount();
            overdue = totalCursor.getCount() - (due + completedCursor.getCount());
        } else if (statisticsMonth > thisMonth && statisticsYear >= thisYear) {
            due = totalCursor.getCount() - completedCursor.getCount();
            overdue = 0;
        } else {
            due = 0;
            overdue = totalCursor.getCount() - completedCursor.getCount();
        }

        high = highCursor.getCount();
        medium = mediumCursor.getCount();
        low = lowCursor.getCount();
        if (completed == 0 && due == 0 && overdue == 0)
            overdue = 1;
        setTaskCompletedPieChart(percentage, completed, due, overdue);
        if (high == 0 && medium == 0 && low == 0)
            low = 1;
        setTaskPriorityPieChart(high, medium, low);


    }

    private void setLineChart(int day, int dayStart, int dayEnd, int totalTasksData[], int completedTasksData[]) {

        ArrayList<Entry> totalTasks = new ArrayList<>();
        for (int i = dayStart; i <= dayEnd; i++) {
            totalTasks.add(new Entry(i, totalTasksData[i]));
        }

        LineDataSet d1 = new LineDataSet(totalTasks, "Total Task");
        d1.setLineWidth(2.5f);
        d1.setCircleRadius(3.5f);
        d1.setHighLightColor(Color.rgb(244, 117, 117));
        d1.setDrawValues(false);

        ArrayList<Entry> completedTasks = new ArrayList<>();
        for (int i = dayStart; i <= dayEnd; i++) {
            completedTasks.add(new Entry(i, completedTasksData[i]));
        }

        LineDataSet d2 = new LineDataSet(completedTasks, "Completed Task");
        d2.setLineWidth(2.5f);
        d2.setCircleRadius(3.5f);
        d2.setHighLightColor(Color.rgb(244, 117, 117));
        d2.setColor(ColorTemplate.MATERIAL_COLORS[0]);
        d2.setCircleColor(ColorTemplate.MATERIAL_COLORS[0]);
        d2.setDrawValues(false);

        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(d1);
        sets.add(d2);

        LineData data = new LineData(sets);
        XAxis xAxis = statisticsLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        if (day == 7)
            xAxis.setGranularity(1f);
        YAxis leftAxis = statisticsLineChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis.setGranularity(1f);
        YAxis rightAxis = statisticsLineChart.getAxisRight();
        rightAxis.setAxisMinimum(0f);
        rightAxis.setGranularity(1f);

        statisticsLineChart.setData(data);
        statisticsLineChart.setTouchEnabled(false);
        statisticsLineChart.getDescription().setEnabled(false);
        statisticsLineChart.animateX(750);
        statisticsLineChart.invalidate();

    }

    private void setBarChart(int day, int dayStart, int dayEnd, int totalTasksData[], int completedTasksData[]) {

        ArrayList<BarEntry> percentageTasks = new ArrayList<>();
        for (int i = dayStart; i <= dayEnd; i++) {
            if ((completedTasksData[i] == 0 || totalTasksData[i] == 0))
                percentageTasks.add(new BarEntry(i, 0));
            else
                percentageTasks.add(new BarEntry(i, (100 * completedTasksData[i]) / totalTasksData[i]));
        }

        BarDataSet data = new BarDataSet(percentageTasks, "Task Completed Percentage");
        data.setColor(ColorTemplate.MATERIAL_COLORS[0]);
        //data.setHighLightAlpha(255);
        BarData cd = new BarData(data);
        cd.setValueTextSize(5f);
        cd.setBarWidth(0.8f);

        XAxis xAxis = statisticsBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        if (day == 7)
            xAxis.setGranularity(1f);
        YAxis leftAxis = statisticsBarChart.getAxisLeft();
        leftAxis.setAxisMaximum(100);
        leftAxis.setAxisMinimum(0);
        YAxis rightAxis = statisticsBarChart.getAxisRight();
        rightAxis.setAxisMaximum(100);
        rightAxis.setAxisMinimum(0);

        statisticsBarChart.setDrawGridBackground(false);
        //statisticsBarChart.setTouchEnabled(false);
        statisticsBarChart.setDrawBarShadow(false);
        statisticsBarChart.setData(cd);
        statisticsBarChart.getDescription().setEnabled(false);
        statisticsBarChart.setFitBars(true);
        statisticsBarChart.animateY(750);
    }

    //
    private void loadTaskData(int day, int dayStart, int dayEnd, int statisticsForMonth, int statisticsForYear) {
        ArrayList<DataSet> dataSets = new ArrayList<>();
        Cursor cursor = databaseHelper.showTaskDataForDateRange(String.valueOf(dayStart), String.valueOf(dayEnd), String.valueOf(statisticsForMonth), String.valueOf(statisticsForYear));
        while (cursor.moveToNext()){
            DataSet dataset=new DataSet(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8),cursor.getString(9),cursor.getString(10));
            dataSets.add(dataset);
        }

        HashMap<String,Pair<Integer,Integer> > taskTitleMap = new HashMap<>();

        for(int i=0;i<dataSets.size();i++){
           DataSet dataSet= dataSets.get(i);

            if ( taskTitleMap.get(dataSet.getTaskName())==null) {
               // StatisticsTaskItem statisticsTaskItem = new StatisticsTaskItem(dataSet.getTaskName(), 10, i);
              //  statisticsTaskItems.add(statisticsTaskItem);
                if(dataSet.getIsCompleted().equals("1"))
                    taskTitleMap.put(dataSet.getTaskName(),Pair.create(1,1));
                else
                    taskTitleMap.put(dataSet.getTaskName(),Pair.create(1,0));
            }
          else
            {
                if(dataSet.getIsCompleted().equals("1"))
                    taskTitleMap.put(dataSet.getTaskName(),Pair.create((taskTitleMap.get(dataSet.getTaskName()).first+1),(taskTitleMap.get(dataSet.getTaskName()).second+1)));
                else
                    taskTitleMap.put(dataSet.getTaskName(),Pair.create((taskTitleMap.get(dataSet.getTaskName()).first+1),(taskTitleMap.get(dataSet.getTaskName()).second)));


//                taskTitleMap.put(dataSet.getTaskName(),taskTitleMap.get(dataSet.getTaskName())+1,);
            }
        }

        for(Map.Entry m:taskTitleMap.entrySet()){
            Pair pair= (Pair) m.getValue();
            StatisticsTaskItem statisticsTaskItem = new StatisticsTaskItem((String)m.getKey(),(Integer)pair.first , (Integer)pair.second);
            statisticsTaskItems.add(statisticsTaskItem);
        }

            for (int i = dayStart; i <= dayEnd; i++) {
            Cursor totalCursor = databaseHelper.showTotalTaskForDay(String.valueOf(i), String.valueOf(statisticsForMonth), String.valueOf(statisticsForYear));
            Cursor completedCursor = databaseHelper.showCompletedTaskForDay(String.valueOf(i), String.valueOf(statisticsForMonth), String.valueOf(statisticsForYear));
            totalTasksDataCount[i] = totalCursor.getCount();
            completedTasksDataCount[i] = completedCursor.getCount();
        }

/*        //test code
        for (int i = 0; i <= 10; i++) {
            StatisticsTaskItem statisticsTaskItem = new StatisticsTaskItem("Android : " + i, 10, i);
            statisticsTaskItems.add(statisticsTaskItem);
        }*/

    }

    //setTaskCompletedPieChart
    private void setTaskCompletedPieChart(int percentage, int completed, int due, int overdue) {
        taskCompletedPieChart.setUsePercentValues(true);
        taskCompletedPieChart.getDescription().setEnabled(false);
        taskCompletedPieChart.setDrawHoleEnabled(true);
        taskCompletedPieChart.setHoleColor(Color.WHITE);
        taskCompletedPieChart.setTransparentCircleRadius(70f);
        taskCompletedPieChart.setRotationEnabled(false);

        ArrayList<PieEntry> values = new ArrayList<>();
        values.add(new PieEntry(completed, "Completed"));
        values.add(new PieEntry(due, "Due"));
        values.add(new PieEntry(overdue, "Overdue"));

        PieDataSet dataSet = new PieDataSet(values, "");
        dataSet.setSliceSpace(2f);
        dataSet.setSelectionShift(3f);
        dataSet.setDrawValues(false);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        dataSet.setValueTextColor(Color.WHITE);
        taskCompletedPieChart.setData(data);
        taskCompletedPieChart.setDrawSliceText(false);
        taskCompletedPieChart.setDrawCenterText(true);
        taskCompletedPieChart.setCenterTextSize(20f);
        taskCompletedPieChart.setCenterTextColor(ColorTemplate.MATERIAL_COLORS[0]);
        taskCompletedPieChart.setCenterText(String.valueOf(percentage) + "%");
        taskCompletedPieChart.animateX(750);
        taskCompletedPieChart.invalidate();
        taskCompletedPieChart.getLegend().setTextSize(8f);
    }

    //SetTodayPriorityPieChart
    private void setTaskPriorityPieChart(int high, int medium, int low) {
        taskPriorityPieChart.setUsePercentValues(true);
        taskPriorityPieChart.getDescription().setEnabled(false);
        taskPriorityPieChart.setDrawHoleEnabled(true);
        taskPriorityPieChart.setHoleColor(Color.WHITE);
        taskPriorityPieChart.setTransparentCircleRadius(70f);
        taskPriorityPieChart.setRotationEnabled(false);

        ArrayList<PieEntry> values = new ArrayList<>();
        values.add(new PieEntry(high, "High"));
        values.add(new PieEntry(medium, "Medium"));
        values.add(new PieEntry(low, "Low"));

        PieDataSet dataSet = new PieDataSet(values, "");
        dataSet.setSliceSpace(2f);
        dataSet.setSelectionShift(3f);
        dataSet.setDrawValues(false);

        dataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        dataSet.setValueTextColor(Color.WHITE);
        taskPriorityPieChart.setData(data);
        taskPriorityPieChart.setDrawSliceText(false);
        taskPriorityPieChart.setDrawCenterText(true);
        taskPriorityPieChart.setCenterTextSize(10f);
        taskPriorityPieChart.setCenterText("Priority");
        taskPriorityPieChart.animateX(750);
        taskPriorityPieChart.invalidate();
        taskPriorityPieChart.getLegend().setTextSize(8f);
    }

    //Statistics RecycleView
    private void setStatisticsRecyclerView(ArrayList<StatisticsTaskItem> statisticsTaskItem) {
        statisticsTaskItemRecylerView.setHasFixedSize(true);
        statisticsTaskItemRecylerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        statisticsTaskItemAdapter = new StatisticsTaskItemAdapter(statisticsTaskItem, getApplicationContext());
        statisticsTaskItemRecylerView.setAdapter(statisticsTaskItemAdapter);
    }


    @Override
    public void onClick(View v) {
        if ((v.getId() == R.id.selectDateId)) {
            MonthYearPickerDialog pd = new MonthYearPickerDialog();
            pd.setListener(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, selectedYear);
                    calendar.set(Calendar.MONTH, selectedMonth - 1);
                    SimpleDateFormat month_date = new SimpleDateFormat("MMMM yyyy");
                    String currentMonth = month_date.format(calendar.getTime());
                    statisticsForMonth = selectedMonth;
                    statisticsForYear = selectedYear;
                    selectMonth.setText(currentMonth);

                    loadTaskData(31, 1, 31, statisticsForMonth, statisticsForYear);
                    setPieChart(31, 1, 31, statisticsForMonth, statisticsForYear);
                    setLineChart(31, 1, 31, totalTasksDataCount, completedTasksDataCount);
                    setBarChart(31, 1, 31, totalTasksDataCount, completedTasksDataCount);
                }
            });
            pd.show(getSupportFragmentManager(), "MonthYearPickerDialog");
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        radioButton = findViewById(checkedId);
        statisticsTitle = (String) radioButton.getText();
        if (statisticsTitle.equals("Last 7 Days")) {
            selectDate.setVisibility(View.GONE);
            last7DaysView.setVisibility(View.VISIBLE);
            int startDay = thisDay - 6;
            int endDay = thisDay;
            if (startDay < 1) {
                startDay = 1;
                endDay = 7 ;
            }
            loadTaskData(7, startDay, endDay, thisMonth, thisYear);
            setPieChart(7, startDay, endDay, thisMonth, thisYear);
            setLineChart(7, startDay, endDay, totalTasksDataCount, completedTasksDataCount);
            setBarChart(7, startDay, endDay, totalTasksDataCount, completedTasksDataCount);
        } else if (statisticsTitle.equals("Month")) {
            last7DaysView.setVisibility(View.GONE);
            selectDate.setVisibility(View.VISIBLE);
            loadTaskData(31, 1, 31, statisticsForMonth, statisticsForYear);
            setPieChart(31, 1, 31, statisticsForMonth, statisticsForYear);
            setLineChart(31, 1, 31, totalTasksDataCount, completedTasksDataCount);
            setBarChart(31, 1, 31, totalTasksDataCount, completedTasksDataCount);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishActivity(1);
    }

}
