package com.krafte.nebworks.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.data.WorkPlaceMemberListData;
import com.krafte.nebworks.pop.WorkMemberOptionActivity;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

import java.util.ArrayList;

public class WorkplaceMemberAdapter extends RecyclerView.Adapter<WorkplaceMemberAdapter.ViewHolder> {
    private static final String TAG = "WorkplaceMemberAdapter";

    private ArrayList<WorkPlaceMemberListData.WorkPlaceMemberListData_list> mData = null;
    Context mContext;
    PreferenceHelper shardpref;
    FragmentManager fragmentManager;
    DBConnection dbConnection = new DBConnection();
    GetResultData resultData = new GetResultData();
    Dlog dlog = new Dlog();

    boolean option_visible = true;
    int before_pos = 0;
    String item_worktime = "";
    String item_hourpay = "";
    String item_status = "";
    String place_id = "";
    String place_owner_id = "";
    String employment_name = "";

    public String USER_INFO_ID = "";
    Activity activity;
    PageMoveClass pm = new PageMoveClass();


    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }

    public WorkplaceMemberAdapter(Context context, ArrayList<WorkPlaceMemberListData.WorkPlaceMemberListData_list> data, FragmentManager fragmentManager) {
        this.mData = data;
        this.mContext = context;
        this.fragmentManager = fragmentManager;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public WorkplaceMemberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.workplace_member_item, parent, false);
        WorkplaceMemberAdapter.ViewHolder vh = new WorkplaceMemberAdapter.ViewHolder(view);

        if (context instanceof Activity)
            activity = (Activity) context;

        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull WorkplaceMemberAdapter.ViewHolder holder, int position) {
        WorkPlaceMemberListData.WorkPlaceMemberListData_list item = mData.get(position);

        try{
            dlog.DlogContext(mContext);
            holder.name.setText(item.getName());
            if(item.getState().equals("null")){
                //대기중인 직원
                holder.add_detail.setVisibility(View.VISIBLE);
                holder.state.setVisibility(View.GONE);
                holder.linear01.setVisibility(View.GONE);
                holder.linear02.setVisibility(View.GONE);
                holder.linear03.setVisibility(View.GONE);
            }else{
                holder.add_detail.setVisibility(View.GONE);
                if((item.getJikgup().equals("null") || item.getJikgup().isEmpty())){
                    holder.linear01.setVisibility(View.GONE);
                }else{
                    holder.jikgup.setText(item.getJikgup());
                }
                if((item.getPay().equals("null") || item.getPay().isEmpty())){
                    holder.linear02.setVisibility(View.GONE);
                }else{
                    holder.pay.setText(item.getPay());
                }
                if((item.getState().equals("null") || item.getState().isEmpty())){
                    holder.linear03.setVisibility(View.GONE);
                }else{
                    String jejikState = "";
                    if(item.getState().equals("1")){
                        //등록,재직
                        jejikState = "재직";
                    }else if(item.getState().equals("2")){
                        //휴직
                        jejikState = "휴직";
                    }
                    holder.jejik.setText(jejikState);
                }

                if(item.getWorktime().equals("오전")) {
                    holder.state.setCardBackgroundColor(Color.parseColor("#68B0FF"));
                    holder.state_tv.setTextColor(Color.parseColor("#ffffff"));
                }else if(item.getWorktime().equals("주간")) {
                    holder.state.setCardBackgroundColor(Color.parseColor("#44F905"));
                    holder.state_tv.setTextColor(Color.parseColor("#ffffff"));
                }else if(item.getWorktime().equals("야간")) {
                    holder.state.setCardBackgroundColor(Color.parseColor("#1D1D1D"));
                    holder.state_tv.setTextColor(Color.parseColor("#ffffff"));
                }else if(item.getWorktime().equals("주말")) {
                    holder.state.setCardBackgroundColor(Color.parseColor("#FF687A"));
                    holder.state_tv.setTextColor(Color.parseColor("#ffffff"));
                }else {
                    holder.state.setCardBackgroundColor(Color.parseColor("#696969"));
                    holder.state_tv.setTextColor(Color.parseColor("#ffffff"));
                }
                holder.state_tv.setText(item.getWorktime());
            }


            holder.add_detail.setOnClickListener(v -> {
                shardpref.putString("mem_id",item.getId());
                shardpref.putString("mem_name",item.getName());
                shardpref.putString("mem_phone",item.getPhone());
                shardpref.putString("mem_gender",item.getGender());
                shardpref.putString("mem_jumin",item.getJumin());
                shardpref.putString("mem_kind",item.getKind());
                shardpref.putString("mem_join_date",item.getJoin_date());
                shardpref.putString("mem_state",item.getState());
                shardpref.putString("mem_jikgup",item.getJikgup());
                shardpref.putString("mem_pay",item.getPay());
                pm.AddMemberDetail(mContext);
            });
            if(!item.getId().equals(place_owner_id)){
                holder.list_setting.setVisibility(View.VISIBLE);
            }else{
                holder.list_setting.setVisibility(View.GONE);
            }
            holder.list_setting.setOnClickListener(v -> {
                shardpref.putString("mem_id",item.getId());
                shardpref.putString("mem_name",item.getName());
                shardpref.putString("mem_phone",item.getPhone());
                shardpref.putString("mem_gender",item.getGender());
                shardpref.putString("mem_jumin",item.getJumin());
                shardpref.putString("mem_kind",item.getKind());
                shardpref.putString("mem_join_date",item.getJoin_date());
                shardpref.putString("mem_state",item.getState());
                shardpref.putString("mem_jikgup",item.getJikgup());
                shardpref.putString("mem_pay",item.getPay());
                Intent intent = new Intent(mContext, WorkMemberOptionActivity.class);
                intent.putExtra("place_id", place_id);
                intent.putExtra("user_id",item.getId());
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                if (mListener != null) {
                    mListener.onItemClick(v, position);
                }
            });
//            holder.edit_bottom.setOnClickListener(v -> {
//                Intent intent = new Intent(mContext, WorkMemberOptionActivity.class);
//                intent.putExtra("place_id", place_id);
//                intent.putExtra("user_id",item.getId());
//                mContext.startActivity(intent);
//                ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                if (mListener != null) {
//                    mListener.onItemClick(v, position);
//                }
//            });
        }catch (Exception e){
            dlog.i("Exception : " + e);
        }

    } // getItemCount : 전체 데이터의 개수를 리턴

    @Override
    public int getItemCount() {
        return mData.size();
    } // 아이템 뷰를 저장하는 뷰홀더 클래스


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name,jikgup,pay,state_tv,jejik;
        CardView add_detail,state,contract_state;
        RelativeLayout list_setting;
        LinearLayout linear01,linear02,linear03;

        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조
            name            = itemView.findViewById(R.id.name);
            jikgup          = itemView.findViewById(R.id.jikgup);
            pay             = itemView.findViewById(R.id.pay);
            state_tv        = itemView.findViewById(R.id.state_tv);
            add_detail      = itemView.findViewById(R.id.add_detail);
            state           = itemView.findViewById(R.id.state);
            list_setting    = itemView.findViewById(R.id.list_setting);
            linear01        = itemView.findViewById(R.id.linear01);
            linear02        = itemView.findViewById(R.id.linear02);
            linear03        = itemView.findViewById(R.id.linear03);
            jejik           = itemView.findViewById(R.id.jejik);
            contract_state  = itemView.findViewById(R.id.contract_state);

            shardpref = new PreferenceHelper(mContext);
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
            place_id = shardpref.getString("place_id", "");
            place_owner_id = shardpref.getString("place_owner_id", "");
            itemView.setOnClickListener(view -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    WorkPlaceMemberListData.WorkPlaceMemberListData_list item = mData.get(pos);
                }
            });

        }
    }

    public void addItem(WorkPlaceMemberListData.WorkPlaceMemberListData_list data) {
        mData.add(data);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
