struct ExpressionContext {
  let evaluators: (CalcExpression.Symbol) -> Function?
  let variableValueProvider: (String) -> Any?
}
