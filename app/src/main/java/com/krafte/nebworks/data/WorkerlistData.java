package com.krafte.nebworks.data;

public class WorkerlistData {
    private static final String TAG = "WorkerlistData";

    public static class WorkerlistData_list {
        String user_id;
        String user_name;
        String img_path;
        String jikgup;
        String worktime;
        String workyoil;

        public WorkerlistData_list(String user_id, String user_name, String img_path,
                String jikgup, String worktime, String workyoil) {
            this.user_id = user_id;
            this.user_name = user_name;
            this.img_path = img_path;
            this.jikgup = jikgup;
            this.worktime = worktime;
            this.workyoil = workyoil;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getImg_path() {
            return img_path;
        }

        public void setImg_path(String img_path) {
            this.img_path = img_path;
        }

        public String getJikgup() {
            return jikgup;
        }

        public void setJikgup(String jikgup) {
            this.jikgup = jikgup;
        }

        public String getWorktime() {
            return worktime;
        }

        public void setWorktime(String worktime) {
            this.worktime = worktime;
        }

        public String getWorkyoil() {
            return workyoil;
        }

        public void setWorkyoil(String workyoil) {
            this.workyoil = workyoil;
        }
    }
}
