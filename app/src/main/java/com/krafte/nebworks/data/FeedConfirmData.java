package com.krafte.nebworks.data;

public class FeedConfirmData {
    public static class FeedConfirmData_list {
        String id;
        String feed_id;
        String write_id;
        String name;
        String img_path;
        String created_at;


        public FeedConfirmData_list(String id, String feed_id, String write_id, String name, String img_path
                , String created_at) {
            this.id = id;
            this.feed_id = feed_id;
            this.write_id = write_id;
            this.name = name;
            this.created_at = created_at;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFeed_id() {
            return feed_id;
        }

        public void setFeed_id(String feed_id) {
            this.feed_id = feed_id;
        }

        public String getWrite_id() {
            return write_id;
        }

        public void setWrite_id(String write_id) {
            this.write_id = write_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImg_path() {
            return img_path;
        }

        public void setImg_path(String img_path) {
            this.img_path = img_path;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }
    }
}
