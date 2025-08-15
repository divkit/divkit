package com.yandex.div.core.widget

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup.LayoutParams
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GridContainerTest {

    private val context: Context = Robolectric.buildActivity(Activity::class.java).get()
    private val cells = cells()

    @Test
    fun `empty grid has zero size`() {
        val grid = with(context) {
            gridContainer()
        }

        grid.measure(measureSpec(MeasureSpec.AT_MOST, 120), measureSpec(MeasureSpec.AT_MOST, 60))

        assertEquals(0, grid.measuredWidth)
        assertEquals(0, grid.measuredHeight)
    }

    @Test
    fun `cell and grid has its own fixed sizes`() {
        val grid = with(context) {
            gridContainer {
                cells[0, 0] = view {
                    layoutParams(width = 80, height = 40)
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.EXACTLY, 120), measureSpec(MeasureSpec.EXACTLY, 60))

        assertEquals(80, cells[0, 0].measuredWidth)
        assertEquals(40, cells[0, 0].measuredHeight)

        assertEquals(120, grid.measuredWidth)
        assertEquals(60, grid.measuredHeight)
    }

    @Test
    fun `grid wraps fixed cell`() {
        val grid = with(context) {
            gridContainer {
                view {
                    layoutParams(width = 80, height = 40)
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.AT_MOST, 120), measureSpec(MeasureSpec.AT_MOST, 60))

        assertEquals(80, grid.measuredWidth)
        assertEquals(40, grid.measuredHeight)
    }

    @Test
    fun `cell size can not be inferred without anchors`() {
        val grid = with(context) {
            gridContainer {
                cells[0, 0] = view {
                    layoutParams(width = LayoutParams.MATCH_PARENT, height = LayoutParams.MATCH_PARENT)
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.EXACTLY, 120), measureSpec(MeasureSpec.EXACTLY, 60))

        assertEquals(0, cells[0, 0].measuredWidth)
        assertEquals(0, cells[0, 0].measuredHeight)
    }

    @Test
    fun `match_parent cell width inferred from column width`() {
        val grid = with(context) {
            gridContainer {
                cells[0, 0] = view {
                    layoutParams(width = LayoutParams.MATCH_PARENT, height = 40)
                }
                cells[1, 0] = view {
                    layoutParams(width = 80, height = 40)
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.EXACTLY, 120), measureSpec(MeasureSpec.EXACTLY, 60))

        assertEquals(80, cells[0, 0].measuredWidth)
    }

    @Test
    fun `match_parent cell height inferred from row height`() {
        val grid = with(context) {
            gridContainer {
                columnCount = 2

                cells[0, 0] = view {
                    layoutParams(width = 80, height = LayoutParams.MATCH_PARENT)
                }
                cells[1, 0] = view {
                    layoutParams(width = 80, height = 40)
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.EXACTLY, 120), measureSpec(MeasureSpec.EXACTLY, 60))

        assertEquals(40, cells[0, 0].measuredHeight)
    }

    @Test
    fun `gone child is not affects grid size`() {
        val grid = with(context) {
            gridContainer {
                view {
                    layoutParams(width = 80, height = 40)
                }
                view {
                    layoutParams(width = 80, height = 40)
                    visibility = View.GONE
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.AT_MOST, 240), measureSpec(MeasureSpec.AT_MOST, 120))

        assertEquals(80, grid.measuredWidth)
        assertEquals(40, grid.measuredHeight)
    }

    @Test
    fun `grid paddings applied`() {
        val grid = with(context) {
            gridContainer {
                columnCount = 2
                setPadding(20, 10, 20, 10)

                view {
                    layoutParams(width = 80, height = 40)
                }
                view {
                    layoutParams(width = 80, height = 40)
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.AT_MOST, 240), measureSpec(MeasureSpec.AT_MOST, 120))

        assertEquals(200, grid.measuredWidth)
        assertEquals(60, grid.measuredHeight)
    }

    @Test
    fun `cell margins applied`() {
        val grid = with(context) {
            gridContainer {
                view {
                    layoutParams(width = 80, height = 40) {
                        leftMargin = 10
                        topMargin = 20
                        rightMargin = 10
                        bottomMargin = 20
                    }
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.AT_MOST, 240), measureSpec(MeasureSpec.AT_MOST, 120))

        assertEquals(100, grid.measuredWidth)
        assertEquals(80, grid.measuredHeight)
    }

    @Test
    fun `horizontal margins sets view horizontal offsets individually`() {
        val grid = with(context) {
            gridContainer {
                cells[0, 0] = view {
                    layoutParams(width = 80, height = 40) {
                        leftMargin = 10
                        rightMargin = 10
                    }
                }
                cells[1, 0] = view {
                    layoutParams(width = 80, height = 40) {
                        leftMargin = 20
                        rightMargin = 20
                    }
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.AT_MOST, 240), measureSpec(MeasureSpec.AT_MOST, 120))
        grid.layout(0, grid.measuredWidth, 0, grid.measuredHeight)

        assertEquals(10, cells[0, 0].left)
        assertEquals(20, cells[1, 0].left)
    }

    @Test
    fun `vertical margins sets view vertical offsets individually`() {
        val grid = with(context) {
            gridContainer {
                columnCount = 2

                cells[0, 0] = view {
                    layoutParams(width = 80, height = 40) {
                        topMargin = 10
                        bottomMargin = 10
                    }
                }
                cells[0, 1] = view {
                    layoutParams(width = 80, height = 40) {
                        topMargin = 20
                        bottomMargin = 20
                    }
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.AT_MOST, 240), measureSpec(MeasureSpec.AT_MOST, 120))
        grid.layout(0, grid.measuredWidth, 0, grid.measuredHeight)

        assertEquals(10, cells[0, 0].top)
        assertEquals(20, cells[0, 1].top)
    }

    @Test
    fun `max width applied to column`() {
        val grid = with(context) {
            gridContainer {
                view {
                    layoutParams(width = 80, height = 40)
                }
                view {
                    layoutParams(width = 160, height = 40)
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.AT_MOST, 240), measureSpec(MeasureSpec.AT_MOST, 120))

        assertEquals(160, grid.measuredWidth)
    }

    @Test
    fun `max height applied to row`() {
        val grid = with(context) {
            gridContainer {
                columnCount = 2

                view {
                    layoutParams(width = 80, height = 40)
                }
                view {
                    layoutParams(width = 80, height = 80)
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.AT_MOST, 240), measureSpec(MeasureSpec.AT_MOST, 120))

        assertEquals(80, grid.measuredHeight)
    }

    @Test
    fun `max spanned cell width applied`() {
        val grid = with(context) {
            gridContainer {
                columnCount = 2

                view {
                    layoutParams(width = 80, height = 40)
                }
                view {
                    layoutParams(width = 80, height = 40)
                }
                view {
                    layoutParams(width = 200, height = 40) {
                        columnSpan = 2
                    }
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.AT_MOST, 240), measureSpec(MeasureSpec.AT_MOST, 120))

        assertEquals(200, grid.measuredWidth)
    }

    @Test
    fun `max spanned cell height applied`() {
        val grid = with(context) {
            gridContainer {
                columnCount = 2

                view {
                    layoutParams(width = 80, height = 40)
                }
                view {
                    layoutParams(width = 80, height = 100) {
                        rowSpan = 2
                    }
                }
                view {
                    layoutParams(width = 80, height = 40)
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.AT_MOST, 240), measureSpec(MeasureSpec.AT_MOST, 120))

        assertEquals(100, grid.measuredHeight)
    }

    @Test
    fun `spanned cell width inferred from column widthes`() {
        val grid = with(context) {
            gridContainer {
                columnCount = 2

                cells[0, 0] = view {
                    layoutParams(width = 80, height = 40)
                }
                cells[0, 1] = view {
                    layoutParams(width = 80, height = 40)
                }
                cells[1, 0] = view {
                    layoutParams(width = LayoutParams.MATCH_PARENT, height = 40) {
                        columnSpan = 2
                    }
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.AT_MOST, 240), measureSpec(MeasureSpec.AT_MOST, 120))

        assertEquals(160,  cells[1, 0].measuredWidth)
    }

    @Test
    fun `spanned cell height inferred from row heights`() {
        val grid = with(context) {
            gridContainer {
                columnCount = 2

                cells[0, 0] = view {
                    layoutParams(width = 80, height = 40)
                }
                cells[0, 1] = view {
                    layoutParams(width = 80, height = LayoutParams.MATCH_PARENT) {
                        rowSpan = 2
                    }
                }
                cells[1, 0] = view {
                    layoutParams(width = 80, height = 40)
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.AT_MOST, 240), measureSpec(MeasureSpec.AT_MOST, 120))

        assertEquals(80, cells[0, 1].measuredHeight)
    }

    @Test
    fun `grid row count inferred from the only cell at bottom line`() {
        val grid = with(context) {
            gridContainer {
                columnCount = 2

                view {
                    layoutParams(width = 80, height = 40)
                }
                view {
                    layoutParams(width = 80, height = 40)
                }
                view {
                    layoutParams(width = 80, height = 40)
                }
            }
        }

        assertEquals(2, grid.rowCount)
    }

    @Test
    fun `grid row count inferred from minimal row span at bottom line`() {
        val grid = with(context) {
            gridContainer {
                columnCount = 2

                view {
                    layoutParams(width = 80, height = 40)
                }
                view {
                    layoutParams(width = 80, height = 40)
                }
                view {
                    layoutParams(width = 80, height = 40) {
                        rowSpan = 2
                    }
                }
                view {
                    layoutParams(width = 80, height = 40) {
                        rowSpan = 3
                    }
                }
            }
        }

        assertEquals(3, grid.rowCount)
    }

    @Test
    fun `cell column span cropped by column count`() {
        val grid = with(context) {
            gridContainer {
                columnCount = 3

                cells[0, 0] = view {
                    layoutParams(width = 80, height = 40)
                }
                cells[0, 1] = view {
                    layoutParams(width = 80, height = 40)
                }
                cells[0, 2] = view {
                    layoutParams(width = 80, height = 40)
                }
                cells[1, 0] = view {
                    layoutParams(width = 80, height = 40)
                }
                cells[1, 1] = view {
                    layoutParams(width = LayoutParams.MATCH_PARENT, height = 40) {
                        columnSpan = 8
                    }
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.AT_MOST, 240), measureSpec(MeasureSpec.AT_MOST, 80))

        assertEquals(160, cells[1, 1].measuredWidth)
    }

    @Test
    fun `cell row span cropped by row count`() {
        val grid = with(context) {
            gridContainer {
                columnCount = 2

                cells[0, 0] = view {
                    layoutParams(width = 80, height = 40)
                }
                cells[0, 1] = view {
                    layoutParams(width = 80, height = 40)
                }
                cells[1, 0] = view {
                    layoutParams(width = 80, height = LayoutParams.MATCH_PARENT) {
                        rowSpan = 8
                    }
                }
                cells[1, 1] = view {
                    layoutParams(width = 80, height = 40)
                }
                cells[2, 1] = view {
                    layoutParams(width = 80, height = 40)
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.AT_MOST, 240), measureSpec(MeasureSpec.AT_MOST, 120))

        assertEquals(80, cells[1, 0].measuredHeight)
    }

    @Test
    fun `cell row span cropped by other one column span`() {
        val grid = with(context) {
            gridContainer {
                columnCount = 3

                cells[0, 0] = view {
                    layoutParams(width = 80, height = 40)
                }
                cells[0, 1] = view {
                    layoutParams(width = 80, height = LayoutParams.MATCH_PARENT) {
                        rowSpan = 3
                    }
                }
                cells[0, 2] = view {
                    layoutParams(width = 80, height = 40)
                }
                cells[1, 0] = view {
                    layoutParams(width = LayoutParams.MATCH_PARENT, height = 40) {
                        columnSpan = 3
                    }
                }
                cells[2, 0] = view {
                    layoutParams(width = 80, height = 40)
                }
                cells[2, 1] = view {
                    layoutParams(width = 80, height = 40)
                }
                cells[2, 2] = view {
                    layoutParams(width = 80, height = 40)
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.AT_MOST, 240), measureSpec(MeasureSpec.AT_MOST, 120))

        assertEquals(40, cells[0, 1].measuredHeight)
    }

    @Test
    fun `unused columns filled by span`() {
        val grid = with(context) {
            gridContainer {
                columnCount = 3

                view {
                    layoutParams(width = 80, height = 40) {
                        columnSpan = 2
                    }
                }
                view {
                    layoutParams(width = 40, height = 40)
                }
                view {
                    layoutParams(width = 40, height = 40)
                }
                view {
                    layoutParams(width = 80, height = 40) {
                        columnSpan = 2
                    }
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.AT_MOST, 240), measureSpec(MeasureSpec.AT_MOST, 120))

        assertEquals(120, grid.measuredWidth)
    }

    @Test
    fun `unused row filled by span`() {
        val grid = with(context) {
            gridContainer {
                columnCount = 2

                view {
                    layoutParams(width = 40, height = 80) {
                        rowSpan = 2
                    }
                }
                view {
                    layoutParams(width = 40, height = 40)
                }
                view {
                    layoutParams(width = 40, height = 80) {
                        rowSpan = 2
                    }
                }
                view {
                    layoutParams(width = 40, height = 40)
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.AT_MOST, 120), measureSpec(MeasureSpec.AT_MOST, 240))

        assertEquals(120, grid.measuredHeight)
    }

    @Test
    fun `weighted column fills free space`() {
        val grid = with(context) {
            gridContainer {
                columnCount = 3

                cells[0, 0] = view {
                    layoutParams(width = 80, height = 40)
                }
                cells[0, 1] = view {
                    layoutParams(width = LayoutParams.MATCH_PARENT, height = 40) {
                        horizontalWeight = 1f
                    }
                }
                cells[0, 2] = view {
                    layoutParams(width = 80, height = 40)
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.EXACTLY, 320), measureSpec(MeasureSpec.AT_MOST, 80))

        assertEquals(160, cells[0, 1].measuredWidth)
    }

    @Test
    fun `weighted row fills free space`() {
        val grid = with(context) {
            gridContainer {
                cells[0, 0] = view {
                    layoutParams(width = 80, height = 40)
                }
                cells[1, 0] = view {
                    layoutParams(width = 80, height = LayoutParams.MATCH_PARENT) {
                        verticalWeight = 1f
                    }
                }
                cells[2, 0] = view {
                    layoutParams(width = 80, height = 40)
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.AT_MOST, 240), measureSpec(MeasureSpec.EXACTLY, 160))

        assertEquals(80, cells[1, 0].measuredHeight)
    }

    @Test
    fun `weighted column proportion preserved`() {
        val grid = with(context) {
            gridContainer {
                columnCount = 2

                cells[0, 0] = view {
                    layoutParams(width = 80, height = 40) {
                        horizontalWeight = 1f
                    }
                }
                cells[0, 1] = view {
                    layoutParams(width = LayoutParams.MATCH_PARENT, height = 40) {
                        horizontalWeight = 2f
                    }
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.EXACTLY, 240), measureSpec(MeasureSpec.AT_MOST, 120))

        assertEquals(160, cells[0, 1].measuredWidth)
    }

    @Test
    fun `weighted row proportions preserved`() {
        val grid = with(context) {
            gridContainer {
                cells[0, 0] = view {
                    layoutParams(width = 80, height = 40) {
                        verticalWeight = 1f
                    }
                }
                cells[1, 0] = view {
                    layoutParams(width = 80, height = LayoutParams.MATCH_PARENT) {
                        verticalWeight = 2f
                    }
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.AT_MOST, 240), measureSpec(MeasureSpec.EXACTLY, 120))

        assertEquals(80, cells[1, 0].measuredHeight)
    }

    @Test
    fun `weighted column proportions preserved with free space`() {
        val grid = with(context) {
            gridContainer {
                columnCount = 2

                cells[0, 0] = view {
                    layoutParams(width = 80, height = 40) {
                        horizontalWeight = 1f
                    }
                }
                cells[0, 1] = view {
                    layoutParams(width = LayoutParams.MATCH_PARENT, height = 40) {
                        horizontalWeight = 2f
                    }
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.EXACTLY, 270), measureSpec(MeasureSpec.AT_MOST, 120))

        assertEquals(180, cells[0, 1].measuredWidth)
    }

    @Test
    fun `weighted row proportions preserved with free space`() {
        val grid = with(context) {
            gridContainer {
                cells[0, 0] = view {
                    layoutParams(width = 80, height = 40) {
                        verticalWeight = 1f
                    }
                }
                cells[1, 0] = view {
                    layoutParams(width = 80, height = LayoutParams.MATCH_PARENT) {
                        verticalWeight = 2f
                    }
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.AT_MOST, 160), measureSpec(MeasureSpec.EXACTLY, 150))

        assertEquals(100, cells[1, 0] .measuredHeight)
    }

    @Test
    fun `max column weight applied`() {
        val grid = with(context) {
            gridContainer {
                columnCount = 4

                cells[0, 0] = view {
                    layoutParams(width = 40, height = 40)
                }
                cells[0, 1] = view {
                    layoutParams(width = LayoutParams.MATCH_PARENT, height = 40) {
                        horizontalWeight = 1f
                    }
                }
                cells[0, 2] = view {
                    layoutParams(width = 40, height = 40)
                }
                cells[0, 3] = view {
                    layoutParams(width = LayoutParams.MATCH_PARENT, height = 40) {
                        horizontalWeight = 1f
                    }
                }
                cells[1, 0] = view {
                    layoutParams(width = 40, height = 40)
                }
                cells[1, 1] = view {
                    layoutParams(width = LayoutParams.MATCH_PARENT, height = 40) {
                        horizontalWeight = 1f
                    }
                }
                cells[1, 2] = view {
                    layoutParams(width = 40, height = 40)
                }
                cells[1, 3] = view {
                    layoutParams(width = LayoutParams.MATCH_PARENT, height = 40) {
                        horizontalWeight = 3f
                    }
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.EXACTLY, 240), measureSpec(MeasureSpec.AT_MOST, 120))

        assertEquals(40, cells[0, 1].measuredWidth)
        assertEquals(40, cells[1, 1].measuredWidth)
        assertEquals(120, cells[0, 3].measuredWidth)
        assertEquals(120, cells[1, 3].measuredWidth)
    }

    @Test
    fun `max row weight applied`() {
        val grid = with(context) {
            gridContainer {
                columnCount = 2

                cells[0, 0] = view {
                    layoutParams(width = 80, height = 40)
                }
                cells[0, 1] = view {
                    layoutParams(width = 80, height = 40)
                }
                cells[1, 0] = view {
                    layoutParams(width = 80, height = LayoutParams.MATCH_PARENT) {
                        verticalWeight = 1f
                    }
                }
                cells[1, 1] = view {
                    layoutParams(width = 80, height = LayoutParams.MATCH_PARENT) {
                        verticalWeight = 1f
                    }
                }
                cells[2, 0] = view {
                    layoutParams(width = 80, height = 40)
                }
                cells[2, 1] = view {
                    layoutParams(width = 80, height = 40)
                }
                cells[3, 0] = view {
                    layoutParams(width = 80, height = LayoutParams.MATCH_PARENT) {
                        verticalWeight = 1f
                    }
                }
                cells[3, 1] = view {
                    layoutParams(width = 80, height = LayoutParams.MATCH_PARENT) {
                        verticalWeight = 3f
                    }
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.AT_MOST, 120), measureSpec(MeasureSpec.EXACTLY, 240))

        assertEquals(40, cells[1, 0].measuredHeight)
        assertEquals(40, cells[1, 1].measuredHeight)
        assertEquals(120, cells[3, 0].measuredHeight)
        assertEquals(120, cells[3, 1].measuredHeight)
    }

    @Test
    fun `cell arranged in column`() {
        val grid = with(context) {
            gridContainer {
                cells[0, 0] = view {
                    layoutParams(width = 80, height = 40)
                }
                cells[1, 0] = view {
                    layoutParams(width = 80, height = 40)
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.AT_MOST, 240), measureSpec(MeasureSpec.AT_MOST, 120))
        grid.layout(0, grid.measuredWidth, 0, grid.measuredHeight)

        assertEquals(0, cells[1, 0].left)
        assertEquals(40, cells[1, 0].top)
    }

    @Test
    fun `cell arranged in row`() {
        val grid = with(context) {
            gridContainer {
                columnCount = 2

                cells[0, 0] = view {
                    layoutParams(width = 80, height = 40)
                }
                cells[0, 1] = view {
                    layoutParams(width = 80, height = 40)
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.AT_MOST, 240), measureSpec(MeasureSpec.AT_MOST, 120))
        grid.layout(0, grid.measuredWidth, 0, grid.measuredHeight)

        assertEquals(80, cells[0, 1].left)
        assertEquals(0, cells[0, 1].top)
    }

    @Test
    fun `fixed column placed out of grid bounds`() {
        val grid = with(context) {
            gridContainer {
                columnCount = 3

                cells[0, 0] = view {
                    layoutParams(width = 80, height = 40)
                }
                cells[0, 1] = view {
                    layoutParams(width = 80, height = 40)
                }
                cells[0, 2] = view {
                    layoutParams(width = 80, height = 40)
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.EXACTLY, 120), measureSpec(MeasureSpec.AT_MOST, 120))
        grid.layout(0, grid.measuredWidth, 0, grid.measuredHeight)

        assertEquals(160, cells[0, 2].left)
        assertEquals(0, cells[0, 2].top)
    }

    @Test
    fun `fixed row placed out of grid bounds`() {
        val grid = with(context) {
            gridContainer {
                cells[0, 0] = view {
                    layoutParams(width = 80, height = 40)
                }
                cells[1, 0] = view {
                    layoutParams(width = 80, height = 40)
                }
                cells[2, 0] = view {
                    layoutParams(width = 80, height = 40)
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.AT_MOST, 240), measureSpec(MeasureSpec.EXACTLY, 60))
        grid.layout(0, grid.measuredWidth, 0, grid.measuredHeight)

        assertEquals(0, cells[2, 0].left)
        assertEquals(80, cells[2, 0].top)
    }

    @Test
    fun `weighted column placed out of grid bounds`() {
        val grid = with(context) {
            gridContainer {
                columnCount = 3

                cells[0, 0] = view {
                    layoutParams(width = LayoutParams.MATCH_PARENT, height = 40) {
                        horizontalWeight = 1f
                    }
                }
                cells[0, 1] = view {
                    layoutParams(width = 80, height = 40) {
                        horizontalWeight = 1f
                    }
                }
                cells[0, 2] = view {
                    layoutParams(width = LayoutParams.MATCH_PARENT, height = 40) {
                        horizontalWeight = 1f
                    }
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.EXACTLY, 120), measureSpec(MeasureSpec.AT_MOST, 120))
        grid.layout(0, grid.measuredWidth, 0, grid.measuredHeight)

        assertEquals(160, cells[0, 2].left)
        assertEquals(0, cells[0, 2].top)
    }

    @Test
    fun `weighted row placed out of grid bounds`() {
        val grid = with(context) {
            gridContainer {
                cells[0, 0] = view {
                    layoutParams(width = 80, height = LayoutParams.MATCH_PARENT) {
                        verticalWeight = 1f
                    }
                }
                cells[1, 0] = view {
                    layoutParams(width = 80, height = 40) {
                        verticalWeight = 1f
                    }
                }
                cells[2, 0] = view {
                    layoutParams(width = 80, height = LayoutParams.MATCH_PARENT) {
                        verticalWeight = 1f
                    }
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.AT_MOST, 240), measureSpec(MeasureSpec.EXACTLY, 60))
        grid.layout(0, grid.measuredWidth, 0, grid.measuredHeight)

        assertEquals(0, cells[2, 0].left)
        assertEquals(80, cells[2, 0].top)
    }

    @Test
    fun `spanned cell fills two columns`() {
        val grid = with(context) {
            gridContainer {
                columnCount = 3

                cells[0, 0] = view {
                    layoutParams(width = 80, height = 40)
                }
                cells[0, 1] = view {
                    layoutParams(width = 80, height = 40)
                }
                cells[0, 2] = view {
                    layoutParams(width = 80, height = 40)
                }
                cells[1, 0] = view {
                    layoutParams(width = 80, height = 40) {
                        columnSpan = 2
                    }
                }
                cells[1, 1] = view {
                    layoutParams(width = 80, height = 40)
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.AT_MOST, 240), measureSpec(MeasureSpec.AT_MOST, 120))
        grid.layout(0, grid.measuredWidth, 0, grid.measuredHeight)

        assertEquals(160, cells[1, 1].left)
        assertEquals(40, cells[1, 1].top)
    }

    @Test
    fun `spanned cell fills two rows`() {
        val grid = with(context) {
            gridContainer {
                columnCount = 2

                cells[0, 0] = view {
                    layoutParams(width = 80, height = 40)
                }
                cells[0, 1] = view {
                    layoutParams(width = 80, height = 40)
                }
                cells[1, 0] = view {
                    layoutParams(width = 80, height = 40) {
                        rowSpan = 2
                    }
                }
                cells[1, 1] = view {
                    layoutParams(width = 80, height = 40)
                }
                cells[2, 0] = view {
                    layoutParams(width = 80, height = 40)
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.AT_MOST, 240), measureSpec(MeasureSpec.AT_MOST, 120))
        grid.layout(0, grid.measuredWidth, 0, grid.measuredHeight)

        assertEquals(80, cells[2, 0].left)
        assertEquals(80, cells[2, 0].top)
    }

    @Test
    fun `cell left gravity applied`() {
        val grid = with(context) {
            gridContainer {
                cells[0, 0] = view {
                    layoutParams(width = 80, height = 40) {
                        gravity = Gravity.LEFT
                    }
                }
                cells[1, 0] = view {
                    layoutParams(width = 160, height = 40)
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.AT_MOST, 240), measureSpec(MeasureSpec.AT_MOST, 120))
        grid.layout(0, grid.measuredWidth, 0, grid.measuredHeight)

        assertEquals(0, cells[0, 0].left)
        assertEquals(0, cells[0, 0].top)
    }

    @Test
    fun `cell right gravity applied`() {
        val grid = with(context) {
            gridContainer {
                cells[0, 0] = view {
                    layoutParams(width = 80, height = 40) {
                        gravity = Gravity.RIGHT
                    }
                }
                cells[1, 0] = view {
                    layoutParams(width = 160, height = 40)
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.AT_MOST, 240), measureSpec(MeasureSpec.AT_MOST, 120))
        grid.layout(0, grid.measuredWidth, 0, grid.measuredHeight)

        assertEquals(80, cells[0, 0].left)
        assertEquals(0, cells[0, 0].top)
    }

    @Test
    fun `cell center horizontal gravity applied`() {
        val grid = with(context) {
            gridContainer {
                cells[0, 0] = view {
                    layoutParams(width = 80, height = 40) {
                        gravity = Gravity.CENTER_HORIZONTAL
                    }
                }
                cells[1, 0] = view {
                    layoutParams(width = 160, height = 40)
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.AT_MOST, 240), measureSpec(MeasureSpec.AT_MOST, 120))
        grid.layout(0, grid.measuredWidth, 0, grid.measuredHeight)

        assertEquals(40, cells[0, 0].left)
        assertEquals(0, cells[0, 0].top)
    }

    @Test
    fun `cell top gravity applied`() {
        val grid = with(context) {
            gridContainer {
                columnCount = 2

                cells[0, 0] = view {
                    layoutParams(width = 80, height = 40) {
                        gravity = Gravity.TOP
                    }
                }
                cells[0, 1] = view {
                    layoutParams(width = 80, height = 80)
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.AT_MOST, 240), measureSpec(MeasureSpec.AT_MOST, 120))
        grid.layout(0, grid.measuredWidth, 0, grid.measuredHeight)

        assertEquals(0, cells[0, 0].left)
        assertEquals(0, cells[0, 0].top)
    }

    @Test
    fun `cell bottom gravity applied`() {
        val grid = with(context) {
            gridContainer {
                columnCount = 2

                cells[0, 0] = view {
                    layoutParams(width = 80, height = 40) {
                        gravity = Gravity.BOTTOM
                    }
                }
                cells[0, 1] = view {
                    layoutParams(width = 80, height = 80)
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.AT_MOST, 240), measureSpec(MeasureSpec.AT_MOST, 120))
        grid.layout(0, grid.measuredWidth, 0, grid.measuredHeight)

        assertEquals(0, cells[0, 0].left)
        assertEquals(40, cells[0, 0].top)
    }

    @Test
    fun `cell center vertical gravity applied`() {
        val grid = with(context) {
            gridContainer {
                columnCount = 2

                cells[0, 0] = view {
                    layoutParams(width = 80, height = 40) {
                        gravity = Gravity.CENTER_VERTICAL
                    }
                }
                cells[0, 1] = view {
                    layoutParams(width = 80, height = 80)
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.AT_MOST, 240), measureSpec(MeasureSpec.AT_MOST, 120))
        grid.layout(0, grid.measuredWidth, 0, grid.measuredHeight)

        assertEquals(0, cells[0, 0].left)
        assertEquals(20, cells[0, 0].top)
    }

    @Test
    fun `grid left gravity applied`() {
        val grid = with(context) {
            gridContainer {
                columnCount = 2
                gravity = Gravity.LEFT

                cells[0, 0] = view {
                    layoutParams(width = 80, height = 40)
                }
                cells[0, 1] = view {
                    layoutParams(width = 80, height = 40)
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.EXACTLY, 240), measureSpec(MeasureSpec.EXACTLY, 120))
        grid.layout(0, grid.measuredWidth, 0, grid.measuredHeight)

        assertEquals(0, cells[0, 0].left)
        assertEquals(0, cells[0, 0].top)
        assertEquals(80, cells[0, 1].left)
        assertEquals(0, cells[0, 1].top)
    }

    @Test
    fun `grid right gravity applied`() {
        val grid = with(context) {
            gridContainer {
                columnCount = 2
                gravity = Gravity.RIGHT

                cells[0, 0] = view {
                    layoutParams(width = 80, height = 40)
                }
                cells[0, 1] = view {
                    layoutParams(width = 80, height = 40)
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.EXACTLY, 240), measureSpec(MeasureSpec.EXACTLY, 120))
        grid.layout(0, grid.measuredWidth, 0, grid.measuredHeight)

        assertEquals(80, cells[0, 0].left)
        assertEquals(0, cells[0, 0].top)
        assertEquals(160, cells[0, 1].left)
        assertEquals(0, cells[0, 1].top)
    }

    @Test
    fun `grid center horizontal gravity applied`() {
        val grid = with(context) {
            gridContainer {
                columnCount = 2
                gravity = Gravity.CENTER_HORIZONTAL

                cells[0, 0] = view {
                    layoutParams(width = 80, height = 40)
                }
                cells[0, 1] = view {
                    layoutParams(width = 80, height = 40)
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.EXACTLY, 240), measureSpec(MeasureSpec.EXACTLY, 120))
        grid.layout(0, grid.measuredWidth, 0, grid.measuredHeight)

        assertEquals(40, cells[0, 0].left)
        assertEquals(0, cells[0, 0].top)
        assertEquals(120, cells[0, 1].left)
        assertEquals(0, cells[0, 1].top)
    }

    @Test
    fun `grid top gravity applied`() {
        val grid = with(context) {
            gridContainer {
                gravity = Gravity.TOP

                cells[0, 0] = view {
                    layoutParams(width = 80, height = 40)
                }
                cells[1, 0] = view {
                    layoutParams(width = 80, height = 40)
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.EXACTLY, 240), measureSpec(MeasureSpec.EXACTLY, 120))
        grid.layout(0, grid.measuredWidth, 0, grid.measuredHeight)

        assertEquals(0, cells[0, 0].left)
        assertEquals(0, cells[0, 0].top)
        assertEquals(0, cells[1, 0].left)
        assertEquals(40, cells[1, 0].top)
    }

    @Test
    fun `grid bottom gravity applied`() {
        val grid = with(context) {
            gridContainer {
                gravity = Gravity.BOTTOM

                cells[0, 0] = view {
                    layoutParams(width = 80, height = 40)
                }
                cells[1, 0] = view {
                    layoutParams(width = 80, height = 40)
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.EXACTLY, 240), measureSpec(MeasureSpec.EXACTLY, 120))
        grid.layout(0, grid.measuredWidth, 0, grid.measuredHeight)

        assertEquals(0, cells[0, 0].left)
        assertEquals(40, cells[0, 0].top)
        assertEquals(0, cells[1, 0].left)
        assertEquals(80, cells[1, 0].top)
    }

    @Test
    fun `grid center vertical gravity applied`() {
        val grid = with(context) {
            gridContainer {
                gravity = Gravity.CENTER_VERTICAL

                cells[0, 0] = view {
                    layoutParams(width = 80, height = 40)
                }
                cells[1, 0] = view {
                    layoutParams(width = 80, height = 40)
                }
            }
        }

        grid.measure(measureSpec(MeasureSpec.EXACTLY, 240), measureSpec(MeasureSpec.EXACTLY, 120))
        grid.layout(0, grid.measuredWidth, 0, grid.measuredHeight)

        assertEquals(0, cells[0, 0].left)
        assertEquals(20, cells[0, 0].top)
        assertEquals(0, cells[1, 0].left)
        assertEquals(60, cells[1, 0].top)
    }

    private fun cells(): MutableMap<Pair<Int, Int>, View> {
        return mutableMapOf()
    }

    private operator fun MutableMap<Pair<Int, Int>, View>.get(row: Int, column: Int): View {
        return get(Pair(row, column)) ?: throw IndexOutOfBoundsException()
    }

    private operator fun MutableMap<Pair<Int, Int>, View>.set(row: Int, column: Int, value: View) {
        put(Pair(row, column), value)
    }

    private fun measureSpec(mode: Int, size: Int = 0) = MeasureSpec.makeMeasureSpec(size, mode)
}
