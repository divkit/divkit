import Foundation

extension DivTypedValue {
  func resolveVariableValue(
    _ expressionResolver: ExpressionResolver
  ) -> DivVariableValue? {
    switch self {
    case let .arrayValue(value):
      if let arrayValue = value.resolveValue(expressionResolver) as? DivArray {
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
      if let dictValue = DivDictionary.fromAny(value.value) {
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

  func resolve(
    _ expressionResolver: ExpressionResolver
  ) -> AnyHashable? {
    switch self {
    case let .arrayValue(value):
      if let arrayValue = value.resolveValue(expressionResolver) as? DivArray {
        return arrayValue
      }
      return nil
    case let .booleanValue(value):
      if let boolValue = value.resolveValue(expressionResolver) {
        return boolValue
      }
      return nil
    case let .colorValue(value):
      if let colorValue = value.resolveValue(expressionResolver) {
        return colorValue
      }
      return nil
    case let .dictValue(value):
      if let dictValue = value.value as? DivDictionary {
        return dictValue
      }
      return nil
    case let .integerValue(value):
      if let integerValue = value.resolveValue(expressionResolver) {
        return integerValue
      }
      return nil
    case let .numberValue(value):
      if let numberValue = value.resolveValue(expressionResolver) {
        return numberValue
      }
      return nil
    case let .stringValue(value):
      if let stringValue = value.resolveValue(expressionResolver) {
        return stringValue
      }
      return nil
    case let .urlValue(value):
      if let urlValue = value.resolveValue(expressionResolver) {
        return urlValue
      }
      return nil
    }
  }
}
