package com.krafte.nebworks.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.krafte.nebworks.R;
import com.krafte.nebworks.data.CategorySpinner;
import com.krafte.nebworks.util.PreferenceHelper;

import java.util.ArrayList;

public class FeedSelectAdapter extends RecyclerView.Adapter<FeedSelectAdapter.ViewHolder> {
    private static final String TAG = "FeedSelectAdapter";
    private ArrayList<CategorySpinner.CategorySpinner_list> mData = null;
    Context mContext;
    int user_kind;
    String TaskKind = "";
    int setKind;
    PreferenceHelper shardpref;
    Drawable icon_off;
    Drawable icon_on;

    //Shared Data
    int categoryItem = 0;

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }

    public FeedSelectAdapter(Context context, ArrayList<CategorySpinner.CategorySpinner_list> data, int user_kind, int setKind) {
        this.mData = data;
        this.user_kind = user_kind;
        this.setKind = setKind;
        mContext = context;
    } // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴

    @NonNull
    @Override
    public FeedSelectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.category_select_item, parent, false);
        FeedSelectAdapter.ViewHolder vh = new FeedSelectAdapter.ViewHolder(view);

        return vh;
    } // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시


    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull FeedSelectAdapter.ViewHolder holder, int position) {
        CategorySpinner.CategorySpinner_list item = mData.get(position);
        categoryItem = shardpref.getInt("categoryItem",0);

        holder.feed_category_select.setText("  " + item.getSetvalue());
        if(position == categoryItem){
            holder.feed_category_select.setCompoundDrawablesWithIntrinsicBounds(icon_on, null, null, null);
        }else{
            holder.feed_category_select.setCompoundDrawablesWithIntrinsicBounds(icon_off, null, null, null);
        }



    } // getItemCount : 전체 데이터의 개수를

    @Override
    public int getItemCount() {
        return mData.size();
    } // 아이템 뷰를 저장하는 뷰홀더 클래스

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView feed_category_select;


        @SuppressLint("UseCompatLoadingForDrawables")
        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조
            feed_category_select = itemView.findViewById(R.id.feed_category_select);


            shardpref = new PreferenceHelper(mContext);
            categoryItem = shardpref.getInt("categoryItem",0);


            itemView.setOnClickListener(view -> {
                int pos = getBindingAdapterPosition();
                if(pos != RecyclerView.NO_POSITION) {
                    CategorySpinner.CategorySpinner_list item = mData.get(pos);
                    feed_category_select.setCompoundDrawablesWithIntrinsicBounds(icon_on, null, null, null);

//                    Log.i(TAG,"categoryItem : " + pos);
                    if(categoryItem != 0){

                    }
                    if (mListener != null) {
                        mListener.onItemClick(view, pos) ;
                    }
                }
            });

        }
    }

    public void addItem(CategorySpinner.CategorySpinner_list data) {
        mData.add(data);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


}
