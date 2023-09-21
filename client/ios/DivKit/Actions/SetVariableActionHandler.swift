import Foundation

final class SetVariableActionHandler {
  private let variableStorage: DivVariablesStorage

  init(variableStorage: DivVariablesStorage) {
    self.variableStorage = variableStorage
  }

  func handle(_ action: DivActionSetVariable, context: DivActionHandlingContext) {
    let expressionResolver = context.expressionResolver
    guard let variableName = action.resolveVariableName(expressionResolver) else {
      return
    }

    guard let variableValue = action.value.asVariableValue(expressionResolver: expressionResolver) else {
      return
    }

    variableStorage.update(
      cardId: context.cardId,
      name: DivVariableName(rawValue: variableName),
      value: variableValue
    )
  }
}


extension DivTypedValue {
  fileprivate func asVariableValue(
    expressionResolver: ExpressionResolver
  ) -> DivVariableValue? {
    switch self {
    case let .arrayValue(value):
      if let arrayValue = value.value as? [AnyHashable] {
        return .array(arrayValue)
      }
      return nil
    case let .booleanValue(value):
      if let boolValue = value.resolveValue(expressionResolver) {
        return .bool(boolValue)
      }
      return nil
    case let .colorValue(value):
      if let colorValue = value.resolveValue(expressionResolver) {
        return .color(colorValue)
      }
      return nil
    case let .dictValue(value):
      if let dictValue = value.value as? [String: AnyHashable] {
        return .dict(dictValue)
      }
      return nil
    case let .integerValue(value):
      if let integerValue = value.resolveValue(expressionResolver) {
        return .integer(integerValue)
      }
      return nil
    case let .numberValue(value):
      if let numberValue = value.resolveValue(expressionResolver) {
        return .number(numberValue)
      }
      return nil
    case let .stringValue(value):
      if let stringValue = value.resolveValue(expressionResolver) {
        return .string(stringValue)
      }
      return nil
    case let .urlValue(value):
      if let urlValue = value.resolveValue(expressionResolver) {
        return .url(urlValue)
      }
      return nil
    }
  }
}
