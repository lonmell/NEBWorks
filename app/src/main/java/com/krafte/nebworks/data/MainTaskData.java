package com.krafte.nebworks.data;

import java.util.List;

public class MainTaskData {
    public static class MainTaskData_list {
        String id;
        String writer_id;
        String kind;
        String title;
        String contents;
        String complete_kind;
        List<String> users;
        String task_date;
        String start_time;
        String end_time;
        String sun;
        String mon;
        String tue;
        String wed;
        String thu;
        String fri;
        String sat;
        String img_path;
        String complete_yn;
        String incomplete_reason;
        String approval_state;
        String task_overdate;
        String reject_reason;
        String updated_at;
        public MainTaskData_list(String id, String writer_id, String kind,
                                 String title, String contents, String complete_kind, List<String> users,
                                 String task_date, String start_time, String end_time, String sun,
                                 String mon, String tue, String wed, String thu,String fri, String sat,
                                 String img_path, String complete_yn, String incomplete_reason,
                                 String approval_state, String task_overdate, String reject_reason, String updated_at) {
            this.id = id;
            this.writer_id = writer_id;
            this.kind = kind;
            this.title = title;
            this.contents = contents;
            this.complete_kind = complete_kind;
            this.users = users;
            this.task_date = task_date;
            this.start_time = start_time;
            this.end_time = end_time;
            this.sun = sun;
            this.mon = mon;
            this.tue = tue;
            this.wed = wed;
            this.thu = thu;
            this.fri = fri;
            this.sat = sat;
            this.img_path = img_path;
            this.complete_yn = complete_yn;
            this.incomplete_reason = incomplete_reason;
            this.approval_state = approval_state;
            this.task_overdate = task_overdate;
            this.reject_reason = reject_reason;
            this.updated_at = updated_at;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getWriter_id() {
            return writer_id;
        }

        public void setWriter_id(String writer_id) {
            this.writer_id = writer_id;
        }

        public String getKind() {
            return kind;
        }

        public void setKind(String kind) {
            this.kind = kind;
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

        public String getComplete_kind() {
            return complete_kind;
        }

        public void setComplete_kind(String complete_kind) {
            this.complete_kind = complete_kind;
        }

        public List<String> getUsers() {
            return users;
        }

        public void setUsers(List<String> users) {
            this.users = users;
        }

        public String getTask_date() {
            return task_date;
        }

        public void setTask_date(String task_date) {
            this.task_date = task_date;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        public String getSun() {
            return sun;
        }

        public void setSun(String sun) {
            this.sun = sun;
        }

        public String getMon() {
            return mon;
        }

        public void setMon(String mon) {
            this.mon = mon;
        }

        public String getTue() {
            return tue;
        }

        public void setTue(String tue) {
            this.tue = tue;
        }

        public String getWed() {
            return wed;
        }

        public void setWed(String wed) {
            this.wed = wed;
        }

        public String getThu() {
            return thu;
        }

        public void setThu(String thu) {
            this.thu = thu;
        }

        public String getFri() {
            return fri;
        }

        public void setFri(String fri) {
            this.fri = fri;
        }

        public String getSat() {
            return sat;
        }

        public void setSat(String sat) {
            this.sat = sat;
        }

        public String getImg_path() {
            return img_path;
        }

        public void setImg_path(String img_path) {
            this.img_path = img_path;
        }

        public String getComplete_yn() {
            return complete_yn;
        }

        public void setComplete_yn(String complete_yn) {
            this.complete_yn = complete_yn;
        }

        public String getIncomplete_reason() {
            return incomplete_reason;
        }

        public void setIncomplete_reason(String incomplete_reason) {
            this.incomplete_reason = incomplete_reason;
        }

        public String getApproval_state() {
            return approval_state;
        }

        public void setApproval_state(String approval_state) {
            this.approval_state = approval_state;
        }

        public String getTask_overdate() {
            return task_overdate;
        }

        public void setTask_overdate(String task_overdate) {
            this.task_overdate = task_overdate;
        }

        public String getReject_reason() {
            return reject_reason;
        }

        public void setReject_reason(String reject_reason) {
            this.reject_reason = reject_reason;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }
    }
}