// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivTypedValue: Sendable {
  case stringValue(StringValue)
  case integerValue(IntegerValue)
  case numberValue(NumberValue)
  case colorValue(ColorValue)
  case booleanValue(BooleanValue)
  case urlValue(UrlValue)
  case dictValue(DictValue)
  case arrayValue(ArrayValue)

  public var value: Serializable {
    switch self {
    case let .stringValue(value):
      return value
    case let .integerValue(value):
      return value
    case let .numberValue(value):
      return value
    case let .colorValue(value):
      return value
    case let .booleanValue(value):
      return value
    case let .urlValue(value):
      return value
    case let .dictValue(value):
      return value
    case let .arrayValue(value):
      return value
    }
  }
}

extension DivTypedValue {
  public init(dictionary: [String: Any], context: ParsingContext) throws {
    let blockType = try dictionary.getField("type") as String
    switch blockType {
    case StringValue.type:
      self = .stringValue(try StringValue(dictionary: dictionary, context: context))
    case IntegerValue.type:
      self = .integerValue(try IntegerValue(dictionary: dictionary, context: context))
    case NumberValue.type:
      self = .numberValue(try NumberValue(dictionary: dictionary, context: context))
    case ColorValue.type:
      self = .colorValue(try ColorValue(dictionary: dictionary, context: context))
    case BooleanValue.type:
      self = .booleanValue(try BooleanValue(dictionary: dictionary, context: context))
    case UrlValue.type:
      self = .urlValue(try UrlValue(dictionary: dictionary, context: context))
    case DictValue.type:
      self = .dictValue(try DictValue(dictionary: dictionary, context: context))
    case ArrayValue.type:
      self = .arrayValue(try ArrayValue(dictionary: dictionary, context: context))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-typed-value", representation: dictionary)
    }
  }
}

#if DEBUG
extension DivTypedValue: Equatable {
  public static func ==(lhs: DivTypedValue, rhs: DivTypedValue) -> Bool {
    switch (lhs, rhs) {
    case let (.stringValue(l), .stringValue(r)):
      return l == r
    case let (.integerValue(l), .integerValue(r)):
      return l == r
    case let (.numberValue(l), .numberValue(r)):
      return l == r
    case let (.colorValue(l), .colorValue(r)):
      return l == r
    case let (.booleanValue(l), .booleanValue(r)):
      return l == r
    case let (.urlValue(l), .urlValue(r)):
      return l == r
    case let (.dictValue(l), .dictValue(r)):
      return l == r
    case let (.arrayValue(l), .arrayValue(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivTypedValue: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
