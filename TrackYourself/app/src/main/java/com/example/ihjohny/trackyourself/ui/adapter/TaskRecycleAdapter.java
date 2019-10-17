package com.example.ihjohny.trackyourself.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ihjohny.trackyourself.R;
import com.example.ihjohny.trackyourself.data.db.model.Task;
import com.example.ihjohny.trackyourself.utils.ExtraFunction;

import java.util.ArrayList;

public class TaskRecycleAdapter extends RecyclerView.Adapter<TaskRecycleAdapter.TaskViewHolder>{

    private ArrayList<Task> dataList=new ArrayList<>();
    private Context context;
    private OnItemClickListener listener;
    private String intentTitle;

    public interface OnItemClickListener
    {
        void onItemClick(int position);
        void onChecked(int position);
        void onNotifOnClick(int position);
        void onNotifOffClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        this.listener=listener;
    }

    public TaskRecycleAdapter(Context context, ArrayList<Task> dataList, String intentTitle) {
        this.dataList = dataList;
        this.context = context;
        this.intentTitle=intentTitle;
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.task_view,parent,false);
        TaskViewHolder holder=new TaskViewHolder(view);
        return holder;
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder
    {
        LinearLayout taskView;
        TextView taskTitle,taskTime,taskDate,priority;
        CheckBox checkBox;
        View bandage;
        ImageView notifOn,notifOff,repeat;
        public TaskViewHolder(View itemView) {
            super(itemView);
            taskView=itemView.findViewById(R.id.taskViewId);
            bandage=itemView.findViewById(R.id.bandageId);
            checkBox=itemView.findViewById(R.id.checkBoxId);
            taskTitle= itemView.findViewById(R.id.taskTitleId);
            notifOn=itemView.findViewById(R.id.taskNotifOn);
            notifOff=itemView.findViewById(R.id.taskNotifOff);
            taskTime=itemView.findViewById(R.id.taskTimeId);
            taskDate=itemView.findViewById(R.id.taskDateID);
            priority=itemView.findViewById(R.id.taskPriorityId);
            repeat=itemView.findViewById(R.id.repeatId);

            taskView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null)
                    {
                        int position=getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION)
                        {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION)
                    {
                        listener.onChecked(position);
                        // if(!intentTitle.equals("Calendar"))checkBox.setChecked(false);
                    }

                }
            });


/*            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(listener!=null)
                    {
                        int position=getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION)
                        {
                            listener.onChecked(position,b);
                           // if(!intentTitle.equals("Calendar"))checkBox.setChecked(false);
                        }
                    }
                }
            });*/

            notifOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null)
                    {
                        // notifyDataSetChanged();
                        int position=getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION)
                        {
                            listener.onNotifOnClick(position);
                        }
                    }
                }
            });

            notifOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null)
                    {
                        //  notifyDataSetChanged();
                        int position=getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION)
                        {
                            listener.onNotifOffClick(position);
                        }
                    }
                }
            });
        }

    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        Task currentSet=dataList.get(position);

        if(currentSet.getIsCompleted().equals("1") && !intentTitle.equals("Pinned Tasks")) {
            holder.taskTitle.setPaintFlags(holder.taskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.bandage.setVisibility(View.GONE);
            holder.checkBox.setChecked(true);
            holder.checkBox.setClickable(false);
            holder.taskTitle.setTextColor(Color.parseColor("#808080"));
        }
        else {
            holder.taskTitle.setPaintFlags(0);
            holder.bandage.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(false);
            holder.checkBox.setClickable(true);
            holder.taskTitle.setTextColor(Color.parseColor("#000000"));
        }
        if(intentTitle.equals("Pinned Tasks")){
            holder.bandage.setVisibility(View.GONE);
            holder.checkBox.setVisibility(View.GONE);
            holder.repeat.setVisibility(View.VISIBLE);
        }
        holder.taskTitle.setText(currentSet.getTaskName());
        if(intentTitle.equals("Today") && ExtraFunction.setRemainingTime(context,currentSet.getHours(),currentSet.getMinutes()).toString().equals("Overdue") && currentSet.getIsCompleted().equals("0"))
        {
            holder.taskTime.setText("Overdue");
        }
        else
           holder.taskTime.setText(ExtraFunction.setTime(currentSet.getHours(),currentSet.getMinutes()));

        if(intentTitle.equals("Today"))
            holder.taskDate.setText("");
        else
            holder.taskDate.setText(ExtraFunction.setDate(context,Integer.parseInt(currentSet.getDays()),Integer.parseInt(currentSet.getMonths()),Integer.parseInt(currentSet.getYears())));
        holder.priority.setText(currentSet.getPriority());

        if(currentSet.getNotif().equals("1") && ( intentTitle.equals("Today") || intentTitle.equals("Upcoming Calendar") || intentTitle.equals("Pinned Calendar") ))
        {
            holder.notifOn.setVisibility(View.VISIBLE);
            holder.notifOff.setVisibility(View.GONE);
        }
        else if(currentSet.getNotif().equals("0") &&  (intentTitle.equals("Today") || intentTitle.equals("Upcoming Calendar") || intentTitle.equals("Pinned Calendar") ))
        {
            holder.notifOn.setVisibility(View.GONE);
            holder.notifOff.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.notifOn.setVisibility(View.GONE);
            holder.notifOff.setVisibility(View.GONE);
        }

        if(currentSet.getHours().equals("24"))
            holder.taskTime.setText("Any Time");

        holder.itemView.setTag(Integer.parseInt(currentSet.getId()));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
