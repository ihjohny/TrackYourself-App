package com.example.ihjohny.trackyourself;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class Today extends Fragment {

    private DatabaseHelper databaseHelper;
    private RecyclerView todayRecyclerView;
    private TaskRecycleAdapter todayAdapter;
    private LinearLayoutManager todayLayoutManager;
    private TimePicker timePicker;
    private DatePicker datePicker;
    private LinearLayout noTaskView;
    private TaskDetailsBottomSheet taskDetailsBottomSheet = new TaskDetailsBottomSheet();
    ArrayList<DataSet> dataList = new ArrayList<>();

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
        View view = inflater.inflate(R.layout.fragment_today, container, false);
        this.getActivity().setTitle("Today");

        noTaskView = (LinearLayout) view.findViewById(R.id.noTaskViewId);
        todayRecyclerView = (RecyclerView) view.findViewById(R.id.taskRecyclerViewId);

        setView();
        return view;
    }

    //loadData
    private void loadData() {
        datePicker = new DatePicker(getContext());
        String currentDay = String.valueOf(datePicker.getDayOfMonth());
        String currentMonth = String.valueOf(datePicker.getMonth() + 1);
        String currentYear = String.valueOf(datePicker.getYear());

        int showCompleted=readSettings("showCompletedOnToday","1");
        int hideCompleted=readSettings("hideCompletedOnToday","0");
        int sort=readSettings("sortByOnToday","0");;

        Cursor cursor = databaseHelper.showDataForDate(hideCompleted, showCompleted, currentDay, currentMonth, currentYear,sort);  //00 for Remaining 11 for Completed 01 for both
        dataList.clear();
        if (cursor.getCount() == 0) {
            noTaskView.setVisibility(LinearLayout.VISIBLE);
        } else {
            noTaskView.setVisibility(LinearLayout.GONE);
            while (cursor.moveToNext()) {
                dataList.add(new DataSet(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10)));
            }
        }
    }

    //setView
    private void setView() {
        loadData();

        todayRecyclerView.setHasFixedSize(true);
        todayLayoutManager = new LinearLayoutManager(getContext());
        todayAdapter = new TaskRecycleAdapter(getContext(), dataList, "Today");
        todayRecyclerView.setLayoutManager(todayLayoutManager);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder dragged, RecyclerView.ViewHolder target) {
/*                int position_dragged = dragged.getAdapterPosition();
                int position_target = target.getAdapterPosition();


                DataSet data_dragged = dataList.get(position_dragged);
                DataSet data_target = dataList.get(position_target);
                databaseHelper.updateData(data_target.getId(), data_dragged.getTaskName(), data_dragged.getNote(), data_dragged.getPriority(), Integer.parseInt(data_dragged.getHours()), Integer.parseInt(data_dragged.getMinutes()), Integer.parseInt(data_dragged.getDays()), Integer.parseInt(data_dragged.getMonths()), Integer.parseInt(data_dragged.getYears()), Integer.parseInt(data_dragged.getIsCompleted()), Integer.parseInt(data_dragged.getNotif()));
                databaseHelper.updateData(data_dragged.getId(), data_target.getTaskName(), data_target.getNote(), data_target.getPriority(), Integer.parseInt(data_target.getHours()), Integer.parseInt(data_target.getMinutes()), Integer.parseInt(data_target.getDays()), Integer.parseInt(data_target.getMonths()), Integer.parseInt(data_target.getYears()), Integer.parseInt(data_target.getIsCompleted()), Integer.parseInt(data_target.getNotif()));

                ExtraFunction.setNotif(getContext(), Integer.parseInt(data_dragged.getId()), Integer.parseInt(data_target.getMinutes()), Integer.parseInt(data_target.getHours()), Integer.parseInt(data_target.getDays()), Integer.parseInt(data_target.getMonths()), Integer.parseInt(data_target.getYears()));
                ExtraFunction.setNotif(getContext(), Integer.parseInt(data_target.getId()), Integer.parseInt(data_dragged.getMinutes()), Integer.parseInt(data_dragged.getHours()), Integer.parseInt(data_dragged.getDays()), Integer.parseInt(data_dragged.getMonths()), Integer.parseInt(data_dragged.getYears()));

                loadData();
                //  todayAdapter.notifyItemMoved(position_dragged, position_target);
                todayAdapter.notifyDataSetChanged();*/

                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                databaseHelper.makeCompleted((Integer) viewHolder.itemView.getTag());
                loadData();
                todayAdapter.notifyDataSetChanged();
            }
        }).attachToRecyclerView(todayRecyclerView);

        todayRecyclerView.setAdapter(todayAdapter);

        todayAdapter.setOnItemClickListener(new TaskRecycleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String id = dataList.get(position).getId();
                Bundle bundle = new Bundle();
                bundle.putString("IntentTitle", "Today");
                taskDetailsBottomSheet.setArguments(bundle);
                taskDetailsBottomSheet.show(getFragmentManager(), id);
                bottomSheetButton();
            }

            @Override
            public void onChecked(int position) {
                {
                    databaseHelper.makeCompleted(Integer.parseInt(dataList.get(position).getId()));
                    loadData();
                    todayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNotifOnClick(int position) {
                databaseHelper.makeNotif(Integer.parseInt(dataList.get(position).getId()), 0);
                loadData();
                todayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNotifOffClick(int position) {
                databaseHelper.makeNotif(Integer.parseInt(dataList.get(position).getId()), 1);
                loadData();
                todayAdapter.notifyDataSetChanged();
            }
        });
    }

    public void bottomSheetButton() {
        taskDetailsBottomSheet.setOnButtonClickListener(new TaskDetailsBottomSheet.TaskDetailsBottomShitListener() {

           @Override
            public void onPinOn(int id) {
                databaseHelper.makePin(id,1);
               loadData();
               todayAdapter.notifyDataSetChanged();
              // Toast.makeText(getContext(),"this is test pin on clicked "+id ,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPinOff(int id) {
                databaseHelper.makePin(id,0);
                loadData();
                todayAdapter.notifyDataSetChanged();
              //  Toast.makeText(getContext(),"this is test pin off clicked "+id ,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteClick(int id) {
                databaseHelper.deleteData(String.valueOf(id));
                loadData();
                todayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCompletedClick(int id) {
                databaseHelper.makeCompleted(Integer.parseInt(String.valueOf(id)));
                loadData();
                todayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onUndoClick(int id) {
                databaseHelper.makeInCompleted(Integer.parseInt(String.valueOf(id)));
                loadData();
                todayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onInitClick(int id) {

            }

            @Override
            public void onEditClick(int id) {
                String title = "Edit Task";
                String completed = "0";
                String idd = String.valueOf(id);
                Intent intent = new Intent(getContext(), TaskDialog.class);
                intent.putExtra("IntentTitle", "Home");
                intent.putExtra("Title", title);
                intent.putExtra("Id", idd);
                intent.putExtra("Completed", completed);
                startActivityForResult(intent, 5);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_layout, menu);
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.filterTasksBySortId).setVisible(true);
        menu.findItem(R.id.filterTasksTodayId).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.sortByName) {

            writeSettings("sortByOnToday", "2");
            setView();
            return true;
        }
        if (item.getItemId() == R.id.sortByTaskTime) {

            writeSettings("sortByOnToday", "1");
            setView();
            return true;
        }
        if (item.getItemId() == R.id.sortByAddedTime) {

            writeSettings("sortByOnToday", "0");
            setView();
            return true;
        }
        if (item.getItemId() == R.id.showCompleted) {

            writeSettings("showCompletedOnToday", "1");
            writeSettings("hideCompletedOnToday", "0");
            setView();
            return true;
        }
        if (item.getItemId() == R.id.hideCompleted) {
            writeSettings("showCompletedOnToday", "0");
            writeSettings("hideCompletedOnToday", "0");
            setView();
            return true;
        }
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

    public void writeSettings(String key, String value) {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("Settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public int readSettings(String key, String defValue) {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("Settings", MODE_PRIVATE);
        return Integer.valueOf(sharedPreferences.getString(key, defValue));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5) {
            if (resultCode == Activity.RESULT_OK) {
                loadData();
                todayAdapter.notifyDataSetChanged();
            }
        }
    }

}
