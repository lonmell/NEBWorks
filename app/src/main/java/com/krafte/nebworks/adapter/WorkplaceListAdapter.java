package com.krafte.nebworks.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
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

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.PlaceListData;
import com.krafte.nebworks.dataInterface.MainContentsInterface;
import com.krafte.nebworks.pop.PlaceBottomNaviActivity;
import com.krafte.nebworks.util.Dlog;
import com.krafte.nebworks.util.PageMoveClass;
import com.krafte.nebworks.util.PreferenceHelper;
import com.krafte.nebworks.util.RetrofitConnect;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/*
 * 2022-10-04 방창배 작성
 * */
public class WorkplaceListAdapter extends RecyclerView.Adapter<WorkplaceListAdapter.ViewHolder> {

    private static final String TAG = "WorkplaceListAdapter";
    private ArrayList<PlaceListData.PlaceListData_list> mData = null;
    Context mContext;
    FragmentManager fragmentManager;
    PreferenceHelper shardpref;
    String USER_INFO_AUTH = "";
    String USER_INFO_ID = "";
    PageMoveClass pm = new PageMoveClass();
    Dlog dlog = new Dlog();
    Activity activity;
    RetrofitConnect rc = new RetrofitConnect();

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public WorkplaceListAdapter(Context context, ArrayList<PlaceListData.PlaceListData_list> data) {
        this.mData = data;
        this.mContext = context;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴


    //--옵션창 열기
    public interface OnClickOptionListener {
        void onClick(View v);
    }

    private OnClickOptionListener Olistener = null;

    public void setOnClickOption(OnClickOptionListener Olistener) {
        this.Olistener = Olistener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.workplace_item, parent, false);
        WorkplaceListAdapter.ViewHolder vh = new WorkplaceListAdapter.ViewHolder(view);

        if (context instanceof Activity)
            activity = (Activity) context;

        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlaceListData.PlaceListData_list item = mData.get(position);

        try {
            holder.first_line.setVisibility(position == 0?View.VISIBLE:View.GONE);

            dlog.i("item.getImg_path() : " + item.getImg_path());
//            Glide.with(mContext).load(item.getImg_path().equals("null")?"":item.getImg_path())
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .skipMemoryCache(true)
//                    .placeholder(R.drawable.identificon)
//                    .into(holder.store_thumnail);
//            if(item.getImg_path().equals("null")){
//                holder.i_o_btn.setVisibility(View.GONE);
//            }
            holder.i_o_btn.setVisibility(View.GONE);
            holder.title.setText(item.getName());

            if (item.getOwner_id().equals(USER_INFO_ID)) {
                //관리자일경우
                holder.list_img_area.setVisibility(View.VISIBLE);
            } else {
                holder.list_img_area.setVisibility(View.GONE);
            }

            if (item.getSave_kind().equals("0")) {
                holder.address.setText("임시저장 중");
                if (item.getOwner_id().equals(USER_INFO_ID)) {
                    //본인이 생성한 매장
                    holder.money_area.setVisibility(View.INVISIBLE);
                    holder.store_kind_state.setVisibility(View.VISIBLE);
                    holder.item_area.setCardBackgroundColor(Color.parseColor("#ffffff"));
                } else {
                    //다른사람이 생성한 매장 ( 초대 받았을 경우 )
                    if (item.getAccept_state().equals("0")) {
                        holder.money_area.setVisibility(View.INVISIBLE);
                        holder.store_kind_state.setVisibility(View.VISIBLE);
                        holder.state_tv.setText("작성하기");
                        holder.item_area.setCardBackgroundColor(Color.parseColor("#F2F2F2"));
                        holder.list_setting.setVisibility(View.INVISIBLE);
                        holder.list_setting.setClickable(false);
                    } else {
                        holder.money_area.setVisibility(View.INVISIBLE);
                        holder.store_kind_state.setVisibility(View.VISIBLE);
                        holder.item_area.setCardBackgroundColor(Color.parseColor("#F2F2F2"));
                        holder.list_setting.setVisibility(View.INVISIBLE);
                        holder.list_setting.setClickable(false);
                    }
                }
            } else if (item.getSave_kind().equals("1")) {
                holder.address.setText(item.getAddress());
                if (item.getOwner_id().equals(USER_INFO_ID)) {
                    //본인이 생성한 매장
                    holder.money_area.setVisibility(View.VISIBLE);
                    holder.store_kind_state.setVisibility(View.INVISIBLE);
                    holder.item_area.setCardBackgroundColor(Color.parseColor("#ffffff"));
                } else {
                    //다른사람이 생성한 매장 ( 초대 받았을 경우 )
                    if (item.getAccept_state().equals("0")) {
                        holder.money_area.setVisibility(View.INVISIBLE);
                        holder.store_kind_state.setVisibility(View.VISIBLE);
                        holder.state_tv.setText("승인대기중");
                        holder.item_area.setCardBackgroundColor(Color.parseColor("#ffffff"));
                        holder.list_setting.setVisibility(View.INVISIBLE);
                        holder.list_setting.setClickable(false);
                    } else {
                        holder.money_area.setVisibility(View.VISIBLE);
                        holder.store_kind_state.setVisibility(View.INVISIBLE);
                        holder.item_area.setCardBackgroundColor(Color.parseColor("#ffffff"));
                        holder.list_setting.setVisibility(View.INVISIBLE);
                        holder.list_setting.setClickable(false);
                    }
                }
            }

            holder.item_peoplecnt.setText(item.getTotal_cnt());
            holder.list_setting.setOnClickListener(v -> {
                shardpref.putString("place_id", item.getId());
                shardpref.putString("place_name", item.getName());
                shardpref.putString("place_owner_id", item.getOwner_id());
                Intent intent = new Intent(mContext, PlaceBottomNaviActivity.class);
                intent.putExtra("left_btn_txt", "닫기");
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.translate_up, 0);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            });
            PlaceWorkCheck(item.getId(), "0", "0", holder);
        } catch (Exception e) {
            dlog.i("Exception : " + e);
        }


    } // getItemCount : 전체 데이터의 개수를 리턴

    @Override
    public int getItemCount() {
        return mData.size();
    } // 아이템 뷰를 저장하는 뷰홀더 클래스


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView store_thumnail;
        TextView title, name, address, state_tv;
        TextView item_peoplecnt, total_money;
        CardView store_kind_state, item_area, store_invite_accept, i_o_btn;
        RelativeLayout list_setting, list_img_area, place_state;
        LinearLayout money_area, total_item, first_line;

        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조
            store_thumnail      = itemView.findViewById(R.id.store_thumnail);
            title               = itemView.findViewById(R.id.title);
            item_peoplecnt      = itemView.findViewById(R.id.item_peoplecnt);
            name                = itemView.findViewById(R.id.name);
            place_state         = itemView.findViewById(R.id.place_state);
            address             = itemView.findViewById(R.id.address);
            list_setting        = itemView.findViewById(R.id.list_setting);
            list_img_area       = itemView.findViewById(R.id.list_img_area);
            money_area          = itemView.findViewById(R.id.money_area);
            store_kind_state    = itemView.findViewById(R.id.store_kind_state);
            total_item          = itemView.findViewById(R.id.total_item);
            item_area           = itemView.findViewById(R.id.item_area);
            state_tv            = itemView.findViewById(R.id.state_tv);
            total_money         = itemView.findViewById(R.id.total_money);
            first_line          = itemView.findViewById(R.id.first_line);
            i_o_btn             = itemView.findViewById(R.id.i_o_btn);

            dlog.DlogContext(mContext);
            shardpref = new PreferenceHelper(mContext);
            USER_INFO_ID = shardpref.getString("USER_INFO_ID", "");

            itemView.setOnClickListener(view -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    PlaceListData.PlaceListData_list item = mData.get(pos);
                    Log.i("WorkplaceListAdapter", "pos : " + pos);
                    shardpref.putString("place_id", item.getId());
                    shardpref.putString("place_owner_id", item.getOwner_id());
                    if (mListener != null) {
                        mListener.onItemClick(view, pos);
                    }
//                    pm.EmployerStoreSetting(mContext);
                }
            });

        }
    }

    public void addItem(PlaceListData.PlaceListData_list workPlaceListData_list) {
        mData.add(workPlaceListData_list);
    }

    public void PlaceWorkCheck(String place_id, String auth, String kind, ViewHolder holder) {
        dlog.i("PlaceWorkCheck place_id : " + place_id);
        dlog.i("PlaceWorkCheck auth : " + auth);
        dlog.i("PlaceWorkCheck USER_INFO_ID : " + USER_INFO_ID);
        dlog.i("PlaceWorkCheck kind : " + kind);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MainContentsInterface.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        MainContentsInterface api = retrofit.create(MainContentsInterface.class);
        Call<String> call = api.getData(place_id, auth, USER_INFO_ID, kind);
        call.enqueue(new Callback<String>() {
            @SuppressLint({"LongLogTag", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
//                    activity.runOnUiThread(() -> {
                    String jsonResponse = rc.getBase64decode(response.body());
                    dlog.i("PlaceWorkCheck jsonResponse length : " + jsonResponse.length());
                    dlog.i("PlaceWorkCheck jsonResponse : " + jsonResponse);
                    try {
                        if (!jsonResponse.equals("[]")) {
                            JSONArray Response = new JSONArray(jsonResponse);
                            try {
                                dlog.i("-----MainData-----");
                                int allPay = 0;
                                for (int i = 0; i < Response.length(); i++) {
                                    allPay += Integer.parseInt(Response.getJSONObject(i).getString("recent_pay").replace(",", ""));
                                }
                                allPay = allPay - Integer.parseInt(Response.getJSONObject(0).getString("deductpay").replace(",", ""));
                                DecimalFormat myFormatter = new DecimalFormat("###,###");
                                holder.total_money.setText(myFormatter.format(allPay) + "원");
                                dlog.i("allPay : " + myFormatter.format(allPay));
                                dlog.i("-----MainData-----");
                            } catch (Exception e) {
                                dlog.i("UserCheck Exception : " + e);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    });
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                dlog.e("에러1 = " + t.getMessage());
            }
        });
    }

}
