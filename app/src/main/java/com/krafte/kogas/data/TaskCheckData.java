package com.krafte.kogas.data;

public class TaskCheckData {
    private static final String TAG = "TaskCheckData";

    public static class TaskCheckData_list {
        String id = "";
        String state = "";
        String request_task_no = "";
        String requester_id = "";
        String requester_name = "";
        String requester_img_path = "";
        String requester_department = "";
        String requester_position = "";
        String title = "";
        String contents = "";
        String complete_kind = "";
        String end_time = "0";
        String complete_time = "0";
        String task_img_path = "";
        String complete_yn = "";
        String incomplete_reason = "";
        String reject_reason = "";
        String task_date = "";
        String request_date = "";
        String approval_date = "";

        public TaskCheckData_list(String id,
                                  String state,
                                  String request_task_no,
                                  String requester_id,
                                  String requester_name,
                                  String requester_img_path,
                                  String requester_department,
                                  String requester_position,
                                  String title,
                                  String contents,
                                  String complete_kind,
                                  String end_time,
                                  String complete_time,
                                  String task_img_path,
                                  String complete_yn,
                                  String incomplete_reason,
                                  String reject_reason,
                                  String task_date,
                                  String request_date,
                                  String approval_date) {
            this.id = id;
            this.state = state;
            this.request_task_no = request_task_no;
            this.requester_id = requester_id;
            this.requester_name = requester_name;
            this.requester_img_path = requester_img_path;
            this.requester_department = requester_department;
            this.requester_position = requester_position;
            this.title = title;
            this.contents = contents;
            this.complete_kind = complete_kind;
            this.end_time = end_time;
            this.complete_time = complete_time;
            this.task_img_path = task_img_path;
            this.complete_yn = complete_yn;
            this.incomplete_reason = incomplete_reason;
            this.reject_reason = reject_reason;
            this.task_date = task_date;
            this.request_date = request_date;
            this.approval_date = approval_date;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getRequest_task_no() {
            return request_task_no;
        }

        public void setRequest_task_no(String request_task_no) {
            this.request_task_no = request_task_no;
        }

        public String getRequester_id() {
            return requester_id;
        }

        public void setRequester_id(String requester_id) {
            this.requester_id = requester_id;
        }

        public String getRequester_name() {
            return requester_name;
        }

        public void setRequester_name(String requester_name) {
            this.requester_name = requester_name;
        }

        public String getRequester_img_path() {
            return requester_img_path;
        }

        public void setRequester_img_path(String requester_img_path) {
            this.requester_img_path = requester_img_path;
        }

        public String getRequester_department() {
            return requester_department;
        }

        public void setRequester_department(String requester_department) {
            this.requester_department = requester_department;
        }

        public String getRequester_position() {
            return requester_position;
        }

        public void setRequester_position(String requester_position) {
            this.requester_position = requester_position;
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

        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        public String getComplete_time() {
            return complete_time;
        }

        public void setComplete_time(String complete_time) {
            this.complete_time = complete_time;
        }

        public String getTask_img_path() {
            return task_img_path;
        }

        public void setTask_img_path(String task_img_path) {
            this.task_img_path = task_img_path;
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

        public String getReject_reason() {
            return reject_reason;
        }

        public void setReject_reason(String reject_reason) {
            this.reject_reason = reject_reason;
        }

        public String getTask_date() {
            return task_date;
        }

        public void setTask_date(String task_date) {
            this.task_date = task_date;
        }

        public String getRequest_date() {
            return request_date;
        }

        public void setRequest_date(String request_date) {
            this.request_date = request_date;
        }

        public String getApproval_date() {
            return approval_date;
        }

        public void setApproval_date(String approval_date) {
            this.approval_date = approval_date;
        }
    }
}
