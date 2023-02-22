package com.krafte.nebworks.data;

public class FCMMessageData {
    private static String messageSnippet1 = "";
    private static String messageSnippet2 = "";

    public String getMessageSnippet1() {
        return messageSnippet1;
    }

    public void setMessageSnippet1(String messageSnippet1) {
        this.messageSnippet1 = messageSnippet1;
    }

    public String getMessageSnippet2() {
        return messageSnippet2;
    }

    public void setMessageSnippet2(String messageSnippet2) {
        this.messageSnippet2 = messageSnippet2;
    }

    private static FCMMessageData instance = null;
    public static synchronized FCMMessageData getInstance(){
        if(null == instance){
            instance = new FCMMessageData();
        }
        return instance;
    }
}
