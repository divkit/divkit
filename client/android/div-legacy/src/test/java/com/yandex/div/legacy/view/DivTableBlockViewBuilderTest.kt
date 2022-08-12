package com.yandex.div.legacy.view

import android.app.Activity
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.yandex.alicekit.core.widget.YandexSansTypefaceProvider
import com.yandex.div.DivAlignment
import com.yandex.div.DivAlignmentVertical
import com.yandex.div.DivSize
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.legacy.DivTextStyleProvider
import com.yandex.div.legacy.R
import com.yandex.div.legacy.column
import com.yandex.div.legacy.columns
import com.yandex.div.legacy.divTable
import com.yandex.div.legacy.image
import com.yandex.div.legacy.imageAndText
import com.yandex.div.legacy.row
import com.yandex.div.legacy.rows
import com.yandex.div.legacy.separator
import com.yandex.div.legacy.text
import com.yandex.div.view.SeparatorView
import com.yandex.div.view.pooling.PseudoViewPool
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivTableBlockViewBuilderTest {

    companion object {
        private const val IMAGE_URL = "http://example.com"
        private const val TEXT = "TEST TEXT"
        private val TEXT_STYLE = TextStyle.Builder()
            .setTypefaceProvider { YandexSansTypefaceProvider(Robolectric.buildActivity(Activity::class.java).get()).regular }
            .setColor(R.color.div_text_dark_disabled_80)
            .setTextSizeDimenRes(R.dimen.div_style_text_size_text_m)
            .setLetterSpacingDimenRes(R.dimen.div_style_text_letter_spacing_no)
            .setLineSpaceExtra(R.dimen.div_style_text_line_space_extra_text_m)
            .createTextStyle()
    }

    private val activity = Robolectric.setupActivity(Activity::class.java)
    private val textStyleProvider = mock<DivTextStyleProvider>()
    private val imageLoader = mock<DivImageLoader>()
    private val divView = mock<DivView>()

    private lateinit var builder: DivTableBlockViewBuilder

    @Before
    fun setUp() {
        whenever(textStyleProvider.getTextStyle(any())).thenReturn(TEXT_STYLE)
        whenever(textStyleProvider.textM).thenReturn(TEXT_STYLE)

        builder = DivTableBlockViewBuilder(activity,
            PseudoViewPool(), imageLoader, textStyleProvider)
    }

    @Test(expected = AssertionError::class)
    fun testInconsistentColumnsAndCellsFailed() {
        val tableBlock = divTable {
            columns {
                column(); column(weight = 1); column()
            }
            rows {
                row {
                    text(TEXT); text(TEXT)
                }
            }
        }

        Assert.assertNull(builder.build(divView, tableBlock))
    }

    @Test(expected = AssertionError::class)
    fun testInconsistentCellsInDifferentRowsFailed() {
        val tableBlock = divTable {
            rows {
                row {
                    text(TEXT); text(TEXT)
                }
                row {
                    image(IMAGE_URL); text(TEXT); image(IMAGE_URL)
                }
            }
        }

        Assert.assertNull(builder.build(divView, tableBlock))
    }

    @Test(expected = AssertionError::class)
    fun testTableWithoutRowsFailed() {
        val tableBlock = divTable {
            rows {}
        }

        Assert.assertNull(builder.build(divView, tableBlock))
    }

    @Test(expected = AssertionError::class)
    fun testTableWithOnlySeparatorFailed() {
        val tableBlock = divTable {
            rows {
                separator()
            }
        }

        Assert.assertNull(builder.build(divView, tableBlock))
    }

    @Test
    fun testTableWithValidDataCreated() {
        val tableBlock = divTable {
            columns {
                column(); column(weight = 1); column()
            }
            rows {
                separator()
                row {
                    text(TEXT); text(TEXT); text(TEXT)
                }
                separator()
                row {
                    image(IMAGE_URL); text(TEXT); image(IMAGE_URL)
                }
                separator()
                row {
                    text(TEXT); imageAndText(text = TEXT, imageUrl = IMAGE_URL); text(TEXT)
                }
                separator()
            }
        }

        Assert.assertNotNull(builder.build(divView, tableBlock))
    }

    @Test
    fun testTableChildCount() {
        val tableBlock = divTable {
            rows {
                row {
                    text(TEXT); text(TEXT); text(TEXT)
                }
            }
        }

        val tableLayout = builder.build(divView, tableBlock) as FitTableLayout
        Assert.assertEquals(3, tableLayout.childCount)
    }

    @Test
    fun testTableChildTypes() {
        val tableBlock = divTable {
            rows {
                row {
                    text(TEXT); image(IMAGE_URL); imageAndText(text = TEXT, imageUrl = IMAGE_URL)
                }
                separator()
            }
        }

        val tableLayout = builder.build(divView, tableBlock) as FitTableLayout
        Assert.assertTrue(tableLayout.getChildAt(0) is TextView)
        Assert.assertTrue(tableLayout.getChildAt(1) is RatioImageView)
        Assert.assertTrue(tableLayout.getChildAt(2) is LinearLayout)
        Assert.assertTrue(tableLayout.getChildAt(3) is SeparatorView)
    }


    @Test
    fun testCellAlignment() {
        val tableBlock = divTable {
            rows {
                row {
                    text(
                        TEXT,
                        horizontalAlignment = DivAlignment.CENTER,
                        verticalAlignment = DivAlignmentVertical.BOTTOM
                    )
                }
            }
        }

        val tableLayout = builder.build(divView, tableBlock) as FitTableLayout
        val textCell = tableLayout.getChildAt(0) as TextView
        val layoutParams = textCell.layoutParams as FitTableLayout.LayoutParams
        Assert.assertEquals(Gravity.CENTER_HORIZONTAL.or(Gravity.BOTTOM), layoutParams.gravity)
    }

    @Test
    fun testRowPaddings() {
        val tableBlock = divTable {
            rows {
                row(topPadding = DivSize.XS, bottomPadding = DivSize.S) {
                    text(TEXT)
                }
            }
        }

        val tableLayout = builder.build(divView, tableBlock) as FitTableLayout
        val textCell = tableLayout.getChildAt(0) as TextView
        val layoutParams = textCell.layoutParams as FitTableLayout.LayoutParams
        Assert.assertEquals(8, layoutParams.topMargin)
        Assert.assertEquals(12, layoutParams.bottomMargin)
    }

    @Test
    fun testColumnPaddings() {
        val tableBlock = divTable {
            columns {
                column(leftPadding = DivSize.M, rightPadding = DivSize.L)
            }
            rows {
                row {
                    text(TEXT)
                }
            }
        }

        val tableLayout = builder.build(divView, tableBlock) as FitTableLayout
        val textCell = tableLayout.getChildAt(0) as TextView
        val layoutParams = textCell.layoutParams as FitTableLayout.LayoutParams
        Assert.assertEquals(16, layoutParams.leftMargin)
        Assert.assertEquals(20, layoutParams.rightMargin)
    }
}
