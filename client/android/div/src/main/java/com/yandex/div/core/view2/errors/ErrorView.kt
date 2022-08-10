package com.yandex.div.core.view2.errors

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import com.yandex.div.R
import com.yandex.div.core.Disposable
import com.yandex.div.core.util.Assert
import com.yandex.div.util.dpToPx

internal class ErrorView(
    private val root: FrameLayout,
    private val errorModel: ErrorModel,
) : Disposable {
    private var counterView: AppCompatTextView? = null
    private var detailsView: DetailsViewGroup? = null
    private var viewModel: ErrorViewModel? = null
        set(value) {
            updateView(field, value)
            field = value
        }

    private val modelObservation = errorModel.observeAndGet { m: ErrorViewModel -> viewModel = m }

    private fun updateView(old: ErrorViewModel?, new: ErrorViewModel?) {
        if (old == null || new == null || old.showDetails != new.showDetails) {
            counterView?.let { root.removeView(it) }
            counterView = null
            detailsView?.let { root.removeView(it) }
            detailsView = null
        }

        if (new == null) {
            return
        }

        if (new.showDetails) {
            tryAddDetailsView()
            detailsView?.text = new.errorDetails
        } else {
            if (new.errorCount > 0) {
                tryAddCounterView()
            } else {
                counterView?.let { root.removeView(it) }
                counterView = null
            }

            counterView?.text = "${new.errorCount}"
        }
    }

    private fun tryAddDetailsView() {
        if (detailsView != null) {
            return
        }

        val view = DetailsViewGroup(root.context,
            onCloseAction = {
                errorModel.hideDetails()
            }, onCopyAction = {

                viewModel?.let {
                    pasteToClipBoard(errorModel.generateFullReport())
                }
            })

        val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT,
        )
        root.addView(view, layoutParams)
        detailsView = view
    }

    private fun pasteToClipBoard(s: String) {
        val clipboardManager =
            root.context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                ?: run {
                    Assert.fail("Failed to access clipboard manager!")
                    return
                }

        clipboardManager.setPrimaryClip(
            ClipData(
                "Error report",
                arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                ClipData.Item(s)
            )
        )

        Toast.makeText(root.context, "Error details are at your clipboard!", Toast.LENGTH_SHORT)
            .show()
    }

    private fun tryAddCounterView() {
        if (counterView != null) {
            return
        }

        val view = AppCompatTextView(root.context).apply {
            setBackgroundResource(R.drawable.error_counter_background)
            textSize = 12f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            elevation = resources.getDimension(R.dimen.div_shadow_elevation)
            setOnClickListener { errorModel.showDetails() }
        }

        val side = 24.dpToPx()
        val layoutParams = FrameLayout.LayoutParams(side, side, Gravity.TOP + Gravity.LEFT)
        val sideMargin = 8.dpToPx()
        layoutParams.topMargin = sideMargin
        layoutParams.leftMargin = sideMargin
        layoutParams.rightMargin = sideMargin
        layoutParams.bottomMargin = sideMargin
        root.addView(view, layoutParams)
        counterView = view
    }

    override fun close() {
        modelObservation.close()
        root.removeView(counterView)
        root.removeView(detailsView)
    }
}

@SuppressLint("ViewConstructor")
private class DetailsViewGroup(
    context: Context,
    private val onCloseAction: () -> Unit,
    private val onCopyAction: () -> Unit,
) : LinearLayout(context) {
    private val errorsOutput = AppCompatTextView(context).apply {
        setTextColor(Color.WHITE)
        gravity = Gravity.LEFT
    }

    init {
        val padding = 8.dpToPx()
        setPadding(padding, padding, padding, padding)
        orientation = HORIZONTAL
        setBackgroundColor(Color.argb(186, 0, 0, 0))
        elevation = resources.getDimension(R.dimen.div_shadow_elevation)
        val controls = LinearLayout(context).apply {
            setPadding(0, 0, /*right*/padding, 0)
            orientation = VERTICAL
            val closeView = ImageView(context).apply {
                setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
                setOnClickListener { onCloseAction() }
            }
            val copyView = ImageView(context).apply {
                setImageResource(android.R.drawable.ic_menu_save)
                setOnClickListener { onCopyAction() }
            }

            addView(
                closeView, LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT,
                )
            )
            addView(
                copyView, LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT,
                )
            )
        }
        addView(
            controls, LayoutParams(
                32.dpToPx(),
                LayoutParams.WRAP_CONTENT,
            )
        )

        addView(
            errorsOutput, LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
            )
        )
    }

    var text: String
        set(value) {
            errorsOutput.text = value
        }
        get() = errorsOutput.text.toString()
}
