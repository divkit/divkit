package com.yandex.div.detekt

import dev.detekt.api.RuleName
import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.RuleSetProvider

class DivKitRuleSetProvider : RuleSetProvider {
    override val ruleSetId = RuleSetId("divkit")

    override fun instance() = RuleSet(ruleSetId, mapOf(RuleName("FileLength") to ::FileLength))
}
