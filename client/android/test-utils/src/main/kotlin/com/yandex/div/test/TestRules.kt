package com.yandex.div.test

import org.junit.rules.RuleChain
import org.junit.rules.TestRule

fun TestRule.chain(rule: TestRule): TestRule =
    (this as? RuleChain)?.around(rule) ?: RuleChain.outerRule(this).around(rule)
