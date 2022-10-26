package com.krafte.kogas.adapter;

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
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.krafte.kogas.R;
import com.krafte.kogas.data.GetResultData;
import com.krafte.kogas.data.WorkPlaceMemberListData;
import com.krafte.kogas.pop.WorkMemberOptionActivity;
import com.krafte.kogas.util.DBConnection;
import com.krafte.kogas.util.Dlog;
import com.krafte.kogas.util.PageMoveClass;
import com.krafte.kogas.util.PreferenceHelper;

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

    boolean item_flag = true;
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

            employment_name = item.getName();
            Glide.with(mContext).load(item.getImg_path())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(holder.profile_img);
            holder.item_employee_name.setText(employment_name);

            if(item.getEmployee_no().equals("null") || item.getEmployee_no().isEmpty()){
                holder.member_inputdate.setText("사번 : ");
                holder.member_inputdate.setTextColor(Color.parseColor("#C2C2C2"));
            }else{
                holder.member_inputdate.setText("사번 : " + item.getEmployee_no());
                holder.member_inputdate.setTextColor(Color.parseColor("#000000"));
            }

            if(item.getDepartment().equals("null") || item.getDepartment().isEmpty()){
                holder.member_jikgup.setText("부서 : ");
                holder.member_jikgup.setTextColor(Color.parseColor("#C2C2C2"));
            }else{
                holder.member_jikgup.setText("부서 : " + item.getDepartment());
                holder.member_jikgup.setTextColor(Color.parseColor("#000000"));
            }

            if(item.getPosition().equals("null") || item.getPosition().isEmpty()){
                holder.member_contract.setText("직급 : ");
                holder.member_contract.setTextColor(Color.parseColor("#C2C2C2"));
            }else{
                holder.member_contract.setText("직급 : " + item.getPosition());
                holder.member_contract.setTextColor(Color.parseColor("#000000"));
            }

            holder.member_area.setOnClickListener(v -> {

            });

            if(!item.getId().equals(place_owner_id)){
                holder.edit_bottom.setVisibility(View.VISIBLE);
            }else{
                holder.edit_bottom.setVisibility(View.GONE);
            }
            holder.edit_bottom.setOnClickListener(v -> {
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
        }catch (Exception e){
            dlog.i("Exception : " + e);
        }

    } // getItemCount : 전체 데이터의 개수를 리턴

    @Override
    public int getItemCount() {
        return mData.size();
    } // 아이템 뷰를 저장하는 뷰홀더 클래스


    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout member_area;
        ImageView profile_img;
        TextView item_employee_name,member_jikgup;
        TextView member_inputdate,member_contract;
        LinearLayout edit_bottom;

        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조
            profile_img = itemView.findViewById(R.id.profile_img);
            item_employee_name = itemView.findViewById(R.id.item_employee_name);

            member_jikgup = itemView.findViewById(R.id.member_jikgup);
            member_inputdate = itemView.findViewById(R.id.member_inputdate);
            member_contract = itemView.findViewById(R.id.member_contract);

            edit_bottom = itemView.findViewById(R.id.edit_bottom);
            member_area = itemView.findViewById(R.id.member_area);

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
