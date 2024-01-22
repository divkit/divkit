func makeVariableValueProvider(
  cardId: DivCardID,
  variablesStorage: DivVariablesStorage,
  prototypesStorage: [String: Any]? = nil
) -> AnyCalcExpression.ValueProvider {
  {
    if let value: Any = prototypesStorage?[$0] {
      return value
    }
    return variablesStorage.getVariableValue(
      cardId: cardId,
      name: DivVariableName(rawValue: $0)
    )
  }
}

func makeFunctionsProvider(
  cardId: DivCardID,
  variablesStorage: DivVariablesStorage,
  variableTracker: @escaping ExpressionResolver.VariableTracker,
  persistentValuesStorage: DivPersistentValuesStorage,
  prototypesStorage: [String: Any]? = nil
) -> FunctionsProvider {
  FunctionsProvider(
    variableValueProvider: {
      if let value: Any = prototypesStorage?[$0] {
        return value
      }
      variableTracker([DivVariableName(rawValue: $0)])
      return variablesStorage.getVariableValue(
        cardId: cardId,
        name: DivVariableName(rawValue: $0)
      )
    },
    persistentValuesStorage: persistentValuesStorage
  )
}
