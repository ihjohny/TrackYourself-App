package com.example.ihjohny.trackyourself.data.db;

public class StatisticsTaskItem {
    private String taskName;
    private int totalTask;
    private int completedTask;

    public StatisticsTaskItem(String taskName, int totalTask, int completedTask) {
        this.taskName = taskName;
        this.totalTask = totalTask;
        this.completedTask = completedTask;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setTotalTask(int totalTask) {
        this.totalTask = totalTask;
    }

    public void setCompletedTask(int completedTask) {
        this.completedTask = completedTask;
    }

    public String getTaskName() {
        return taskName;
    }

    public int getTotalTask() {
        return totalTask;
    }

    public int getCompletedTask() {
        return completedTask;
    }

    public int getTaskPercentage()
    {
        if(totalTask==0 || completedTask==0) return 0;
        else{
            return (completedTask*100)/totalTask;
        }
    }
}
