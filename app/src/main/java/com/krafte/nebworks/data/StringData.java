package com.krafte.nebworks.data;

public class StringData {
    private static final String TAG = "PlaceNotiData";

    public static class StringData_list {
        String item = "";

        public StringData_list(String item) {
            this.item = item;
        }

        public String getItem() {
            return item;
        }

        public void setItem(String item) {
            this.item = item;
        }
    }
}
