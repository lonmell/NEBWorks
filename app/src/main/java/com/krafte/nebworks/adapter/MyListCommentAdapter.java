package com.krafte.nebworks.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.krafte.nebworks.R;
import com.krafte.nebworks.data.MyCommentData;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PreferenceHelper;

import java.util.ArrayList;

public class MyListCommentAdapter extends RecyclerView.Adapter<MyListCommentAdapter.ViewHolder> {
    private static final String TAG = "MyListCommentAdapter";
    private ArrayList<MyCommentData.MyCommentData_list> mData = null;
    Context mContext;
    Activity activity;
    PreferenceHelper shardpref;

    //shared data
    String USER_INFO_ID = "";
    String USER_INFO_NICKNAME = "";
    String USER_INFO_NAME = "";

    //Other
    DateCurrent dc = new DateCurrent();
    Dlog dlog = new Dlog();

    String TodayTxt = "";
    public interface OnItemClickListener {
        void onItemClick(View v, int position, String comment_id, String comment, String writer_name
                , String feed_id, String write_id, String title, String contents, String comment_contents, String write_date);
    }

    public interface OnItemClickListener2 {
        void onItemClick2(View v, int position, String writer_id, String writer_name);
    }

    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null;
    private OnItemClickListener2 mListener2 = null;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public void setOnItemClickListener2(OnItemClickListener2 listener2) {
        this.mListener2 = listener2;
    }

    public MyListCommentAdapter(Context context, ArrayList<MyCommentData.MyCommentData_list> data) {
        this.mData = data;
        this.mContext = context;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.mylist_comment_item, parent, false);
        ViewHolder vh = new ViewHolder(view);

        if (context instanceof Activity)
            activity = (Activity) context;

        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint({"LongLogTag", "SetTextI18n", "NotifyDataSetChanged", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyCommentData.MyCommentData_list item = mData.get(position);

        try {
            dlog.DlogContext(mContext);
            shardpref = new PreferenceHelper(mContext);
            USER_INFO_NICKNAME = shardpref.getString("USER_INFO_NICKNAME", "");
            USER_INFO_NAME = shardpref.getString("USER_INFO_NAME", "");

            TodayTxt = dc.GET_YEAR + "-" + dc.GET_MONTH + "-" + dc.GET_DAY;
            dlog.i("TodayTxt : " + TodayTxt);

            holder.comment_title.setText(item.getTitle());
            Glide.with(mContext)
                    .load(item.getWriter_img_path())
                    .into(holder.profile_img);

            holder.profile_name.setText(item.getWriter_name());
            holder.write_date.setText(item.getUpdated_at());

            if(item.getEdit_yn().equals("n")){
                holder.comment_edityn.setVisibility(View.GONE);
            }else{
                holder.comment_edityn.setVisibility(View.VISIBLE);
            }
            holder.comment_contents.setText(item.getComment());

            holder.list_setting.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onItemClick(v, position,item.getId(),item.getComment(), item.getWriter_name()
                            ,item.getFeed_id(), item.getWriter_id(), "", "", item.getComment(), item.getUpdated_at());
                }
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
        RelativeLayout list_setting;
        TextView comment_title, profile_name, write_date, comment_contents, comment_edityn;
        ImageView profile_img;
                
        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조
            list_setting        = itemView.findViewById(R.id.list_setting);
            comment_title       = itemView.findViewById(R.id.comment_title);
            profile_name        = itemView.findViewById(R.id.profile_name);
            write_date          = itemView.findViewById(R.id.write_date);
            comment_contents    = itemView.findViewById(R.id.comment_contents);
            profile_img         = itemView.findViewById(R.id.profile_img);
            comment_edityn      = itemView.findViewById(R.id.comment_edityn);

            shardpref = new PreferenceHelper(mContext);
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");

            itemView.setOnClickListener(view -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    MyCommentData.MyCommentData_list item = mData.get(pos);
                }
            });
        }
    }

    public void addItem(MyCommentData.MyCommentData_list data) {
        mData.add(data);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
