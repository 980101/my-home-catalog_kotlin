package org.tensorflow.lite.examples.classification;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private ArrayList<String> arrayList;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_history;
        Button btn_delete;

        ViewHolder(View itemView) {
            super(itemView);

            tv_history = itemView.findViewById(R.id.tv_item_history);
            btn_delete = itemView.findViewById(R.id.btn_item_history);

            itemView.setOnClickListener(this);
            btn_delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos =getAbsoluteAdapterPosition();
            String style = tv_history.getText().toString();

            switch (v.getId()) {
                case R.id.item_history:
                    goMain(v, pos, style);
                    break;
                case R.id.btn_item_history:
                    removeAt(pos, style);
                    break;
            }
        }
    }

    public void goMain (View v, int position, String style) {
        if (position != RecyclerView.NO_POSITION) {
            // style 값 받아오기
            String furniture = ((CustomActivity)CustomActivity.mContext).furniture;

            Intent intent = new Intent(v.getContext(), MainActivity.class);
            intent.putExtra("style", style);
            intent.putExtra("type", furniture);
            v.getContext().startActivity(intent);
        }
    }

    public void removeAt(int position, String style) {
        // 데이터 삭제 : View 부분
        arrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, arrayList.size());
        // 데이터 삭제 : Data 부분
        SharedPreferences mPreferences = ((CameraActivity)CameraActivity.mContext).mPreferences;
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.remove(style);
        editor.commit();
    }

    HistoryAdapter(ArrayList<String> arrayList) {
        this.arrayList = arrayList;
    }

    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(HistoryAdapter.ViewHolder holder, int position) {
        String text = arrayList.get(position);
        holder.tv_history.setText(text);
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }
}
