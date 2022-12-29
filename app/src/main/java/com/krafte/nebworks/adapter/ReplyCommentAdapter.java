package com.krafte.nebworks.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.krafte.nebworks.R;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.data.WorkReplyCommentData;
import com.krafte.nebworks.dataInterface.AddLikeInterface;
import com.krafte.nebworks.pop.CommunityOptionActivity;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ReplyCommentAdapter extends RecyclerView.Adapter<ReplyCommentAdapter.ViewHolder> {
    private static final String TAG = "ReplyCommentAdapter";
    private ArrayList<WorkReplyCommentData.WorkReplyCommentData_list> mData = null;
    Context mContext;
    Activity activity;
    int user_kind;
    String TaskKind = "";
    int setKind;
    PreferenceHelper shardpref;

    boolean[] likeonof;

    //shared data
    String USER_INFO_ID = "";
    String USER_INFO_NICKNAME = "";
    String USER_INFO_NAME = "";
    String WriteName = "";
    String feedwriter_id = "";
    //Other
    GetResultData resultData = new GetResultData();
    DateCurrent dc = new DateCurrent();
    Dlog dlog = new Dlog();

    String TodayTxt = "";
    Drawable like_off, like_on;

    public interface OnItemClickListener {
        void onItemClick(View v, int position, String comment_id, String comment, String writer_name
                , String feed_id, String write_id, String title, String contents, String comment_contents, String write_date);
    }

    // intent.putExtra("feed_id", Response.getJSONObject(position).getString("feed_id"));
//                                            intent.putExtra("comment_id", Response.getJSONObject(position).getString("id"));
//                                            intent.putExtra("write_id", Response.getJSONObject(position).getString("writer_id"));
//                                            intent.putExtra("writer_name", WriteName);
//                                            intent.putExtra("title", "");
//                                            intent.putExtra("contents", "");
//                                            intent.putExtra("comment_contents", Response.getJSONObject(position).getString("comment"));
//                                            intent.putExtra("write_date", Response.getJSONObject(position).getString("created_at"));
    public interface OnItemClickListener2 {
        void onItemClick2(View v, int position, String writer_id, String writer_name);
    }

    // 리스너 객체 참조를 저장하는 변수
    private ReplyCommentAdapter.OnItemClickListener mListener = null;
    private ReplyCommentAdapter.OnItemClickListener2 mListener2 = null;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(ReplyCommentAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }

    public void setOnItemClickListener2(ReplyCommentAdapter.OnItemClickListener2 listener2) {
        this.mListener2 = listener2;
    }

    public ReplyCommentAdapter(Context context, ArrayList<WorkReplyCommentData.WorkReplyCommentData_list> data, String feedwriter_id) {
        this.mData = data;
        this.mContext = context;
        this.feedwriter_id = feedwriter_id;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public ReplyCommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.workreplycomment_item, parent, false);
        ReplyCommentAdapter.ViewHolder vh = new ReplyCommentAdapter.ViewHolder(view);

        if (context instanceof Activity)
            activity = (Activity) context;

        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull ReplyCommentAdapter.ViewHolder holder, int position) {
        WorkReplyCommentData.WorkReplyCommentData_list item = mData.get(position);

        try {

            dlog.DlogContext(mContext);
            shardpref = new PreferenceHelper(mContext);
            USER_INFO_NICKNAME = shardpref.getString("USER_INFO_NICKNAME", "");
            USER_INFO_NAME = shardpref.getString("USER_INFO_NAME", "");

            String User_name = item.getWriter_name().isEmpty() ? item.getWriter_name() : "";
            TodayTxt = dc.GET_YEAR + "-" + dc.GET_MONTH + "-" + dc.GET_DAY;
            dlog.i("TodayTxt : " + TodayTxt);
            dlog.i("getWrite_date : " + item.getCreated_at().substring(0, 10));

            dlog.i("getWriter_name : " + User_name);
            dlog.i("item.getOriginal_comment_no() " + item.getId());

            dlog.i("----------원댓 작성자 구분----------");
            dlog.i("write_id : " + item.getWrite_id());
            dlog.i("write_name : " + item.getWriter_name());
            dlog.i("feed_write_id : " + feedwriter_id);
            dlog.i("FeedWriterName : " + User_name);
            dlog.i("게시글 번호 : " + item.getId());
            dlog.i("작성자여부 : " + item.getWrite_id().equals(User_name));
            if (item.getWrite_id().equals(feedwriter_id) && item.getWrite_id().equals(USER_INFO_ID)) {
                holder.reply_comment_user_id.setTextColor(Color.parseColor("#6495ed"));
                holder.reply_Writer_box.setVisibility(View.VISIBLE);
                holder.reply_edit_bottom.setVisibility(View.VISIBLE);
                holder.reply_edit_bottom.setClickable(true);
            } else if (item.getWrite_id().equals(feedwriter_id) && !item.getWrite_id().equals(USER_INFO_ID)) {
                holder.reply_comment_user_id.setTextColor(Color.parseColor("#696969"));
                holder.reply_Writer_box.setVisibility(View.VISIBLE);
                holder.reply_edit_bottom.setVisibility(View.INVISIBLE);
                holder.reply_edit_bottom.setClickable(false);
            } else if (!item.getWrite_id().equals(feedwriter_id) && item.getWrite_id().equals(USER_INFO_ID)) {
                holder.reply_comment_user_id.setTextColor(Color.parseColor("#696969"));
                holder.reply_Writer_box.setVisibility(View.INVISIBLE);
                holder.reply_edit_bottom.setVisibility(View.VISIBLE);
                holder.reply_edit_bottom.setClickable(true);
            } else if (!item.getWrite_id().equals(feedwriter_id) && !item.getWrite_id().equals(USER_INFO_ID)) {
                holder.reply_comment_user_id.setTextColor(Color.parseColor("#696969"));
                holder.reply_Writer_box.setVisibility(View.INVISIBLE);
                holder.reply_edit_bottom.setVisibility(View.INVISIBLE);
                holder.reply_edit_bottom.setClickable(false);
            } else {
                holder.reply_comment_user_id.setTextColor(Color.parseColor("#696969"));
                holder.reply_Writer_box.setVisibility(View.INVISIBLE);
                holder.reply_edit_bottom.setVisibility(View.INVISIBLE);
                holder.reply_edit_bottom.setClickable(false);
            }
            holder.reply_edit_bottom.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onItemClick(v, position, item.getId(), item.getComment(), item.getWriter_name()
                            , item.getFeed_id(), item.getWrite_id(), "", "", item.getComment(), item.getCreated_at());
                }
            });
            dlog.i("----------원댓 작성자 구분----------");

            Glide.with(mContext).load(item.getWriter_img_path())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(holder.reply_profile_img);
