package org.tensorflow.lite.examples.classification

import androidx.recyclerview.widget.RecyclerView
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.Button
import android.widget.TextView
import java.util.ArrayList

class HistoryAdapter internal constructor(private val arrayList: ArrayList<String?>?) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var tv_history: TextView
        var btn_delete: Button
        override fun onClick(v: View) {
            val pos = absoluteAdapterPosition
            val style = tv_history.text.toString()
            when (v.id) {
                R.id.item_history -> goMain(v, pos, style)
                R.id.btn_item_history -> removeAt(pos, style)
            }
        }

        init {
            tv_history = itemView.findViewById(R.id.tv_item_history)
            btn_delete = itemView.findViewById(R.id.btn_item_history)
            itemView.setOnClickListener(this)
            btn_delete.setOnClickListener(this)
        }
    }

    fun goMain(v: View, position: Int, style: String?) {
        if (position != RecyclerView.NO_POSITION) {
            // style 값 받아오기
            val furniture = (CustomActivity.Companion.mContext as CustomActivity).furniture
            val intent = Intent(v.context, MainActivity::class.java)
            intent.putExtra("style", style)
            intent.putExtra("type", furniture)
            v.context.startActivity(intent)
        }
    }

    fun removeAt(position: Int, style: String?) {
        // 데이터 삭제 : View 부분
        arrayList!!.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, arrayList.size)
        // 데이터 삭제 : Data 부분
        val mPreferences = (CameraActivity.Companion.mContext as CameraActivity).mPreferences
        val editor = mPreferences!!.edit()
        editor.remove(style)
        editor.commit()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val text = arrayList!![position]
        holder.tv_history.text = text
    }

    override fun getItemCount(): Int {
        return arrayList?.size ?: 0
    }
}