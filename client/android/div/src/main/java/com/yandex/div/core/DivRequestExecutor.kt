package com.yandex.div.core

import android.net.Uri
import com.yandex.div.core.images.LoadReference

interface DivRequestExecutor {

    class Header(val name: String, val value: String)

    class Request(
        val url: Uri,
        val method: String,
        val headers: List<Header>?,
        val body: String,
    )

    interface Callback {
        fun onSuccess()
        fun onFail()
    }

    fun execute(request: Request, callback: Callback?): LoadReference

    companion object {
        @JvmField
        val STUB = object : DivRequestExecutor {
            override fun execute(request: Request, callback: Callback?) = LoadReference { }
        }
    }
}
