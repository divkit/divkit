package com.yandex.test.util

import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

fun Statement.chain(desc: Description, rule: TestRule): Statement = rule.apply(this, desc)

fun TestRule.chain(rule: TestRule): TestRule =
    (this as? RuleChain)?.around(rule) ?: RuleChain.outerRule(this).around(rule)
