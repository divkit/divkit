package com.yandex.div.lint

import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity

object OnPreDrawListenerIssue {

    const val ID = "OnPreDrawListenerIssue"

    private const val DESCRIPTION = "ViewTreeObserver.OnPreDrawListener is potentially unsafe"
    private const val EXPLANATION = """Return of `false` from `ViewTreeObserver.OnPreDrawListener.onPreDraw()` may break the drawing of the entire screen.
Replace it with `com.yandex.div.core.view.OverridableOnPreDrawListener` or `com.yandex.div.core.view.OnPreDrawListeners.onPreDrawListener(...)` function."""

    private val issue = Issue.create(
        id = ID,
        briefDescription = DESCRIPTION,
        explanation = EXPLANATION,
        category = Category.CORRECTNESS,
        priority = 8,
        severity = Severity.ERROR,
        implementation = Implementation(
            OnPreDrawListenerDetector::class.java,
            Scope.JAVA_FILE_SCOPE
        )
    )

    fun get(): Issue = issue

    fun fullDescription(): String = "$DESCRIPTION\n$EXPLANATION"
}
