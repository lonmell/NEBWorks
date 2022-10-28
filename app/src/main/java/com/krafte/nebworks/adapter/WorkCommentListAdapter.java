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
import com.krafte.nebworks.data.WorkCommentData;
import com.krafte.nebworks.pop.CommunityOptionActivity;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PreferenceHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WorkCommentListAdapter extends RecyclerView.Adapter<WorkCommentListAdapter.ViewHolder> {
    private static final String TAG = "WorkCommentListAdapter";
    private ArrayList<WorkCommentData.WorkCommentData_list> mData = null;
    Context mContext;
    Activity activity;
    int user_kind;
    String TaskKind = "";
    int setKind;
    PreferenceHelper shardpref;

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
        void onItemClick(View v, int position);
    }

    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public WorkCommentListAdapter(Context context, ArrayList<WorkCommentData.WorkCommentData_list> data,String feedwriter_id) {
        this.mData = data;
        this.mContext = context;
        this.feedwriter_id = feedwriter_id;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.workcomment_item, parent, false);
        ViewHolder vh = new ViewHolder(view);

        if (context instanceof Activity)
            activity = (Activity) context;

        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorkCommentData.WorkCommentData_list item = mData.get(position);

        try{
            dlog.DlogContext(mContext);

//        like_off = mContext.getApplicationContext().getResources().getDrawable(R.drawable.like);
//        like_on = mContext.getApplicationContext().getResources().getDrawable(R.drawable.like_red);

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
                holder.comment_user_id.setTextColor(Color.parseColor("#6495ed"));
                holder.Writer_box.setVisibility(View.VISIBLE);
                holder.edit_bottom.setVisibility(View.VISIBLE);
                holder.edit_bottom.setClickable(true);
            } else if (item.getWrite_id().equals(feedwriter_id) &&  !item.getWrite_id().equals(USER_INFO_ID)) {
                holder.comment_user_id.setTextColor(Color.parseColor("#696969"));
                holder.Writer_box.setVisibility(View.VISIBLE);
                holder.edit_bottom.setVisibility(View.INVISIBLE);
                holder.edit_bottom.setClickable(false);
            } else if (!item.getWrite_id().equals(feedwriter_id) && item.getWrite_id().equals(USER_INFO_ID)) {
                holder.comment_user_id.setTextColor(Color.parseColor("#696969"));
                holder.Writer_box.setVisibility(View.INVISIBLE);
                holder.edit_bottom.setVisibility(View.VISIBLE);
                holder.edit_bottom.setClickable(true);
            } else if (!item.getWrite_id().equals(feedwriter_id) &&  !item.getWrite_id().equals(USER_INFO_ID)) {
                holder.comment_user_id.setTextColor(Color.parseColor("#696969"));
                holder.Writer_box.setVisibility(View.INVISIBLE);
                holder.edit_bottom.setVisibility(View.INVISIBLE);
                holder.edit_bottom.setClickable(false);
            } else {
                holder.comment_user_id.setTextColor(Color.parseColor("#696969"));
                holder.Writer_box.setVisibility(View.INVISIBLE);
                holder.edit_bottom.setVisibility(View.INVISIBLE);
                holder.edit_bottom.setClickable(false);
            }
            holder.edit_bottom.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onItemClick(v, position);
                }
//                setUpdateWorktodo(position);
//            CommunityOptionActivity coa = new CommunityOptionActivity();
//            coa.show(getSupportFragmentManager(),"CommunityOptionActivity");
            });
            dlog.i("----------원댓 작성자 구분----------");

            Glide.with(mContext).load(item.getWriter_img_path())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(holder.profile_img);
