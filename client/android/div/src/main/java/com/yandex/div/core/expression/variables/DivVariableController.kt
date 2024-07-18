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
 * Allows to introduce new global variables and update existing.
 * 1. Global variables can be introduced only once and their types cannot change.
 * 2. Global variables cannot be removed.
 *
 * These limitations comes from ability to observe and set value of any [Variable].
 * If you remove variable "X" and add another "X" variable once again then observers of original "X"
 * won't be notified about changes in new "X". Moving observers and value outside of variable scope
 * will only increase complexity and simple api like variable.set("new_value") will be
 * transformed into "globalOrLocalVariableScope.findScopeOf(variable).set("new_value")".
 */
class DivVariableController(
    private val internalVariableController: DivVariableController? = null
) {
    private val mainHandler = Handler(Looper.getMainLooper())
    private val variables = ConcurrentHashMap<String, Variable>()
    private val declarationObservers = ConcurrentLinkedQueue<DeclarationObserver>()
    private val declaredVariableNames = mutableSetOf<String>()
    private val pendingDeclaration = mutableSetOf<String>()
    private val externalVariableRequestObservers = ConcurrentLinkedQueue<VariableRequestObserver>()

    private val requestsObserver = { variableName: String ->
        externalVariableRequestObservers.forEach { it.invoke(variableName) }
    }

    internal val variableSource = MultiVariableSource(this, requestsObserver)

    /**
     * Will declare new variable in the current instance of GlobalVariableController.
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
     * Return true if variable already declared in this or internal GlobalVariableController.
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

    private fun putOrUpdateInternal(vararg variables: Variable) {
        val newDeclaredVariables = mutableListOf<Variable>()
        synchronized(declaredVariableNames) {
            variables.forEach { variable ->
                if (!declaredVariableNames.contains(variable.name)) {
                    declaredVariableNames.add(variable.name)
                    pendingDeclaration.remove(variable.name)
                    newDeclaredVariables.add(variable)
                }
                this.variables[variable.name]?.let { existing ->
                    existing.setValue(from = variable)
                    variable.addObserver { existing.setValue(from = it) }
                    return@forEach
                }
                this.variables.put(variable.name, variable)?.let { existing ->
                    Assert.fail("""
                    Wanted to put new variable '$variable', but variable with such name
                    already exists '$existing'! Is there a race?
                """.trimIndent())
                }
            }
        }
        // Declaration notifications must happen apart from updates and declarations
        // to evade errors during updates of properties which use multiple variables.
        // Property update may fail cause only part of variables just got declared.
        if (newDeclaredVariables.isNotEmpty()) {
            declarationObservers.forEach { observer ->
                newDeclaredVariables.forEach { variable -> observer.invoke(variable) }
            }
        }
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

    internal fun addDeclarationObserver(observer: (Variable) -> Unit) {
        declarationObservers.add(observer)
        internalVariableController?.addDeclarationObserver(observer)
    }

    internal fun removeDeclarationObserver(observer: (Variable) -> Unit) {
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

    internal fun captureAllVariables(): List<Variable> {
        return variables.values + (internalVariableController?.captureAllVariables() ?: emptyList())
    }

}
