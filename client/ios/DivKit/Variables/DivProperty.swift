import Foundation
import VGSL

struct DivProperty {
  let expressionResolver: ExpressionResolver
  let actionHandler: DivActionHandler?
  let getValue: String
  let newValueVariableName: String
  let valueType: DivEvaluableType
  let actions: [DivAction]

  func typedValue<T>() -> T? {
    if let resolvedValue = toVariableValue() {
      return resolvedValue.typedValue()
    } else {
      return nil
    }
  }

  func toVariableValue() -> DivVariableValue? {
    switch valueType {
    case .string:
      if let resolved = expressionResolver.resolve(getValue) as? String {
        return .string(resolved)
      }
      return .string(getValue)
    case .integer:
      if let resolved = expressionResolver.resolve(getValue) as? Int {
        return .integer(resolved)
      } else if let resolved = Int(getValue) {
        return .integer(resolved)
      }
      return nil
    case .number:
      if let resolved = expressionResolver.resolve(getValue) as? Double {
        return .number(resolved)
      } else if let resolved = Double(getValue) {
        return .number(resolved)
      }
      return nil
    case .boolean:
      if let resolved = expressionResolver.resolve(getValue) as? Bool {
        return .bool(resolved)
      } else if let resolved = Bool(getValue) {
        return .bool(resolved)
      }
      return nil
    case .url:
      if let resolved = expressionResolver.resolve(getValue) as? URL {
        return .url(resolved)
      } else if let resolved = URL(string: getValue) {
        return .url(resolved)
      }
      return nil
    case .dict:
      if let resolved = expressionResolver.resolve(getValue) as? DivDictionary {
        return .dict(resolved)
      }
      return nil
    case .array:
      if let resolved = expressionResolver.resolve(getValue) as? DivArray {
        return .array(resolved)
      }
      return nil
    case .color:
      if let resolved = expressionResolver.resolveColor(getValue) {
        return .color(resolved)
      }
      return nil
    case .datetime:
      return nil
    }
  }
}

#if DEBUG
extension DivProperty: Equatable {
  static func ==(lhs: DivProperty, rhs: DivProperty) -> Bool {
    lhs.getValue == rhs.getValue &&
    lhs.valueType == rhs.valueType &&
    lhs.actions == rhs.actions
  }
}
#endif
