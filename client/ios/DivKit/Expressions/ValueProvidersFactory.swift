func makeVariableValueProvider(
  cardId: DivCardID,
  variablesStorage: DivVariablesStorage,
  prototypesStorage: PrototypesValueStorage? = nil
) -> AnyCalcExpression.ValueProvider {
  {
    if let value: Any? = prototypesStorage?.findValue(expression: $0) {
      return value
    }
    return variablesStorage.getVariableValue(
      cardId: cardId,
      name: DivVariableName(rawValue: $0)
    )
  }
}

func makeFunctionsProvider(
  variableTracker: @escaping ExpressionResolver.VariableTracker,
  variableValueProvider: @escaping AnyCalcExpression.ValueProvider,
  persistentValuesStorage: DivPersistentValuesStorage,
  prototypesStorage: PrototypesValueStorage? = nil
) -> FunctionsProvider {
  FunctionsProvider(
    variableValueProvider: {
      if let value: Any? = prototypesStorage?.findValue(expression: $0) {
        return value
      }
      variableTracker([DivVariableName(rawValue: $0)])
      return variableValueProvider($0)
    },
    persistentValuesStorage: persistentValuesStorage
  )
}
