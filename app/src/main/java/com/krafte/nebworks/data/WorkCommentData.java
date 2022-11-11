package com.krafte.nebworks.data;

public class WorkCommentData {
    private static final String TAG = "WorkCommentData";

    public static class WorkCommentData_list {
        String id;
        String feed_id;
        String comment;
        String write_id;
        String writer_name;
        String writer_img_path;
        String edit_yn;
        String delete_yn;
        String created_at;
        String updated_at;

        public WorkCommentData_list(String id, String feed_id, String comment, String write_id,
                                    String writer_name, String writer_img_path, String edit_yn, String delete_yn, String created_at, String updated_at) {
            this.id = id;
            this.feed_id = feed_id;
            this.comment = comment;
            this.write_id = write_id;
            this.writer_name = writer_name;
            this.writer_img_path = writer_img_path;
            this.edit_yn = edit_yn;
            this.delete_yn = delete_yn;
            this.created_at = created_at;
            this.updated_at = updated_at;
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

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getWrite_id() {
            return write_id;
        }

        public void setWrite_id(String write_id) {
            this.write_id = write_id;
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

        public String getDelete_yn() {
            return delete_yn;
        }

        public void setDelete_yn(String delete_yn) {
            this.delete_yn = delete_yn;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }
    }
}