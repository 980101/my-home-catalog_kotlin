package org.tensorflow.lite.examples.classification

import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.content.Intent
import android.view.ViewGroup
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import android.view.View
import android.widget.ImageView
import java.util.ArrayList

class ItemAdapter     // 생성자에서 데이터 리스트 객체를 전달받음
(private val arrayList: ArrayList<ItemData?>?) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
    // 아이템 뷰를 저장하는 뷰홀더 클래스
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var iv_photo: ImageView
        var tv_name: TextView
        var tv_price: TextView

        override fun onClick(v: View) {
            when (v.id) {
                R.id.item_main -> goDetail(v, absoluteAdapterPosition)
            }
        }

        init {
            // 뷰 객체에 대한 참조
            iv_photo = itemView.findViewById(R.id.iv_photo)
            tv_name = itemView.findViewById(R.id.tv_name)
            tv_price = itemView.findViewById(R.id.tv_price)
            itemView.setOnClickListener(this)
        }
    }

    fun goDetail(v: View, position: Int) {
        val intent = Intent(v.context, DetailActivity::class.java)
        val data = arrayList!![position]
        intent.putExtra("image", data!!.image)
        intent.putExtra("name", data.name)
        intent.putExtra("price", data.price)
        intent.putExtra("link", data.link)
        v.context.startActivity(intent)
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_furniture, parent, false)
        return ViewHolder(view)
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val data = arrayList!![position]
        Glide.with(viewHolder.iv_photo)
                .load(data!!.image)
                .into(viewHolder.iv_photo)
        viewHolder.tv_name.text = data!!.name
        viewHolder.tv_price.text = data!!.price

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
    override fun getItemCount(): Int {
        return arrayList?.size ?: 0
    }
}