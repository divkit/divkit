package com.yandex.div.core.view2.logging.patch

import com.yandex.div.core.view2.logging.bind.SimpleRebindReporter

internal interface PatchEventReporter: SimpleRebindReporter {
    fun onPatchSuccess()

    fun onPatchNoState()

    companion object {
        val STUB = object : PatchEventReporter {
            override fun onPatchSuccess() = Unit

            override fun onPatchNoState() = Unit
        }
    }
}
