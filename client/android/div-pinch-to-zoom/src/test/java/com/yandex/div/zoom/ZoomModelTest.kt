package com.yandex.div.zoom

import android.graphics.Matrix
import android.graphics.PointF
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ZoomModelTest {

    private val model = ZoomModel()

    @Test
    fun `translate does not applied when there is no scale`() {
        model.prepare(
            location = PointF(0.0f, 0.0f),
            pivotPoint = PointF(0.0f, 0.0f)
        )

        model.translateBy(10.0f, 10.0f)

        val matrix = model.transformMatrix().toArray()
        assertEquals(0.0f, matrix[Matrix.MTRANS_X])
        assertEquals(0.0f, matrix[Matrix.MTRANS_Y])
    }

    @Test
    fun `translate affected by low scale`() {
        model.prepare(
            location = PointF(0.0f, 0.0f),
            pivotPoint = PointF(0.0f, 0.0f)
        )

        model.scaleBy(1.5f)
        model.translateBy(10.0f, 10.0f)

        val matrix = model.transformMatrix().toArray()
        assertEquals(5.0f, matrix[Matrix.MTRANS_X])
        assertEquals(5.0f, matrix[Matrix.MTRANS_Y])
    }

    @Test
    fun `translate does not applied at 0 progress`() {
        model.prepare(
            location = PointF(0.0f, 0.0f),
            pivotPoint = PointF(0.0f, 0.0f)
        )

        model.scaleBy(2.0f)
        model.translateBy(10.0f, 10.0f)

        val matrix = model.transformMatrix(progress = 0.0f).toArray()
        assertEquals(0.0f, matrix[Matrix.MTRANS_X])
        assertEquals(0.0f, matrix[Matrix.MTRANS_Y])
    }

    @Test
    fun `translate affected by low scale and partial progress`() {
        model.prepare(
            location = PointF(0.0f, 0.0f),
            pivotPoint = PointF(0.0f, 0.0f)
        )

        model.scaleBy(2.0f)
        model.translateBy(10.0f, 10.0f)

        val matrix = model.transformMatrix(progress = 0.5f).toArray()
        assertEquals(2.5f, matrix[Matrix.MTRANS_X])
        assertEquals(2.5f, matrix[Matrix.MTRANS_Y])
    }

    @Test
    fun `translate affected by partial progress only at large scale`() {
        model.prepare(
            location = PointF(0.0f, 0.0f),
            pivotPoint = PointF(0.0f, 0.0f)
        )

        model.scaleBy(4.0f)
        model.translateBy(10.0f, 10.0f)

        val matrix = model.transformMatrix(progress = 0.5f).toArray()
        assertEquals(5.0f, matrix[Matrix.MTRANS_X])
        assertEquals(5.0f, matrix[Matrix.MTRANS_Y])
    }

    @Test
    fun `translate applied at scale 2x`() {
        model.prepare(
            location = PointF(0.0f, 0.0f),
            pivotPoint = PointF(0.0f, 0.0f)
        )

        model.scaleBy(2.0f)
        model.translateBy(10.0f, 10.0f)

        val matrix = model.transformMatrix().toArray()
        assertEquals(10.0f, matrix[Matrix.MTRANS_X])
        assertEquals(10.0f, matrix[Matrix.MTRANS_Y])
    }

    @Test
    fun `translate applied consistently`() {
        model.prepare(
            location = PointF(0.0f, 0.0f),
            pivotPoint = PointF(0.0f, 0.0f)
        )

        model.scaleBy(2.0f)
        model.translateBy(10.0f, 10.0f)
        model.translateBy(10.0f, 10.0f)

        val matrix = model.transformMatrix().toArray()
        assertEquals(20.0f, matrix[Matrix.MTRANS_X])
        assertEquals(20.0f, matrix[Matrix.MTRANS_Y])
    }

    @Test
    fun `scale does not applied at 0 progress`() {
        model.prepare(
            location = PointF(0.0f, 0.0f),
            pivotPoint = PointF(20.0f, 20.0f)
        )

        model.scaleBy(2.0f)

        val matrix = model.transformMatrix(progress = 0.0f).toArray()
        assertEquals(1.0f, matrix[Matrix.MSCALE_X])
        assertEquals(1.0f, matrix[Matrix.MSCALE_Y])
    }

    @Test
    fun `scale affected by partial progress`() {
        model.prepare(
            location = PointF(0.0f, 0.0f),
            pivotPoint = PointF(20.0f, 20.0f)
        )

        model.scaleBy(2.0f)

        val matrix = model.transformMatrix(progress = 0.5f).toArray()
        assertEquals(1.5f, matrix[Matrix.MSCALE_X])
        assertEquals(1.5f, matrix[Matrix.MSCALE_Y])
    }

    @Test
    fun `scale applied`() {
        model.prepare(
            location = PointF(0.0f, 0.0f),
            pivotPoint = PointF(20.0f, 20.0f)
        )

        model.scaleBy(2.0f)

        val matrix = model.transformMatrix().toArray()
        assertEquals(2.0f, matrix[Matrix.MSCALE_X])
        assertEquals(2.0f, matrix[Matrix.MSCALE_Y])
    }

    @Test
    fun `scale clamped to min 1`() {
        model.prepare(
            location = PointF(0.0f, 0.0f),
            pivotPoint = PointF(20.0f, 20.0f)
        )

        model.scaleBy(0.5f)

        val matrix = model.transformMatrix().toArray()
        assertEquals(1.0f, matrix[Matrix.MSCALE_X])
        assertEquals(1.0f, matrix[Matrix.MSCALE_Y])
    }

    @Test
    fun `scale clamped to min 4`() {
        model.prepare(
            location = PointF(0.0f, 0.0f),
            pivotPoint = PointF(20.0f, 20.0f)
        )

        model.scaleBy(6.0f)

        val matrix = model.transformMatrix().toArray()
        assertEquals(4.0f, matrix[Matrix.MSCALE_X])
        assertEquals(4.0f, matrix[Matrix.MSCALE_Y])
    }

    @Test
    fun `scale applied consistently`() {
        model.prepare(
            location = PointF(0.0f, 0.0f),
            pivotPoint = PointF(20.0f, 20.0f)
        )

        model.scaleBy(2.0f)
        model.scaleBy(1.5f)

        val matrix = model.transformMatrix().toArray()
        assertEquals(3.0f, matrix[Matrix.MSCALE_X])
        assertEquals(3.0f, matrix[Matrix.MSCALE_Y])
    }

    @Test
    fun `prepare() clears translate and scale`() {
        model.prepare(
            location = PointF(0.0f, 0.0f),
            pivotPoint = PointF(0.0f, 0.0f)
        )

        model.scale(2.0f)
        model.translateBy(10.0f, 10.0f)

        model.prepare(
            location = PointF(0.0f, 0.0f),
            pivotPoint = PointF(0.0f, 0.0f)
        )

        val matrix = model.transformMatrix().toArray()
        assertEquals(0.0f, matrix[Matrix.MTRANS_X])
        assertEquals(0.0f, matrix[Matrix.MTRANS_Y])
        assertEquals(1.0f, matrix[Matrix.MSCALE_X])
        assertEquals(1.0f, matrix[Matrix.MSCALE_Y])
    }

    private fun Matrix.toArray(): FloatArray {
        return FloatArray(9).apply {
            getValues(this)
        }
    }
}
