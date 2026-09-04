package com.yandex.div.internal.core

import com.yandex.div.internal.core.TextRangeCloudBackground.LineBounds
import kotlin.test.Test
import kotlin.test.assertEquals

class TextRangeCloudBackgroundTest {

    @Test
    fun `single line emits a closed rounded contour`() {
        val path = path(LineBounds(0, 0, 100, 20))

        assertEquals(
            listOf(
                "M 96.0 0.0",
                "Q 3.6 0.4 4.0 4.0",
                "L 0.0 12.0",
                "Q -0.4 3.6 -4.0 4.0",
                "L -92.0 0.0",
                "Q -3.6 -0.4 -4.0 -4.0",
                "L 0.0 -12.0",
                "Q 0.4 -3.6 4.0 -4.0",
                "Z",
            ),
            path,
        )
    }

    @Test
    fun `disconnected lines emit separate contours`() {
        val first = LineBounds(100, 0, 150, 20)
        val second = LineBounds(0, 20, 50, 40)

        assertEquals(path(first) + path(second), path(first, second))
    }

    @Test
    fun `disconnected lines shifted to the right emit separate contours`() {
        val first = LineBounds(0, 0, 50, 20)
        val second = LineBounds(100, 20, 150, 40)

        assertEquals(path(first) + path(second), path(first, second))
    }

    @Test
    fun `close line bounds coalesce to a continuous contour`() {
        val path = path(LineBounds(0, 0, 100, 20), LineBounds(2, 20, 98, 40))

        assertEquals(path(LineBounds(0, 0, 100, 40)), path)
    }

    @Test
    fun `empty ranges emit no contour`() {
        assertEquals(emptyList(), path())
    }

    private fun path(vararg lines: LineBounds): List<String> {
        val sink = RecordingPath()
        TextRangeCloudBackground.buildPath(lines.toList(), cornerRadius = 4, sink = sink)
        return sink.operations
    }

    private class RecordingPath : TextRangeCloudBackground.PathSink {
        val operations = mutableListOf<String>()

        override fun moveTo(x: Float, y: Float) {
            operations += "M $x $y"
        }

        override fun relativeLineTo(dx: Float, dy: Float) {
            operations += "L $dx $dy"
        }

        override fun relativeQuadraticTo(dx1: Float, dy1: Float, dx2: Float, dy2: Float) {
            operations += "Q $dx1 $dy1 $dx2 $dy2"
        }

        override fun close() {
            operations += "Z"
        }
    }
}
