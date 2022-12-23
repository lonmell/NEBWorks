package com.krafte.nebworks.data;

public class WorkPlaceEmloyeeNotifyData {
    private static final String TAG = "WorkPlaceEmloyeeNotifyData";

    public static class WorkPlaceEmloyeeNotifyData_list {
        String id        = "";
        String push_date = "";
        String push_time = "";
        String user_id   = "";
        String img_path  = "";
        String title     = "";
        String contents  = "";
        String read_yn   = "";

        public WorkPlaceEmloyeeNotifyData_list(String id, String push_date, String push_time, String user_id,
                String img_path, String title, String contents, String read_yn) {
            super();
            this.id = id;
            this.push_date = push_date;
            this.push_time = push_time;
            this.user_id = user_id;
            this.img_path = img_path;
            this.title = title;
            this.contents = contents;
            this.read_yn = read_yn;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPush_date() {
            return push_date;
        }

        public void setPush_date(String push_date) {
            this.push_date = push_date;
        }

        public String getPush_time() {
            return push_time;
        }

        public void setPush_time(String push_time) {
            this.push_time = push_time;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getImg_path() {
            return img_path;
        }

        public void setImg_path(String img_path) {
            this.img_path = img_path;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContents() {
            return contents;
        }

        public void setContents(String contents) {
            this.contents = contents;
        }

        public String getRead_yn() {
            return read_yn;
        }

        public void setRead_yn(String read_yn) {
            this.read_yn = read_yn;
        }
    }

}
