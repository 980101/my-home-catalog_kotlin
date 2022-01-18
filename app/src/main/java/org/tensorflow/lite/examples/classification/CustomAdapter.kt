package org.tensorflow.lite.examples.classification

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import java.util.ArrayList

class CustomAdapter(private val arrayList: ArrayList<CustomData>, private val mListener: OnListItemSelectedInterface)
    : RecyclerView.Adapter<CustomAdapter.CustomViewHolder>() {

    var selectedIdx = -1

    interface OnListItemSelectedInterface {
        fun onItemSelected(v: View?, position: Int)
    }

    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val iv_icon: ImageView
        val tv_name: TextView

        override fun onClick(v: View) {
            when (v.id) {
                R.id.item_custom -> selectItem(v, absoluteAdapterPosition)
            }
        }

        init {
            iv_icon = itemView.findViewById(R.id.iv_custom_item)
            tv_name = itemView.findViewById(R.id.tv_custom_item)
            itemView.setOnClickListener(this)
        }
    }

    fun selectItem(v: View?, position: Int) {
        selectedIdx = position
        mListener.onItemSelected(v, position)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_button, parent, false)

        // 아이템의 크기를 동적으로 변경
        val width = parent.measuredWidth / 2
        val height = parent.measuredHeight / 3
        view.minimumWidth = width
        view.minimumHeight = height

        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {

        holder.iv_icon.setImageResource(arrayList[position].iv_icon)
        holder.tv_name.text = arrayList[position].tv_name
        if (selectedIdx == holder.absoluteAdapterPosition) {
            holder.itemView.setBackgroundResource(R.drawable.btn_custom_clicked)
        } else {
            holder.itemView.setBackgroundResource(R.drawable.btn_custom_unclicked)
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}