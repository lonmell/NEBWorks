package com.krafte.nebworks.data;

public class WorkGotoListData {

    public static class WorkGotoListData_list {
        String day = "";
        String day_off = "";
        String in_time = "";
        String out_time = "";
        String late_time = "";
        String working_time = "";

        public WorkGotoListData_list(String day,
                String day_off,
                String in_time,
                String out_time,
                String late_time,
                String working_time) {
            this.day = day;
            this.day_off = day_off;
            this.in_time = in_time;
            this.out_time = out_time;
            this.late_time = late_time;
            this.working_time = working_time;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public String getDay_off() {
            return day_off;
        }

        public void setDay_off(String day_off) {
            this.day_off = day_off;
        }

        public String getIn_time() {
            return in_time;
        }

        public void setIn_time(String in_time) {
            this.in_time = in_time;
        }

        public String getOut_time() {
            return out_time;
        }

        public void setOut_time(String out_time) {
            this.out_time = out_time;
        }

        public String getLate_time() {
            return late_time;
        }

        public void setLate_time(String late_time) {
            this.late_time = late_time;
        }

        public String getWorking_time() {
            return working_time;
        }

        public void setWorking_time(String working_time) {
            this.working_time = working_time;
        }
    }
}