package com.yandex.div.legacy.view;

import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import com.yandex.alicekit.core.utils.Assert;
import com.yandex.alicekit.core.utils.Views;
import com.yandex.div.DivAction;
import com.yandex.div.DivImageElement;
import com.yandex.div.DivSize;
import com.yandex.div.DivTableBlock;
import com.yandex.div.core.images.DivImageLoader;
import com.yandex.div.core.images.LoadReference;
import com.yandex.div.legacy.DivTextStyleProvider;
import com.yandex.div.legacy.LegacyDivDataUtils;
import com.yandex.div.legacy.R;
import com.yandex.div.legacy.dagger.DivLegacyScope;
import com.yandex.div.legacy.dagger.LegacyNames;
import com.yandex.div.util.Position;
import com.yandex.div.view.SeparatorView;
import com.yandex.div.view.pooling.ViewPool;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

@DivLegacyScope
public class DivTableBlockViewBuilder extends DivBaseViewBuilder<DivTableBlock> {

    private static final String FACTORY_TAG_TABLE = "DivTableBlockViewBuilder.TABLE";
    private static final String FACTORY_TAG_TEXT = "DivTableBlockViewBuilder.TEXT";
    private static final String FACTORY_TAG_IMAGE = "DivTableBlockViewBuilder.IMAGE";
    private static final String FACTORY_TAG_TEXT_AND_IMAGE = "DivTableBlockViewBuilder.TEXT_AND_IMAGE";
    private static final String FACTORY_TAG_SEPARATOR = "DivTableBlockViewBuilder.SEPARATOR";

    private static final int INVALID_COLUMNS_COUNT = -1;
    private static final int INVALID_PADDING = -1;

    @NonNull
    private final Context mContext;
    @NonNull
    private final ViewPool mViewPool;
    @NonNull
    private final DivImageLoader mImageLoader;
    @NonNull
    private final DivTextStyleProvider mTextStyleProvider;
    @DimenRes
    private final int mDefaultColumnPaddingHorizontalRes;

    @Inject
    DivTableBlockViewBuilder(@NonNull @Named(LegacyNames.THEMED_CONTEXT) Context context,
                             @NonNull ViewPool viewPool,
                             @NonNull DivImageLoader imageLoader,
                             @NonNull DivTextStyleProvider textStyleProvider) {
        super();
        mContext = context;
        mViewPool = viewPool;
        mImageLoader = imageLoader;
        mTextStyleProvider = textStyleProvider;
        mDefaultColumnPaddingHorizontalRes = getPaddingResource(DivSize.XS);

        mViewPool.register(FACTORY_TAG_TABLE, () -> new FitTableLayout(mContext), 4);
        mViewPool.register(FACTORY_TAG_TEXT, () -> new AppCompatTextView(mContext), 8);
        mViewPool.register(FACTORY_TAG_IMAGE, () -> new RatioImageView(mContext), 8);
        mViewPool.register(FACTORY_TAG_TEXT_AND_IMAGE, () -> createTextAndImageCell(mContext), 8);
        mViewPool.register(FACTORY_TAG_SEPARATOR, () -> new SeparatorView(mContext), 8);
    }

    @Nullable
    @Override
    protected View build(@NonNull DivView divView, @NonNull DivTableBlock tableBlock) {
        int columnCount = validateAndGetColumnCount(tableBlock);
        if (columnCount > 0) {
            FitTableLayout tableLayout = mViewPool.obtain(FACTORY_TAG_TABLE);
            tableLayout.setColumnCount(columnCount);
            Views.setPadding(tableLayout, R.dimen.div_horizontal_padding, Views.VIEW_SIDE_LEFT);
            Views.setPadding(tableLayout, R.dimen.div_horizontal_padding, Views.VIEW_SIDE_RIGHT);
            buildViews(divView, tableLayout, tableBlock);
            return tableLayout;
        }

        return null;
    }

