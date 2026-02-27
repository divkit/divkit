// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivVariable: Sendable {
  case stringVariable(StringVariable)
  case numberVariable(NumberVariable)
  case integerVariable(IntegerVariable)
  case booleanVariable(BooleanVariable)
  case colorVariable(ColorVariable)
  case urlVariable(UrlVariable)
  case dictVariable(DictVariable)
  case arrayVariable(ArrayVariable)
  case propertyVariable(PropertyVariable)

  public var value: Serializable {
    switch self {
    case let .stringVariable(value):
      return value
    case let .numberVariable(value):
      return value
    case let .integerVariable(value):
      return value
    case let .booleanVariable(value):
      return value
    case let .colorVariable(value):
      return value
    case let .urlVariable(value):
      return value
    case let .dictVariable(value):
      return value
    case let .arrayVariable(value):
      return value
    case let .propertyVariable(value):
      return value
    }
  }
}

extension DivVariable {
  public init(dictionary: [String: Any], context: ParsingContext) throws {
    let dictionary = context.templateResolver?(dictionary) ?? dictionary
    let blockType = try dictionary.getField("type") as String
    switch blockType {
    case StringVariable.type:
      self = .stringVariable(try StringVariable(dictionary: dictionary, context: context))
    case NumberVariable.type:
      self = .numberVariable(try NumberVariable(dictionary: dictionary, context: context))
    case IntegerVariable.type:
      self = .integerVariable(try IntegerVariable(dictionary: dictionary, context: context))
    case BooleanVariable.type:
      self = .booleanVariable(try BooleanVariable(dictionary: dictionary, context: context))
    case ColorVariable.type:
      self = .colorVariable(try ColorVariable(dictionary: dictionary, context: context))
    case UrlVariable.type:
      self = .urlVariable(try UrlVariable(dictionary: dictionary, context: context))
    case DictVariable.type:
      self = .dictVariable(try DictVariable(dictionary: dictionary, context: context))
    case ArrayVariable.type:
      self = .arrayVariable(try ArrayVariable(dictionary: dictionary, context: context))
    case PropertyVariable.type:
      self = .propertyVariable(try PropertyVariable(dictionary: dictionary, context: context))
    default:
      throw DeserializationError.requiredFieldIsMissing(field: "type")
    }
  }
}

#if DEBUG
extension DivVariable: Equatable {
  public static func ==(lhs: DivVariable, rhs: DivVariable) -> Bool {
    switch (lhs, rhs) {
    case let (.stringVariable(l), .stringVariable(r)):
      return l == r
    case let (.numberVariable(l), .numberVariable(r)):
      return l == r
    case let (.integerVariable(l), .integerVariable(r)):
      return l == r
    case let (.booleanVariable(l), .booleanVariable(r)):
      return l == r
    case let (.colorVariable(l), .colorVariable(r)):
      return l == r
    case let (.urlVariable(l), .urlVariable(r)):
      return l == r
    case let (.dictVariable(l), .dictVariable(r)):
      return l == r
    case let (.arrayVariable(l), .arrayVariable(r)):
      return l == r
    case let (.propertyVariable(l), .propertyVariable(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivVariable: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