//            holder.comment_user_write_date.setText(" | " + item.getWrite_date().substring(0,10));
            holder.comment_user_id.setText(item.getWriter_name());
            String comment_contents = "";
            if (item.getDelete_yn().equals("y")) {
//                holder.comment_user_write_date.setVisibility(View.INVISIBLE);
//                holder.sub_like_cnt.setVisibility(View.INVISIBLE);
                holder.edit_bottom.setVisibility(View.INVISIBLE);
                holder.edit_bottom.setClickable(false);
                holder.delete_comment.setVisibility(View.VISIBLE);
                holder.edit_comment.setVisibility(View.GONE);
                comment_contents = "작성자에 의해 삭제된 댓글입니다.";
                holder.comment_user_contents.setTextColor(Color.parseColor("#DEDEDE"));
            } else {
                holder.delete_comment.setVisibility(View.GONE);
                if (item.getEdit_yn().equals("y")) {
                    holder.edit_comment.setVisibility(View.VISIBLE);
                    comment_contents = item.getComment();
                    holder.comment_user_contents.setTextColor(Color.parseColor("#000000"));
                } else {
                    holder.edit_comment.setVisibility(View.GONE);
                    comment_contents = item.getComment();
                    holder.comment_user_contents.setTextColor(Color.parseColor("#000000"));
                }

            }
            holder.comment_user_contents.setText(comment_contents);

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
                    holder.comment_user_write_date.setText("오늘");
                } else if (diffMin < 60) {
                    holder.comment_user_write_date.setText(Math.abs(diffMin) + "분 전");
                } else if (diffHor < 24) {
                    holder.comment_user_write_date.setText(Math.abs(diffHor) + "시간 전");
                } else if (diffDays > 0) {
                    if (diffDays > 7) {
                        holder.comment_user_write_date.setText(item.getCreated_at());
                    } else {
                        holder.comment_user_write_date.setText(Math.abs(diffDays) + "일 전");
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.origianl_comment.setOnClickListener(v -> {

            });
        }catch (Exception e){
            dlog.i("Exception : " + e);
        }

    } // getItemCount : 전체 데이터의 개수를 리턴

    @Override
    public int getItemCount() {
        return mData.size();
    } // 아이템 뷰를 저장하는 뷰홀더 클래스

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout origianl_comment, edit_bottom;
        TextView comment_user_id;
        TextView comment_user_contents;
        TextView edit_comment;
        TextView comment_user_write_date;
        ImageView profile_img, delete_comment;
        TextView Writer_box;


        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조

            origianl_comment = itemView.findViewById(R.id.origianl_comment);
            comment_user_id = itemView.findViewById(R.id.comment_user_id);
            comment_user_contents = itemView.findViewById(R.id.comment_user_contents);

            comment_user_write_date = itemView.findViewById(R.id.comment_user_write_date);
            Writer_box = itemView.findViewById(R.id.Writer_box);
            profile_img = itemView.findViewById(R.id.profile_img);

            edit_bottom = itemView.findViewById(R.id.edit_bottom);
            edit_comment = itemView.findViewById(R.id.edit_comment);
            delete_comment = itemView.findViewById(R.id.delete_comment);

            shardpref = new PreferenceHelper(mContext);
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");

//            itemView.setOnClickListener(view -> {
//                int pos = getBindingAdapterPosition();
//                if(pos != RecyclerView.NO_POSITION){
//                    WorkCommentData.WorkCommentData_list item = mData.get(pos);
//                    if (mListener != null) {
//                        mListener.onItemClick(view, pos) ;
//                    }
//                }
//            });
        }
    }

    public void addItem(WorkCommentData.WorkCommentData_list data) {
        mData.add(data);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    private void UpdateView(String flag, String board_no, String comment_no) {
//        @SuppressLint({"NotifyDataSetChanged", "LongLogTag"}) Thread th = new Thread(() -> {
//            dbConnection.workCheckListData_lists.clear();
//            dbConnection.WorkCommunityAddData(flag, board_no, comment_no, USER_INFO_ID, "","", "", "", "", "", "", "");
//            dlog.i("(WorkCommunityDetailActivity)UpdateView flag : " + flag);
//            activity.runOnUiThread(() -> {
//                dlog.i("(WorkCommunityDetailActivity)resultData : " + resultData.getRESULT());
//
////                if (resultData.getRESULT().equals("success")) {
////
////                } else {
////                    Log.i( "(setRecyclerAddComment)Error2 / flag:" + flag);
////                }
//            });
//        });
//        th.start();
//        try {
//            th.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }


    //댓글 수정
    private void setUpdateWorktodo(int pos) {
        WorkCommentData.WorkCommentData_list item = mData.get(pos);
        shardpref.putString("comment_no",item.getId());
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
        intent.putExtra("write_nickname","");
        mContext.startActivity(intent);
        ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
}
