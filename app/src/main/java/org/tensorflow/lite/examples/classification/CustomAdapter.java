package org.tensorflow.lite.examples.classification;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    private ArrayList<CustomData> arrayList;

    int selectedIdx = -1;

    public interface OnListItemSelectedInterface {
        void onItemSelected(View v, int position);
    }

    private OnListItemSelectedInterface mListener;

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView iv_icon;
        TextView tv_name;

        public CustomViewHolder(View itemView) {
            super(itemView);

            iv_icon = itemView.findViewById(R.id.iv_custom_item);
            tv_name = itemView.findViewById(R.id.tv_custom_item);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.item_custom :
                    selectItem(v, getAbsoluteAdapterPosition());
                    break;
            }
        }
    }

    public void selectItem(View v, int position) {
        selectedIdx = position;
        mListener.onItemSelected(v, position);
        notifyDataSetChanged();
    }

    public CustomAdapter(ArrayList<CustomData> arrayList, OnListItemSelectedInterface listener) {
        this.arrayList = arrayList;
        this.mListener = listener;
    }

    @Override
    public CustomAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_button, parent, false);

        // 아이템의 크기를 동적으로 변경
        int width = parent.getMeasuredWidth() / 2;
        int height = parent.getMeasuredHeight() / 3;
        view.setMinimumWidth(width);
        view.setMinimumHeight(height);

        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(CustomAdapter.CustomViewHolder holder, int position) {
        holder.iv_icon.setImageResource(arrayList.get(position).getIv_icon());
        holder.tv_name.setText(arrayList.get(position).getTv_name());

        if (selectedIdx == holder.getAbsoluteAdapterPosition()) {
            holder.itemView.setBackgroundResource(R.drawable.btn_custom_clicked);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.btn_custom_unclicked);
        }
    }

    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);
    }
}
