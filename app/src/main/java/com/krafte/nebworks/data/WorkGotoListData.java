package com.krafte.nebworks.data;

public class WorkGotoListData {

    public static class WorkGotoListData_list {
        String day = "";
        String yoil = "";
        String in_time = "";
        String out_time = "";
        String workdiff = "";
        String state = "";
        String sieob1 = "";
        String sieob2 = "";
        String jongeob1 = "";
        String jongeob2 = "";
        String vaca_accept = "";

        public WorkGotoListData_list( String day, String yoil, String in_time,
                String out_time, String workdiff, String state, String sieob1, String sieob2,
                String jongeob1, String jongeob2, String vaca_accept) {
            this.day = day;
            this.yoil = yoil;
            this.in_time = in_time;
            this.out_time = out_time;
            this.workdiff = workdiff;
            this.state = state;
            this.sieob1 = sieob1;
            this.sieob2 = sieob2;
            this.jongeob1 = jongeob1;
            this.jongeob2 = jongeob2;
            this.vaca_accept = vaca_accept;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public String getYoil() {
            return yoil;
        }

        public void setYoil(String yoil) {
            this.yoil = yoil;
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

        public String getWorkdiff() {
            return workdiff;
        }

        public void setWorkdiff(String workdiff) {
            this.workdiff = workdiff;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getSieob1() {
            return sieob1;
        }

        public void setSieob1(String sieob1) {
            this.sieob1 = sieob1;
        }

        public String getSieob2() {
            return sieob2;
        }

        public void setSieob2(String sieob2) {
            this.sieob2 = sieob2;
        }

        public String getJongeob1() {
            return jongeob1;
        }

        public void setJongeob1(String jongeob1) {
            this.jongeob1 = jongeob1;
        }

        public String getJongeob2() {
            return jongeob2;
        }

        public void setJongeob2(String jongeob2) {
            this.jongeob2 = jongeob2;
        }

        public String getVaca_accept() {
            return vaca_accept;
        }

        public void setVaca_accept(String vaca_accept) {
            this.vaca_accept = vaca_accept;
        }
    }
}