package com.krafte.nebworks.data;

public class CategorySpinner {
    private static final String TAG = "CategorySpinner";

    public static class CategorySpinner_list {
        private String setvalue = null;

        public CategorySpinner_list(String setvalue) {
            super();
            this.setvalue = setvalue;
        }

        public String getSetvalue() {
            return setvalue;
        }

        public void setSetvalue(String setvalue) {
            this.setvalue = setvalue;
        }
    }
}