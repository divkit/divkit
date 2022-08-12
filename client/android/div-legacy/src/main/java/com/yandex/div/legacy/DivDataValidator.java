package com.yandex.div.legacy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import com.yandex.div.DivButtonsBlock;
import com.yandex.div.DivContainerBlock;
import com.yandex.div.DivData;
import com.yandex.div.DivFooterBlock;
import com.yandex.div.DivGalleryBlock;
import com.yandex.div.DivImageBlock;
import com.yandex.div.DivSeparatorBlock;
import com.yandex.div.DivTableBlock;
import com.yandex.div.DivTabsBlock;
import com.yandex.div.DivTitleBlock;
import com.yandex.div.DivTrafficBlock;
import com.yandex.div.DivUniversalBlock;
import com.yandex.div.legacy.view.ButtonsDivBlockViewBuilder;
import com.yandex.div.legacy.view.DivTableBlockViewBuilder;
import com.yandex.div.legacy.view.FooterDivViewBuilder;
import com.yandex.div.legacy.view.UniversalDivViewBuilder;
import java.util.List;

/**
 * Validates div data blocks against their specifications.
 * <p>
 * NOTE: This class is not thread safe!
 */
@WorkerThread
public class DivDataValidator extends DivVisitor<Void> {
    private final Processor mProcessor = new Processor();

    /**
     * Process validation on each div block within each div state. Amount of valid blocks will be written under
     * corresponding to a div state index in given {@code answer} array.
     *
     * @param divData div data for validation
     * @param answer  the array into which the data of validation are to be
     *                stored, if it is big enough; otherwise, a new array of the same
     *                runtime type is allocated for this purpose.
     * @return an array containing validation data
     */
    @NonNull
    public int[] validate(@NonNull DivData divData, @Nullable int[] answer) {
        final int statesSize = divData.states.size();
        if (answer == null || answer.length != statesSize) {
            answer = new int[statesSize];
        }

        for (int i = 0; i < statesSize; i++) {
            mProcessor.reset();

            final DivData.State state = divData.states.get(i);
            for (DivData.State.Block block : state.blocks) {
                visit(block);
            }

            answer[i] = mProcessor.get();
        }

        return answer;
    }

    @Nullable
    @Override
    protected Void visit(@NonNull DivButtonsBlock data) {
        if (ButtonsDivBlockViewBuilder.isValidBlock(data)) {
            mProcessor.increment();
        }
        return null;
    }

    @Nullable
    @Override
    protected Void visit(@NonNull DivFooterBlock data) {
        if (FooterDivViewBuilder.isValidBlock(data)) {
            mProcessor.increment();
        }
        return null;
    }

    @Nullable
    @Override
    protected Void visit(@NonNull DivImageBlock data) {
        if (LegacyDivDataUtils.isDivImageValid(data.image)) {
            mProcessor.increment();
        }
        return null;
    }

    @Nullable
    @Override
    protected Void visit(@NonNull DivSeparatorBlock data) {
        return null; // Separator block does not count towards data validation
    }

    @Nullable
    @Override
    protected Void visit(@NonNull DivTableBlock data) {
        if (DivTableBlockViewBuilder.validateAndGetColumnCount(data) > 0) {
            mProcessor.increment();
        }
        return null;
    }

    @Nullable
    @Override
    protected Void visit(@NonNull DivTitleBlock data) {
        if (LegacyDivDataUtils.isValidBlock(data)) {
            mProcessor.increment();
        }
        return null;
    }

    @Nullable
    @Override
    protected Void visit(@NonNull DivTrafficBlock data) {
        if (LegacyDivDataUtils.isValidBlock(data)) {
            mProcessor.increment();
        }
        return null;
    }

    @Nullable
    @Override
    protected Void visit(@NonNull DivUniversalBlock data) {
        if (UniversalDivViewBuilder.isValidBlock(data)) {
            mProcessor.increment();
        }
        return null;
    }

    @Nullable
    @Override
    protected Void visit(@NonNull DivGalleryBlock data) {
        final List<DivContainerBlock> items = data.items;
        for (final DivContainerBlock item : items) {
            visit(item);
        }
        return null;
    }

    @Nullable
    @Override
    protected Void visit(@NonNull DivContainerBlock data) {
        for (final DivContainerBlock.Children child : data.children) {
            visit(child);
        }
        return null;
    }

    @Nullable
    @Override
    protected Void visit(@NonNull DivTabsBlock data) {
        for (final DivTabsBlock.Item item : data.items) {
            visit(item.content);
        }
        return null;
    }

    private static class Processor {
        private int mCounter = 0;

        void reset() {
            mCounter = 0;
        }

        void increment() {
            mCounter++;
        }

        int get() {
            return mCounter;
        }
    }
}
