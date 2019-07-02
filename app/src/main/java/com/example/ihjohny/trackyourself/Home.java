package com.example.ihjohny.trackyourself;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Home extends android.support.v4.app.Fragment implements View.OnClickListener {


    private ScrollView scrollView;

    private CheckBox nextTaskCheckBox;
    private TextView nextTaskText, nextTaskTitleText, nextTaskPriorityText, nextTaskTimeText, nextTaskDateText;
    private ImageView clockIcon, homeNextTasknotifOn, homeNextTasknotifOff;
    private LinearLayout nextTaskLinearLayout, noTaskLinearLayout, noPreviousTask, noUpcomingTask;
    private DatabaseHelper databaseHelper;
    private DataSet dataSet;

    private TextView todayDate, totalTasks, completedTasks, completedPercentage, priorityHigh, priorityMedium, priorityLow;
    private TimePicker timePicker;
    private DatePicker datePicker;

    private LinearLayout previousTasks, upcomingTasks, statistics, pinnedList;

    private PieChart taskCompletedPieChart, taskPriorityPieChart;
    private int dueCount = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        databaseHelper = new DatabaseHelper(getContext());
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        scrollView = (ScrollView) view.findViewById(R.id.homeScrollViewId);
        noTaskLinearLayout = (LinearLayout) view.findViewById(R.id.noTaskId);
        nextTaskLinearLayout = (LinearLayout) view.findViewById(R.id.nextTaskId);
        nextTaskCheckBox = (CheckBox) view.findViewById(R.id.homeNextTaskCheckBoxId);
        nextTaskTitleText = (TextView) view.findViewById(R.id.homeNextTaskTitleId);
        nextTaskTimeText = (TextView) view.findViewById(R.id.homeNextTaskTimeId);
        nextTaskPriorityText = (TextView) view.findViewById(R.id.homeNextTaskPriorityId);
        nextTaskDateText = (TextView) view.findViewById(R.id.homeNextTaskDateId);
        homeNextTasknotifOn = view.findViewById(R.id.homeNextTaskNotifOnId);
        homeNextTasknotifOff = view.findViewById(R.id.homeNextTaskNotifOffId);

        todayDate = (TextView) view.findViewById(R.id.todayDateId);

        taskCompletedPieChart = view.findViewById(R.id.taskCompletedPieChartId);
        taskPriorityPieChart = view.findViewById(R.id.taskPriorityPieChartId);
        statistics = view.findViewById(R.id.statisticsId);
        pinnedList = view.findViewById(R.id.pinnedListId);
        previousTasks = view.findViewById(R.id.previousTasksId);
        upcomingTasks = view.findViewById(R.id.upcomingTasksId);

        statistics.setOnClickListener(this);
        pinnedList.setOnClickListener(this);
        previousTasks.setOnClickListener(this);
        upcomingTasks.setOnClickListener(this);


        setScrollViewPosition();
        setNextTaskCheckBox();
        setNextTask();
        setYourDay();

        return view;
    }

    //scrollViewPosition
    private void setScrollViewPosition() {
        scrollView.scrollTo(0, 0);
        scrollView.setFocusableInTouchMode(true);
        scrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
    }

    //setNextTaskCheckBox
    private void setNextTaskCheckBox() {
        nextTaskCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                databaseHelper.makeCompleted(Integer.parseInt(dataSet.getId()));
                setNextTask();
                setYourDay();
            }
        });
    }

    //setNextTask
    private void setNextTask() {
        int temp = 9000000;
        dataSet = null;
        timePicker = new TimePicker(getContext());
        datePicker = new DatePicker(getContext());
        String currentHour = String.valueOf(timePicker.getCurrentHour());
        String currentMinute = String.valueOf(timePicker.getCurrentMinute());
        String currentDay = String.valueOf(datePicker.getDayOfMonth());
        String currentMonth = String.valueOf(datePicker.getMonth() + 1);
        String currentYear = String.valueOf(datePicker.getYear());

        Cursor cursor = databaseHelper.showDataForDateAndUpTime(currentHour, currentDay, currentMonth, currentYear);
        dueCount = 0;
        if (cursor.getCount() == 0) {
            nextTaskLinearLayout.setVisibility(LinearLayout.GONE);
            noTaskLinearLayout.setVisibility(LinearLayout.VISIBLE);
        } else {
            while (cursor.moveToNext()) {
                if (cursor.getString(9).equals("0")) {
                    if (currentHour.equals(cursor.getString(4)) && Integer.parseInt(currentMinute) > Integer.parseInt(cursor.getString(5))) {
                        continue;
                    }
                    int current = (60 * (Integer.parseInt(cursor.getString(4)))) + (Integer.parseInt(cursor.getString(5)));
                    if (current < temp) {
                        temp = current;
                        dataSet = new DataSet(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10));
                    }
                    dueCount++;
                }
            }
            if (dataSet != null) {
                noTaskLinearLayout.setVisibility(LinearLayout.GONE);
                nextTaskLinearLayout.setVisibility(LinearLayout.VISIBLE);
                nextTaskCheckBox.setChecked(false);
                nextTaskTitleText.setText(dataSet.getTaskName());
                nextTaskPriorityText.setText(dataSet.getPriority());
                // nextTaskTimeText.setText(dataSet.getHours() + " : " + dataSet.getMinutes());
                nextTaskTimeText.setText(ExtraFunction.setRemainingTime(getContext(), dataSet.getHours(), dataSet.getMinutes()));
                //  nextTaskDateText.setText(ExtraFunction.setDate(getContext(),Integer.parseInt(dataSet.getDays()),Integer.parseInt(dataSet.getMonths()),Integer.parseInt(dataSet.getYears())));
                if (dataSet.getNotif().equals("1")) {
                    homeNextTasknotifOn.setVisibility(View.VISIBLE);
                    homeNextTasknotifOff.setVisibility(View.GONE);
                } else {
                    homeNextTasknotifOn.setVisibility(View.GONE);
                    homeNextTasknotifOff.setVisibility(View.VISIBLE);
                }
            } else {
                nextTaskLinearLayout.setVisibility(LinearLayout.GONE);
                noTaskLinearLayout.setVisibility(LinearLayout.VISIBLE);
            }
        }
    }

    private void setYourDay() {
        int percentage = 0, completed = 0, due = 0, overdue = 0, high = 0, medium = 0, low = 0;
        datePicker = new DatePicker(getContext());
        String currentDay = String.valueOf(datePicker.getDayOfMonth());
        String currentMonth = String.valueOf(datePicker.getMonth() + 1);
        String currentYear = String.valueOf(datePicker.getYear());
        Cursor totalCursor = databaseHelper.showTotalTaskForDay(currentDay, currentMonth, currentYear);
        Cursor completedCursor = databaseHelper.showCompletedTaskForDay(currentDay, currentMonth, currentYear);
        Cursor highCursor = databaseHelper.showTaskForDayAndPriority("High", currentDay, currentMonth, currentYear);
        Cursor mediumCursor = databaseHelper.showTaskForDayAndPriority("Medium", currentDay, currentMonth, currentYear);
        Cursor lowCursor = databaseHelper.showTaskForDayAndPriority("Low", currentDay, currentMonth, currentYear);

        if (totalCursor.getCount() != 0)
            percentage = (100 * completedCursor.getCount()) / totalCursor.getCount();


        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, datePicker.getYear());
        calendar.set(Calendar.MONTH, datePicker.getMonth());
        calendar.set(Calendar.DATE, datePicker.getDayOfMonth());
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        todayDate.setText(currentDate);

        completed = completedCursor.getCount();
        due = dueCount;
        overdue = totalCursor.getCount() - (dueCount + completedCursor.getCount());
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

    //setTaskCompletedPieChart
    private void setTaskCompletedPieChart(int percentage, int completed, int due, int overdue) {
        taskCompletedPieChart.setUsePercentValues(true);
        taskCompletedPieChart.getDescription().setEnabled(false);
        //todayTaskPieChart.setExtraOffsets(5,10,5,5);
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

//        dataSet.setColors(int [] {Color.parseColor("#000000")},getContext());
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        dataSet.setValueTextColor(Color.WHITE);
        taskCompletedPieChart.setData(data);
        taskCompletedPieChart.setDrawSliceText(false);
        taskCompletedPieChart.setDrawCenterText(true);
        taskCompletedPieChart.setCenterTextSize(20f);
        taskCompletedPieChart.setCenterText(String.valueOf(percentage) + "%");
        taskCompletedPieChart.setCenterTextColor(ColorTemplate.MATERIAL_COLORS[0]);
        taskCompletedPieChart.animateX(750);
        taskCompletedPieChart.invalidate();
        taskCompletedPieChart.getLegend().setTextSize(8f);
    }

    //SetTodayPriorityPieChart
    private void setTaskPriorityPieChart(int high, int medium, int low) {
        taskPriorityPieChart.setUsePercentValues(true);
        taskPriorityPieChart.getDescription().setEnabled(false);
        //todayTaskPieChart.setExtraOffsets(5,10,5,5);
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

//        dataSet.setColors(int [] {Color.parseColor("#000000")},getContext());
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


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.statisticsId) {
            Intent intent = new Intent(getContext(), Statistics.class);
            intent.putExtra("Title", "Statistics");
            startActivityForResult(intent, 14);
        } else if (v.getId() == R.id.pinnedListId) {
            Intent intent = new Intent(getContext(), RemainingTasks.class);
            intent.putExtra("Title", "Pinned Tasks");
            startActivityForResult(intent, 15);
        } else if (v.getId() == R.id.previousTasksId) {
            Intent intent = new Intent(getContext(), RemainingTasks.class);
            intent.putExtra("Title", "Previous Tasks");
            startActivityForResult(intent, 12);
        } else if (v.getId() == R.id.upcomingTasksId) {
            Intent intent = new Intent(getContext(), RemainingTasks.class);
            intent.putExtra("Title", "Upcoming Tasks");
            startActivityForResult(intent, 13);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_layout, menu);
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.filterTasksBySortId).setVisible(false);
        menu.findItem(R.id.filterTasksTodayId).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.shareId) {
            //  Toast.makeText(CompletedActivity.this,"Share",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String shareBody = "Track YourSelf";
            String shareSub = "“Focus on being productive instead of busy.” ~ Tim Ferriss";
            intent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
            intent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(intent, "Share Using"));
            return true;
        }
        if (item.getItemId() == R.id.aboutId) {
            //   Toast.makeText(CompletedActivity.this,"about",Toast.LENGTH_SHORT).show();
            AlertDialog.Builder about = new AlertDialog.Builder(getContext());
            View view = getLayoutInflater().inflate(R.layout.about_dialog, null);
            about.setView(view);
            AlertDialog dialog = about.create();
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12 || requestCode == 13 || requestCode == 14 || requestCode == 15) //return from previous tasks
        {
            setNextTask();
            setYourDay();
        }
    }

}
