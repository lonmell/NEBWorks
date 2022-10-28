package com.krafte.nebworks.data;

public class WorkPlaceMemberListData {
    private static final String TAG = "WorkPlaceMemberListData";

    public static class WorkPlaceMemberListData_list{
        String id = null;
        String name = null;
        String kind = null;
        String account = null;
        String employee_no = null;
        String department = null;
        String position = null;
        String img_path = null;


        public WorkPlaceMemberListData_list(String id, String name, String kind, String account,
                String employee_no, String department, String position, String img_path){
            this.id = id;
            this.name = name;
            this.kind = kind;
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

        public String getKind() {
            return kind;
        }

        public void setKind(String kind) {
            this.kind = kind;
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
