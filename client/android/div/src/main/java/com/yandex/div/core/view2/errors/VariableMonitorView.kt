package com.yandex.div.core.view2.errors

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.InputType
import android.text.method.ScrollingMovementMethod
import android.view.KeyEvent
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.R
import com.yandex.div.core.view2.divs.dpToPx
import com.yandex.div.data.Variable


@SuppressLint("ViewConstructor")
internal class VariableMonitorView(
    private val context: Context,
    variableMonitor: VariableMonitor
) : LinearLayout(context) {

    private val variablesAdapter = VariableAdapter(variableMonitor::mutateVariable)
    private val title = createTableTitle()

    init {
        orientation = VERTICAL
        variableMonitor.setVariablesUpdatedCallback(::updateTable)

        addView(title, LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT)
        )
        addView(createTable(), LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT)
        )
    }

    private fun createTableTitle() = LinearLayout(context).apply {
        val widths = listOf(VariableView.NAME_WIDTH, VariableView.TYPE_WIDTH, 100)
        listOf("name", "type", "value")
            .map(::createCellTitle)
            .zip(widths)
            .forEach { (view, width) ->
                addView(view, LayoutParams(
                    width.dpToPx(resources.displayMetrics),
                    LayoutParams.WRAP_CONTENT)
                )
            }
    }

    private fun createTable() = RecyclerView(context).apply {
        layoutManager = LinearLayoutManager(context)
        adapter = variablesAdapter
        setBackgroundColor(Color.argb(50, 0, 0, 0))
    }

    private fun createCellTitle(title: String) = TextView(context).apply {
        setPadding(VariableView.CELL_PADDING.dpToPx(resources.displayMetrics))
        setTextColor(Color.WHITE)
        text = title
        setTypeface(typeface, Typeface.BOLD)
    }

    private fun updateTable(newList: List<Pair<String, Variable>>) {
        variablesAdapter.submitList(newList.map { (path, variable) -> variable.toModel(path) }) {
            title.isVisible = variablesAdapter.itemCount != 0
        }
    }
}

private typealias VariableMutator = (name: String, path: String, value: String) -> Unit

private class VariableAdapter(
    private val variableMutator: VariableMutator
) : ListAdapter<VariableModel, VariableAdapter.VariableViewHolder>(VariableDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VariableViewHolder(VariableView(parent.context), variableMutator)

    override fun onBindViewHolder(holder: VariableViewHolder, position: Int) =
        holder.bind(currentList[position])

    class VariableViewHolder(
        private val root: VariableView,
        private val variableMutator: VariableMutator
    ) : RecyclerView.ViewHolder(root) {

        fun bind(variable: VariableModel) {
            root.apply {
                nameText.text = variable.fullName()
                typeText.text = variable.type
                valueText.setText(variable.value)
                valueText.inputType = variable.inputType()

                onEnterAction = { newValue ->
                    variableMutator.invoke(variable.name, variable.path, newValue)
                }
            }
        }

        private fun VariableModel.fullName() =
            if (path.isNotEmpty()) "$path/$name" else name

        private fun VariableModel.inputType() = when(type) {
            "number", "integer" -> InputType.TYPE_CLASS_NUMBER
            else -> InputType.TYPE_CLASS_TEXT
        }
    }

    private class VariableDiffUtilCallback : DiffUtil.ItemCallback<VariableModel>() {
        override fun areItemsTheSame(oldItem: VariableModel, newItem: VariableModel)
            = oldItem.name == newItem.name

        override fun areContentsTheSame(oldItem: VariableModel, newItem: VariableModel)
            = oldItem.value == newItem.value
    }
}

@SuppressLint("ViewConstructor")
private class VariableView(
    context: Context
) : LinearLayout(context) {
    val nameText = createCell()
    val typeText = createCell()
    val valueText = createEditableCell()

    var onEnterAction: (String) -> Unit = {}

    init {
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT,
        )
        orientation = HORIZONTAL

        addCell(nameText, NAME_WIDTH)
        addCell(typeText, TYPE_WIDTH)

        addView(valueText, LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT, 1f)
        )
    }

    private fun createCell() = TextView(context).apply {
        configureCommon()
    }

    private fun createEditableCell() = EditText(context).apply {
        configureCommon()
        imeOptions = EditorInfo.IME_ACTION_DONE

        setOnEditorActionListener { _, actionId, event ->
            if (event != null && event.action != KeyEvent.ACTION_DOWN) {
                return@setOnEditorActionListener false
            }
            return@setOnEditorActionListener when(actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    onEnterAction.invoke(text.toString())
                    true
                }
                else -> false
            }
        }
    }

    private fun TextView.configureCommon() {
        val p = CELL_PADDING.dpToPx(resources.displayMetrics)
        setPadding(p, p, p, p)
        setTextColor(Color.WHITE)
        setBackgroundResource(R.drawable.table_cell_background)
        setHorizontallyScrolling(true)
        movementMethod = ScrollingMovementMethod()
        maxLines = 1
    }

    private fun addCell(cell: TextView, width: Int) {
        addView(cell, LayoutParams(
            width.dpToPx(resources.displayMetrics),
            LayoutParams.MATCH_PARENT)
        )
    }

    companion object {
        const val NAME_WIDTH = 200
        const val TYPE_WIDTH = 60
        const val CELL_PADDING = 8
    }
}

private data class VariableModel(
    val name: String,
    val path: String,
    val type: String,
    val value: String
)

private fun Variable.toModel(path: String) = VariableModel(
    name,
    path,
    getType(),
    getValue().toString()
)

private fun Variable.getType() =
    when (this) {
        is Variable.ArrayVariable -> "array"
        is Variable.BooleanVariable -> "boolean"
        is Variable.ColorVariable -> "color"
        is Variable.DictVariable -> "dict"
        is Variable.DoubleVariable -> "number"
        is Variable.IntegerVariable -> "integer"
        is Variable.StringVariable -> "string"
        is Variable.UrlVariable -> "url"
        is Variable.PropertyVariable -> "property"
    }
