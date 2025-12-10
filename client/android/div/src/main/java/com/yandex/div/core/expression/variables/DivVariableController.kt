package com.yandex.div.core.expression.variables

import android.os.Handler
import android.os.Looper
import com.yandex.div.data.Variable
import com.yandex.div.data.VariableDeclarationException
import com.yandex.div.data.VariableMutationException
import com.yandex.div.internal.Assert
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Allows to introduce new variables and update existing.
 */
class DivVariableController(
    private val internalVariableController: DivVariableController? = null
) {
    private val mainHandler = Handler(Looper.getMainLooper())
    private val variables = ConcurrentHashMap<String, Variable>()
    private val declarationObservers = ConcurrentLinkedQueue<DeclarationObserver>()
    private val undeclaredVariables = mutableMapOf<String, String>()
    private val declaredVariableNames = mutableSetOf<String>()
    private val pendingDeclaration = mutableSetOf<String>()
    private val externalVariableRequestObservers = ConcurrentLinkedQueue<VariableRequestObserver>()

    private val requestsObserver = { variableName: String ->
        externalVariableRequestObservers.forEach { it.invoke(variableName) }
    }

    internal val variableSource = MultiVariableSource(this, requestsObserver)

    /**
     * Will declare new variable in the current instance of VariableController.
     * @throws VariableDeclarationException if variable already declared.
     */
    @Throws(VariableDeclarationException::class)
    fun declare(vararg variables: Variable) {
        synchronized(declaredVariableNames) {
            val alreadyDeclaredVariables = variables.filter {
                declaredVariableNames.contains(it.name)
                || pendingDeclaration.contains(it.name)
            }

            if (alreadyDeclaredVariables.isNotEmpty()) {
                throw VariableDeclarationException("""
                        Wanted to declare new variable(s) '$alreadyDeclaredVariables',
                        but variable(s) with such name(s) already exists!
                    """.trimIndent())
            }

            pendingDeclaration.addAll(variables.map { it.name })
        }

        if (mainHandler.looper != Looper.myLooper()) {
            mainHandler.post {
                putOrUpdateInternal(*variables)
            }
            return
        }
        putOrUpdateInternal(*variables)
    }

    /**
     * Return variable if declared. If this variable is not found in current controller method
     * will check internal variable controllers recursively.
     *
     * Please note that changing value of given variable will change the value of global variable,
     * but not vise-versa.
     */
    fun get(variableName: String): Variable? {
        return if (isDeclaredLocal(variableName)) {
            variables[variableName]
        } else {
            internalVariableController?.get(variableName)
        }
    }

    /**
     * Return true if variable already declared in this or internal VariableController.
     */
    fun isDeclared(variableName: String): Boolean = synchronized(declaredVariableNames) {
        return if (isDeclaredLocal(variableName)) {
            true
        } else {
            internalVariableController?.isDeclared(variableName) == true
        }
    }

    /**
     * Will add new global variables if they did not exist or
     * update values of existing variables.
     *
     * PLEASE NOTE THAT CHANGING VALUE OF GIVEN VARIABLES WILL
     * CHANGE VALUE OF ORIGINAL GLOBAL VARIABLES, BUT NOT VISE-VERSA!
     */
    @Throws(VariableMutationException::class)
    fun putOrUpdate(vararg variables: Variable) {
        if (mainHandler.looper != Looper.myLooper()) {
            mainHandler.post {
                putOrUpdateInternal(*variables)
            }
            return
        }
        putOrUpdateInternal(*variables)
    }

    /**
     * Fully replace all the variables.
     * Declare new variables, updates old values, and removes old variables which
     * are not provided in the @param variables.
     * Doesn't effect internalVariableController.
     *
     * PLEASE NOTE THAT CHANGING VALUE OF GIVEN VARIABLES WILL
     * CHANGE VALUE OF ORIGINAL GLOBAL VARIABLES, BUT NOT VISE-VERSA!
     */
    @Throws(VariableMutationException::class)
    fun replaceAll(vararg variables: Variable)  {
        if (mainHandler.looper != Looper.myLooper()) {
            mainHandler.post {
                replaceAllInternal(*variables)
            }
            return
        }
        replaceAllInternal(*variables)
    }

    /**
     * Removes all variables provided in @param variables.
     * Doesn't effect internalVariableController.
     */
    fun removeAll(vararg variablesNames: String)  {
        if (mainHandler.looper != Looper.myLooper()) {
            mainHandler.post {
                removeVariableInternal(*variablesNames)
            }
            return
        }
        removeVariableInternal(*variablesNames)
    }

    private fun putOrUpdateInternal(vararg variables: Variable) {
        val newDeclaredVariables = mutableListOf<Variable>()
        synchronized(declaredVariableNames) {
            variables.forEach { variable ->
                val undeclaredVariableType = undeclaredVariables[variable.name]
                if (undeclaredVariableType != null && undeclaredVariableType != variable::class.java.name) {
                    throw VariableMutationException("Cannot declare new variable with type = " +
                        "${variable::class.java.name}, because this variable have been declared " +
                        "with another type = $undeclaredVariableType")
                }

                if (!declaredVariableNames.contains(variable.name)) {
                    declaredVariableNames.add(variable.name)
                    pendingDeclaration.remove(variable.name)
                    newDeclaredVariables.add(variable)
                }

                val existingVariable = this.variables[variable.name]
                if (existingVariable == variable) {
                    return@forEach
                }

                existingVariable?.let { existing ->
                    existing.setValue(from = variable)
                    variable.addObserver {
                        // TODO(DIVKIT-7313): check out thread safety
                        existing.setValue(from = it)
                    }
                    return@forEach
                }
                this.variables.put(variable.name, variable)?.let { existing ->
                    Assert.fail("""
                    Wanted to put new variable '$variable', but variable with such name
                    already exists '$existing'! Is there a race?
                """.trimIndent())
                }
                undeclaredVariables.remove(variable.name)
            }
        }
        // Declaration notifications must happen apart from updates and declarations
        // to evade errors during updates of properties which use multiple variables.
        // Property update may fail cause only part of variables just got declared.
        if (newDeclaredVariables.isNotEmpty()) {
            declarationObservers.forEach { observer ->
                newDeclaredVariables.forEach { variable -> observer.onDeclared(variable) }
            }
        }
    }

    private fun removeVariableInternal(vararg names: String) {
        val existingVariables = variables.filter {
            names.contains(it.key)
        }

        synchronized(declaredVariableNames) {
            existingVariables.forEach { existing ->
                declaredVariableNames.remove(existing.key)
                undeclaredVariables[existing.key] = existing.value::class.java.name
                variables.remove(existing.key)
            }
        }

        declarationObservers.forEach { observer ->
            existingVariables.forEach { (_, variable) ->
                observer.onUndeclared(variable)
            }
        }
    }

    private fun replaceAllInternal(vararg newVariables: Variable) {
        val variablesToRemove = variables.filter {
            !newVariables.any { newVar: Variable ->
                it.key == newVar.name
            }
        }.values

        val variablesToUpdate = newVariables.toMutableList().apply { removeAll(variablesToRemove) }

        variablesToRemove.map { it.name }.forEach { removeVariableInternal(it) }
        variablesToUpdate.forEach { putOrUpdateInternal(it) }
    }

    private fun isDeclaredLocal(variableName: String): Boolean = synchronized(declaredVariableNames) {
        declaredVariableNames.contains(variableName)
    }

    /**
     * Allows to track requests to defined or not yet defined variables.
     * Also subscribe on requests in internal controllers recursively.
     */
    fun addVariableRequestObserver(observer: VariableRequestObserver) {
        externalVariableRequestObservers.add(observer)
        internalVariableController?.addVariableRequestObserver(observer)
    }

    /**
     * Removes observer from this instance and internal controllers recursively.
     */
    fun removeVariableRequestObserver(observer: VariableRequestObserver) {
        externalVariableRequestObservers.remove(observer)
        internalVariableController?.removeVariableRequestObserver(observer)
    }

    /**
     * Captures all variables from this instance and internal controllers recursively.
     */
    fun captureAllVariables(): List<Variable> {
        return variables.values + (internalVariableController?.captureAllVariables() ?: emptyList())
    }

    internal fun addDeclarationObserver(observer: DeclarationObserver) {
        declarationObservers.add(observer)
        internalVariableController?.addDeclarationObserver(observer)
    }

    internal fun removeDeclarationObserver(observer: DeclarationObserver) {
        declarationObservers.remove(observer)
        internalVariableController?.removeDeclarationObserver(observer)
    }

    internal fun addVariableObserver(observer: (Variable) -> Unit) {
        variables.values.forEach {
            it.addObserver(observer)
        }
        internalVariableController?.addVariableObserver(observer)
    }

    internal fun removeVariablesObserver(observer: (Variable) -> Unit) {
        variables.values.forEach {
            it.removeObserver(observer)
        }
        internalVariableController?.removeVariablesObserver(observer)
    }

    internal fun receiveVariablesUpdates(observer: (Variable) -> Unit) {
        variables.values.forEach {
            observer.invoke(it)
        }
        internalVariableController?.receiveVariablesUpdates(observer)
    }
}
