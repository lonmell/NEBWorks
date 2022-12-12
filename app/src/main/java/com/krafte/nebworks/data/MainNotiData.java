package com.krafte.nebworks.data;

public class MainNotiData {
    public static class MainNotiData_list {
        String feed_title;
        String updated_at;

        public MainNotiData_list(String feed_title, String updated_at) {
            this.feed_title = feed_title;
            this.updated_at = updated_at;
        }

        public String getFeed_title() {
            return feed_title;
        }

        public void setFeed_title(String feed_title) {
            this.feed_title = feed_title;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }
    }
}
