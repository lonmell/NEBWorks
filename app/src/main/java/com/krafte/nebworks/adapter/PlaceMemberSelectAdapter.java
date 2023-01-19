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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.GetResultData;
import com.krafte.nebworks.data.WorkPlaceMemberListData;
import com.krafte.nebworks.util.DBConnection;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;

import java.util.ArrayList;

public class PlaceMemberSelectAdapter extends RecyclerView.Adapter<PlaceMemberSelectAdapter.ViewHolder> {
    private static final String TAG = "PlaceMemberSelectAdapter";

    private ArrayList<WorkPlaceMemberListData.WorkPlaceMemberListData_list> mData = null;
    Context mContext;
    PreferenceHelper shardpref;
    DBConnection dbConnection = new DBConnection();
    GetResultData resultData = new GetResultData();
    Dlog dlog = new Dlog();

    String place_id = "";
    String place_owner_id = "";

    public String USER_INFO_ID = "";
    Activity activity;
    PageMoveClass pm = new PageMoveClass();


    public interface OnItemClickListener {
        void onItemClick(View v, int position, String user_id, String user_name);
    }
    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null;
    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public PlaceMemberSelectAdapter(Context context, ArrayList<WorkPlaceMemberListData.WorkPlaceMemberListData_list> data) {
        this.mData = data;
        this.mContext = context;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public PlaceMemberSelectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.select_member_item, parent, false);
        PlaceMemberSelectAdapter.ViewHolder vh = new PlaceMemberSelectAdapter.ViewHolder(view);

        if (context instanceof Activity)
            activity = (Activity) context;

        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PlaceMemberSelectAdapter.ViewHolder holder, int position) {
        WorkPlaceMemberListData.WorkPlaceMemberListData_list item = mData.get(position);

        try {
            dlog.DlogContext(mContext);
            holder.name.setText(item.getName());

            holder.state.setVisibility(View.GONE);
            holder.contract_state.setVisibility(View.GONE);
            holder.linear04.setVisibility(View.GONE);
            if ((item.getPay().equals("null") || item.getPay().isEmpty())) {
                holder.pay.setText("상세정보 입력 전");
                holder.pay.setTextColor(Color.parseColor("#a9a9a9"));
            } else {
                holder.pay.setText(item.getPay());
                holder.pay.setTextColor(Color.parseColor("#000000"));
            }

            if ((item.getJikgup().equals("null") || item.getJikgup().isEmpty())) {
                holder.linear01.setVisibility(View.GONE);
            } else {
                holder.jikgup.setText(item.getJikgup());
            }

            holder.check_area.setOnClickListener(v -> {
                holder.check_area_icon.setBackgroundResource(R.drawable.ic_selectmem_on);
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
        CardView add_detail, state, contract_state;
        LinearLayout linear01, linear02, linear03, linear04;
        RelativeLayout check_area;
        ImageView check_area_icon;

        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조
            name            = itemView.findViewById(R.id.name);
            jikgup          = itemView.findViewById(R.id.jikgup);
            pay             = itemView.findViewById(R.id.pay);
            state_tv        = itemView.findViewById(R.id.state_tv);
            add_detail      = itemView.findViewById(R.id.add_detail);
            state           = itemView.findViewById(R.id.state);
            linear01        = itemView.findViewById(R.id.linear01);
            linear02        = itemView.findViewById(R.id.linear02);
            linear03        = itemView.findViewById(R.id.linear03);
            linear04        = itemView.findViewById(R.id.linear04);
            contract_state  = itemView.findViewById(R.id.contract_state);
            check_area      = itemView.findViewById(R.id.check_area);
            check_area_icon = itemView.findViewById(R.id.check_area_icon);

            shardpref = new PreferenceHelper(mContext);
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");
            place_id = shardpref.getString("place_id", "");
            place_owner_id = shardpref.getString("place_owner_id", "");
            itemView.setOnClickListener(view -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    WorkPlaceMemberListData.WorkPlaceMemberListData_list item = mData.get(pos);
                    if (mListener != null) {
                        mListener.onItemClick(view, pos, item.getId(), item.getName());
                    }
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
