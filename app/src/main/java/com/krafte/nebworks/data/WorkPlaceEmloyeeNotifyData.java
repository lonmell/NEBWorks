package com.krafte.nebworks.data;

public class WorkPlaceEmloyeeNotifyData {
    private static final String TAG = "WorkPlaceEmloyeeNotifyData";

    public static class WorkPlaceEmloyeeNotifyData_list {
        String id = "";
        String kind = "";
        String title = "";
        String contents = "";
        String read_yn = "";
        String sender_id = "";
        String sender_name = "";
        String push_date = "";

        public WorkPlaceEmloyeeNotifyData_list(
                String id,
                String kind,
                String title,
                String contents,
                String read_yn,
                String sender_id,
                String sender_name,
                String push_date
        ) {
            super();
            this.id = id;
            this.kind = kind;
            this.title = title;
            this.contents = contents;
            this.read_yn = read_yn;
            this.sender_id = sender_id;
            this.sender_name = sender_name;
            this.push_date = push_date;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        public String getRead_yn() {
            return read_yn;
        }

        public void setRead_yn(String read_yn) {
            this.read_yn = read_yn;
        }

        public String getSender_id() {
            return sender_id;
        }

        public void setSender_id(String sender_id) {
            this.sender_id = sender_id;
        }

        public String getSender_name() {
            return sender_name;
        }

        public void setSender_name(String sender_name) {
            this.sender_name = sender_name;
        }

        public String getPush_date() {
            return push_date;
        }

        public void setPush_date(String push_date) {
            this.push_date = push_date;
        }
    }

}
