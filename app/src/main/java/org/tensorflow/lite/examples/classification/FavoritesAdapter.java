package org.tensorflow.lite.examples.classification;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private ArrayList<ItemData> arrayList;

    public interface OnListItemSelectedInterface {
        void onItemSelected(View v, int position);
    }

    private OnListItemSelectedInterface mListener;

    // 아이템 뷰를 저장하는 뷰홀더 클래스
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView iv_photo;
        TextView tv_name, tv_price;
        Button btn_item;

        public ViewHolder(View itemView) {
            super(itemView);

            // 뷰 객체에 대한 참조
            iv_photo = itemView.findViewById(R.id.iv_item_favorites);
            tv_name = itemView.findViewById(R.id.tv_item_favorites_name);
            tv_price = itemView.findViewById(R.id.tv_item_favorites_price);
            btn_item = itemView.findViewById(R.id.btn_item_favorites);

            itemView.setOnClickListener(this);
            btn_item.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAbsoluteAdapterPosition();

            switch (v.getId()) {
                case R.id.item_favorites:
                    goDetail(v, pos);
                    break;
                case R.id.btn_item_favorites:
                    removeItem(v, pos);
                    break;
            }
        }
    }

    public void goDetail(View v, int position) {
        Intent intent = new Intent(v.getContext(), DetailActivity.class);

        ItemData data = arrayList.get(position);
        intent.putExtra("image", data.getImage());
        intent.putExtra("name", data.getName());
        intent.putExtra("price", data.getPrice());
        intent.putExtra("link", data.getLink());

        v.getContext().startActivity(intent);
    }

    public void removeItem(View v, int position) {
        mListener.onItemSelected(v, position);
        notifyDataSetChanged();
    }

    public FavoritesAdapter(ArrayList<ItemData> arrayList, OnListItemSelectedInterface onListItemSelectedInterface) {
        this.arrayList = arrayList;
        this.mListener = onListItemSelectedInterface;
    }

    @Override
    public FavoritesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorites, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(FavoritesAdapter.ViewHolder holder, int position) {
        ItemData data = arrayList.get(position);

        Glide.with(holder.iv_photo)
                .load(data.getImage())
                .into(holder.iv_photo);
        holder.tv_name.setText(data.getName());
        holder.tv_price.setText(data.getPrice());
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }
}
