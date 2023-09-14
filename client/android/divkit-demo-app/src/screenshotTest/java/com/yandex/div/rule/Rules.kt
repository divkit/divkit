@file:JvmName("ScreenshotTestRules")

package com.yandex.div.rule

import com.yandex.div.picasso.PicassoDivImageLoader
import com.yandex.divkit.demo.Container
import com.yandex.test.idling.waitForIdlingResource
import com.yandex.test.rules.ClosePopupsRule
import com.yandex.test.rules.FailshotRule
import com.yandex.test.rules.LogcatReportRule
import com.yandex.test.rules.NoAnimationsRule
import com.yandex.test.rules.WindowHierarchyRule
import com.yandex.test.screenshot.ScreenshotRule
import com.yandex.test.util.chain
import org.junit.rules.TestRule

fun screenshotRule(
    relativePath: String = "",
    name: String = "",
    casePath: String,
    skipScreenshotCapture: Boolean = false,
    innerRule: () -> TestRule,
): TestRule {
    return LogcatReportRule(reportOnSuccess = true)
        .chain(CheckCaseRule(casePath))
        .chain(FailshotRule())
        .chain(NoAnimationsRule())
        .chain(ClosePopupsRule())
        .chain(innerRule())
        .chain(WindowHierarchyRule(reportOnSuccess = true))
        .run {
            if (skipScreenshotCapture) {
                this
            } else {
                chain(ScreenshotRule(relativePath, name, casePath).apply {
                    beforeScreenshotTaken {
                        try {
                            waitForIdlingResource(ImageLoadingIdlingResource(Container.imageLoader as PicassoDivImageLoader))
                        } catch (e: Exception) {
                            (Container.imageLoader as PicassoDivImageLoader).resetIdle()
                            throw e
                        }
                    }
                })
            }
        }
}
