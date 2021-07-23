package com.codepath.noteit;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//https://stackoverflow.com/questions/34852162/make-the-last-recyclerview-item-fill-spaces-in-recyclerview-if-the-data-count-i

public class GridItemDecoration extends RecyclerView.ItemDecoration
{
    private int mHorizontalSpacing = 10;
    private int mVerticalSpacing = 10;

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
    {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildPosition(view);
        if (parent.getLayoutManager() instanceof GridLayoutManager)
        {
            GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
            int spanCount, column;
            if (position == parent.getAdapter().getItemCount() - 1 && position % 3 == 0)
            {
                spanCount = 1;
                outRect.left = mHorizontalSpacing;
                outRect.right = parent.getWidth() - mHorizontalSpacing;
            }
            else if (position == parent.getAdapter().getItemCount() - 1 && position % 3 == 1) {
                spanCount = 2;
                column = position % spanCount;
                outRect.left = mHorizontalSpacing * (spanCount - column) / spanCount;
                outRect.right = mHorizontalSpacing * (column + 1) / spanCount;
            }
            else {
                spanCount = layoutManager.getSpanCount();
                column = position % spanCount;
                outRect.left = mHorizontalSpacing * (spanCount - column) / spanCount;
                outRect.right = mHorizontalSpacing * (column + 1) / spanCount;
            }



            if (position < spanCount)
            {
                outRect.top = mVerticalSpacing;
            }
            outRect.bottom = mVerticalSpacing;
        }
    }
}
