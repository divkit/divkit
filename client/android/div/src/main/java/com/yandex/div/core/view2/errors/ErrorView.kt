package com.yandex.div.core.view2.errors

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.os.TransactionTooLargeException
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import com.yandex.div.R
import com.yandex.div.core.Disposable
import com.yandex.div.core.expression.variables.VariableController
import com.yandex.div.core.view2.divs.dpToPx
import com.yandex.div.internal.Assert
import com.yandex.div.internal.widget.FrameContainerLayout

internal class ErrorView(
    private val root: ViewGroup,
    private val errorModel: ErrorModel,
    private val showPermanently: Boolean,
) : Disposable {
    private var counterView: ViewGroup? = null
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
            detailsView?.text = new.getDetails()
            detailsView?.updateVariables(errorModel.getAllControllers())
        } else {
            if (new.getCounterText().isNotEmpty() || showPermanently) {
                tryAddCounterView()
            } else {
                counterView?.let { root.removeView(it) }
                counterView = null
            }

            (counterView?.getChildAt(0) as? AppCompatTextView)?.run {
                text = new.getCounterText()
                setBackgroundResource(new.getCounterBackground())
            }
        }
    }

    private fun tryAddDetailsView() {
        if (detailsView != null) {
            return
        }

        val view = DetailsViewGroup(root.context, errorModel.getErrorHandler(),
            onExecuteAction = { actionString ->
                errorModel.executeAction(actionString)
            },
            onCloseAction = {
                errorModel.hideDetails()
            },
            onCopyAction = {
                viewModel?.let {
                    val fullReport = errorModel.generateReport()
                    pasteToClipBoard(fullReport).onFailure {
                        if (it.causedByTransactionTooLargeException()) {
                            pasteToClipBoard(errorModel.generateReport(dumpCardContent = false))
                        }
                    }
                }
            }
        )

        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        root.addView(view, layoutParams)
        detailsView = view
    }

    private fun pasteToClipBoard(s: String): Result<Unit> {
        val clipboardManager =
            root.context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                ?: run {
                    Assert.fail("Failed to access clipboard manager!")
                    return Result.success(Unit)
                }

        try {
            clipboardManager.setPrimaryClip(
                ClipData(
                    "Error report",
                    arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                    ClipData.Item(s)
                )
            )
        } catch (e: Exception) {
            return Result.failure(RuntimeException("Failed paste report to clipboard!", e))
        }

        Toast.makeText(root.context, "Errors, DivData and Variables are dumped to clipboard!",
            Toast.LENGTH_LONG).show()

        return Result.success(Unit)
    }

    private fun tryAddCounterView() {
        if (counterView != null) {
            return
        }

        val view = AppCompatTextView(root.context).apply {
            setBackgroundResource(R.drawable.error_counter_background)
            textSize = 12f
            setTextColor(Color.BLACK)
            gravity = Gravity.CENTER
            elevation = resources.getDimension(R.dimen.div_shadow_elevation)
            setOnClickListener { errorModel.showDetails() }
        }

        val metrics = root.context.resources.displayMetrics

        val side = 24.dpToPx(metrics)
        val layoutParams = MarginLayoutParams(side, side)
        val sideMargin = 8.dpToPx(metrics)
        layoutParams.topMargin = sideMargin
        layoutParams.leftMargin = sideMargin
        layoutParams.rightMargin = sideMargin
        layoutParams.bottomMargin = sideMargin
        val counterContainer = FrameContainerLayout(root.context).apply {
            addView(view, layoutParams)
        }
        root.addView(counterContainer,
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        counterView = counterContainer
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
    errorHandler: (Throwable) -> Unit,
    private val onExecuteAction: (String) -> Unit,
    private val onCloseAction: () -> Unit,
    private val onCopyAction: () -> Unit,
) : LinearLayout(context) {

    private val variableMonitor = VariableMonitor(errorHandler)

    private val errorsOutput = createErrorsOutput()
    private val actionInput = createActionInput()
    private val monitorView = VariableMonitorView(context, variableMonitor)

    init {
        configureView()
    }

    var text: String
        set(value) {
            errorsOutput.text = value
        }
        get() = errorsOutput.text.toString()
    
    fun updateVariables(controllers: Map<String, VariableController>) {
        variableMonitor.controllerMap = controllers
    }

    private fun createTopPanel() = LinearLayout(context).apply {
        orientation = HORIZONTAL

        addView(
            createControls(), LayoutParams(
                32.dpToPx(resources.displayMetrics),
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

    private fun createControls() = LinearLayout(context).apply {
        val padding = 8.dpToPx(resources.displayMetrics)
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

    private fun createErrorsOutput() = AppCompatTextView(context).apply {
        setTextColor(Color.WHITE)
        gravity = Gravity.LEFT
    }

    private fun createActionInput() = EditText(context).apply {
        setTextColor(Color.WHITE)
        setPadding(24, 12, 24, 12)
        setBackgroundResource(R.drawable.action_box_background)
        setHorizontallyScrolling(true)
    }

    private fun createActionExecutionBox() = LinearLayout(context).apply {
        orientation = VERTICAL

        addView(
            actionInput, LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT,
                1f
            )
        )
        addView(
            Button(context).apply {
                text = "execute"
                setOnClickListener { onExecuteAction(actionInput.text.toString()) }
            },
            LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
            )
        )
    }

    private fun configureView() {
        val padding = 8.dpToPx(resources.displayMetrics)
        setPadding(padding, padding, padding, padding)
        orientation = VERTICAL
        setBackgroundColor(Color.argb(186, 0, 0, 0))
        elevation = resources.getDimension(R.dimen.div_shadow_elevation)

        addView(
            createTopPanel(), LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
            )
        )
        addView(
            createActionExecutionBox(), LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT,
            )
        )
        addView(
            monitorView, LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT,
            )
        )
    }

}

private fun Throwable.causedByTransactionTooLargeException(): Boolean {
    return this is TransactionTooLargeException ||
            this.cause?.causedByTransactionTooLargeException() == true
}
