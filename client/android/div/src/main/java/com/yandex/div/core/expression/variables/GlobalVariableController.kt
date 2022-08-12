package com.yandex.div.core.expression.variables

import android.os.Handler
import android.os.Looper
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.util.Assert
import com.yandex.div.data.Variable
import com.yandex.div.data.VariableMutationException
import com.yandex.div.util.SynchronizedList
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
    private val declaredVariableNames = mutableListOf<String>()

    internal val variableSource = VariableSource(
        variables,
        declarationObservers
    )

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
        variables.forEach { variable ->
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

        // Declaration notifications must happen apart from updates and declarations
        // to evade errors during updates of properties which use multiple variables.
        // Property update may fail cause only part of variables just got declared.
        variables.forEach { variable ->
            val name = variable.name
            if (!declaredVariableNames.contains(name)) {
                declaredVariableNames.add(name)
                declarationObservers.forEach { it.invoke(variable) }
            }
        }
    }
}