    public static int validateAndGetColumnCount(@NonNull DivTableBlock divData) {
        int columnCount = INVALID_COLUMNS_COUNT;
        int rowCount = 0;

        for (DivTableBlock.Row row : divData.rows) {
            final DivTableBlock.RowElement rowElement = row.asRowElement();
            final DivTableBlock.SeparatorElement separatorElement = row.asSeparatorElement();

            if (rowElement != null) {
                final List<DivTableBlock.RowElement.Cell> cells = rowElement.cells;
                if (columnCount != INVALID_COLUMNS_COUNT) {
                    if (columnCount != cells.size()) {
                        Assert.fail("Found cells size inconsistency!");
                        return INVALID_COLUMNS_COUNT;
                    }
                } else {
                    columnCount = cells.size();
                }

                for (final DivTableBlock.RowElement.Cell cell : cells) {
                    if (LegacyDivDataUtils.isDivTextValid(cell.text)
                            || LegacyDivDataUtils.isDivImageValid(cell.image)) {
                        // row has valid element
                        rowCount++;
                        break;
                    }
                }

            } else if (separatorElement == null) {
                Assert.fail("Unknown row type: " + row.type);
                return INVALID_COLUMNS_COUNT;
            }
        }

        if (columnCount != INVALID_COLUMNS_COUNT && divData.columns != null && divData.columns.size() != columnCount) {
            Assert.fail("Columns data doesn't matches cells size!");
            return INVALID_COLUMNS_COUNT;
        }

        if (rowCount == 0) {
            Assert.fail("No single data row was added!");
            return INVALID_COLUMNS_COUNT;
        }

        return columnCount;
    }

    private void buildViews(@NonNull DivView divView,
                            @NonNull FitTableLayout tableLayout,
                            @NonNull DivTableBlock tableBlock) {
        tableLayout.removeAllViews();

        final List<View> tableViews = createViews(divView, tableLayout.getColumnCount(), tableBlock.rows, tableBlock.columns);

        for (View cellView: tableViews) {
            tableLayout.addView(cellView);
        }
    }

    @NonNull
    private List<View> createViews(@NonNull DivView divView,
                                   int columnCount,
                                   @NonNull List<DivTableBlock.Row> rowData,
                                   @Nullable List<DivTableBlock.Column> columnData) {
        final int rowsCount = rowData.size();
        final List<View> tableViews = new ArrayList<>();

        //noinspection ForLoopReplaceableByForEach
        for (int rowIndex = 0; rowIndex < rowsCount; rowIndex++) {
            final DivTableBlock.Row row = rowData.get(rowIndex);

            final DivTableBlock.RowElement rowElement = row.asRowElement();
            if (rowElement != null) {
                final List<DivTableBlock.RowElement.Cell> cells = rowElement.cells;
                for (int columnIndex = 0; columnIndex < cells.size(); columnIndex++) {
                    DivTableBlock.Column column = columnData == null ? null : columnData.get(columnIndex);
                    DivTableBlock.RowElement.Cell cell = cells.get(columnIndex);
                    View cellView = buildCellView(divView, cell, column);
                    applyColumnPaddings(cellView, column);
                    applyRowPaddings(cellView, rowElement);
                    tableViews.add(cellView);
                }
            }

            final DivTableBlock.SeparatorElement separatorElement = row.asSeparatorElement();
            if (separatorElement != null) {
                View separatorView = buildSeparatorView(separatorElement, columnCount);
                tableViews.add(separatorView);
            }
        }
        return tableViews;
    }

    @NonNull
    private View buildCellView(@NonNull DivView divView,
                               @NonNull DivTableBlock.RowElement.Cell cell,
                               @Nullable DivTableBlock.Column column) {
        View view;

        if (LegacyDivDataUtils.isTextOnlyDiv(cell.text, cell.image)) {
            view = buildTextDivView(cell);
        } else if (LegacyDivDataUtils.isTextAndImageDiv(cell.text, cell.image)) {
            view = buildTextAndImageDivView(divView, cell);
        } else if (LegacyDivDataUtils.isImageOnlyDiv(cell.text, cell.image)) {
            //noinspection ConstantConditions
            view = buildImageDivView(divView, cell.image, getImageSizeDimenRes(cell.imageSize));
        } else {
            view = buildUnknownCell();
        }

        final int gravity = DivViewUtils.verticalAlignmentToGravity(cell.verticalAlignment) |
                DivViewUtils.horizontalAlignmentToGravity(cell.horizontalAlignment);

        final int weight = column == null ? 0 : column.weight;
        setLayoutParams(view, gravity, weight);

        final DivAction action = cell.action;
        if (action != null) {
            divView.setActionHandlerForView(view, action);
        }
        return view;
    }