//            holder.comment_user_write_date.setText(" | " + item.getWrite_date().substring(0,10));
            holder.reply_comment_user_id.setText(item.getWriter_name());
            String comment_contents = "";
            if (item.getDelete_yn().equals("y")) {
//                holder.comment_user_write_date.setVisibility(View.INVISIBLE);
//                holder.sub_like_cnt.setVisibility(View.INVISIBLE);
                holder.reply_edit_bottom.setVisibility(View.INVISIBLE);
                holder.reply_edit_bottom.setClickable(false);
                holder.reply_delete_comment.setVisibility(View.VISIBLE);
                holder.reply_edit_comment.setVisibility(View.GONE);
                comment_contents = "작성자에 의해 삭제된 댓글입니다.";
                holder.reply_comment_user_contents.setTextColor(Color.parseColor("#DEDEDE"));
            } else {
                holder.reply_delete_comment.setVisibility(View.GONE);
                if (item.getEdit_yn().equals("y")) {
                    holder.reply_edit_comment.setVisibility(View.VISIBLE);
                    comment_contents = item.getComment();
                    holder.reply_comment_user_contents.setTextColor(Color.parseColor("#000000"));
                } else {
                    holder.reply_edit_comment.setVisibility(View.GONE);
                    comment_contents = item.getComment();
                    holder.reply_comment_user_contents.setTextColor(Color.parseColor("#000000"));
                }

            }
            holder.reply_comment_user_contents.setText(comment_contents);

            try {
                @SuppressLint("SimpleDateFormat")
                Date format1 = new SimpleDateFormat("yyyy-MM-dd").parse(TodayTxt);
                @SuppressLint("SimpleDateFormat")
                Date format2 = new SimpleDateFormat("yyyy-MM-dd").parse(item.getCreated_at().substring(0, 10));

                long diffSec = (format1.getTime() - format2.getTime()) / 1000; //초 차이
                long diffMin = (format1.getTime() - format2.getTime()) / 60000; //분 차이
                long diffHor = (format1.getTime() - format2.getTime()) / 3600000; //시 차이
                long diffDays = diffSec / (24 * 60 * 60); //일자수 차이

                if (diffSec < 60) {
                    holder.reply_comment_user_write_date.setText("오늘");
                } else if (diffMin < 60) {
                    holder.reply_comment_user_write_date.setText(Math.abs(diffMin) + "분 전");
                } else if (diffHor < 24) {
                    holder.reply_comment_user_write_date.setText(Math.abs(diffHor) + "시간 전");
                } else if (diffDays > 0) {
                    if (diffDays > 7) {
                        holder.reply_comment_user_write_date.setText(item.getCreated_at());
                    } else {
                        holder.reply_comment_user_write_date.setText(Math.abs(diffDays) + "일 전");
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.reply_cnt.setOnClickListener(v -> {
                if (mListener2 != null) {
                    mListener2.onItemClick2(v, position, item.getComment_id(), item.getWriter_name());
                }
            });

            //좋아요 영역
            holder.reply_like_cnt.setText(item.getLike_cnt());
            if (item.getMylike_cnt().equals("0")) {
                likeonof[position] = false;
                holder.reply_like_icon.setBackgroundResource(R.drawable.ic_like_off);
            } else {
                likeonof[position] = true;
                holder.reply_like_icon.setBackgroundResource(R.drawable.ic_like_on);
            }
            holder.reply_like_area.setOnClickListener(v -> {
                if (!likeonof[position]) {
                    likeonof[position] = true;
                    holder.reply_like_icon.setBackgroundResource(R.drawable.ic_like_on);
                } else {
                    likeonof[position] = false;
                    holder.reply_like_icon.setBackgroundResource(R.drawable.ic_like_off);
                }
                AddLike(item.getFeed_id(), item.getId(), holder);
            });
        } catch (Exception e) {
            dlog.i("Exception : " + e);
        }

    } // getItemCount : 전체 데이터의 개수를 리턴

    @Override
    public int getItemCount() {
        return mData.size();
    } // 아이템 뷰를 저장하는 뷰홀더 클래스

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout reply_comment, reply_edit_bottom, reply_like_area;
        ImageView reply_profile_img, reply_like_icon;
        TextView reply_comment_user_id, reply_Writer_box, reply_comment_user_write_date;
        ImageView reply_delete_comment;
        TextView reply_comment_user_contents, reply_edit_comment;
        TextView reply_cnt, reply_like_cnt;

        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조

            reply_comment = itemView.findViewById(R.id.reply_comment);
            reply_profile_img = itemView.findViewById(R.id.reply_profile_img);
            reply_comment_user_id = itemView.findViewById(R.id.reply_comment_user_id);
            reply_Writer_box = itemView.findViewById(R.id.reply_Writer_box);
            reply_comment_user_write_date = itemView.findViewById(R.id.reply_comment_user_write_date);
            reply_delete_comment = itemView.findViewById(R.id.reply_delete_comment);
            reply_comment_user_contents = itemView.findViewById(R.id.reply_comment_user_contents);
            reply_edit_comment = itemView.findViewById(R.id.reply_edit_comment);
            reply_edit_bottom = itemView.findViewById(R.id.reply_edit_bottom);
            reply_cnt = itemView.findViewById(R.id.reply_cnt);
            reply_like_area = itemView.findViewById(R.id.reply_like_area);
            reply_like_icon = itemView.findViewById(R.id.reply_like_icon);
            reply_like_cnt = itemView.findViewById(R.id.reply_like_cnt);

            //likeonof false로 세팅
            likeonof = new boolean[mData.size()];
            for (int a = 0; a < mData.size(); a++) {
                likeonof[a] = false;
            }

            shardpref = new PreferenceHelper(mContext);
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
            like_off = mContext.getApplicationContext().getResources().getDrawable(R.drawable.ic_like_off);
            like_on = mContext.getApplicationContext().getResources().getDrawable(R.drawable.ic_like_on);

            itemView.setOnClickListener(view -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    WorkReplyCommentData.WorkReplyCommentData_list item = mData.get(pos);

                }
            });
        }
    }

    public void addItem(WorkReplyCommentData.WorkReplyCommentData_list data) {
        mData.add(data);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    String like_cnt = "0";
    RetrofitConnect rc = new RetrofitConnect();

    public void AddLike(String feed_id, String comment_id, ViewHolder holder) {
        dlog.i("-----UpdateView Check-----");
        dlog.i("feed_id : " + feed_id);
        dlog.i("-----UpdateView Check-----");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AddLikeInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        AddLikeInterface api = retrofit.create(AddLikeInterface.class);
        Call<String> call = api.getData(feed_id, comment_id, USER_INFO_ID, "1");
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                dlog.i("UpdateView Callback : " + response.body());
                activity.runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonResponse = rc.getBase64decode(response.body());
                        dlog.i("jsonResponse length : " + jsonResponse.length());
                        dlog.i("jsonResponse : " + jsonResponse);
                        dlog.i("UpdateView jsonResponse length : " + jsonResponse.length());
                        dlog.i("UpdateView jsonResponse : " + jsonResponse);
                        like_cnt = jsonResponse.replace("\"", "");
                        holder.reply_like_cnt.setText(like_cnt);
                    }
                });
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                dlog.e("에러1 = " + t.getMessage());
            }
        });
    }

    //댓글 수정
    private void setUpdateWorktodo(int pos) {
        WorkReplyCommentData.WorkReplyCommentData_list item = mData.get(pos);
        shardpref.putString("comment_no", item.getId());
        Intent intent = new Intent(mContext, CommunityOptionActivity.class);
        WriteName = item.getWriter_name().isEmpty() ? item.getWriter_name() : "";

        intent.putExtra("state", "EditComment");
        intent.putExtra("feed_id", item.getFeed_id());
        intent.putExtra("comment_no", item.getId());
        intent.putExtra("write_id", item.getWrite_id());
        intent.putExtra("writer_name", WriteName);
        intent.putExtra("title", "");
        intent.putExtra("contents", "");
        intent.putExtra("comment_contents", item.getComment());
        intent.putExtra("write_date", item.getCreated_at());
        intent.putExtra("write_nickname", "");
        mContext.startActivity(intent);
        ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
}
