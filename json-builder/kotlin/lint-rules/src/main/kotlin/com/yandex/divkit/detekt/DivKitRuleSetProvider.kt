package com.yandex.divkit.detekt

import com.yandex.divkit.detekt.rule.DeprecatedApiRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

internal const val DIVKIT_RULE_SET_ID = "divkit"

class DivKitRuleSetProvider : RuleSetProvider {

    override val ruleSetId = DIVKIT_RULE_SET_ID

    override fun instance(config: Config): RuleSet = RuleSet(
        ruleSetId,
        listOf(
            DeprecatedApiRule(config),
        )
    )
}
