package com.krafte.nebworks.data;

/*
 * 2022-10-07 방창배 작성
 * */
public class PlaceNotiData {
    private static final String TAG = "PlaceNotiData";

    public static class PlaceNotiData_list {
        String id = "";
        String place_id = "";
        String title = "";
        String contents = "";
        String writer_id = "";
        String writer_name = "";
        String writer_img_path = "";
        String jikgup = "";
        String view_cnt = "0";
        String comment_cnt = "0";
        String link = "";
        String feed_img_path = "";
        String created_at = "";
        String updated_at = "";
        String open_date = "";
        String close_date = "";
        String boardkind = "";
        String category = "";

        public PlaceNotiData_list(String id, String place_id, String title, String contents,
                String writer_id, String writer_name, String writer_img_path, String jikgup,
                String view_cnt, String comment_cnt, String link,
                String feed_img_path, String created_at, String updated_at,
                String open_date, String close_date, String boardkind, String category) {
            this.id = id;
            this.place_id = place_id;
            this.title = title;
            this.contents = contents;
            this.writer_id = writer_id;
            this.writer_name = writer_name;
            this.writer_img_path = writer_img_path;
            this.jikgup = jikgup;
            this.view_cnt = view_cnt;
            this.comment_cnt = comment_cnt;
            this.link = link;
            this.feed_img_path = feed_img_path;
            this.created_at = created_at;
            this.updated_at = updated_at;
            this.open_date = open_date;
            this.close_date = close_date;
            this.boardkind = boardkind;
            this.category = category;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPlace_id() {
            return place_id;
        }

        public void setPlace_id(String place_id) {
            this.place_id = place_id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContents() {
            return contents;
        }

        public void setContents(String contents) {
            this.contents = contents;
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

        public String getJikgup() {
            return jikgup;
        }

        public void setJikgup(String jikgup) {
            this.jikgup = jikgup;
        }

        public String getView_cnt() {
            return view_cnt;
        }

        public void setView_cnt(String view_cnt) {
            this.view_cnt = view_cnt;
        }

        public String getComment_cnt() {
            return comment_cnt;
        }

        public void setComment_cnt(String comment_cnt) {
            this.comment_cnt = comment_cnt;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getFeed_img_path() {
            return feed_img_path;
        }

        public void setFeed_img_path(String feed_img_path) {
            this.feed_img_path = feed_img_path;
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

        public String getOpen_date() {
            return open_date;
        }

        public void setOpen_date(String open_date) {
            this.open_date = open_date;
        }

        public String getClose_date() {
            return close_date;
        }

        public void setClose_date(String close_date) {
            this.close_date = close_date;
        }

        public String getBoardkind() {
            return boardkind;
        }

        public void setBoardkind(String boardkind) {
            this.boardkind = boardkind;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }
    }
}
