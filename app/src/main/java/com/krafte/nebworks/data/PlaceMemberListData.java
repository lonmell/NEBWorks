package com.krafte.nebworks.data;

public class PlaceMemberListData {
    private static final String TAG = "WorkPlaceMemberListData";

    public static class PlaceMemberListData_list{
        String id;
        String name;
        String phone;
        String gender;
        String img_path;
        String jumin;
        String join_date;
        String state;
        String jikgup;
        String pay;

        public PlaceMemberListData_list( String id, String name, String phone,
                String gender, String img_path, String jumin, String join_date, String state, String jikgup, String pay){
            this.id = id;
            this.name = name;
            this.phone = phone;
            this.gender = gender;
            this.img_path = img_path;
            this.jumin = jumin;
            this.join_date = join_date;
            this.state = state;
            this.jikgup = jikgup;
            this.pay = pay;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getImg_path() {
            return img_path;
        }

        public void setImg_path(String img_path) {
            this.img_path = img_path;
        }

        public String getJumin() {
            return jumin;
        }

        public void setJumin(String jumin) {
            this.jumin = jumin;
        }

        public String getJoin_date() {
            return join_date;
        }

        public void setJoin_date(String join_date) {
            this.join_date = join_date;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getJikgup() {
            return jikgup;
        }

        public void setJikgup(String jikgup) {
            this.jikgup = jikgup;
        }

        public String getPay() {
            return pay;
        }

        public void setPay(String pay) {
            this.pay = pay;
        }
    }
}