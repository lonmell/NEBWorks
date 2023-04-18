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
import com.krafte.nebworks.data.WorkPlaceMemberListData;
import com.krafte.nebworks.pop.WorkMemberOptionActivity;
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
//            if(USER_INFO_AUTH.equals("1") && item.getKind().equals("0")){
//                holder.total_item.setVisibility(View.GONE);
//            }
            dlog.DlogContext(mContext);
            /*
             * 직급
             * -- 대표님 : 점주가 생성
             *  -- 관리자 : 근로자가 생성
             */
            dlog.i("WorkplaceMemberAdapter getkind : " + item.getKind());
            holder.name.setText(item.getName());

            Glide.with(mContext).load(item.getImg_path())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(holder.member_profile);
            if(item.getKind().equals("0")){
                //승인대기상태
//                if(place_owner_id.equals(USER_INFO_ID)){
//
//                }else{
//
//                }
                holder.linear04.setVisibility(place_owner_id.equals(USER_INFO_ID)?View.VISIBLE:View.GONE);
                holder.linear01.setVisibility(View.GONE);
                holder.linear02.setVisibility(View.GONE);
                holder.contract_state.setVisibility(View.GONE);
                holder.state.setVisibility(View.GONE);
                holder.list_setting.setVisibility(View.INVISIBLE);

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
            }else if(item.getKind().equals("4")){
                //퇴직상태
                holder.linear01.setVisibility(View.GONE);
                holder.linear02.setVisibility(View.GONE);
                holder.state_tv.setText("퇴직");
                holder.list_setting.setVisibility(View.INVISIBLE);
                holder.contract_area.setVisibility(View.VISIBLE);
                holder.add_detail.setVisibility(View.VISIBLE);

                holder.contract_area.setCardBackgroundColor(Color.parseColor("#1483FE"));
                holder.contract_area_tv.setText("복직");
                holder.contract_area_tv.setTextColor(Color.parseColor("#000000"));

                holder.add_detail.setCardBackgroundColor(Color.parseColor("#FF3D00"));
                holder.add_detail_tv.setText("직원삭제");
                holder.add_detail_tv.setTextColor(Color.parseColor("#ffffff"));

                holder.linear04.setVisibility(place_owner_id.equals(USER_INFO_ID)?View.VISIBLE:View.GONE);
                holder.contract_area.setOnClickListener(v -> {
                    if (mListener2 != null) {
                        mListener2.onItemClick(v, position,2);
                    }
                });
                holder.add_detail_tv.setOnClickListener(v -> {
                    if (mListener2 != null) {
                        mListener2.onItemClick(v, position,3);
                    }
                });

            }else{
                dlog.DlogContext(mContext);
                if(item.getState().equals("null") && item.getContract_cnt().equals("0")){
                    //직원 상세정보가 없을때
                    holder.add_detail.setVisibility(View.VISIBLE);
                    holder.linear04.setVisibility(place_owner_id.equals(USER_INFO_ID)?View.VISIBLE:View.GONE);

                    holder.contract_state_tv.setText("근로계약서 미진행");

//                    holder.contract_state.setVisibility(View.GONE);
                    holder.state.setVisibility(View.GONE);
                }else{
                    if(item.getJikgup().equals("대표님") || item.getJikgup().equals("관리자")){
                        holder.add_detail.setVisibility(View.VISIBLE);
                        holder.state.setVisibility(View.GONE);
                        holder.linear02.setVisibility(View.GONE);
                        holder.linear04.setVisibility(View.GONE);
                        holder.contract_state.setVisibility(View.GONE);
                        dlog.i("item.getJikgup() : " + item.getJikgup());
                    }else{
                        if((item.getPay().equals("null") || item.getPay().isEmpty())){
                            holder.pay.setText("상세정보 입력 전");
                            holder.pay.setTextColor(Color.parseColor("#a9a9a9"));
                        }else{
                            holder.pay.setText(item.getPay());
                            holder.pay.setTextColor(Color.parseColor("#000000"));
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
                        holder.contract_state_tv.setText("근로계약서 진행");
                    } else {
                        holder.contract_state_tv.setText("근로계약서 미진행");
                    }
                }

                if(USER_INFO_AUTH.equals("0")){
                    holder.list_setting.setVisibility(View.VISIBLE);
                    holder.list_setting.setClickable(true);
                    holder.list_setting.setEnabled(true);
                }else{
                    holder.list_setting.setVisibility(View.INVISIBLE);
                    holder.list_setting.setClickable(false);
                    holder.list_setting.setEnabled(false);
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
                    dlog.i("------list_setting------");
                    dlog.i("mem_id : "          + item.getId());
                    dlog.i("mem_account : "     + item.getAccount());
                    dlog.i("mem_name : "        + item.getName());
                    dlog.i("mem_phone : "       + item.getPhone());
                    dlog.i("mem_gender : "      + item.getGender());
                    dlog.i("mem_jumin : "       + item.getJumin());
                    dlog.i("mem_kind : "        + item.getKind());
                    dlog.i("mem_join_date : "   + item.getJoin_date());
                    dlog.i("mem_state : "       + item.getState());
                    dlog.i("mem_jikgup : "      + item.getJikgup());
                    dlog.i("mem_pay : "         + item.getPay());
                    dlog.i("------list_setting------");
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
        TextView name,jikgup,pay,state_tv,contract_area_tv,add_detail_tv,contract_state_tv;
        CardView add_detail,state,contract_state,contract_area;
        RelativeLayout list_setting, total_item;
        LinearLayout linear01,linear02,linear04;
        ImageView member_profile;

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
            linear04            = itemView.findViewById(R.id.linear04);
            contract_state      = itemView.findViewById(R.id.contract_state);

            add_detail          = itemView.findViewById(R.id.add_detail);
            add_detail_tv       = itemView.findViewById(R.id.add_detail_tv);

            contract_area       = itemView.findViewById(R.id.contract_area);
            contract_area_tv    = itemView.findViewById(R.id.contract_area_tv);
            contract_state_tv   = itemView.findViewById(R.id.contract_state_tv);
            member_profile      = itemView.findViewById(R.id.member_profile);
            total_item          = itemView.findViewById(R.id.total_item);

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
                    if (mListener2 != null) {
                        mListener2.onItemClick(view, pos,4);
                    }
//                    WorkPlaceMemberListData.WorkPlaceMemberListData_list item = mData.get(pos);
//                    shardpref.putString("stub_place_id",place_id);
//                    shardpref.putString("stub_user_id",item.getId());
//                    shardpref.putString("stub_user_name",item.getName());
//                    shardpref.putString("stub_user_account",item.getAccount());
//                    shardpref.putString("change_place_name",item.getPlace_name());
//                    pm.MemberDetail(mContext);
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
