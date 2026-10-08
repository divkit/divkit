package com.yandex.div.rule

import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.rules.ExternalResource

class ActivityClassRule(private val activityRule: ActivityScenarioRule<*>) : ExternalResource() {
    override fun after() = activityRule.scenario.close()
}
