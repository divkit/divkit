package com.yandex.div

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.rule.ActivityTestRule
import com.yandex.div.rule.uiTestRule
import com.yandex.div.steps.DivViewInteractions.viewWithTag
import com.yandex.div.steps.DivViewInteractions.viewWithTagAndText
import com.yandex.div.steps.divView
import com.yandex.div.view.isDisplayed
import com.yandex.divkit.demo.DummyActivity
import com.yandex.divkit.demo.div.DemoDiv2Logger
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DivActionTest {

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
    fun scopedActionChangesLocalVariable() {
        divView {
            testAsset = "ui_test_data/actions/scoped_actions.json"
            activityRule.buildContainer(MATCH_PARENT, WRAP_CONTENT)

            tapOnText("Scoped")

            assert {
                viewWithTag(tag = "title").checkHasText(text = "Title has been changed")
            }
        }
    }

    @Test
    fun notScopedActionDoesNotChangeLocalVariable() {
        divView {
            testAsset = "ui_test_data/actions/scoped_actions.json"
            activityRule.buildContainer(MATCH_PARENT, WRAP_CONTENT)

            tapOnText("Not scoped")

            assert {
                viewWithTag(tag = "title").checkHasText(text = "Lorem ipsum")
            }
        }
    }

    @Test
    fun scopedActionChangesLocalVariable_fromNestedScope() {
        divView {
            testAsset = "ui_test_data/actions/scoped_actions_nested_scope.json"
            activityRule.buildContainer(MATCH_PARENT, WRAP_CONTENT)

            tapOnText("Content scope")

            assert {
                viewWithTag(tag = "title").checkHasText(text = "Title has been changed from 'content' scope")
            }
        }
    }

    @Test
    fun scopedActionDoesNotChangeLocalVariable_whenScopeIsAmbiguous() {
        divView {
            testAsset = "ui_test_data/actions/scoped_actions_ambiguous_scope.json"
            activityRule.buildContainer(MATCH_PARENT, WRAP_CONTENT)

            tapOnText("Ambiguous scope")

            assert {
                viewWithTagAndText(tag = "some_text", text = "Lorem ipsum").isDisplayed()
            }
        }
    }
}
