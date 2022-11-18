package com.krafte.nebworks.data;

public class StringTwoData {
    private static final String TAG = "StringTwoData";

    public static class StringTwoData_list {
        String item = "";
        String item2 = "";

        public StringTwoData_list(String item, String item2) {
            this.item = item;
            this.item2 = item2;
        }

        public String getItem() {
            return item;
        }

        public void setItem(String item) {
            this.item = item;
        }

        public String getItem2() {
            return item2;
        }

        public void setItem2(String item2) {
            this.item2 = item2;
        }
    }
}