    private void applyColumnPaddings(@NonNull View view, @Nullable DivTableBlock.Column colData) {
        @DimenRes int leftPaddingRes = colData != null ? getPaddingResource(colData.leftPadding) : mDefaultColumnPaddingHorizontalRes;
        @DimenRes int rightPaddinRes = colData != null ? getPaddingResource(colData.rightPadding) : mDefaultColumnPaddingHorizontalRes;

        FitTableLayout.LayoutParams params = (FitTableLayout.LayoutParams) view.getLayoutParams();
        Resources resources = view.getResources();
        params.leftMargin = resources.getDimensionPixelSize(leftPaddingRes);
        params.rightMargin = resources.getDimensionPixelSize(rightPaddinRes);
    }

    private void applyRowPaddings(@NonNull View view, @NonNull DivTableBlock.RowElement row) {
        @DimenRes int topPaddingRes = getPaddingResource(row.topPadding);
        @DimenRes int bottomPaddingRes = getPaddingResource(row.bottomPadding);

        FitTableLayout.LayoutParams params = (FitTableLayout.LayoutParams) view.getLayoutParams();
        Resources resources = view.getResources();
        params.topMargin = resources.getDimensionPixelSize(topPaddingRes);
        params.bottomMargin = resources.getDimensionPixelSize(bottomPaddingRes);
    }

    private void setLayoutParams(@NonNull View view, int gravity, int weight) {
        FitTableLayout.LayoutParams params = (FitTableLayout.LayoutParams) view.getLayoutParams();
        if (params == null) {
            params = new FitTableLayout.LayoutParams();
        }

        params.gravity = gravity;
        params.weight = weight;
        view.setLayoutParams(params);
    }

    @NonNull
    private View buildTextDivView(@NonNull DivTableBlock.RowElement.Cell cell) {
        TextStyle textStyle = mTextStyleProvider.getTextStyle(cell.textStyle);
        final TextView textView = mViewPool.obtain(FACTORY_TAG_TEXT);
        textView.setMaxLines(1);
        textView.setIncludeFontPadding(false);

        textView.setText(cell.text);
        textStyle.apply(textView);

        return textView;
    }

    @NonNull
    private View buildImageDivView(@NonNull DivView divView,
                                   @NonNull DivImageElement image,
                                   @DimenRes int imageSizeDimenRes) {
        final RatioImageView imageView = mViewPool.obtain(FACTORY_TAG_IMAGE);
        imageView.setApplyOn(RatioImageView.APPLY_ON_WIDTH);
        imageView.setRatio(DivViewUtils.getImageRatio(image));

        final int imageSize = mContext.getResources().getDimensionPixelSize(imageSizeDimenRes);
        FitTableLayout.LayoutParams params = new FitTableLayout.LayoutParams();
        params.height = imageSize;
        imageView.setLayoutParams(params);

        LoadReference loadReference = mImageLoader.loadImage(image.imageUrl.toString(), imageView);
        divView.addLoadReference(loadReference, imageView);

        return imageView;
    }

