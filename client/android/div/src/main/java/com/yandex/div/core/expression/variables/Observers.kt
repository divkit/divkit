package com.yandex.div.core.expression.variables

import com.yandex.div.data.Variable

internal typealias DeclarationObserver = (Variable) -> Unit
internal typealias VariableRequestObserver = (String) -> Unit
