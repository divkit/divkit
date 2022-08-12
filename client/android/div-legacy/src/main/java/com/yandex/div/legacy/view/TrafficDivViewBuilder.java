package com.yandex.div.legacy.view;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.yandex.alicekit.core.utils.Assert;
import com.yandex.alicekit.core.utils.Views;
import com.yandex.div.DivAction;
import com.yandex.div.DivTrafficBlock;
import com.yandex.div.legacy.Alignment;
import com.yandex.div.legacy.LegacyDivDataUtils;
import com.yandex.div.legacy.R;
import com.yandex.div.legacy.dagger.DivLegacyScope;
import com.yandex.div.legacy.dagger.LegacyNames;
import com.yandex.div.view.PaddingItemDecoration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

@DivLegacyScope
public class TrafficDivViewBuilder extends DivElementDataViewBuilder<DivTrafficBlock> {

    @NonNull
    private final Context mThemedContext;

    @Inject
    TrafficDivViewBuilder(@NonNull @Named(LegacyNames.THEMED_CONTEXT) Context themedContext) {
        super();
        mThemedContext = themedContext;
    }

    @Override
    @Nullable
    protected View build(@NonNull DivView divView, @NonNull DivTrafficBlock divData) {
        List<DivTrafficBlock.Item> items = getValidItems(divData);

        if (items.isEmpty()) {
            return null;
        }

        Context context = divView.getContext();

        RecyclerView recyclerView = Views.inflate(context, R.layout.div_traffic_list);
        recyclerView.setAdapter(new Adapter(items, divData.action));
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);

        if (divData.action != null) {
            recyclerView.addOnItemTouchListener(new RecyclerViewClickListener(context, recyclerView));
        }

        int itemPadding = context.getResources().getDimensionPixelSize(R.dimen.div_traffic_item_padding_horizontal);
        recyclerView.addItemDecoration(new PaddingItemDecoration(0, itemPadding, 0));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        final Alignment alignment = DivViewUtils.divAlignmentToAlignment(divData.alignment);

        switch (alignment) {
            case LEFT:
                layoutParams.gravity = Gravity.LEFT;
                break;
            case CENTER:
                layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
                break;
            case RIGHT:
                layoutParams.gravity = Gravity.RIGHT;
                break;
            default:
                Assert.fail("Unknown value");
                break;
        }
        recyclerView.setLayoutParams(layoutParams);

        return recyclerView;
    }

    @NonNull
    private List<DivTrafficBlock.Item> getValidItems(@NonNull DivTrafficBlock divData) {
        if (divData.items.isEmpty()) {
            return Collections.emptyList();
        }

        List<DivTrafficBlock.Item> itemList = new ArrayList<>();
        for (DivTrafficBlock.Item item : divData.items) {
            if (LegacyDivDataUtils.isDivTextValid(item.score)) {
                itemList.add(item);
            }
        }

        return itemList;
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        @NonNull
        private final TextView mScore;
        @NonNull
        private final TextView mText;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            mScore = Views.findViewAndCast(itemView, R.id.div_traffic_score);
            mText = Views.findViewAndCast(itemView, R.id.div_traffic_text);
        }

        void bind(@NonNull DivTrafficBlock.Item divItem) {
            mScore.setText(divItem.score);
            Views.setTextOrHide(mText, divItem.text);

            GradientDrawable background = (GradientDrawable) mScore.getBackground();
            int strokeWidth = itemView.getResources().getDimensionPixelSize(R.dimen.div_traffic_item_stroke_width);
            background.setStroke(strokeWidth, divItem.color);
        }
    }

    private class Adapter extends RecyclerView.Adapter<ItemViewHolder> {
        @Nullable
        private final DivAction mAction;

        @NonNull
        private final List<DivTrafficBlock.Item> mItems;

        Adapter(@NonNull List<DivTrafficBlock.Item> items, @Nullable DivAction action) {
            mItems = items;
            mAction = action;
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = Views.inflate(parent, R.layout.div_traffic_item);

            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {
            holder.bind(mItems.get(position));
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }
    }

    // RecyclerView does not support onClickListener, on clicks for items are supported.
    // So we detect clicks manually here.
    // This detects clicks between items too.
    private class RecyclerViewClickListener extends RecyclerView.SimpleOnItemTouchListener {
        @NonNull
        private final RecyclerView mRecyclerView;
        @NonNull
        private final GestureDetector mGestureDetector;

        RecyclerViewClickListener(@NonNull Context context, @NonNull RecyclerView recyclerView) {
            mRecyclerView = recyclerView;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
            if (mRecyclerView.hasOnClickListeners() && mGestureDetector.onTouchEvent(e)) {
                mRecyclerView.callOnClick();
                return true;
            }
            return false;
        }
    }
}
