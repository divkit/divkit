package com.yandex.test.screenshot.tasks

import kotlin.test.Test
import kotlin.test.assertEquals

class CompareScreenshotsTaskTest {

    @Test
    fun `partial selected case output remains required`() {
        val selectedPrefix =
            "com.yandex.div.DivComposeInteractiveScreenshotTest/div-input/fixed_length_input_mask"
        val skipped = (1..10).map { step ->
            "viewPixelCopy/$selectedPrefix/step$step.png"
        } + "viewPixelCopy/com.yandex.div.OtherTest/unrelated.png"

        assertEquals(
            skipped.dropLast(1),
            requiredSkippedReferences(skipped, selectedPrefix),
        )
    }

    @Test
    fun `single screenshot case uses exact png path`() {
        val selectedPrefix = "com.yandex.div.Div2ScreenshotTest/div-text/solid-background"
        val skipped = listOf(
            "viewPixelCopy/$selectedPrefix.png",
            "viewPixelCopy/$selectedPrefix-extra.png",
        )

        assertEquals(
            listOf(skipped.first()),
            requiredSkippedReferences(skipped, selectedPrefix),
        )
    }

    @Test
    fun `regular comparison keeps all skipped references required`() {
        val skipped = listOf("viewPixelCopy/com.yandex.div.SomeTest/example.png")

        assertEquals(skipped, requiredSkippedReferences(skipped, ""))
    }
}
