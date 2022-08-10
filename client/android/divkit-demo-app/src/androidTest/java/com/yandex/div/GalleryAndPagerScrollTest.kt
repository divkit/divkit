package com.yandex.div

import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.rule.ActivityTestRule
import com.yandex.div.rule.uiTestRule
import com.yandex.div.steps.gallery
import com.yandex.div.steps.pager
import com.yandex.div.steps.visibilityActions
import com.yandex.divkit.demo.DummyActivity
import com.yandex.divkit.demo.div.DemoDiv2Logger
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GalleryAndPagerScrollTest {

    private val activityRule = ActivityTestRule(DummyActivity::class.java)

    @get:Rule
    val rule = uiTestRule { activityRule }

    @Before
    fun clearLogActions() {
        DemoDiv2Logger.clearLogActions()
    }

    @After
    fun unregisterAllResources() {
        val resources: Collection<IdlingResource> = IdlingRegistry.getInstance().resources
        if (!resources.isEmpty()) {
            resources.forEach { IdlingRegistry.getInstance().unregister(it) }
        }
    }

    @Test
    fun galleryScrollTest() {
        gallery {
            testAsset = "scenarios/gallery/gallery_swipe.json"
            activityRule.buildContainer()

            swipeLeft()
        }

        visibilityActions {
            assert {
                checkGalleryScroll("card")
                checkScrollCompleted("card")
            }
        }
    }

    @Test
    fun galleryScrollPagedTest() {
        gallery {
            testAsset = "scenarios/gallery/gallery_swipe_paged.json"
            activityRule.buildContainer()

            swipeLeft()
        }

        visibilityActions {
            assert {
                checkGalleryScroll("card")
                checkScrollCompleted("card", 1, 3)
            }
        }
    }

    @Test
    fun pagerScrollTest() {
        pager {
            testAsset = "scenarios/pager/pager.json"
            activityRule.buildContainer()

            swipeLeft()
            assert { checkOnPage(1) }

            swipeRight()
            assert { checkOnPage(0) }
        }

        visibilityActions {
            assert {
                checkViewShownLogged("gallery", "content_item_show:1")
                checkPagerChangePageLogged("gallery", 1)
            }
        }
    }
}
