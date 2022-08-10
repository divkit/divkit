package com.yandex.div.legacy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yandex.alicekit.core.utils.Assert;
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

/**
 * Base visitor class to process div data tree.
 *
 * @param <T> returning type of operation over div block
 */
public abstract class DivVisitor<T> {
    /**
     * Visit block
     *
     * @param block div block
     */
    @Nullable
    public T visit(@NonNull DivData.State.Block block) {
        DivButtonsBlock buttonsBlock = block.asDivButtonsBlock();
        if (buttonsBlock != null) {
            return visit(buttonsBlock);
        }

        DivFooterBlock footerBlock = block.asDivFooterBlock();
        if (footerBlock != null) {
            return visit(footerBlock);
        }

        DivImageBlock imageBlock = block.asDivImageBlock();
        if (imageBlock != null) {
            return visit(imageBlock);
        }

        DivSeparatorBlock separatorBlock = block.asDivSeparatorBlock();
        if (separatorBlock != null) {
            return visit(separatorBlock);
        }

        DivTableBlock tableBlock = block.asDivTableBlock();
        if (tableBlock != null) {
            return visit(tableBlock);
        }

        DivTitleBlock titleBlock = block.asDivTitleBlock();
        if (titleBlock != null) {
            return visit(titleBlock);
        }

        DivTrafficBlock trafficBlock = block.asDivTrafficBlock();
        if (trafficBlock != null) {
            return visit(trafficBlock);
        }

        DivUniversalBlock universalBlock = block.asDivUniversalBlock();
        if (universalBlock != null) {
            return visit(universalBlock);
        }

        DivGalleryBlock galleryBlock = block.asDivGalleryBlock();
        if (galleryBlock != null) {
            return visit(galleryBlock);
        }

        DivContainerBlock containerBlock = block.asDivContainerBlock();
        if (containerBlock != null) {
            return visit(containerBlock);
        }

        DivTabsBlock tabsBlock = block.asDivTabsBlock();
        if (tabsBlock != null) {
            return visit(tabsBlock);
        }

        // todo(ntcheban) should we fail or just ignore ?
        Assert.fail("Unknown div block got " + block.toString());
        return null;
    }

    @Nullable
    public T visit(@NonNull DivContainerBlock.Children block) {
        DivButtonsBlock buttonsBlock = block.asDivButtonsBlock();
        if (buttonsBlock != null) {
            return visit(buttonsBlock);
        }

        DivFooterBlock footerBlock = block.asDivFooterBlock();
        if (footerBlock != null) {
            return visit(footerBlock);
        }

        DivImageBlock imageBlock = block.asDivImageBlock();
        if (imageBlock != null) {
            return visit(imageBlock);
        }

        DivSeparatorBlock separatorBlock = block.asDivSeparatorBlock();
        if (separatorBlock != null) {
            return visit(separatorBlock);
        }

        DivTableBlock tableBlock = block.asDivTableBlock();
        if (tableBlock != null) {
            return visit(tableBlock);
        }

        DivTitleBlock titleBlock = block.asDivTitleBlock();
        if (titleBlock != null) {
            return visit(titleBlock);
        }

        DivTrafficBlock trafficBlock = block.asDivTrafficBlock();
        if (trafficBlock != null) {
            return visit(trafficBlock);
        }

        DivUniversalBlock universalBlock = block.asDivUniversalBlock();
        if (universalBlock != null) {
            return visit(universalBlock);
        }

        DivContainerBlock containerBlock = block.asDivContainerBlock();
        if (containerBlock != null) {
            return visit(containerBlock);
        }

        DivGalleryBlock galleryBlock = block.asDivGalleryBlock();
        if (galleryBlock != null) {
            return visit(galleryBlock);
        }

        // todo(ntcheban) should we fail or just ignore ?
        Assert.fail("Unknown div block got " + block.toString());
        return null;
    }

    @Nullable
    protected abstract T visit(@NonNull DivButtonsBlock data);

    @Nullable
    protected abstract T visit(@NonNull DivFooterBlock data);

    @Nullable
    protected abstract T visit(@NonNull DivImageBlock data);

    @Nullable
    protected abstract T visit(@NonNull DivSeparatorBlock data);

    @Nullable
    protected abstract T visit(@NonNull DivTableBlock data);

    @Nullable
    protected abstract T visit(@NonNull DivTitleBlock data);

    @Nullable
    protected abstract T visit(@NonNull DivTrafficBlock data);

    @Nullable
    protected abstract T visit(@NonNull DivUniversalBlock data);

    @Nullable
    protected abstract T visit(@NonNull DivGalleryBlock data);

    @Nullable
    protected abstract T visit(@NonNull DivContainerBlock data);

    @Nullable
    protected abstract T visit(@NonNull DivTabsBlock data);
}
