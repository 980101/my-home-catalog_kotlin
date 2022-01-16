package org.tensorflow.lite.examples.classification;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class HistoryItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public HistoryItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.right = space;
    }
}
