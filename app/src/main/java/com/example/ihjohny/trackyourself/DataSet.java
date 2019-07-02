package com.example.ihjohny.trackyourself;

public class DataSet {
    private String id;
    private String taskName;
    private String note;
    private String priority;
    private String hours;
    private String minutes;
    private String days;
    private String months;
    private String years;
    private String isCompleted;
    private String notif;

    public DataSet(String id, String taskName, String note, String priority, String hours, String minutes,String days,String months,String years,String isCompleted,String notif) {
        this.id = id;
        this.taskName = taskName;
        this.note = note;
        this.priority = priority;
        this.hours = hours;
        this.minutes = minutes;
        this.days=days;
        this.months=months;
        this.years=years;
        this.isCompleted=isCompleted;
        this.notif=notif;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getMinutes() {
        return minutes;
    }

    public void setMinutes(String minutes) {
        this.minutes = minutes;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getMonths() {
        return months;
    }

    public void setMonths(String months) {
        this.months = months;
    }

    public String getYears() {
        return years;
    }

    public void setYears(String years) {
        this.years = years;
    }

    public String getIsCompleted() { return isCompleted; }

    public void setIsCompleted(String isCompleted) { this.isCompleted = isCompleted; }

    public String getNotif() { return notif; }

    public void setNotif(String notif) {
        this.notif = notif;
    }
}
