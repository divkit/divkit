final class SetStoredValueActionHandler {
  private let persistentValuesStorage: DivPersistentValuesStorage

  init(persistentValuesStorage: DivPersistentValuesStorage) {
    self.persistentValuesStorage = persistentValuesStorage
  }

  func handle(_ action: DivActionSetStoredValue, context: DivActionHandlingContext) {
    let expressionResolver = context.expressionResolver
    guard let name = action.resolveName(expressionResolver),
          let lifetime = action.resolveLifetime(expressionResolver),
          let value = action.value.resolve(expressionResolver),
          let type = action.value.storedValueType else {
      return
    }

    persistentValuesStorage.set(
      value: DivStoredValue(
        name: name,
        value: ExpressionValueConverter.stringify(value),
        type: type,
        lifetimeInSec: lifetime
      )
    )
  }
}

extension DivTypedValue {
  fileprivate var storedValueType: DivStoredValue.ValueType? {
    switch self {
    case .booleanValue:
      .boolean
    case .colorValue:
      .color
    case .integerValue:
      .integer
    case .numberValue:
      .number
    case .stringValue:
      .string
    case .urlValue:
      .url
    case .arrayValue, .dictValue:
      nil
    }
  }
}
