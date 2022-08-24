package com.yandex.divkit.demo.div.urlopen

import android.annotation.SuppressLint
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import kotlinx.coroutines.CoroutineScope
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class UrlOpenActivityUi(val scope: CoroutineScope) : AnkoComponent<UrlOpenActivity> {

    var onUrlSelect: (suspend (String) -> Unit)? = null
    var onCancel: (() -> Unit)? = null

    private val url get() = urlGetter()
    private lateinit var urlGetter: () -> String

    private val coroutineContext = scope.coroutineContext

    @SuppressLint("SetTextI18n")
    override fun createView(ui: AnkoContext<UrlOpenActivity>) =
        ui.frameLayout {

            val spinner = progressBar {
                isIndeterminate = true
                visibility = View.GONE
                padding = dip(150)
            }.lparams {
                width = matchParent
                height = matchParent
            }

            lateinit var mainLayout: ViewGroup
            mainLayout = verticalLayout {
                val input = editText(text = "https://") {
                    inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI
                    singleLine = false
                    maxLines = 5
                    setHorizontallyScrolling(false)
                    urlGetter = { text.toString() }
                }.lparams(
                    width = matchParent,
                    height = dip(250)
                )

                linearLayout {

                    space().lparams(weight = 1f)

                    button("Open") {
                        onClick(coroutineContext) {
                            mainLayout.visibility = View.GONE
                            spinner.visibility = View.VISIBLE
                            onUrlSelect?.invoke(url)
                        }
                    }
                    button("Cancel") {
                        onClick(coroutineContext) { onCancel?.invoke() }
                    }
                }.lparams {
                    width = matchParent
                    height = dip(50)
                }
            }
        }
}
