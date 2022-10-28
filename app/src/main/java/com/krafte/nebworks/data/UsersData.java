package com.krafte.nebworks.data;

import java.util.List;

public class UsersData {
    public static class UsersData_list {
        String user_id;
        String user_name;
        String img_path;

        public UsersData_list(String user_id, String user_name, String img_path) {
            this.user_id = user_id;
            this.user_name = user_name;
            this.img_path = img_path;

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
    }
}
