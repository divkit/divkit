package com.yandex.div.core.expression.variables

import android.os.Handler
import android.os.Looper
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.data.Variable
import com.yandex.div.data.VariableDeclarationException
import com.yandex.div.data.VariableMutationException
import com.yandex.div.internal.Assert
import com.yandex.div.internal.util.SynchronizedList
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

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
@DivScope
class GlobalVariableController @Inject constructor() {
    private val mainHandler = Handler(Looper.getMainLooper())
    private val variables = ConcurrentHashMap<String, Variable>()
    private val declarationObservers = SynchronizedList<(Variable) -> Unit>()
    private val declaredVariableNames = mutableSetOf<String>()
    private val pendingDeclaration = mutableSetOf<String>()
    private val externalVariableRequestObservers = SynchronizedList<(variableName: String) -> Unit>()

    private val requestsObserver = { variableName: String ->
        externalVariableRequestObservers.forEach { it.invoke(variableName) }
    }

    internal val variableSource = VariableSource(
        variables,
        requestsObserver,
        declarationObservers
    )

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
        putOrUpdate(*variables)
    }

    fun get(variableName: String): Variable? {
        return variables[variableName]
    }

    fun isDeclared(variableName: String): Boolean = synchronized(declaredVariableNames) {
        declaredVariableNames.contains(variableName)
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
        declarationObservers.forEach { observer ->
            newDeclaredVariables.forEach { variable -> observer.invoke(variable)}
        }
    }

    /**
     * Allows to track requests to defined or not yet defined global variables.
     */
    fun addVariableRequestObserver(observer: (variableName: String) -> Unit) {
        externalVariableRequestObservers.add(observer)
    }

    fun removeVariableRequestObserver(observer: (variableName: String) -> Unit) {
        externalVariableRequestObservers.remove(observer)
    }
}
