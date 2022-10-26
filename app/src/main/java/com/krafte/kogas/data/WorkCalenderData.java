package com.krafte.kogas.data;

public class WorkCalenderData {
    private static final String TAG = "WorkCalenderData";

    public static class WorkCalenderData_list {
        String ym = "";
        String Sun = "";
        String Mon = "";
        String Tue = "";
        String Wed = "";
        String Thu = "";
        String Fri = "";
        String Sat = "";

        public WorkCalenderData_list(String ym, String Sun, String Mon, String Tue, String Wed,
                                     String Thu, String Fri, String Sat) {
            this.ym = ym;
            this.Sun = Sun;
            this.Mon = Mon;
            this.Tue = Tue;
            this.Wed = Wed;
            this.Thu = Thu;
            this.Fri = Fri;
            this.Sat = Sat;
        }

        public String getYm() {
            return ym;
        }

        public void setYm(String ym) {
            this.ym = ym;
        }

        public String getSun() {
            return Sun;
        }

        public void setSun(String sun) {
            Sun = sun;
        }

        public String getMon() {
            return Mon;
        }

        public void setMon(String mon) {
            Mon = mon;
        }

        public String getTue() {
            return Tue;
        }

        public void setTue(String tue) {
            Tue = tue;
        }

        public String getWed() {
            return Wed;
        }

        public void setWed(String wed) {
            Wed = wed;
        }

        public String getThu() {
            return Thu;
        }

        public void setThu(String thu) {
            Thu = thu;
        }

        public String getFri() {
            return Fri;
        }

        public void setFri(String fri) {
            Fri = fri;
        }

        public String getSat() {
            return Sat;
        }

        public void setSat(String sat) {
            Sat = sat;
        }
    }
}
