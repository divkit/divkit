package androidx.recyclerview.widget;

import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Needed to fix scrolling conflicts if the items have scrollable views inside them
 */
public class FixScrollTouchHelper extends ItemTouchHelper {
    public FixScrollTouchHelper(@NonNull Callback callback) {
        super(callback);
    }

    @Override
    @Nullable
    View findChildView(@NonNull MotionEvent event) {
        View childView = super.findChildView(event);
        if (childView == null) {
            return null;
        }

        RecyclerView.ViewHolder viewHolder = mRecyclerView.findContainingViewHolder(childView);
        if (viewHolder instanceof ScrollableHolder && ((ScrollableHolder) viewHolder).hasScrollableViewUnder(event)) {
            return null; // underlying view will handle scroll
        } else {
            return childView;
        }
    }

    public interface ScrollableHolder {
        boolean hasScrollableViewUnder(@NonNull MotionEvent event);
    }
}
