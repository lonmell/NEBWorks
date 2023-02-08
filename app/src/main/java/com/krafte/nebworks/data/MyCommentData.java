package com.krafte.nebworks.data;

public class MyCommentData {
    private static final String TAG = "PaymentData2";

    public static class MyCommentData_list {
        String id;
        String feed_id;
        String title;
        String comment;
        String writer_id;
        String writer_name;
        String writer_img_path;
        String edit_yn;
        String updated_at;

        public MyCommentData_list(String id, String feed_id, String title, String comment, String writer_id
                , String writer_name, String writer_img_path, String edit_yn, String updated_at) {
            this.id                 = id;
            this.feed_id            = feed_id;
            this.title              = title;
            this.comment            = comment;
            this.writer_id          = writer_id;
            this.writer_name        = writer_name;
            this.writer_img_path    = writer_img_path;
            this.edit_yn            = edit_yn;
            this.updated_at         = updated_at;
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

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getWriter_id() {
            return writer_id;
        }

        public void setWriter_id(String writer_id) {
            this.writer_id = writer_id;
        }

        public String getWriter_name() {
            return writer_name;
        }

        public void setWriter_name(String writer_name) {
            this.writer_name = writer_name;
        }

        public String getWriter_img_path() {
            return writer_img_path;
        }

        public void setWriter_img_path(String writer_img_path) {
            this.writer_img_path = writer_img_path;
        }

        public String getEdit_yn() {
            return edit_yn;
        }

        public void setEdit_yn(String edit_yn) {
            this.edit_yn = edit_yn;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }
    }
}
