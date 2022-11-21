package com.krafte.nebworks.data;

public class MemberInoutData {
    public static class MemberInoutData_list {
        String id;
        String place_id;
        String user_id;
        String name;
        String yoil;
        String io_day;
        String chulgeun;
        String toegeun;

        public MemberInoutData_list(String id, String place_id, String user_id, String name
                , String yoil, String io_day, String chulgeun, String toegeun) {
            this.id         = id;
            this.place_id   = place_id;
            this.user_id    = user_id;
            this.name       = name;
            this.yoil       = yoil;
            this.io_day     = io_day;
            this.chulgeun   = chulgeun;
            this.toegeun    = toegeun;
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

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getYoil() {
            return yoil;
        }

        public void setYoil(String yoil) {
            this.yoil = yoil;
        }

        public String getIo_day() {
            return io_day;
        }

        public void setIo_day(String io_day) {
            this.io_day = io_day;
        }

        public String getChulgeun() {
            return chulgeun;
        }

        public void setChulgeun(String chulgeun) {
            this.chulgeun = chulgeun;
        }

        public String getToegeun() {
            return toegeun;
        }

        public void setToegeun(String toegeun) {
            this.toegeun = toegeun;
        }
    }
}
