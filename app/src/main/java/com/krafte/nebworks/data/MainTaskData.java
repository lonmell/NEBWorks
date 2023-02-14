package com.krafte.nebworks.data;

public class MainTaskData {
    public static class MainTaskData_list {
        String title;
        String end_date;
        String end_hour;
        String end_min;

        public MainTaskData_list(String title, String end_date, String end_hour, String end_min) {
            this.title    = title;
            this.end_date = end_date;
            this.end_hour = end_hour;
            this.end_min  = end_min;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getEnd_date() {
            return end_date;
        }

        public void setEnd_date(String end_date) {
            this.end_date = end_date;
        }

        public String getEnd_hour() {
            return end_hour;
        }

        public void setEnd_hour(String end_hour) {
            this.end_hour = end_hour;
        }

        public String getEnd_min() {
            return end_min;
        }

        public void setEnd_min(String end_min) {
            this.end_min = end_min;
        }
    }
}
