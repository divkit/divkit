package com.yandex.div

import androidx.test.rule.ActivityTestRule
import com.yandex.div.rule.uiTestRule
import com.yandex.div.steps.gallery
import com.yandex.div.steps.logCount
import com.yandex.div.steps.pager
import com.yandex.div.steps.visibilityActions
import com.yandex.divkit.demo.DummyActivity
import org.junit.Rule
import org.junit.Test

class DivCollectionAdapterTest {

    private val activityRule = ActivityTestRule(DummyActivity::class.java)

    @get:Rule
    val rule = uiTestRule { activityRule }

    @Test
    fun galleryReusesItems() {
        logCount(GALLERY_TAG, MESSAGE_REUSE_FAILED) {
            gallery {
                testAsset = "div2-test/gallery/gallery-reuse.json"
                activityRule.buildContainer()

                visibilityActions {
                    assert {
                        checkViewShownLogged(CARD_ID, MESSAGE_TEST_DONE)
                    }
                }
            }

            assert {
                logCountLessOrEquals(4)
            }
        }
    }

    @Test
    fun galleryReusesItemsWithReuseId() {
        logCount(GALLERY_TAG, MESSAGE_REUSE_FAILED) {
            gallery {
                testAsset = "div2-test/gallery/gallery-reuse-with-reuseid.json"
                activityRule.buildContainer()

                visibilityActions {
                    assert {
                        checkViewShownLogged(CARD_ID, MESSAGE_TEST_DONE)
                    }
                }
            }

            assert {
                logCountEquals(0)
            }
        }
    }

    @Test
    fun pagerReusesItems() {
        logCount(PAGER_TAG, MESSAGE_REUSE_FAILED) {
            pager {
                testAsset = "div2-test/pager/pager-reuse.json"
                activityRule.buildContainer()

                visibilityActions {
                    assert {
                        checkViewShownLogged(CARD_ID, MESSAGE_TEST_DONE)
                    }
                }
            }

            assert {
                logCountLessOrEquals(2)
            }
        }
    }

    @Test
    fun pagerReusesItemsWithReuseId() {
        logCount(PAGER_TAG, MESSAGE_REUSE_FAILED) {
            pager {
                testAsset = "div2-test/pager/pager-reuse-with-reuseid.json"
                activityRule.buildContainer()

                visibilityActions {
                    assert {
                        checkViewShownLogged(CARD_ID, MESSAGE_TEST_DONE)
                    }
                }
            }

            assert {
                logCountEquals(0)
            }
        }
    }

    companion object {
        private const val GALLERY_TAG = "DivGalleryViewHolder"
        private const val PAGER_TAG = "DivPagerViewHolder"
        private const val CARD_ID = "div2_sample_card"
        private const val MESSAGE_REUSE_FAILED = "reuse failed"
        private const val MESSAGE_TEST_DONE = "test_done"
    }
}
