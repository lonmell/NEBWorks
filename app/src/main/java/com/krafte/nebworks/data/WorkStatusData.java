package com.krafte.nebworks.data;


public class WorkStatusData {
//    $t->id 		     = $row->id;                // user_id
//    $t->name 		   = $row->name;              // 이름
//    $t->kind       = $row->kind;              // 0: 정직원, 1: 협력업체
//    $t->img_path 	 = $row->img_path;          // 이미지 경로
//    $t->department = $row->department;        // 부서
//    $t->position 	 = $row->position;          // 직책
//    $t->commute    = $row->commute;
    public static class WorkStatusData_list{
        String id = "";
        String name = "";
        String kind = "";
        String img_path = "";
        String department = "";
        String position = "";
        String commute = "";


        public WorkStatusData_list(String id, String name, String kind, String img_path,
                                   String department, String position, String commute){
            this.id = id;
            this.name = name;
            this.kind = kind;
            this.img_path = img_path;
            this.department = department;
            this.position = position;
            this.commute = commute;
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

    public String getImg_path() {
        return img_path;
    }

    public void setImg_path(String img_path) {
        this.img_path = img_path;
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

    public String getCommute() {
        return commute;
    }

    public void setCommute(String commute) {
        this.commute = commute;
    }
}
}