    @NonNull
    private View buildTextAndImageDivView(@NonNull DivView divView,
                                          @NonNull DivTableBlock.RowElement.Cell cell) {
        final LinearLayout textAndImage = mViewPool.obtain(FACTORY_TAG_TEXT_AND_IMAGE);

        ImageView imageView = (ImageView) textAndImage.getChildAt(0);
        TextView textView = (TextView) textAndImage.getChildAt(1);

        LinearLayout.LayoutParams firstChildParams;
        if (DivViewUtils.divPositionToPosition(cell.imagePosition) == Position.LEFT) {
            firstChildParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        } else {
            textAndImage.removeView(textView);
            textAndImage.addView(textView, 0);
            firstChildParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
        }
        firstChildParams.rightMargin = mContext.getResources().getDimensionPixelSize(R.dimen.div_compound_drawable_padding);

        // binding
        textView.setText(cell.text);
        final TextStyle textStyle = mTextStyleProvider.getTextM();
        textStyle.apply(textView);

        //noinspection ConstantConditions
        LoadReference loadReference = mImageLoader.loadImage(cell.image.imageUrl.toString(), imageView);
        divView.addLoadReference(loadReference, imageView);

        return textAndImage;
    }

    private static LinearLayout createTextAndImageCell(Context context) {
        LinearLayout textAndImage = new LinearLayout(context);
        textAndImage.setOrientation(LinearLayout.HORIZONTAL);
        textAndImage.setGravity(Gravity.CENTER);

        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        int imageSize = context.getResources().getDimensionPixelSize(R.dimen.div_table_image_size_m);
        LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(imageSize, imageSize);
        imageView.setLayoutParams(imageLayoutParams);

        TextView textView = new AppCompatTextView(context);
        textView.setIncludeFontPadding(false);
        textView.setMaxLines(1);

        textAndImage.addView(imageView);
        textAndImage.addView(textView);
        return textAndImage;
    }

    @NonNull
    private View buildSeparatorView(DivTableBlock.SeparatorElement separatorElement, int columnCount) {
        SeparatorView separatorView = mViewPool.obtain(FACTORY_TAG_SEPARATOR);

        separatorView.setDividerHeightResource(R.dimen.div_separator_delimiter_height);
        separatorView.setDividerColor(separatorElement.color);
        Views.setPadding(separatorView, R.dimen.div_table_padding_xxs, Views.VIEW_SIDE_TOP);

        FitTableLayout.LayoutParams params = new FitTableLayout.LayoutParams(
                FitTableLayout.LayoutParams.MATCH_PARENT, FitTableLayout.LayoutParams.WRAP_CONTENT);
        params.span = columnCount;
        separatorView.setLayoutParams(params);

        return separatorView;
    }

    @NonNull
    private View buildUnknownCell() {
        final Space space = new Space(mContext);
        space.setLayoutParams(new FitTableLayout.LayoutParams());
        return space;
    }

    @DimenRes
    private static int getPaddingResource(@DivSize @Nullable String size) {
        if (size == null) {
            return INVALID_PADDING;
        }

        switch (size) {
            case DivSize.XS:
                return R.dimen.div_table_padding_xs;
            case DivSize.S:
                return R.dimen.div_table_padding_s;
            case DivSize.M:
                return R.dimen.div_table_padding_m;
            case DivSize.L:
                return R.dimen.div_table_padding_l;
            case DivSize.XL:
                return R.dimen.div_table_padding_xl;
            case DivSize.XXL:
                return R.dimen.div_table_padding_xxl;
            case DivSize.XXS:
                return R.dimen.div_table_padding_xxs;
            case DivSize.ZERO:
                return R.dimen.div_table_padding_zero;
            default:
                return INVALID_PADDING;
        }
    }

    @DimenRes
    private static int getImageSizeDimenRes(@Nullable @DivSize String imageSize) {
        if (imageSize == null) {
            return R.dimen.div_table_image_size_m;
        }

        switch (imageSize) {
            case DivSize.XS:
                return R.dimen.div_table_image_size_xs;
            case DivSize.S:
                return R.dimen.div_table_image_size_s;
            case DivSize.L:
                return R.dimen.div_table_image_size_l;
            case DivSize.XL:
                return R.dimen.div_table_image_size_xl;
            case DivSize.XXL:
                return R.dimen.div_table_image_size_xxl;
            case DivSize.M:
            default:
                return R.dimen.div_table_image_size_m;
        }
    }
}
