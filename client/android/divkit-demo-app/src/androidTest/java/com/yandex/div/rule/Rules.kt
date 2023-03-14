@file:JvmName("AndroidTestRules")

package com.yandex.div.rule

import com.yandex.test.rules.ClosePopupsRule
import com.yandex.test.rules.FailshotRule
import com.yandex.test.rules.LogcatReportRule
import com.yandex.test.rules.NoAnimationsRule
import com.yandex.test.rules.WindowHierarchyRule
import com.yandex.test.util.chain
import org.junit.rules.TestRule

fun uiTestRule(innerRule: () -> TestRule): TestRule {
    return LogcatReportRule()
        .chain(NoAnimationsRule())
        .chain(ClosePopupsRule())
        .chain(innerRule())
        .chain(WindowHierarchyRule())
        .chain(FailshotRule())
}
