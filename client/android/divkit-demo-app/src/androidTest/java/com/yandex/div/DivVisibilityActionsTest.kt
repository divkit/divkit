package com.yandex.div

import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.rule.ActivityTestRule
import com.yandex.divkit.demo.DummyActivity
import com.yandex.divkit.demo.div.DemoDiv2Logger
import com.yandex.div.steps.gallery
import com.yandex.div.steps.includedActions
import com.yandex.div.steps.pager
import com.yandex.div.steps.visibilityActions
import com.yandex.div.rule.uiTestRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class VisibilityActionsTest {

    private val activityRule = ActivityTestRule(DummyActivity::class.java)

    @get:Rule
    val rule = uiTestRule { activityRule }

    @Before
    fun clearLogs() {
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
    fun galleryVisibilityActionsLog() {
        gallery {
            testAsset = "scenarios/gallery/gallery_swipe.json"
            activityRule.buildContainer()
        }
        visibilityActions { awaitViewShown() }
        DemoDiv2Logger.clearLogActions()

        gallery { swipeLeft() }
        visibilityActions { awaitViewShown() }
        gallery { swipeLeft() }

        visibilityActions {
            assert {
                checkGalleryScroll("card")
                checkScrollCompleted("card")
            }
        }
    }

    @Test
    fun pagerVisibilityActionsLog() {
        pager {
            testAsset = "scenarios/action_visibility/pager.json"
            activityRule.buildContainer()

            swipeLeft()
        }

        visibilityActions { assert { checkViewShownLogged("gallery", "content_item_show:1") } }
    }

    @Test
    fun pagerSwipeVisibilityActionsLog() {
        pager {
            testAsset = "scenarios/action_visibility/swipe_div_pager.json"
            activityRule.buildContainer()

            swipeLeft()
            swipeLeft()
        }

        visibilityActions { assert { checkViewShownLogged("gallery", "content_item_show:1") } }
        visibilityActions { assert { checkViewShownLogged("gallery", "container") } }
    }

    @Test
    fun pagerSwipeVisibilityActions() {
        pager {
            testAsset = "scenarios/action_visibility/swipe_div_pager.json"
            activityRule.buildContainer()

            swipeLeft()
            swipeLeft()
        }

        visibilityActions { assert { checkViewShownLogged("gallery", "content_item_show:1") } }
    }

    @Test
    fun firstPagePagerVisibilityActions() {
        pager {
            testAsset = "scenarios/action_visibility/pager.json"
            activityRule.buildContainer()
        }

        visibilityActions {
            assert {
                checkViewShownLogged("gallery","content_item_show:0")
                checkViewShownLogged("gallery","goose_shown")
            }
        }
    }

    @Test
    fun defaultItemPagerVisibilityActions() {
        pager {
            testAsset = "scenarios/action_visibility/default_item.json"
            activityRule.buildContainer()
        }
        visibilityActions {
            assert {
                checkViewShownLogged("gallery","content_item_show:4")
            }
        }
    }

    @Test
    fun includedVisibilityActions() {
        includedActions {
            activityRule.buildContainer()

            expand()
            showComments()

            visibilityActions {
                assert {
                    checkViewShownLogged("article_card", "separator")
                    checkViewShownLogged("article_card", "text")
                    checkViewShownLogged("article_card", "text2")
                    checkViewShownLogged("article_card", "comment_state")
                    checkViewShownLogged("article_card", "comment_01")
                }
            }
        }
    }
}
