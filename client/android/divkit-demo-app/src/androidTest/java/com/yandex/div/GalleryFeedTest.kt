package com.yandex.div

import androidx.test.rule.ActivityTestRule
import com.yandex.div.rule.uiTestRule
import com.yandex.div.steps.galleryFeed
import com.yandex.divkit.demo.div.DemoDiv2Logger.Companion.withCaptureVisibilityActions
import com.yandex.divkit.demo.div.GalleryFeedTestActivity
import org.junit.Rule
import org.junit.Test

class GalleryFeedTest {

    private val activityTestRule = ActivityTestRule(GalleryFeedTestActivity::class.java)

    @get:Rule
    val rule = uiTestRule { activityTestRule }

    @Test
    fun dispatchItemsVisibilityActions_whenFeedScrolled() {
        galleryFeed {
            val capturedVisibilityActions = withCaptureVisibilityActions { scrollPerItem() }
            assert {
                checkTrackedVisibilityActionsForContent(visibilityActions = capturedVisibilityActions)
            }
        }
    }
}
