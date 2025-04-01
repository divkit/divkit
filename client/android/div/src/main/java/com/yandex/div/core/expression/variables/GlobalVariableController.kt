package com.yandex.div.core.expression.variables

import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.data.Variable
import com.yandex.div.data.VariableDeclarationException
import com.yandex.div.data.VariableMutationException

@Deprecated("Do not use this class")
@InternalApi
class GlobalVariableController @JvmOverloads constructor(
    @get:JvmName("delegate") internal val delegate: DivVariableController = DivVariableController()
) {

    @Throws(VariableDeclarationException::class)
    fun declare(vararg variables: Variable) {
        delegate.declare(*variables)
    }

    fun get(variableName: String): Variable? {
        return delegate.get(variableName)
    }

    fun isDeclared(variableName: String): Boolean {
        return delegate.isDeclared(variableName)
    }

    @Throws(VariableMutationException::class)
    fun putOrUpdate(vararg variables: Variable) {
        delegate.putOrUpdate(*variables)
    }

    fun addVariableRequestObserver(observer: VariableRequestObserver) {
        delegate.addVariableRequestObserver(observer)
    }

    fun removeVariableRequestObserver(observer: VariableRequestObserver) {
        delegate.removeVariableRequestObserver(observer)
    }
}
