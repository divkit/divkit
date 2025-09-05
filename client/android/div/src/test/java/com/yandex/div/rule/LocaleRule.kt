package com.yandex.div.rule

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.util.Locale

class LocaleRule(
    private val locale: Locale = Locale("en", "US")
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

    private fun setLocale(locale: Locale): Locale {
        val oldLocale = Locale.getDefault()
        Locale.setDefault(locale);
        return oldLocale
    }

    private fun restoreLocale(locale: Locale) {
        Locale.setDefault(locale);
    }
}
