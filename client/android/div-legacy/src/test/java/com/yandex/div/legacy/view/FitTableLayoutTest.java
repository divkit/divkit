package com.yandex.div.legacy.view;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import androidx.annotation.NonNull;
import com.yandex.alicekit.core.utils.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class FitTableLayoutTest {

    private static final float FLOAT_COMPARISON_ERROR = 0.001f;

    private Context mContext;
    private FitTableLayout mTableLayout;

    @Before
    public void setUp() {
        mContext = Robolectric.buildActivity(Activity.class).get();
        mTableLayout = new FitTableLayout(mContext);
    }

    @Test
    public void testExactCellSizeApplied() {
        View cellView = createCellView(200, 40);

        mTableLayout.addView(cellView);

        mTableLayout.onMeasure(
                MeasureSpec.makeMeasureSpec(240, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(60, MeasureSpec.EXACTLY));

        Assert.assertEquals(200, cellView.getMeasuredWidth());
        Assert.assertEquals(40, cellView.getMeasuredHeight());
    }

    @Test
    public void testTableWrapsContent() {
        View cellView = createCellView(200, 40);

        mTableLayout.addView(cellView);

        mTableLayout.onMeasure(
                MeasureSpec.makeMeasureSpec(240, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(60, MeasureSpec.AT_MOST));

        Assert.assertEquals(200, mTableLayout.getMeasuredWidth());
        Assert.assertEquals(40, mTableLayout.getMeasuredHeight());
    }

    @Test
    public void testPaddingApplied() {
        View cellView1 = createCellView(40, 40);
        View cellView2 = createCellView(40, 40);

        mTableLayout.setColumnCount(2);
        mTableLayout.addView(cellView1);
        mTableLayout.addView(cellView2);
        mTableLayout.setPadding(10, 0, 10, 0);

        mTableLayout.onMeasure(
                MeasureSpec.makeMeasureSpec(240, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(60, MeasureSpec.AT_MOST));

        Assert.assertEquals(100, mTableLayout.getMeasuredWidth());
        Assert.assertEquals(40, mTableLayout.getMeasuredHeight());
    }

    @Test
    public void testCellMarginsApplied() {
        View cellView = createCellView(80, 40);
        setMargins(cellView, 10, 4, 10, 4);

        mTableLayout.addView(cellView);

        mTableLayout.onMeasure(
                MeasureSpec.makeMeasureSpec(240, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(60, MeasureSpec.AT_MOST));

        Assert.assertEquals(100, mTableLayout.getMeasuredWidth());
        Assert.assertEquals(48, mTableLayout.getMeasuredHeight());
    }

    @Test
    public void testMaxColumnHorizontalMarginsApplied() {
        View cellView1 = createCellView(80, 40);
        View cellView2 = createCellView(60, 40);
        setMargins(cellView1, 10, 0, 10, 0);
        setMargins(cellView2, 20, 0, 20, 0);

        mTableLayout.addView(cellView1);
        mTableLayout.addView(cellView2);

        mTableLayout.onMeasure(
                MeasureSpec.makeMeasureSpec(240, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(100, MeasureSpec.AT_MOST));

        Assert.assertEquals(120, mTableLayout.getMeasuredWidth());
        Assert.assertEquals(80, mTableLayout.getMeasuredHeight());
    }

    @Test
    public void testMaxRowVerticalMarginsApplied() {
        View cellView1 = createCellView(80, 40);
        View cellView2 = createCellView(80, 40);
        setMargins(cellView1, 0, 10, 0, 20);
        setMargins(cellView2, 0, 20, 0, 10);

        mTableLayout.setColumnCount(2);
        mTableLayout.addView(cellView1);
        mTableLayout.addView(cellView2);

        mTableLayout.onMeasure(
                MeasureSpec.makeMeasureSpec(240, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(100, MeasureSpec.AT_MOST));

        Assert.assertEquals(160, mTableLayout.getMeasuredWidth());
        Assert.assertEquals(80, mTableLayout.getMeasuredHeight());
    }

    @Test
    public void testMaxColumnWidthApplied() {
        View cellView1 = createCellView(80, 40);
        View cellView2 = createCellView(160, 40);

        mTableLayout.addView(cellView1);
        mTableLayout.addView(cellView2);

        mTableLayout.onMeasure(
                MeasureSpec.makeMeasureSpec(240, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(100, MeasureSpec.AT_MOST));

        Assert.assertEquals(160, mTableLayout.getMeasuredWidth());
    }

    @Test
    public void testMaxRowHeightApplied() {
        View cellView1 = createCellView(80, 40);
        View cellView2 = createCellView(80, 80);

        mTableLayout.setColumnCount(2);
        mTableLayout.addView(cellView1);
        mTableLayout.addView(cellView2);

        mTableLayout.onMeasure(
                MeasureSpec.makeMeasureSpec(240, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(100, MeasureSpec.AT_MOST));

        Assert.assertEquals(80, mTableLayout.getMeasuredHeight());
    }

    @Test
    public void testMaxColumnWithSpanWidthApplied() {
        View cellView1_1 = createCellView(80, 40);
        View cellView1_2 = createCellView(80, 40);
        View cellView2_1 = createCellView(200, 60);
        getLayoutParams(cellView2_1).span = 2;

        mTableLayout.setColumnCount(2);
        mTableLayout.addView(cellView1_1);
        mTableLayout.addView(cellView1_2);
        mTableLayout.addView(cellView2_1);

        mTableLayout.onMeasure(
                MeasureSpec.makeMeasureSpec(240, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(100, MeasureSpec.AT_MOST));

        Assert.assertEquals(200, mTableLayout.getMeasuredWidth());
    }

    @Test
    public void testSpannedCellWidthCorrectness() {
        View cellView1_1 = createCellView(80, 40);
        View cellView1_2 = createCellView(80, 40);
        View cellView2_1 = createCellView(FitTableLayout.LayoutParams.MATCH_PARENT, 40);
        getLayoutParams(cellView2_1).span = 2;

        mTableLayout.setColumnCount(2);
        mTableLayout.addView(cellView1_1);
        mTableLayout.addView(cellView1_2);
        mTableLayout.addView(cellView2_1);

        mTableLayout.onMeasure(
                MeasureSpec.makeMeasureSpec(240, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(100, MeasureSpec.AT_MOST));

        Assert.assertEquals(160, cellView2_1.getMeasuredWidth());
    }

    @Test
    public void testGoneChildIsNotAffectsGridSize() {
        View cellView1 = createCellView(80, 40);
        View cellView2 = createCellView(160, 60);
        cellView2.setVisibility(View.GONE);

        mTableLayout.addView(cellView1);
        mTableLayout.addView(cellView2);

        mTableLayout.onMeasure(
                MeasureSpec.makeMeasureSpec(240, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(100, MeasureSpec.AT_MOST));

        Assert.assertEquals(80, mTableLayout.getMeasuredWidth());
        Assert.assertEquals(40, mTableLayout.getMeasuredHeight());
    }

    @Test
    public void testCellSpanConstraintedByColumnCount() {
        View cellView1_1 = createCellView(80, 40);
        View cellView1_2 = createCellView(80, 40);
        View cellView1_3 = createCellView(80, 40);
        View cellView2_1 = createCellView(80, 40);
        View cellView2_2 = createCellView(FitTableLayout.LayoutParams.MATCH_PARENT, 40);
        getLayoutParams(cellView2_2).span = 10;

        mTableLayout.setColumnCount(3);
        mTableLayout.addView(cellView1_1);
        mTableLayout.addView(cellView1_2);
        mTableLayout.addView(cellView1_3);
        mTableLayout.addView(cellView2_1);
        mTableLayout.addView(cellView2_2);

        mTableLayout.onMeasure(
                MeasureSpec.makeMeasureSpec(240, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(100, MeasureSpec.AT_MOST));

        Assert.assertEquals(160, cellView2_2.getMeasuredWidth());
    }

    @Test
    public void testWeightedColumnFillsFreeSpace() {
        View cellView1 = createCellView(40, 40);
        View cellView2 = createCellView(FitTableLayout.LayoutParams.MATCH_PARENT, 40);
        View cellView3 = createCellView(40, 40);
        getLayoutParams(cellView2).weight = 1;

        mTableLayout.setColumnCount(3);
        mTableLayout.addView(cellView1);
        mTableLayout.addView(cellView2);
        mTableLayout.addView(cellView3);

        mTableLayout.onMeasure(
                MeasureSpec.makeMeasureSpec(240, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(100, MeasureSpec.AT_MOST));

        Assert.assertEquals(160, cellView2.getMeasuredWidth());
    }

    @Test
    public void testWeightedColumnsProportionsPreserved() {
        View cellView1 = createCellView(80, 40);
        View cellView2 = createCellView(FitTableLayout.LayoutParams.MATCH_PARENT, 40);
        getLayoutParams(cellView1).weight = 1;
        getLayoutParams(cellView2).weight = 2;

        mTableLayout.setColumnCount(2);
        mTableLayout.addView(cellView1);
        mTableLayout.addView(cellView2);

        mTableLayout.onMeasure(
                MeasureSpec.makeMeasureSpec(240, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(100, MeasureSpec.AT_MOST));

        Assert.assertEquals(160, cellView2.getMeasuredWidth());
    }

    @Test
    public void testWeightedColumnsProportionsPreservedWithFreeSpace() {
        View cellView1 = createCellView(80, 40);
        View cellView2 = createCellView(FitTableLayout.LayoutParams.MATCH_PARENT, 40);
        getLayoutParams(cellView1).weight = 1;
        getLayoutParams(cellView2).weight = 2;

        mTableLayout.setColumnCount(2);
        mTableLayout.addView(cellView1);
        mTableLayout.addView(cellView2);

        mTableLayout.onMeasure(
                MeasureSpec.makeMeasureSpec(270, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(100, MeasureSpec.AT_MOST));

        Assert.assertEquals(180, cellView2.getMeasuredWidth());
    }

    @Test
    public void testMaxColumnWeightApplied() {
        View cellView1_1 = createCellView(40, 40);
        View cellView1_2 = createCellView(FitTableLayout.LayoutParams.MATCH_PARENT, 40);
        View cellView1_3 = createCellView(40, 40);
        View cellView1_4 = createCellView(FitTableLayout.LayoutParams.MATCH_PARENT, 40);
        View cellView2_1 = createCellView(40, 40);
        View cellView2_2 = createCellView(FitTableLayout.LayoutParams.MATCH_PARENT, 40);
        View cellView2_3 = createCellView(40, 40);
        View cellView2_4 = createCellView(FitTableLayout.LayoutParams.MATCH_PARENT, 40);
        getLayoutParams(cellView1_2).weight = 1;
        getLayoutParams(cellView1_4).weight = 1;
        getLayoutParams(cellView2_2).weight = 1;
        getLayoutParams(cellView2_4).weight = 3;

        mTableLayout.setColumnCount(4);
        mTableLayout.addView(cellView1_1);
        mTableLayout.addView(cellView1_2);
        mTableLayout.addView(cellView1_3);
        mTableLayout.addView(cellView1_4);
        mTableLayout.addView(cellView2_1);
        mTableLayout.addView(cellView2_2);
        mTableLayout.addView(cellView2_3);
        mTableLayout.addView(cellView2_4);

        mTableLayout.onMeasure(
                MeasureSpec.makeMeasureSpec(240, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(100, MeasureSpec.AT_MOST));

        Assert.assertEquals(40, cellView1_2.getMeasuredWidth());
        Assert.assertEquals(40, cellView2_2.getMeasuredWidth());
        Assert.assertEquals(120, cellView1_4.getMeasuredWidth());
        Assert.assertEquals(120, cellView2_4.getMeasuredWidth());
    }

    @Test
    public void testColumnLayout() {
        View cellView1 = createCellView(80, 40);
        View cellView2 = createCellView(80, 40);

        mTableLayout.addView(cellView1);
        mTableLayout.addView(cellView2);

        mTableLayout.onMeasure(
                MeasureSpec.makeMeasureSpec(240, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(100, MeasureSpec.AT_MOST));
        mTableLayout.onLayout(true, 0, mTableLayout.getMeasuredWidth(), 0, mTableLayout.getMeasuredHeight());

        Assert.assertEquals(0, cellView2.getLeft());
        Assert.assertEquals(40, cellView2.getTop());
    }

    @Test
    public void testRowLayout() {
        View cellView1 = createCellView(80, 40);
        View cellView2 = createCellView(80, 40);

        mTableLayout.setColumnCount(2);
        mTableLayout.addView(cellView1);
        mTableLayout.addView(cellView2);

        mTableLayout.onMeasure(
                MeasureSpec.makeMeasureSpec(240, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(100, MeasureSpec.AT_MOST));
        mTableLayout.onLayout(true, 0, mTableLayout.getMeasuredWidth(), 0, mTableLayout.getMeasuredHeight());

        Assert.assertEquals(80, cellView2.getLeft());
        Assert.assertEquals(0, cellView2.getTop());
    }

    @Test
    public void testSpannedCellLayout() {
        View cellView1_1 = createCellView(80, 40);
        View cellView1_2 = createCellView(80, 40);
        View cellView1_3 = createCellView(80, 40);
        View cellView2_1 = createCellView(80, 40);
        View cellView2_2 = createCellView(80, 40);
        getLayoutParams(cellView2_1).span = 2;

        mTableLayout.setColumnCount(3);
        mTableLayout.addView(cellView1_1);
        mTableLayout.addView(cellView1_2);
        mTableLayout.addView(cellView1_3);
        mTableLayout.addView(cellView2_1);
        mTableLayout.addView(cellView2_2);

        mTableLayout.onMeasure(
                MeasureSpec.makeMeasureSpec(240, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(100, MeasureSpec.AT_MOST));
        mTableLayout.onLayout(true, 0, mTableLayout.getMeasuredWidth(), 0, mTableLayout.getMeasuredHeight());

        Assert.assertEquals(160, cellView2_2.getLeft());
        Assert.assertEquals(40, cellView2_2.getTop());
    }

    @Test
    public void testCellHorizontalGravityApplied() {
        View cellView1 = createCellView(80, 40);
        View cellView2 = createCellView(160, 40);
        getLayoutParams(cellView1).gravity = Gravity.CENTER_HORIZONTAL;

        mTableLayout.addView(cellView1);
        mTableLayout.addView(cellView2);

        mTableLayout.onMeasure(
                MeasureSpec.makeMeasureSpec(240, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(100, MeasureSpec.AT_MOST));
        mTableLayout.onLayout(true, 0, mTableLayout.getMeasuredWidth(), 0, mTableLayout.getMeasuredHeight());

        Assert.assertEquals(40, cellView1.getLeft());
        Assert.assertEquals(0, cellView1.getTop());
    }

    @Test
    public void testCellVerticalGravityApplied() {
        View cellView1 = createCellView(80, 40);
        View cellView2 = createCellView(80, 60);
        getLayoutParams(cellView1).gravity = Gravity.BOTTOM;

        mTableLayout.setColumnCount(2);
        mTableLayout.addView(cellView1);
        mTableLayout.addView(cellView2);

        mTableLayout.onMeasure(
                MeasureSpec.makeMeasureSpec(240, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(100, MeasureSpec.AT_MOST));
        mTableLayout.onLayout(true, 0, mTableLayout.getMeasuredWidth(), 0, mTableLayout.getMeasuredHeight());

        Assert.assertEquals(0, cellView1.getLeft());
        Assert.assertEquals(20, cellView1.getTop());
    }

    @Test
    public void testDownscaleApplied() {
        View cellView = createCellView(320, 40);

        mTableLayout.addView(cellView);

        mTableLayout.onMeasure(
                MeasureSpec.makeMeasureSpec(160, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(40, MeasureSpec.EXACTLY));
        mTableLayout.onLayout(true, 0, mTableLayout.getMeasuredWidth(), 0, mTableLayout.getMeasuredHeight());

        float expectedScale = 0.5f;
        Assert.assertTrue(equalFloats(cellView.getScaleX(), expectedScale));
        Assert.assertTrue(equalFloats(cellView.getScaleY(), expectedScale));
    }

    @Test
    public void testDownscaleAppliedToAllCellsInColumn() {
        View cellView1 = createCellView(160, 40);
        View cellView2 = createCellView(80, 40);

        mTableLayout.addView(cellView1);
        mTableLayout.addView(cellView2);

        mTableLayout.onMeasure(
                MeasureSpec.makeMeasureSpec(80, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(100, MeasureSpec.AT_MOST));
        mTableLayout.onLayout(true, 0, mTableLayout.getMeasuredWidth(), 0, mTableLayout.getMeasuredHeight());

        float expectedScale = 0.5f;
        Assert.assertTrue(equalFloats(cellView1.getScaleX(), expectedScale));
        Assert.assertTrue(equalFloats(cellView1.getScaleY(), expectedScale));
        Assert.assertTrue(equalFloats(cellView2.getScaleX(), expectedScale));
        Assert.assertTrue(equalFloats(cellView2.getScaleY(), expectedScale));
    }

    @Test
    public void testDownscaleAppliedToAllCellsInRow() {
        View cellView1 = createCellView(160, 40);
        View cellView2 = createCellView(80, 40);

        mTableLayout.setColumnCount(2);
        mTableLayout.addView(cellView1);
        mTableLayout.addView(cellView2);

        mTableLayout.onMeasure(
                MeasureSpec.makeMeasureSpec(160, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(40, MeasureSpec.EXACTLY));
        mTableLayout.onLayout(true, 0, mTableLayout.getMeasuredWidth(), 0, mTableLayout.getMeasuredHeight());

        float expectedScale = 0.6667f;
        Assert.assertTrue(equalFloats(cellView1.getScaleX(), expectedScale));
        Assert.assertTrue(equalFloats(cellView1.getScaleY(), expectedScale));
        Assert.assertTrue(equalFloats(cellView2.getScaleX(), expectedScale));
        Assert.assertTrue(equalFloats(cellView2.getScaleY(), expectedScale));
    }

    @NonNull
    private View createCellView(int widht, int height) {
        View cellView = new View(mContext);
        cellView.setLayoutParams(new FitTableLayout.LayoutParams(widht, height));
        return cellView;
    }

    @NonNull
    private static FitTableLayout.LayoutParams getLayoutParams(@NonNull View view) {
        return (FitTableLayout.LayoutParams) view.getLayoutParams();
    }

    private static void setMargins(@NonNull View view, int left, int top, int right, int bottom) {
        FitTableLayout.LayoutParams layoutParams = getLayoutParams(view);
        layoutParams.leftMargin = left;
        layoutParams.topMargin = top;
        layoutParams.rightMargin = right;
        layoutParams.bottomMargin = bottom;
    }

    private static boolean equalFloats(float lhs, float rhs) {
        return Math.abs(lhs - rhs) < FLOAT_COMPARISON_ERROR;
    }
}
