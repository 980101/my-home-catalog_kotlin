package org.tensorflow.lite.examples.classification;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private ArrayList<ItemData> arrayList;

    // 아이템 뷰를 저장하는 뷰홀더 클래스
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView iv_photo;
        TextView tv_name;
        TextView tv_price;

        public ViewHolder(View itemView) {
            super(itemView);

            // 뷰 객체에 대한 참조
            iv_photo = itemView.findViewById(R.id.iv_photo);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_price = itemView.findViewById(R.id.tv_price);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.item_main:
                    goDetail(v, getAbsoluteAdapterPosition());
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

    // 생성자에서 데이터 리스트 객체를 전달받음
    public ItemAdapter(ArrayList<ItemData> arrayList) {
        this.arrayList = arrayList;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_furniture, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        ItemData data = arrayList.get(position);

        Glide.with(viewHolder.iv_photo)
                .load(data.getImage())
                .into(viewHolder.iv_photo);
        viewHolder.tv_name.setText(data.getName());
        viewHolder.tv_price.setText(data.getPrice());

//        viewHolder.itemView.setTag(position);
//        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intentToDetail = new Intent(context.getApplicationContext(), DetailActivity.class);
//                intentToDetail.putExtra("image", data.getImage());
//                intentToDetail.putExtra("name", data.getName());
//                intentToDetail.putExtra("price", data.getPrice());
//                intentToDetail.putExtra("link", data.getLink());
//                context.startActivity(intentToDetail);
//            }
//        });
    }

    // getItemCount() - 전체 데이터 개수 리턴
    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }
}
