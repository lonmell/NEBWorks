package com.krafte.nebworks.data;

public class ReturnPageData {
    private static String page = "";

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    private static ReturnPageData instance = null;
    public static synchronized ReturnPageData getInstance(){
        if(null == instance){
            instance = new ReturnPageData();
        }
        return instance;
    }
}
