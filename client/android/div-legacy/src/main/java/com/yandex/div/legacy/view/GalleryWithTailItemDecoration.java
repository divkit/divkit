package com.yandex.div.legacy.view;

import android.graphics.Rect;
import android.view.View;
import androidx.annotation.Px;
import androidx.recyclerview.widget.RecyclerView;

class GalleryWithTailItemDecoration extends RecyclerView.ItemDecoration {

    @Px
    private final int mFirstItemPaddingLeft;
    @Px
    private final int mMidItemPaddingRight;
    @Px
    private final int mTailPaddingLeft;
    @Px
    private final int mTailPaddingRight;
    @Px
    private final int mPaddingTop;
    @Px
    private final int mPaddingBottom;

    public GalleryWithTailItemDecoration(@Px int firstItemPaddingLeft, @Px int midItemPaddingRight,
                                         @Px int tailPaddingLeft, @Px int tailPaddingRight,
                                         @Px int paddingTop, @Px int paddingBottom) {
        mFirstItemPaddingLeft = firstItemPaddingLeft;
        mMidItemPaddingRight = midItemPaddingRight;
        mTailPaddingLeft = tailPaddingLeft;
        mTailPaddingRight = tailPaddingRight;
        mPaddingTop = paddingTop;
        mPaddingBottom = paddingBottom;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getLayoutManager().getPosition(view);
        int itemCount = parent.getAdapter().getItemCount();

        int paddingLeft;
        int paddingRight;

        if (itemCount == 1) {
            // tail only
            paddingLeft = mTailPaddingLeft;
            paddingRight = mTailPaddingRight;
        } else if (position == itemCount - 1) {
            // tail
            paddingLeft = 0;
            paddingRight = mTailPaddingRight;
        } else {
            boolean isFirst = position == 0;
            boolean isBeforeTail = (position == itemCount - 2);

            paddingLeft = isFirst ? mFirstItemPaddingLeft : 0;
            paddingRight = isBeforeTail ? mTailPaddingLeft : mMidItemPaddingRight;
        }

        outRect.set(paddingLeft, mPaddingTop, paddingRight, mPaddingBottom);
    }
}
