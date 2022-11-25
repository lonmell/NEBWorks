package com.krafte.nebworks.data;

public class WorkPlaceMemberListData {
    private static final String TAG = "WorkPlaceMemberListData";

    public static class WorkPlaceMemberListData_list{
        String id;
        String place_name;
        String account;
        String name;
        String phone;
        String gender;
        String img_path;
        String jumin;
        String kind;
        String join_date;
        String state;
        String jikgup;
        String pay;
        String worktime;


        public WorkPlaceMemberListData_list( String id, String place_name, String account, String name, String phone,
                  String gender, String img_path, String jumin, String kind, String join_date, String state, String jikgup, String pay, String worktime){
            this.id = id;
            this.place_name = place_name;
            this.account = account;
            this.name = name;
            this.phone = phone;
            this.gender = gender;
            this.img_path = img_path;
            this.jumin = jumin;
            this.kind = kind;
            this.join_date = join_date;
            this.state = state;
            this.jikgup = jikgup;
            this.pay = pay;
            this.worktime = worktime;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPlace_name() {
            return place_name;
        }

        public void setPlace_name(String place_name) {
            this.place_name = place_name;
        }

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
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

        public String getKind() {
            return kind;
        }

        public void setKind(String kind) {
            this.kind = kind;
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

        public String getWorktime() {
            return worktime;
        }

        public void setWorktime(String worktime) {
            this.worktime = worktime;
        }
    }
}
