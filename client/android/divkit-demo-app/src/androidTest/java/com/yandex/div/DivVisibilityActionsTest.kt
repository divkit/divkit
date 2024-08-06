package com.yandex.div

import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.rule.ActivityTestRule
import com.yandex.div.rule.uiTestRule
import com.yandex.div.steps.divView
import com.yandex.div.steps.gallery
import com.yandex.div.steps.includedActions
import com.yandex.div.steps.pager
import com.yandex.div.steps.visibilityActions
import com.yandex.divkit.demo.DummyActivity
import com.yandex.divkit.demo.div.DemoDiv2Logger
import com.yandex.divkit.demo.div.divContext
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
            testAsset = "regression_test_data/gallery/gallery_swipe.json"
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
            testAsset = "regression_test_data/visibility_actions/pager.json"
            activityRule.buildContainer()

            swipeLeft()
        }

        visibilityActions { assert { checkViewShownLogged("gallery", "content_item_show:1") } }
    }

    @Test
    fun pagerSwipeVisibilityActionsLog() {
        pager {
            testAsset = "regression_test_data/visibility_actions/swipe_div_pager.json"
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
            testAsset = "regression_test_data/visibility_actions/swipe_div_pager.json"
            activityRule.buildContainer()

            swipeLeft()
            swipeLeft()
        }

        visibilityActions { assert { checkViewShownLogged("gallery", "content_item_show:1") } }
    }

    @Test
    fun firstPagePagerVisibilityActions() {
        pager {
            testAsset = "regression_test_data/visibility_actions/pager.json"
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
            testAsset = "regression_test_data/visibility_actions/default_item.json"
            activityRule.buildContainer()
        }
        visibilityActions {
            assert {
                checkViewShownLogged("gallery","content_item_show:4")
            }
        }
    }

    @Test
    fun galleryChildOfItemVisibilityActions() {
        gallery {
            testAsset = "regression_test_data/visibility_actions/gallery_child_of_item.json"
            activityRule.buildContainer()
        }

        for (index in 0..4) {
            gallery { scrollTo(index) }
            visibilityActions {
                assert {
                    checkViewShownWithText("Last catched visibility action: item_$index")
                }
            }
        }
    }

    @Test
    fun disabledVisibilityActions() {
        gallery {
            testAsset = "regression_test_data/visibility_actions/is_enabled.json"
            activityRule.buildContainer()
        }

        visibilityActions {
            gallery { scrollTo(4) }
            assert {
                checkViewShownWithText("Last catched visibility action: none")
            }
        }
    }

    @Test
    fun enabledVisibilityActions() {
        gallery {
            testAsset = "regression_test_data/visibility_actions/is_enabled.json"
            activityRule.buildContainer()
        }

        visibilityActions {
            click("Enable actions")
            gallery { scrollTo(4) }
            assert {
                checkViewShownWithText("Last catched visibility action: item_4")
            }
        }
    }

    @Test
    fun visibilityActionsInMultipleViews() {
        val divContext = divContext(activityRule.activity)

        divView {
            testAsset = "ui_test_data/visibility_actions/visibility_action_cycle.json"
            divContext.buildContainer("test_card_tag")

            visibilityActions {
                awaitViewShownLogged("card", "second_state_is_visible")
            }

            cleanUp()
            detachFromParent()
        }

        divView {
            testAsset = "ui_test_data/visibility_actions/visibility_action_cycle.json"
            divContext.buildContainer("test_card_tag")

            visibilityActions {
                assert {
                    checkViewShownLogged("card", "fourth_state_is_visible")
                }
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
