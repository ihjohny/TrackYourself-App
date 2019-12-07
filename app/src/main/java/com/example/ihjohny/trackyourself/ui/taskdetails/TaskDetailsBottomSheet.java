package com.example.ihjohny.trackyourself.ui.taskdetails;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ihjohny.trackyourself.R;
import com.example.ihjohny.trackyourself.data.db.DatabaseHelper;
import com.example.ihjohny.trackyourself.utils.ExtraFunction;

import java.text.DateFormat;
import java.util.Calendar;

public class TaskDetailsBottomSheet extends BottomSheetDialogFragment {

    private DatabaseHelper databaseHelper;
    private TaskDetailsBottomShitListener Listener;
    private TextView time, date, priority, name, note;
    private Button completed, undo, init, edit, delete, pin;
    private RelativeLayout banner;
    private ImageView pinOn, pinOff;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.task_details_bottom_sheet, container, false);

        databaseHelper = new DatabaseHelper(getContext());
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        name = v.findViewById(R.id.bottomTaskNameId);
        pinOn = v.findViewById(R.id.bottomPinOnId);
        pinOff = v.findViewById(R.id.bottomPinOffId);
        time = v.findViewById(R.id.bottomTaskTimeId);
        date = v.findViewById(R.id.bottomTaskDateId);
        priority = v.findViewById(R.id.bottomTaskPriorityId);
        note = v.findViewById(R.id.bottomTaskNoteId);
        completed = v.findViewById(R.id.taskCompletedButtonId);
        undo = v.findViewById(R.id.taskUndoButtonId);
        init = v.findViewById(R.id.taskInitButtonId);
        edit = v.findViewById(R.id.taskEditButtonId);
        delete = v.findViewById(R.id.taskDeleteButtonId);
        pin = v.findViewById(R.id.pinOffButtonId);
        banner = v.findViewById(R.id.bottomTaskBannerId);

        String intentTitle = getArguments().getString("IntentTitle");

        if (intentTitle.equals("Today") || intentTitle.equals("Calendar") || intentTitle.equals("Pinned Tasks")) {
            banner.setBackgroundColor(Color.parseColor("#4CAF50"));
        } else if (intentTitle.equals("Previous Tasks")) {
            banner.setBackgroundColor(Color.parseColor("#F44336"));
        } else if (intentTitle.equals("Upcoming Tasks")) {
            banner.setBackgroundColor(Color.parseColor("#FFC107"));
        }

        Cursor cursor = databaseHelper.showTaskForId(getTag());
        while (cursor.moveToNext()) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, Integer.parseInt(cursor.getString(8)));
            calendar.set(Calendar.MONTH, Integer.parseInt(cursor.getString(7)) - 1);
            calendar.set(Calendar.DATE, Integer.parseInt(cursor.getString(6)));
            String Date = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());

            name.setText(cursor.getString(1));
            if (intentTitle.equals("Today")) {
                if (ExtraFunction.setRemainingTime(getContext(), cursor.getString(4), cursor.getString(5)).toString().equals("Overdue"))
                    time.setText(ExtraFunction.setTime(cursor.getString(4), cursor.getString(5)) + "(Overdue)");
                else
                    time.setText(ExtraFunction.setRemainingTime(getContext(), cursor.getString(4), cursor.getString(5)));
            } else
                time.setText(ExtraFunction.setTime(cursor.getString(4), cursor.getString(5)));


            //     date.setText(ExtraFunction.setDate(getContext(),,,);
/*            if(!cursor.getString(11).equals("1"))
                date.setText(Date);
            else
                date.setText("Pinned");*/

            priority.setText(cursor.getString(3));
            note.setText(cursor.getString(2));
            //   note.setMovementMethod(new ScrollingMovementMethod());

            if (cursor.getString(9).equals("1") && !intentTitle.equals("Pinned Tasks")) {
                undo.setVisibility(View.VISIBLE);
                completed.setVisibility(View.GONE);
                init.setVisibility(View.GONE);
                delete.setVisibility(View.VISIBLE);
                pin.setVisibility(View.GONE);
            } else if (intentTitle.equals("Pinned Tasks")) {
                init.setVisibility(View.VISIBLE);
                completed.setVisibility(View.GONE);
                undo.setVisibility(View.GONE);
                delete.setVisibility(View.GONE);
                pin.setVisibility(View.VISIBLE);

            }
            if (cursor.getString(11).equals("1")) {
                pinOn.setVisibility(View.VISIBLE);
                pinOff.setVisibility(View.GONE);
            } else {
                pinOn.setVisibility(View.GONE);
                pinOff.setVisibility(View.VISIBLE);
            }
        }

        pinOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pinOff.setVisibility(View.VISIBLE);
                pinOn.setVisibility(View.GONE);
                Listener.onPinOff(Integer.parseInt(getTag()));
                dismiss();
            }
        });

        pinOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pinOn.setVisibility(View.VISIBLE);
                pinOff.setVisibility(View.GONE);
                Listener.onPinOn(Integer.parseInt(getTag()));
                dismiss();
            }
        });

        completed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Listener.onCompletedClick(Integer.parseInt(getTag()));
                dismiss();
            }
        });

        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Listener.onUndoClick(Integer.parseInt(getTag()));
                dismiss();
            }
        });

        init.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Listener.onInitClick(Integer.parseInt(getTag()));
                dismiss();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Listener.onDeleteClick(Integer.parseInt(getTag()));
                dismiss();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Listener.onEditClick(Integer.parseInt(getTag()));
                dismiss();
            }
        });

        pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pinOff.setVisibility(View.VISIBLE);
                pinOn.setVisibility(View.GONE);
                Listener.onPinOff(Integer.parseInt(getTag()));
                dismiss();
            }
        });

        return v;
    }

    public void setOnButtonClickListener(TaskDetailsBottomShitListener listener) {
        this.Listener = listener;
    }

    public interface TaskDetailsBottomShitListener {
        void onPinOn(int id);

        void onPinOff(int id);

        void onDeleteClick(int id);

        void onCompletedClick(int id);

        void onUndoClick(int id);

        void onInitClick(int id);

        void onEditClick(int id);
    }
}
