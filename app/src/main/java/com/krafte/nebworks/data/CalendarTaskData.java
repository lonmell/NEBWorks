package com.krafte.nebworks.data;

import java.util.List;

public class CalendarTaskData {
    public static class CalendarTaskData_list {
        String id;
        String kind;
        String title;
        String contents;
        String complete_kind;
        List<String> users;

        public CalendarTaskData_list(String id, String kind, String title, String contents, String complete_kind, List<String> users) {
            this.id = id;
            this.kind = kind;
            this.title = title;
            this.contents = contents;
            this.complete_kind = complete_kind;
            this.users = users;
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
    }
}
