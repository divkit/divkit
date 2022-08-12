package com.yandex.div.legacy.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yandex.alicekit.core.utils.Assert;
import com.yandex.div.legacy.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// TODO(gulevsky): add RTL support
public final class FitTableLayout extends ViewGroup {

    private static final int MAX_SIZE = 32768;
    private static final int DEFAULT_COLUMN_COUNT = 1;
    private static final int UNINITIALIZED_HASH = 0;

    private final Grid mGrid = new Grid();

    private int mLastLayoutParamsHashCode = UNINITIALIZED_HASH;
    private boolean mInitialized = false;

    public FitTableLayout(@NonNull Context context) {
        this(context, null);
    }

    public FitTableLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FitTableLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FitTableLayout, defStyleAttr, 0);
        try {
            setColumnCount(array.getInt(R.styleable.FitTableLayout_android_columnCount, DEFAULT_COLUMN_COUNT));
        } finally {
            array.recycle();
        }
        mInitialized = true;
    }

    public int getColumnCount() {
        return mGrid.getColumnCount();
    }

    public void setColumnCount(int count) {
        mGrid.setColumnCount(count);
        invalidateStructure();
        requestLayout();
    }

    public int getRowCount() {
        return mGrid.getRowCount();
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams();
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        if (lp instanceof LayoutParams) {
            return new LayoutParams((LayoutParams) lp);
        } else if (lp instanceof MarginLayoutParams) {
            return new LayoutParams((MarginLayoutParams) lp);
        }
        return new LayoutParams(lp);
    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);

        child.setPivotX(0.0f);
        child.setPivotY(0.0f);
        invalidateStructure();
    }

    @Override
    public void onViewRemoved(View child) {
        super.onViewRemoved(child);
        invalidateStructure();
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
        if (mInitialized) {
            invalidateMeasurement();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        checkConsistency();
        invalidateMeasurement();

        resetChildrenScale();

        int hPadding = getPaddingLeft() + getPaddingRight();
        int vPadding = getPaddingTop() + getPaddingBottom();

        int maxWidthSpecWithoutPadding = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(MAX_SIZE - hPadding),
                MeasureSpec.getMode(widthMeasureSpec));
        int maxHeightSpecWithoutPadding = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(MAX_SIZE - vPadding),
                MeasureSpec.getMode(heightMeasureSpec));

        measureChildrenInitial(maxWidthSpecWithoutPadding, maxHeightSpecWithoutPadding);

        int widthSpecWithoutPadding = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(widthMeasureSpec - hPadding),
                MeasureSpec.getMode(widthMeasureSpec));
        int heightSpecWithoutPadding = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(heightMeasureSpec - vPadding),
                MeasureSpec.getMode(heightMeasureSpec));

        int contentWidth = mGrid.calcWidth(widthSpecWithoutPadding);
        int contentHeight = mGrid.calcHeight(heightSpecWithoutPadding);

        remeasureMatchParentChildren(widthSpecWithoutPadding, heightSpecWithoutPadding);

        float downscale = mGrid.getDownscaleFactor();
        if (downscale < 1.0f) {
            contentHeight = downscale(contentHeight, downscale);
        }

        int measuredWidth = Math.max(contentWidth + hPadding, getSuggestedMinimumWidth());
        int measuredHeight = Math.max(contentHeight + vPadding, getSuggestedMinimumHeight());

        setMeasuredDimension(
                resolveSizeAndState(measuredWidth, widthMeasureSpec, 0),
                resolveSizeAndState(measuredHeight, heightMeasureSpec, 0));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        checkConsistency();

        int childCount = getChildCount();
        List<? extends Bounds> columns = mGrid.getColumns();
        List<? extends Bounds> rows = mGrid.getRows();
        List<Cell> cells = mGrid.getCells();

        float downscale = mGrid.getDownscaleFactor();
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);

            if (child.getVisibility() == View.GONE) {
                continue;
            }

            LayoutParams params = getLayoutParams(child);
            Cell cell = cells.get(i);
            int cellLeft = getCellLeft(columns, cell);
            int cellTop = getCellTop(rows, cell);
            int cellWidth = getCellRight(columns, cell) - cellLeft;
            int cellHeight = getCellBottom(rows, cell) - cellTop;
            int childLeft = calcChildHorizontalPosition(cellLeft, cellWidth, child.getMeasuredWidth(), params.gravity);
            int childTop = calcChildVerticalPosition(cellTop, cellHeight, child.getMeasuredHeight(), params.gravity);

            if (downscale < 1.0f) {
                child.setScaleX(downscale);
                child.setScaleY(downscale);
                childLeft = downscale(childLeft, downscale);
                childTop = downscale(childTop, downscale);
            }

            childLeft += paddingLeft;
            childTop += paddingTop;

            child.layout(childLeft, childTop, childLeft + child.getMeasuredWidth(),
                         childTop + child.getMeasuredHeight());
        }
    }

    private static int calcChildHorizontalPosition(int cellLeft, int cellWidth, int childWidth, int gravity) {
        int horizontalGravity = gravity & Gravity.HORIZONTAL_GRAVITY_MASK;
        switch (horizontalGravity) {
            case Gravity.RIGHT:
                return cellLeft + cellWidth - childWidth;

            case Gravity.CENTER_HORIZONTAL:
                return cellLeft + (cellWidth - childWidth) / 2;

            case Gravity.LEFT:
            default:
                return cellLeft;
        }
    }

    private static int calcChildVerticalPosition(int cellTop, int cellHeight, int childHeight, int gravity) {
        int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;
        switch (verticalGravity) {
            case Gravity.BOTTOM:
                return cellTop + cellHeight - childHeight;

            case Gravity.CENTER_VERTICAL:
                return cellTop + (cellHeight - childHeight) / 2;

            case Gravity.TOP:
            default:
                return cellTop;
        }
    }

    private int downscale(int size, float factor) {
        return (int) Math.ceil(size * factor);
    }

    private void resetChildrenScale() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            child.setScaleX(1.0f);
            child.setScaleY(1.0f);
        }
    }

    private void measureChildrenInitial(int widthSpec, int heightSpec) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }

            LayoutParams params = getLayoutParams(child);
            int childWidth = params.width == LayoutParams.MATCH_PARENT ? 0 : params.width;
            int childHeight = params.height == LayoutParams.MATCH_PARENT ? 0 : params.height;
            measureChildInitial(child, widthSpec, heightSpec, childWidth, childHeight);
        }
    }

    private void remeasureMatchParentChildren(int widthSpec, int heightSpec) {
        List<Cell> cells = mGrid.getCells();
        List<? extends Bounds> columns = mGrid.getColumns();
        List<? extends Bounds> rows = mGrid.getRows();
        int childCount = getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }

            LayoutParams params = getLayoutParams(child);
            if (params.width != LayoutParams.MATCH_PARENT && params.height != LayoutParams.MATCH_PARENT) {
                continue;
            }

            Cell cell = cells.get(i);
            int celldWidth = getCellWidth(columns, cell);
            int cellHeight = getCellHeight(rows, cell);
            measureMatchParentChild(child, widthSpec, heightSpec, params.width, params.height, celldWidth, cellHeight);
        }
    }

    private static void measureChildInitial(View child, int parentWidthSpec, int parentHeightSpec, int childWidth, int childHeight) {
        int childWidthSpec = getChildMeasureSpec(parentWidthSpec, 0, childWidth);
        int childHeightSpec = getChildMeasureSpec(parentHeightSpec, 0, childHeight);
        child.measure(childWidthSpec, childHeightSpec);
    }

    private static void measureMatchParentChild(View child, int parentWidthSpec, int parentHeightSpec,
                                                int childWidth, int childHeight,
                                                int celldWidth, int cellHeight) {
        int childWidthSpec = childWidth == LayoutParams.MATCH_PARENT ?
                MeasureSpec.makeMeasureSpec(celldWidth, MeasureSpec.EXACTLY) :
                getChildMeasureSpec(parentWidthSpec, 0, childWidth);
        int childHeightSpec = childHeight == LayoutParams.MATCH_PARENT ?
                MeasureSpec.makeMeasureSpec(cellHeight, MeasureSpec.EXACTLY) :
                getChildMeasureSpec(parentHeightSpec, 0, childHeight);
        child.measure(childWidthSpec, childHeightSpec);
    }

    private static int getCellLeft(@NonNull List<? extends Bounds> columns, @NonNull Cell cell) {
        Bounds firstColumn = columns.get(cell.column);
        return firstColumn.offset + firstColumn.leadingMargin;
    }

    private static int getCellRight(@NonNull List<? extends Bounds> columns, @NonNull Cell cell) {
        Bounds lastColumn = columns.get(cell.lastColumn());
        return lastColumn.offset + lastColumn.size - lastColumn.trailingMargin;
    }

    private static int getCellTop(@NonNull List<? extends Bounds> rows, @NonNull Cell cell) {
        Bounds row = rows.get(cell.row);
        return row.offset + row.leadingMargin;
    }

    private static int getCellBottom(@NonNull List<? extends Bounds> rows, @NonNull Cell cell) {
        Bounds row = rows.get(cell.row);
        return row.offset + row.size - row.trailingMargin;
    }

    private static int getCellWidth(@NonNull List<? extends Bounds> columns, @NonNull Cell cell) {
        return getCellRight(columns, cell) - getCellLeft(columns, cell);
    }

    private static int getCellHeight(@NonNull List<? extends Bounds> rows, @NonNull Cell cell) {
        return getCellBottom(rows, cell) - getCellTop(rows, cell);
    }

    private void invalidateStructure() {
        mLastLayoutParamsHashCode = UNINITIALIZED_HASH;
        mGrid.invalidateStructure();
    }

    private void invalidateMeasurement() {
        mGrid.invalidateMeasurement();
    }

    private int computeLayoutParamsHashCode() {
        int result = 223;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            LayoutParams lp = getLayoutParams(child);
            result = 31 * result + lp.hashCode();
        }
        return result;
    }

    private void checkConsistency() {
        if (mLastLayoutParamsHashCode == UNINITIALIZED_HASH) {
            validateLayoutParams();
            mLastLayoutParamsHashCode = computeLayoutParamsHashCode();
        } else if (mLastLayoutParamsHashCode != computeLayoutParamsHashCode()) {
            invalidateStructure();
            checkConsistency();
        }
    }

    private void validateLayoutParams() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            LayoutParams params = getLayoutParams(child);

            if (params.weight < 0) {
                throw new IllegalStateException("Negative weights are not supported.");
            }

            if (params.span < 0) {
                throw new IllegalStateException("Negative spans are not supported.");
            }
        }
    }

    private static LayoutParams getLayoutParams(View child) {
        return (LayoutParams) child.getLayoutParams();
    }

    public static class LayoutParams extends MarginLayoutParams {

        private static final int DEFAULT_GRAVITY = Gravity.LEFT | Gravity.TOP;
        private static final int DEFAULT_WIDTH = WRAP_CONTENT;
        private static final int DEFAULT_HEIGHT = WRAP_CONTENT;
        private static final int DEFAULT_SPAN = 1;
        static final float UNDEFINED_WEIGHT = 0.0f;

        public int gravity;
        public int span;
        public float weight;

        public LayoutParams() {
            this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            gravity = DEFAULT_GRAVITY;
            span = DEFAULT_SPAN;
            weight = UNDEFINED_WEIGHT;
        }

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);

            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FitTableLayout_Layout);
            try {
                gravity = array.getInt(R.styleable.FitTableLayout_Layout_android_layout_gravity, DEFAULT_GRAVITY);
                span = array.getInt(R.styleable.FitTableLayout_Layout_android_layout_span, DEFAULT_SPAN);
                weight = array.getFloat(R.styleable.FitTableLayout_Layout_android_layout_weight, UNDEFINED_WEIGHT);
            } finally {
                array.recycle();
            }
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(LayoutParams source) {
            super(source);
            this.gravity = source.gravity;
            this.span = source.span;
            this.weight = source.weight;
        }

        @Override
        protected void setBaseAttributes(TypedArray attributes, int widthAttr, int heightAttr) {
            this.width = attributes.getLayoutDimension(widthAttr, DEFAULT_WIDTH);
            this.height = attributes.getLayoutDimension(heightAttr, DEFAULT_HEIGHT);
        }

        @Override
        public boolean equals(Object that) {
            if (this == that) {
                return true;
            }
            if (that == null || getClass() != that.getClass()) {
                return false;
            }

            LayoutParams params = (LayoutParams) that;
            return width == params.width &&
                    height == params.height &&
                    leftMargin == params.leftMargin &&
                    rightMargin == params.rightMargin &&
                    topMargin == params.topMargin &&
                    bottomMargin == params.bottomMargin &&
                    gravity == params.gravity &&
                    span == params.span &&
                    weight == params.weight;
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + gravity;
            result = 31 * result + span;
            result = 31 * result + Float.floatToIntBits(weight);
            return result;
        }
    }

    private static class Bounds {

        int size;
        int offset;
        int leadingMargin;
        int trailingMargin;

        Bounds(int size, int offset) {
            this(size, offset, 0, 0);
        }

        Bounds(int size, int offset, int leadingMargin, int trailingMargin) {
            this.size = size;
            this.offset = offset;
            this.leadingMargin = leadingMargin;
            this.trailingMargin = trailingMargin;
        }

        int getMargins() {
            return leadingMargin + trailingMargin;
        }

        int getCellSize() {
            return size - getMargins();
        }

        void include(int viewSize, int leadingMargin, int trailingMargin) {
            int cellSize = getCellSize();

            this.leadingMargin = Math.max(this.leadingMargin, leadingMargin);
            this.trailingMargin = Math.max(this.trailingMargin, trailingMargin);
            cellSize = Math.max(cellSize, viewSize);

            this.size = cellSize + this.leadingMargin + this.trailingMargin;
        }
    }

    private static class WeightedBounds extends Bounds {

        float weight = LayoutParams.UNDEFINED_WEIGHT;

        WeightedBounds(int size, int offset) {
            super(size, offset);
        }

        WeightedBounds(int size, int offset, int leadingMargin, int trailingMargin, float weight) {
            super(size, offset, leadingMargin, trailingMargin);
            this.weight = weight;
        }

        void include(int viewsize, int leadingMargin, int trailingMargin, float weight) {
            super.include(viewsize, leadingMargin, trailingMargin);
            this.weight = Math.max(this.weight, weight);
        }

        boolean isFlexible() {
            return weight != LayoutParams.UNDEFINED_WEIGHT;
        }
    }

    private static class Cell {

        final int viewIndex;
        final int column;
        final int row;
        final int span;

        Cell(int viewIndex, int column, int row, int span) {
            this.viewIndex = viewIndex;
            this.column = column;
            this.row = row;
            this.span = span;
        }

        int lastColumn() {
            return column + span - 1;
        }
    }

    private static class SizeConstraint {

        public int min;
        public int max;

        SizeConstraint(int min, int max) {
            this.min = min;
            this.max = max;
        }

        void set(int min, int max) {
            this.min = min;
            this.max = max;
        }
    }

    private class Grid {

        @NonNull
        private final List<WeightedBounds> mColumns = new ArrayList<>();
        @NonNull
        private final List<Bounds> mRows = new ArrayList<>();
        @NonNull
        private final List<Cell> mCells = new ArrayList<>();

        @NonNull
        private final SizeConstraint mWidthConstraint = new SizeConstraint(0, MAX_SIZE);
        @NonNull
        private final SizeConstraint mHeigthConstraint = new SizeConstraint(0, MAX_SIZE);
        private final Comparator<Cell> mLastColumnCellComparator = new LastColumnCellComparator();

        private int mColumnCount;
        private int mRowCount;
        private boolean mStructureValid;
        private boolean mColumnsMeasurmentValid;
        private boolean mRowsMeasurmentValid;

        int getColumnCount() {
            return mColumnCount;
        }

        void setColumnCount(int count) {
            mColumnCount = count;
        }

        int getRowCount() {
            if (!mStructureValid) {
                createStructure();
            }
            return mRowCount;
        }

        void invalidateStructure() {
            mCells.clear();
            mStructureValid = false;

            invalidateMeasurement();
        }

        void invalidateMeasurement() {
            mColumns.clear();
            mRows.clear();
            mColumnsMeasurmentValid = false;
            mRowsMeasurmentValid = false;
        }

        @NonNull
        List<Cell> getCells() {
            if (!mStructureValid) {
                createStructure();
            }
            return mCells;
        }

        @NonNull
        List<? extends Bounds> getColumns() {
            if (!mStructureValid) {
                createStructure();
            }
            if (!mColumnsMeasurmentValid) {
                measureColumns();
            }
            return mColumns;
        }

        @NonNull
        List<? extends Bounds> getRows() {
            if (!mStructureValid) {
                createStructure();
            }
            if (!mRowsMeasurmentValid) {
                measureRows();
            }
            return mRows;
        }

        int calcWidth(int widthSpec) {
            setParentConstraints(mWidthConstraint, widthSpec);
            int gridWidth = getGridWidth();
            return Math.max(mWidthConstraint.min, Math.min(gridWidth, mWidthConstraint.max));
        }

        int calcHeight(int heightSpec) {
            setParentConstraints(mHeigthConstraint, heightSpec);
            int gridHeight = getGridHeight();
            return Math.max(mHeigthConstraint.min, Math.min(gridHeight, mHeigthConstraint.max));
        }

        @FloatRange(from = 0.0f, to = 1.0f)
        float getDownscaleFactor() {
            int gridWidth = getGridWidth();
            if (gridWidth <= mWidthConstraint.max) {
                return 1.0f;
            }
            return (float) mWidthConstraint.max / gridWidth;
        }

        private int getGridWidth() {
            List<? extends Bounds> columns = getColumns();
            if (columns.isEmpty()) {
                return 0;
            }
            Bounds lastColumn = columns.get(columns.size() - 1);
            return lastColumn.offset + lastColumn.size;
        }

        private int getGridHeight() {
            List<? extends Bounds> rows = getRows();
            if (rows.isEmpty()) {
                return 0;
            }
            Bounds lastRow = rows.get(rows.size() - 1);
            return lastRow.offset + lastRow.size;
        }

        private void setParentConstraints(@NonNull SizeConstraint constraints, int measureSpec) {
            int mode = MeasureSpec.getMode(measureSpec);
            int size = MeasureSpec.getSize(measureSpec);
            switch (mode) {
                case MeasureSpec.UNSPECIFIED:
                    constraints.set(0, MAX_SIZE);
                    break;

                case MeasureSpec.EXACTLY:
                    constraints.set(size, size);
                    break;

                case MeasureSpec.AT_MOST:
                    constraints.set(0, size);
                    break;
            }
        }

        private void createStructure() {
            if (mStructureValid) {
                Assert.fail("Rectreation of valid structure is not allowed");
                return;
            }

            int columnCount = mColumnCount;
            int childCount = getChildCount();
            int filledCells = 0;

            for (int i = 0; i < childCount; i++) {
                LayoutParams params = getLayoutParams(getChildAt(i));
                int column = filledCells % columnCount;
                int row = filledCells / columnCount;
                int span = Math.min(params.span, columnCount - column);

                mCells.add(new Cell(i, column, row, span));
                filledCells += span;
            }

            mRowCount = (filledCells - 1) / columnCount + 1;
            mStructureValid = true;
        }

        private void measureRows() {
            for (int i = 0; i < mRowCount; i++) {
                mRows.add(new Bounds(0, 0));
            }

            for (int i = 0; i < mCells.size(); i++) {
                Cell cell = mCells.get(i);
                Bounds row = mRows.get(cell.row);
                View child = getChildAt(cell.viewIndex);

                LayoutParams params = getLayoutParams(child);
                row.include(child.getMeasuredHeight(), params.topMargin, params.bottomMargin);
            }

            int totalHeight = 0;
            for (int i = 0; i < mRowCount; i++) {
                Bounds row = mRows.get(i);
                row.offset = totalHeight;
                totalHeight += row.size;
            }

            mRowsMeasurmentValid = true;
        }

        private void measureColumns() {
            for (int i = 0; i < mColumnCount; i++) {
                mColumns.add(new WeightedBounds(0, 0));
            }

            List<Cell> reorderedCells = createLastColumnOrderedCells();
            List<Cell> fixedSpannedCells = new ArrayList<>();
            List<Cell> flexibleSpannedCells = new ArrayList<>();

            // Pass one: placing children without span
            for (int i = 0; i < reorderedCells.size(); i++) {
                Cell cell = reorderedCells.get(i);
                View child = getChildAt(cell.viewIndex);
                if (child.getVisibility() == GONE) {
                    continue;
                }

                LayoutParams params = getLayoutParams(child);
                WeightedBounds column = mColumns.get(cell.column);
                if (cell.span == 1) {
                    column.include(child.getMeasuredWidth(), params.leftMargin, params.rightMargin, params.weight);
                } else {
                    if (isFlexibleSpan(cell)) {
                        flexibleSpannedCells.add(cell);
                    } else {
                        fixedSpannedCells.add(cell);
                    }
                    Bounds endColumn = mColumns.get(cell.column + cell.span - 1);
                    column.include(0, params.leftMargin, Integer.MIN_VALUE, params.weight);
                    endColumn.include(0, Integer.MIN_VALUE, params.rightMargin);
                }
            }

            // Pass two: placing children with fixed span
            for (int i = 0; i < fixedSpannedCells.size(); i++) {
                Cell cell = fixedSpannedCells.get(i);
                View child = getChildAt(cell.viewIndex);
                Bounds firstColumn = mColumns.get(cell.column);
                Bounds lastColumn = mColumns.get(cell.column + cell.span - 1);

                int remainedWidth = child.getMeasuredWidth() + firstColumn.leadingMargin;
                for (int j = 0; j < cell.span - 1; j++) {
                    WeightedBounds column = mColumns.get(cell.column + j);
                    remainedWidth -= column.size;
                }
                lastColumn.include(remainedWidth - lastColumn.leadingMargin, lastColumn.leadingMargin,
                                   lastColumn.trailingMargin);
            }

            // Pass three: placing children with flexible span
            for (int i = 0; i < flexibleSpannedCells.size(); i++) {
                Cell cell = flexibleSpannedCells.get(i);
                View child = getChildAt(cell.viewIndex);
                Bounds firstColumn = mColumns.get(cell.column);
                Bounds lastColumn = mColumns.get(cell.column + cell.span - 1);

                int flexibleWidth = child.getMeasuredWidth() + firstColumn.leadingMargin + lastColumn.trailingMargin;
                float spanTotalWeight = 0.0f;
                for (int j = 0; j < cell.span; j++) {
                    WeightedBounds column = mColumns.get(cell.column + j);
                    if (column.isFlexible()) {
                        spanTotalWeight += column.weight;
                    } else {
                        flexibleWidth -= column.size;
                    }
                }

                for (int j = 0; j < cell.span; j++) {
                    WeightedBounds column = mColumns.get(cell.column + j);
                    if (column.isFlexible()) {
                        int viewWidth = (int) Math.ceil(column.weight / spanTotalWeight * flexibleWidth);
                        viewWidth -= column.leadingMargin + column.trailingMargin;
                        column.include(viewWidth, column.leadingMargin, column.trailingMargin);
                    }
                }
            }

            // Pass four: adjusting weighted columns

            float totalWeight = 0.0f;
            float maxWeightedWidth = 0.0f;
            for (int i = 0; i < mColumnCount; i++) {
                WeightedBounds column = mColumns.get(i);
                if (column.isFlexible()) {
                    totalWeight += column.weight;
                    maxWeightedWidth = Math.max(maxWeightedWidth, column.getCellSize() / column.weight);
                }
            }
            int minTotalWidth = 0;
            for (int i = 0; i < mColumnCount; i++) {
                WeightedBounds column = mColumns.get(i);
                if (column.isFlexible()) {
                    int viewWidth = (int) Math.ceil(column.weight * maxWeightedWidth);
                    column.include(viewWidth, column.leadingMargin, column.trailingMargin);
                }
                minTotalWidth += column.size;
            }
            int freeSpace = calcFreeSpace(minTotalWidth);
            for (int i = 0; i < mColumnCount; i++) {
                WeightedBounds column = mColumns.get(i);
                if (column.isFlexible()) {
                    int viewWidth = (int) Math.ceil(column.getCellSize() + (freeSpace * column.weight / totalWeight));
                    column.include(viewWidth, column.leadingMargin, column.trailingMargin);
                }
            }

            // Pass five: columns aligning
            int totalWidth = 0;
            for (int i = 0; i < mColumnCount; i++) {
                Bounds column = mColumns.get(i);
                column.offset = totalWidth;
                totalWidth += column.size;
            }

            mColumnsMeasurmentValid = true;
        }

        @NonNull
        private List<Cell> createLastColumnOrderedCells() {
            List<Cell> result = new ArrayList<>(mCells);
            Collections.sort(result, mLastColumnCellComparator);
            return result;
        }

        private boolean isFlexibleSpan(@NonNull Cell cell) {
            if (cell.span == 1) {
                return false;
            }

            for (int i = 0; i < cell.span; i++) {
                WeightedBounds column = mColumns.get(cell.column + i);
                if (column.isFlexible()) {
                    return true;
                }
            }
            return false;
        }

        private int calcFreeSpace(int minWidth) {
            return Math.max(0, mWidthConstraint.max - minWidth);
        }
    }

    private static final class LastColumnCellComparator implements Comparator<Cell> {
        @Override
        public int compare(Cell lhs, Cell rhs) {
            int lhsLastColumn = lhs.column + lhs.span;
            int rhsLastColumn = rhs.column + rhs.span;
            if (lhsLastColumn < rhsLastColumn) {
                return -1;
            } else if (lhsLastColumn > rhsLastColumn) {
                return 1;
            } else {
                return Integer.compare(lhs.row, rhs.row);
            }
        }
    }
}
