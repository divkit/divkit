package com.yandex.divkit.demo.div.urlopen

import android.annotation.SuppressLint
import android.content.Context
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import kotlinx.coroutines.CoroutineScope
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.button
import org.jetbrains.anko.dip
import org.jetbrains.anko.editText
import org.jetbrains.anko.frameLayout
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.padding
import org.jetbrains.anko.progressBar
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.singleLine
import org.jetbrains.anko.space
import org.jetbrains.anko.verticalLayout

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
                    button("paste.yandex-team.ru") {
                        onClick(coroutineContext) { input.setPasteUrl() }
                    }

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

    @SuppressLint("SetTextI18n")
    private fun EditText.setPasteUrl() {
        setText("https://paste.yandex-team.ru/???/text")
        setSelection(29, 32)
        requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
}
