package com.krafte.nebworks.data;

public class TodoReuseData {
    public static class TodoReuseData_list {
        String id;
        String writer_id;
        String title;
        String contents;
        String complete_kind;
        String start_time;
        String end_time;
        String sun;
        String mon;
        String tue;
        String wed;
        String thu;
        String fri;
        String sat;
        String task_overdate;

        public TodoReuseData_list(String id, String writer_id, String title, String contents,
                                  String complete_kind, String start_time, String end_time,
                                  String sun, String mon, String tue, String wed, String thu,String fri, String sat,String task_overdate) {
            this.id = id;
            this.writer_id = writer_id;
            this.title = title;
            this.contents = contents;
            this.complete_kind = complete_kind;
            this.start_time = start_time;
            this.end_time = end_time;
            this.sun = sun;
            this.mon = mon;
            this.tue = tue;
            this.wed = wed;
            this.thu = thu;
            this.fri = fri;
            this.sat = sat;
            this.task_overdate = task_overdate;
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

        public String getTask_overdate() {
            return task_overdate;
        }

        public void setTask_overdate(String task_overdate) {
            this.task_overdate = task_overdate;
        }
    }
}
