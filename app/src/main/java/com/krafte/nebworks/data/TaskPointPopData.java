package com.krafte.nebworks.data;

public class TaskPointPopData {
    private static final String TAG = "ContractData";

    public static class TaskPointPopData_list {
        String kind;
        String title;


        public TaskPointPopData_list(String kind, String title) {
            this.kind = kind;
            this.title = title;
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

    }
}
