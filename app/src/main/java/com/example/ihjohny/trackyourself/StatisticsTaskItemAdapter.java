package com.example.ihjohny.trackyourself;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class StatisticsTaskItemAdapter extends RecyclerView.Adapter<StatisticsTaskItemAdapter.TaskItemViewHolder> {

    private ArrayList<StatisticsTaskItem> taskItem=new ArrayList<>();
    private Context context;

    public StatisticsTaskItemAdapter(ArrayList<StatisticsTaskItem> taskItem, Context context) {
        this.taskItem = taskItem;
        this.context = context;
    }

    public class TaskItemViewHolder extends RecyclerView.ViewHolder{

        private ImageView icon;
        private TextView title,percentage,total,completed;
        private ProgressBar progressBar;

        public TaskItemViewHolder(View itemView) {
            super(itemView);
            icon=itemView.findViewById(R.id.statisticsTaskIconId);
            title=itemView.findViewById(R.id.statisticsTaskTitleId);
            percentage=itemView.findViewById(R.id.statisticsTaskPercentageId);
            total=itemView.findViewById(R.id.statisticsTaskTotalId);
            completed=itemView.findViewById(R.id.statisticsTaskCompletedId);
            progressBar=itemView.findViewById(R.id.statisticsTaskProgressBarId);
        }
    }

    @Override
    public TaskItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.statistics_task_view,parent,false);
        TaskItemViewHolder taskItemViewHolder=new TaskItemViewHolder(view);
        return taskItemViewHolder;
    }

    @Override
    public void onBindViewHolder(TaskItemViewHolder holder, int position) {
       StatisticsTaskItem statisticsTaskItem= taskItem.get(position);
       holder.title.setText(statisticsTaskItem.getTaskName());
       holder.percentage.setText(String.valueOf(statisticsTaskItem.getTaskPercentage())+"%");
       holder.progressBar.setProgress(statisticsTaskItem.getTaskPercentage());
       holder.total.setText("Total : "+String.valueOf(statisticsTaskItem.getTotalTask()));
       holder.completed.setText("Completed : "+String.valueOf(statisticsTaskItem.getCompletedTask()));
    }

    @Override
    public int getItemCount() {
        return taskItem.size();
    }

}
