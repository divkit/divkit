package com.yandex.div.core.state;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;
import java.util.Map;

/**
 * Describes view state (current selected div state, scrolls etc), so div views that accept this data could save previous layout
 */
public class DivViewState {

    private final long mCurrentDivStateId;
    @NonNull
    private final Map<String, BlockState> mBlockStates;

    DivViewState(long currentDivStateId) {
        this(currentDivStateId, new ArrayMap<>());
    }

    DivViewState(long currentDivStateId, @NonNull Map<String, BlockState> blockStates) {
        mCurrentDivStateId = currentDivStateId;
        mBlockStates = blockStates;
    }

    public long getCurrentDivStateId() {
        return mCurrentDivStateId;
    }

    @Nullable
    public <T extends BlockState> T getBlockState(@NonNull String blockId) {
        //noinspection unchecked
        return (T) mBlockStates.get(blockId);
    }

    public <T extends BlockState> void putBlockState(@NonNull String blockId, @NonNull T blockState) {
        mBlockStates.put(blockId, blockState);
    }

    public void reset() {
        mBlockStates.clear();
    }

    @NonNull
    Map<String, BlockState> getBlockStates() {
        return mBlockStates;
    }

    interface BlockState {
    }

}
