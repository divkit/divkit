package com.yandex.divkit.demo.div.ui

import android.view.View

data class ButtonDraft(
        val onClickAction: ()->Unit,
        val description: Int,
        val setter: ((View)->Unit)? = null)
