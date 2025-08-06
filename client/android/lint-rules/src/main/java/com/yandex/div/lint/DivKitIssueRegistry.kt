package com.yandex.div.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API

class DivKitIssueRegistry : IssueRegistry() {

    override val api = CURRENT_API

    override val minApi = 8

    override val issues = listOf(OnPreDrawListenerIssue.get())

    override val vendor = Vendor(
        feedbackUrl = "https://divkit.tech/",
        identifier = "com.yandex.div",
        vendorName = "DivKit Open Source Project"
    )
}
