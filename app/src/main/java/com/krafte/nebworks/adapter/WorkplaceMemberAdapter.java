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

    String place_id = "";
    String place_owner_id = "";

    String USER_INFO_ID = "";
    String USER_INFO_AUTH = "";
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


    public interface OnItemClickListener2 {
        void onItemClick(View v, int position, int kind) ;
    }
    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener2 mListener2 = null ;
    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener2(OnItemClickListener2 listener2) {
        this.mListener2 = listener2 ;
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
            holder.name.setText(item.getName());
            if(item.getKind().equals("0")){
                //승인대기상태
                holder.linear04.setVisibility(View.VISIBLE);
                holder.linear01.setVisibility(View.GONE);
                holder.linear02.setVisibility(View.GONE);
                holder.linear03.setVisibility(View.GONE);
                holder.contract_state.setVisibility(View.GONE);
                holder.state.setVisibility(View.GONE);

                holder.contract_area_tv.setText("거절");
                holder.contract_area.setOnClickListener(v -> {
                    if (mListener2 != null) {
                        mListener2.onItemClick(v, position,1);
                    }
                });
                holder.add_detail_tv.setText("수락");
                holder.add_detail.setOnClickListener(v -> {
                    if (mListener2 != null) {
                        mListener2.onItemClick(v, position,2);
                    }
                });
            }else{
                dlog.DlogContext(mContext);
                if(item.getState().equals("null")){
                    //직원 상세정보가 없을때
                    holder.add_detail.setVisibility(View.VISIBLE);
                    holder.linear04.setVisibility(View.VISIBLE);

                    holder.linear03.setVisibility(View.GONE);
                    holder.contract_state.setVisibility(View.GONE);
                    holder.state.setVisibility(View.GONE);
                }else{
                    if(item.getJikgup().equals("관리자")){
                        holder.add_detail.setVisibility(View.VISIBLE);
                        holder.state.setVisibility(View.GONE);
                        holder.linear02.setVisibility(View.GONE);
                        holder.linear03.setVisibility(View.GONE);
                        holder.linear04.setVisibility(View.GONE);
                        holder.contract_state.setVisibility(View.GONE);
                    }else{
                        if((item.getPay().equals("null") || item.getPay().isEmpty())){
                            holder.pay.setText("상세정보 입력 전");
                            holder.pay.setTextColor(Color.parseColor("#a9a9a9"));
                        }else{
                            holder.pay.setText(item.getPay());
                            holder.pay.setTextColor(Color.parseColor("#000000"));
                        }
                        if((item.getState().equals("null") || item.getState().isEmpty())){
                            holder.jejik.setText("상세정보 입력 전");
                            holder.jejik.setTextColor(Color.parseColor("#a9a9a9"));
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
                            holder.jejik.setTextColor(Color.parseColor("#000000"));
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
                    holder.add_detail.setVisibility(View.GONE);
                    if((item.getJikgup().equals("null") || item.getJikgup().isEmpty())){
                        holder.linear01.setVisibility(View.GONE);
                    }else{
                        holder.jikgup.setText(item.getJikgup());
                    }
                    if(item.getContract_cnt().equals("1")){
                        holder.contract_state.setCardBackgroundColor(Color.parseColor("#68B0FF"));
                        holder.contract_state_tv.setTextColor(Color.parseColor("#000000"));
                    }
                }


                holder.add_detail.setOnClickListener(v -> {
                    shardpref.putString("mem_id",item.getId());
                    shardpref.putString("mem_account",item.getAccount());
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

                if(USER_INFO_AUTH.equals("0")){
                    holder.list_setting.setVisibility(View.VISIBLE);
                }else{
                    holder.list_setting.setVisibility(View.INVISIBLE);
                    holder.list_setting.setClickable(false);
                    holder.list_setting.setEnabled(false);
                }
                holder.list_setting.setOnClickListener(v -> {
                    shardpref.putString("mem_id",item.getId());
                    shardpref.putString("mem_account",item.getAccount());
                    shardpref.putString("mem_name",item.getName());
                    shardpref.putString("mem_phone",item.getPhone());
                    shardpref.putString("mem_gender",item.getGender());
                    shardpref.putString("mem_jumin",item.getJumin());
                    shardpref.putString("mem_kind",item.getKind());
                    shardpref.putString("mem_join_date",item.getJoin_date());
                    shardpref.putString("mem_state",item.getState());
                    shardpref.putString("mem_jikgup",item.getJikgup());
                    shardpref.putString("mem_pay",item.getPay());
                    shardpref.putString("remote", "member");
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
            }
        }catch (Exception e){
            dlog.i("Exception : " + e);
        }

    } // getItemCount : 전체 데이터의 개수를 리턴

    @Override
    public int getItemCount() {
        return mData.size();
    } // 아이템 뷰를 저장하는 뷰홀더 클래스


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name,jikgup,pay,state_tv,jejik,contract_area_tv,add_detail_tv,contract_state_tv;
        CardView add_detail,state,contract_state,contract_area;
        RelativeLayout list_setting;
        LinearLayout linear01,linear02,linear03,linear04;

        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조
            name                = itemView.findViewById(R.id.name);
            jikgup              = itemView.findViewById(R.id.jikgup);
            pay                 = itemView.findViewById(R.id.pay);
            state_tv            = itemView.findViewById(R.id.state_tv);

            state               = itemView.findViewById(R.id.state);
            list_setting        = itemView.findViewById(R.id.list_setting);
            linear01            = itemView.findViewById(R.id.linear01);
            linear02            = itemView.findViewById(R.id.linear02);
            linear03            = itemView.findViewById(R.id.linear03);
            linear04            = itemView.findViewById(R.id.linear04);
            jejik               = itemView.findViewById(R.id.jejik);
            contract_state      = itemView.findViewById(R.id.contract_state);

            add_detail          = itemView.findViewById(R.id.add_detail);
            add_detail_tv       = itemView.findViewById(R.id.add_detail_tv);

            contract_area       = itemView.findViewById(R.id.contract_area);
            contract_area_tv    = itemView.findViewById(R.id.contract_area_tv);
            contract_state_tv   = itemView.findViewById(R.id.contract_state_tv);

            shardpref      = new PreferenceHelper(mContext);
            USER_INFO_ID   = shardpref.getString("USER_INFO_ID", "");
            place_id       = shardpref.getString("place_id", "");
            place_owner_id = shardpref.getString("place_owner_id", "");
            USER_INFO_AUTH = shardpref.getString("USER_INFO_AUTH", "");

            itemView.setOnClickListener(view -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
//                    stub_place_id = shardpref.getString("stub_place_id", "0");
//                    stub_user_id = shardpref.getString("stub_user_id", "0");
//                    stub_user_account = shardpref.getString("stub_user_account", "");
//                    change_place_name = shardpref.getString("change_place_name", "");
                    WorkPlaceMemberListData.WorkPlaceMemberListData_list item = mData.get(pos);
                    shardpref.putString("stub_place_id",place_id);
                    shardpref.putString("stub_user_id",item.getId());
                    shardpref.putString("stub_user_account",item.getAccount());
                    shardpref.putString("change_place_name",item.getPlace_name());
                    pm.MemberDetail(mContext);
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
