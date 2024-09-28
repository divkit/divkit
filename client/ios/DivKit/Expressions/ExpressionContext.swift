struct ExpressionContext {
  let evaluators: (CalcExpression.Symbol) -> Function?
  let variableValueProvider: (String) -> Any?
  let customFunctionsStorageProvider: (String) -> DivFunctionsStorage?
  let errorTracker: ExpressionErrorTracker
}
