package com.yandex.div.rule

import com.ibm.icu.util.ULocale
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement


class ULocaleRule(
    private val locale: ULocale = ULocale("en", "US")
) : TestRule {

    override fun apply(base: Statement, description: Description?): Statement {
        return object : Statement() {
            override fun evaluate() {
                val localeToRestore = setLocale(locale)
                try {
                    base.evaluate()
                } finally {
                    restoreLocale(localeToRestore)
                }
            }
        }
    }

    private fun setLocale(locale: ULocale): ULocale {
        val oldLocale = ULocale.getDefault()
        ULocale.setDefault(locale);
        return oldLocale
    }

    private fun restoreLocale(locale: ULocale) {
        ULocale.setDefault(locale);
    }
}
