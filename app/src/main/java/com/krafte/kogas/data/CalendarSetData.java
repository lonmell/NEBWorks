package com.krafte.kogas.data;

import java.util.List;

public class CalendarSetData {
    public static class CalendarSetData_list {
        String day;
        String week;
        List<String> task;

        public CalendarSetData_list(String day, String week, List<String> task) {
            this.day = day;
            this.week = week;
            this.task = task;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public String getWeek() {
            return week;
        }

        public void setWeek(String week) {
            this.week = week;
        }

        public List<String> getTask() {
            return task;
        }

        public void setTask(List<String> task) {
            this.task = task;
        }
    }
}
