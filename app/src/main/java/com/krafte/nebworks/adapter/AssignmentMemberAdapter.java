package com.krafte.nebworks.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.krafte.nebworks.R;
import com.krafte.nebworks.data.PlaceMemberListData;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PreferenceHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AssignmentMemberAdapter extends RecyclerView.Adapter<AssignmentMemberAdapter.ViewHolder> {

    private static final String TAG = "AssignmentMemberAdapter";
    private ArrayList<PlaceMemberListData.PlaceMemberListData_list> mData = null;
    Context mContext;
    PreferenceHelper shardpref;
    int select_flag = -1;
    List<String> memberArray = new ArrayList<>();
    Dlog dlog = new Dlog();
    String users = "";
    int limit_cnt = 0;

    public AssignmentMemberAdapter(Context context, ArrayList<PlaceMemberListData.PlaceMemberListData_list> data) {
        this.mData = data;
        this.mContext = context;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.assignment_member_item, parent, false);
        ViewHolder vh = new ViewHolder(view);

        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AssignmentMemberAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        PlaceMemberListData.PlaceMemberListData_list item = mData.get(position);
        try{
            Log.i(TAG, "item.getThumnail_url() : " + item.getImg_path());
            Log.i(TAG, "item.getEmployment_name() : " + item.getName() + "|" + item.getJikgup());

            users = shardpref.getString("users", "0").replace("[", "").replace("]", "");
            if (!users.isEmpty()) {
                limit_cnt++;
                dlog.i("users : " + users);
                String str = Arrays.toString(users.split(", ")).replace("[", "").replace("]", "");
//            dlog.i("member check : " + item.getId() + " / check " + (str.contains(item.getId())));
                holder.select_radio_btn.setChecked((str.contains(item.getId())));
                if (limit_cnt == mData.size()) {
                    memberArray.addAll(Arrays.asList(users.split(", ")).stream().distinct().collect(Collectors.toList()));
                    dlog.i("limit_cnt memberArray : " + memberArray);
                }
            }
            holder.user_name.setText(item.getName() + "(" + (item.getJikgup().equals("null") ? "" : item.getJikgup()) + ")");
//        Log.i(TAG,"select_radio_btn : " + (position == select_flag));
//        holder.select_radio_btn.setChecked(position == select_flag);

            if (!item.getImg_path().isEmpty()) {
                Glide.with(mContext).load(item.getImg_path())
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(holder.profile_img);
            }


            holder.area_box.setOnClickListener(v -> {
                select_flag = position;

                if (holder.select_radio_btn.isChecked()) {
                    holder.select_radio_btn.setChecked(false);
                    memberArray.remove(item.getId());
                    dlog.i("remove : " + item.getId());
                    dlog.i("memberArray check " + item.getId() + " / " + Arrays.asList(memberArray).contains(item.getId()));
                    dlog.i("OnClick memberArray remove : " + memberArray.removeIf(str -> str.equals(item.getId())));
                } else {
                    holder.select_radio_btn.setChecked(true);
                    memberArray.add(item.getId());
                    dlog.i("add : " + item.getId());
                    dlog.i("OnClick memberArray add : " + memberArray);
                }

                if (mListener != null) {
                    mListener.onItemClick(v, position, memberArray);
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
        TextView user_name;
        RadioButton select_radio_btn;
        RelativeLayout area_box;
        ImageView profile_img;

        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조
//            item_image = itemView.findViewById(R.id.item_image);
            user_name = itemView.findViewById(R.id.user_name);
            select_radio_btn = itemView.findViewById(R.id.select_radio_btn);
            area_box = itemView.findViewById(R.id.area_box);
            profile_img = itemView.findViewById(R.id.profile_img);
            shardpref = new PreferenceHelper(mContext);
            dlog.DlogContext(mContext);


            // 아이템 클릭 이벤트 처리.
            itemView.setOnClickListener(v -> {
                select_radio_btn.setChecked(false);
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    // 리스너 객체의 메서드 호출.
                    PlaceMemberListData.PlaceMemberListData_list item = mData.get(pos);

                }
            });

        }
    }

    public void addItem(PlaceMemberListData.PlaceMemberListData_list data) {
        mData.add(data);
    }


    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position, List<String> memberArray);
    }

}
