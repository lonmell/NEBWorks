package com.krafte.nebworks.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import com.krafte.nebworks.R;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.data.WorkStatusData;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

import java.util.ArrayList;

public class WorkstatusDataListAdapter extends RecyclerView.Adapter<WorkstatusDataListAdapter.ViewHolder> {
    private static final String TAG = "WorkstatusDataListAdapter";

    private ArrayList<WorkStatusData.WorkStatusData_list> mData = null;
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
    String store_no = "";
    String employment_name = "";
    String place_owner_id = "";
    public String USER_INFO_ID = "";
    Activity activity;
    PageMoveClass pm = new PageMoveClass();

    public WorkstatusDataListAdapter(Context context, ArrayList<WorkStatusData.WorkStatusData_list> data) {
        this.mData = data;
        this.mContext = context;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public WorkstatusDataListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.workstatus_item, parent, false);
        WorkstatusDataListAdapter.ViewHolder vh = new WorkstatusDataListAdapter.ViewHolder(view);

        if (context instanceof Activity)
            activity = (Activity) context;

        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull WorkstatusDataListAdapter.ViewHolder holder, int position) {
        WorkStatusData.WorkStatusData_list item = mData.get(position);

        try{
            Glide.with(mContext).load(item.getImg_path())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.certi01)
                    .skipMemoryCache(true)
                    .into(holder.thumnail_in);

            holder.user_name.setText(item.getName() + (place_owner_id.equals(item.getId())?"(관리자)":""));
            holder.department.setText(item.getDepartment() + " " + item.getPosition());


            if(item.getCommute().equals("0")){
                holder.status.setText("근무 중");
                holder.status.setTextColor(Color.parseColor("#5B93FF"));
            }else if(item.getCommute().equals("1")){
                holder.status.setText("퇴근");
                holder.status.setTextColor(Color.parseColor("#FF0000"));
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

        RelativeLayout area_total;
        ImageView thumnail_in;
        TextView user_name,department,status;
        LinearLayout list_setting;

        ViewHolder(View itemView) {
            super(itemView);

            area_total = itemView.findViewById(R.id.area_total);
            thumnail_in = itemView.findViewById(R.id.thumnail_in);
            user_name = itemView.findViewById(R.id.user_name);
            department = itemView.findViewById(R.id.department);
            status = itemView.findViewById(R.id.status);
            list_setting = itemView.findViewById(R.id.list_setting);

            dlog.DlogContext(mContext);
            shardpref = new PreferenceHelper(mContext);
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
            place_owner_id= shardpref.getString("place_owner_id", "");
            itemView.setOnClickListener(view -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    WorkStatusData.WorkStatusData_list item = mData.get(pos);
                    shardpref.putString("search_id",item.getId());
                    shardpref.putString("search_name",item.getName());
                    shardpref.putString("search_kind",item.getKind());
                    shardpref.putString("search_img_path",item.getImg_path());
                    shardpref.putString("search_department",item.getDepartment());
                    shardpref.putString("search_position",item.getPosition());
                    shardpref.putString("search_commute",item.getCommute());

                    pm.WorkStateDetailGo(mContext);
                }
            });

            itemView.setOnLongClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    WorkStatusData.WorkStatusData_list item = mData.get(pos);

                }
                return true;
            });
        }
    }

    public void addItem(WorkStatusData.WorkStatusData_list data) {
        mData.add(data);
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
