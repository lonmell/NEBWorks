package com.krafte.nebworks.data;

public class WorkStatusTapData {
    public static class WorkStatusTapData_list {
        String id = "";
        String place_id = "";
        String user_id = "";
        String name = "";
        String img_path = "";
        String kind = "";
        String jikgup = "";
        String join_date = "";
        String io_date = "";
        String io_time = "";


        public WorkStatusTapData_list(String id, String place_id, String user_id, String name, String img_path, String kind
                , String jikgup, String join_date, String io_date, String io_time) {
            this.id = id;
            this.place_id = place_id;
            this.user_id = user_id;
            this.name = name;
            this.img_path = img_path;
            this.kind = kind;
            this.jikgup = jikgup;
            this.join_date = join_date;
            this.io_date = io_date;
            this.io_time = io_time;
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

        public String getImg_path() {
            return img_path;
        }

        public void setImg_path(String img_path) {
            this.img_path = img_path;
        }

        public String getKind() {
            return kind;
        }

        public void setKind(String kind) {
            this.kind = kind;
        }

        public String getJikgup() {
            return jikgup;
        }

        public void setJikgup(String jikgup) {
            this.jikgup = jikgup;
        }

        public String getJoin_date() {
            return join_date;
        }

        public void setJoin_date(String join_date) {
            this.join_date = join_date;
        }

        public String getIo_date() {
            return io_date;
        }

        public void setIo_date(String io_date) {
            this.io_date = io_date;
        }

        public String getIo_time() {
            return io_time;
        }

        public void setIo_time(String io_time) {
            this.io_time = io_time;
        }
    }
}
