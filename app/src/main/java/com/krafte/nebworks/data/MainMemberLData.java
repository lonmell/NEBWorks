package com.krafte.nebworks.data;

public class MainMemberLData {
    public static class MainMemberLData_list {
        String id;
        String join_date;
        String user_id;
        String user_name;
        String user_img;
        String recent_pay;

        public MainMemberLData_list(String id, String join_date, String user_id,
                                 String user_name, String user_img,String recent_pay) {
            this.id = id;
            this.join_date = join_date;
            this.user_id = user_id;
            this.user_name = user_name;
            this.user_img = user_img;
            this.recent_pay = recent_pay;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getJoin_date() {
            return join_date;
        }

        public void setJoin_date(String join_date) {
            this.join_date = join_date;
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

        public String getUser_img() {
            return user_img;
        }

        public void setUser_img(String user_img) {
            this.user_img = user_img;
        }

        public String getRecent_pay() {
            return recent_pay;
        }

        public void setRecent_pay(String recent_pay) {
            this.recent_pay = recent_pay;
        }
    }
}
