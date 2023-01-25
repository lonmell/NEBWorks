package com.krafte.nebworks.adapter;

import android.annotation.SuppressLint;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.krafte.nebworks.R;
import com.krafte.nebworks.data.WorkPlaceMemberListData;
import com.krafte.nebworks.util.DateCurrent;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MemberListPopAdapter extends RecyclerView.Adapter<MemberListPopAdapter.ViewHolder> {
    private static final String TAG = "MemberListPopAdapter";
    private ArrayList<WorkPlaceMemberListData.WorkPlaceMemberListData_list> mData = null;
    Context mContext;
    PreferenceHelper shardpref;
    Dlog dlog = new Dlog();
    PageMoveClass pm = new PageMoveClass();
    RetrofitConnect rc = new RetrofitConnect();
    DateCurrent dc = new DateCurrent();

    List<String> item_user_id;
    List<String> item_user_name;
    List<String> item_user_img;
    List<String> item_user_position;

    String getuser_id = "";
    String getuser_name = "";
    String getuser_img  = "";
    String getuser_position = "";
    int kind = 0;
    public MemberListPopAdapter(Context context, ArrayList<WorkPlaceMemberListData.WorkPlaceMemberListData_list> data,int kind) {
        this.mData = data;
        this.mContext = context;
        this.kind = kind;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public MemberListPopAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.memberlist_popitem, parent, false);
        MemberListPopAdapter.ViewHolder vh = new MemberListPopAdapter.ViewHolder(view);
        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MemberListPopAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        WorkPlaceMemberListData.WorkPlaceMemberListData_list item = mData.get(position);
        try{
            dlog.i("mData : " + mData.size());
            Glide.with(mContext).load(item.getImg_path().replace("[", "").replace("]", ""))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(holder.profile_img);
            holder.name.setText(item.getName());
            holder.jikgup.setText(item.getJikgup().equals("null")?"미정":item.getJikgup().replace("[", "").replace("]", ""));
            if(kind == 0){
                if(!getuser_id.isEmpty()){
                    if(getuser_id.contains(item.getId())){
                            holder.select_member_icon.setBackgroundResource(R.drawable.task_member_check);
                            holder.view.setVisibility(View.VISIBLE);
                    }
                }
                holder.confirm_date.setVisibility(View.GONE);
                holder.item_total.setBackgroundColor(Color.parseColor("#FFFFFF"));
                holder.item_total.setOnClickListener(v -> {
                    if(holder.view.getVisibility() == View.VISIBLE){
                        holder.view.setVisibility(View.INVISIBLE);
                        holder.select_member_icon.setBackgroundResource(R.drawable.task_member_check_none);
                        item_user_id.remove(item.getId());
                        item_user_name.remove(item.getName());
                        item_user_img.remove(item.getImg_path());
                        item_user_position.remove(item.getJikgup());
                    }else{
                        holder.view.setVisibility(View.VISIBLE);
                        holder.select_member_icon.setBackgroundResource(R.drawable.task_member_check);
                        item_user_id.add(item.getId());
                        item_user_name.add(item.getName());
                        item_user_img.add(item.getImg_path());
                        item_user_position.add(item.getJikgup());
                    }
                    dlog.i("item_user_id : " + item_user_id);
                    dlog.i("item_user_name : " + item_user_name);
                    dlog.i("item_user_img : " + item_user_img);
                    dlog.i("item_user_position : " + item_user_position);
                    shardpref.putString("item_user_id", String.valueOf(item_user_id).replace("[","").replace("]","").replace(" ",""));
                    shardpref.putString("item_user_name", String.valueOf(item_user_name).replace("[","").replace("]","").replace(" ",""));
                    shardpref.putString("item_user_img", String.valueOf(item_user_img).replace("[","").replace("]","").replace(" ",""));
                    shardpref.putString("item_user_position", String.valueOf(item_user_position).replace("[","").replace("]","").replace(" ",""));
                });
            }else if(kind == 1){
                holder.confirm_date.setVisibility(View.VISIBLE);
                holder.confirm_date.setText(dc.GET_YEAR + "." + dc.GET_MONTH + "." + dc.GET_DAY + " 확인");
                holder.select_member_icon.setVisibility(View.INVISIBLE);
                holder.item_total.setClickable(false);
                holder.item_total.setBackgroundResource(R.drawable.grayback_gray_round);
                holder.underline.setVisibility(View.GONE);
            }

        }catch (Exception e){
            e.printStackTrace();
        }


    } // getItemCount : 전체 데이터의 개수를 리턴

    @Override
    public int getItemCount() {
        return mData.size();
    } // 아이템 뷰를 저장하는 뷰홀더 클래스

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView profile_img,select_member_icon;
        TextView name,jikgup,confirm_date;
        RelativeLayout select_member_area,item_total;
        LinearLayout underline;
        View view;

        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조
            dlog.DlogContext(mContext);
            shardpref = new PreferenceHelper(mContext);

            profile_img            = itemView.findViewById(R.id.profile_img);
            select_member_icon     = itemView.findViewById(R.id.select_member_icon);
            name                   = itemView.findViewById(R.id.name);
            jikgup                 = itemView.findViewById(R.id.jikgup);
            select_member_area     = itemView.findViewById(R.id.select_member_area);
            item_total             = itemView.findViewById(R.id.item_total);
            confirm_date           = itemView.findViewById(R.id.confirm_date);
            underline              = itemView.findViewById(R.id.underline);
            view                   = itemView.findViewById(R.id.view);

            item_user_id          = new ArrayList<>();
            item_user_name        = new ArrayList<>();
            item_user_img         = new ArrayList<>();
            item_user_position    = new ArrayList<>();
            item_user_id.clear();
            item_user_name.clear();
            item_user_img.clear();
            item_user_position.clear();

            getuser_id = shardpref.getString("item_user_id","").replace("[","").replace("]","").replace(" ","").trim();
            getuser_name = shardpref.getString("item_user_name","").replace("[","").replace("]","").replace(" ","").trim();
            getuser_img = shardpref.getString("item_user_img","").replace("[","").replace("]","").replace(" ","").trim();
            getuser_position = shardpref.getString("item_user_position","").replace("[","").replace("]","").replace(" ","").trim();

            if(!getuser_id.isEmpty()){
                item_user_id.addAll(Arrays.asList(getuser_id.split(",")));
            }
            if(!getuser_name.isEmpty()){
                item_user_name.addAll(Arrays.asList(getuser_name.split(",")));
            }
            if(!getuser_img.isEmpty()){
                item_user_img.addAll(Arrays.asList(getuser_img.split(",")));
            }
            if(!getuser_position.isEmpty()){
                item_user_position.addAll(Arrays.asList(getuser_position.split(",")));
            }
            itemView.setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    // 리스너 객체의 메서드 호출.
                    WorkPlaceMemberListData.WorkPlaceMemberListData_list item = mData.get(pos);

                }
            });
        }
    }

    public void addItem(WorkPlaceMemberListData.WorkPlaceMemberListData_list data) {
        mData.add(data);
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }
}
