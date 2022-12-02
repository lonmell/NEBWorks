package com.krafte.nebworks.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.krafte.nebworks.R;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.data.WorkStatusTapData;
import com.krafte.nebworks.pop.WorkMemberOptionActivity;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorkTapMemberAdapter extends RecyclerView.Adapter<WorkTapMemberAdapter.ViewHolder> {
    private static final String TAG = "WorkTapMemberAdapter";

    private ArrayList<WorkStatusTapData.WorkStatusTapData_list> mData = null;
    Context mContext;
    PreferenceHelper shardpref;
    FragmentManager fragmentManager;
    DBConnection dbConnection = new DBConnection();
    GetResultData resultData = new GetResultData();
    Dlog dlog = new Dlog();

    String place_id = "";
    String place_owner_id = "";
    String state = "";
    public String USER_INFO_ID = "";
    Activity activity;
    PageMoveClass pm = new PageMoveClass();
    int lastpos = -99;
    List<String> yoil = new ArrayList<>();


    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    // 리스너 객체 참조를 저장하는 변수
    private WorkTapMemberAdapter.OnItemClickListener mListener = null;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(WorkTapMemberAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }

    public WorkTapMemberAdapter(Context context, ArrayList<WorkStatusTapData.WorkStatusTapData_list> data, FragmentManager fragmentManager) {
        this.mData = data;
        this.mContext = context;
        this.fragmentManager = fragmentManager;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public WorkTapMemberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.workpwork_member_item, parent, false);
        WorkTapMemberAdapter.ViewHolder vh = new WorkTapMemberAdapter.ViewHolder(view);

        if (context instanceof Activity)
            activity = (Activity) context;

        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull WorkTapMemberAdapter.ViewHolder holder, int position) {
        WorkStatusTapData.WorkStatusTapData_list item = mData.get(position);
        try {
            dlog.DlogContext(mContext);
            holder.name.setText(item.getName());

            Glide.with(mContext).load(item.getImg_path())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(holder.user_thumnail);

            holder.jikgup.setText(item.getJikgup().equals("null") ? "미정" : item.getJikgup());

            yoil.addAll(Arrays.asList(item.getYoil().split(",")));

            if (item.getKind().equals("0")) {
                state = "근무 중";
                holder.state_tv.setTextColor(R.color.blue);
                holder.state.setCardBackgroundColor(Color.parseColor("#E0EAFB"));
            } else if (item.getKind().equals("1")) {
                state = "퇴근";
                holder.state_tv.setTextColor(Color.parseColor("#696969"));
                holder.state.setCardBackgroundColor(Color.parseColor("#dbdbdb"));
            } else if (item.getKind().equals("2")) {
                //출근날인데 출근 안한거
                state = item.getCommuting();
                holder.state_tv.setTextColor(Color.parseColor("#DD6540"));
                holder.state.setCardBackgroundColor(Color.parseColor("#FCF0EC"));
            }
            holder.pay.setText(item.getYoil().isEmpty() ? "" : item.getYoil());
            holder.state_tv.setText(state);

            holder.list_setting.setOnClickListener(v -> {
                shardpref.putString("mem_id", item.getId());
                shardpref.putString("mem_name", item.getName());
                Intent intent = new Intent(mContext, WorkMemberOptionActivity.class);
                intent.putExtra("place_id", place_id);
                intent.putExtra("user_id", item.getId());
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                if (mListener != null) {
                    mListener.onItemClick(v, position);
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
        TextView name, jikgup, pay, state_tv;
        CardView add_detail, state, edit_linear;
        RelativeLayout list_setting, item_total;
        LinearLayout linear01, linear02;
        ImageView user_thumnail;

        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조
            item_total = itemView.findViewById(R.id.item_total);
            name = itemView.findViewById(R.id.name);
            jikgup = itemView.findViewById(R.id.jikgup);
            pay = itemView.findViewById(R.id.pay);
            state_tv = itemView.findViewById(R.id.state_tv);
            add_detail = itemView.findViewById(R.id.add_detail);
            state = itemView.findViewById(R.id.state);
            list_setting = itemView.findViewById(R.id.list_setting);
            linear01 = itemView.findViewById(R.id.linear01);
            linear02 = itemView.findViewById(R.id.linear02);
            user_thumnail = itemView.findViewById(R.id.user_thumnail);


            shardpref = new PreferenceHelper(mContext);
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
            place_id = shardpref.getString("place_id", "");
            place_owner_id = shardpref.getString("place_owner_id", "");
            itemView.setOnClickListener(view -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                }
            });

        }
    }

    public void addItem(WorkStatusTapData.WorkStatusTapData_list data) {
        mData.add(data);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
