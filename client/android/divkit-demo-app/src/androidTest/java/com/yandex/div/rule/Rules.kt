@file:JvmName("AndroidTestRules")

package com.yandex.div.rule

import com.yandex.div.test.chain
import com.yandex.test.rules.ClosePopupsRule
import com.yandex.test.rules.NoAnimationsRule
import org.junit.rules.TestRule

fun uiTestRule(innerRule: () -> TestRule): TestRule {
    return NoAnimationsRule()
        .chain(ClosePopupsRule())
        .chain(innerRule())
}
