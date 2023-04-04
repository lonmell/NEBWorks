package com.krafte.nebworks.data;

public class WorkGetallData {
    public static class WorkGetallData_list {
        String task_month = "";
        String day = "";
        String id = "";
        String place_id = "";
        String kind = "";
        String title = "";
        String task_date = "";

        public WorkGetallData_list(String task_month, String day, String id, String place_id,
                                     String kind, String title, String task_date) {
            this.task_month = task_month;
            this.day = day;
            this.id = id;
            this.place_id = place_id;
            this.kind = kind;
            this.title = title;
            this.task_date = task_date;
        }

        public String getTask_month() {
            return task_month;
        }

        public void setTask_month(String task_month) {
            this.task_month = task_month;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPlace_id() {
            return place_id;
        }

        public void setPlace_id(String place_id) {
            this.place_id = place_id;
        }

        public String getKind() {
            return kind;
        }

        public void setKind(String kind) {
            this.kind = kind;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTask_date() {
            return task_date;
        }

        public void setTask_date(String task_date) {
            this.task_date = task_date;
        }
    }
}
