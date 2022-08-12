package com.yandex.div.legacy.view;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yandex.div.DivBaseBlock;
import com.yandex.div.legacy.DivBlockWithId;

public abstract class DivBaseViewBuilder<B extends DivBaseBlock> {

    @Nullable
    public View build(@NonNull DivView divView, @NonNull B block, @NonNull String path) {
        block.setBlockId(DivBlockWithId.appendId(path, block.getClass().getSimpleName()));
        return build(divView, block);
    }

    @Nullable
    protected abstract View build(@NonNull DivView divView, @NonNull B block);

}
