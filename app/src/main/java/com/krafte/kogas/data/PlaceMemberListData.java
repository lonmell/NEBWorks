package com.krafte.kogas.data;

public class PlaceMemberListData {
    private static final String TAG = "WorkPlaceMemberListData";

    public static class PlaceMemberListData_list{
        private String id = null;
        private String name = null;
        private String account = null;
        private String employee_no = null;
        private String department = null;
        private String position = null;
        private String img_path = null;

        public PlaceMemberListData_list(String id, String name,String account, String employee_no,String department, String position,String img_path){
            this.id = id;
            this.name = name;
            this.account = account;
            this.employee_no = employee_no;
            this.department = department;
            this.position = position;
            this.img_path = img_path;
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

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getEmployee_no() {
            return employee_no;
        }

        public void setEmployee_no(String employee_no) {
            this.employee_no = employee_no;
        }

        public String getDepartment() {
            return department;
        }

        public void setDepartment(String department) {
            this.department = department;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getImg_path() {
            return img_path;
        }

        public void setImg_path(String img_path) {
            this.img_path = img_path;
        }
    }
}