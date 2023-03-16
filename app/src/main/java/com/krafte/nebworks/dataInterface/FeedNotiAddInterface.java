package com.krafte.nebworks.dataInterface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface FeedNotiAddInterface {
    String URL = "https://nepworks.net/NEBWorks/feed/";
    //https://nepworks.net/NEBWorks/feed/post.php?place_id=166&title=제목입니다&contents=내용입니다&writer_id=199&link=&img_path=https://nepworks.net/NEBWorks/image/feed_img/199_.JPEG,https://nepworks.net/NEBWorks/image/feed_img/199_.JPEG,https://nepworks.net/NEBWorks/image/feed_img/199_.JPEG&file_path
    @FormUrlEncoded
    @POST("post.php")
    Call<String> getData(
            @Field("place_id") String place_id,
            @Field("title") String title,
            @Field("contents") String contents,
            @Field("writer_id") String writer_id,
            @Field("link") String link,
            @Field("img_path") String img_path,
            @Field("file_path") String file_path,
            @Field("open_date") String open_date,
            @Field("close_date") String close_date,
            @Field("kind") String kind,
            @Field("boardkind") String boardkind,
            @Field("category") String category,
            @Field("nicknameyn") String nicknameyn,
            @Field("fix_yn") String fix_yn
    );
}