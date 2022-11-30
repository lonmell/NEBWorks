package com.krafte.nebworks.data;

public class SecondTapCommunityData {
    public static class SecondTapCommunityData_list {
        String id = "";
        String category = "";
        String title = "";
        String gigan = "";
        String location = "";
        String date = "";
        String view_cnt = "";
        String link = "";


        public SecondTapCommunityData_list(String id, String category, String title, String gigan,
                                  String location, String date, String view_cnt, String link) {
            this.id = id;
            this.category = category;
            this.title = title;
            this.gigan = gigan;
            this.location = location;
            this.date = date;
            this.view_cnt = view_cnt;
            this.link = link;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getGigan() {
            return gigan;
        }

        public void setGigan(String gigan) {
            this.gigan = gigan;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getView_cnt() {
            return view_cnt;
        }

        public void setView_cnt(String view_cnt) {
            this.view_cnt = view_cnt;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }
    }
}
