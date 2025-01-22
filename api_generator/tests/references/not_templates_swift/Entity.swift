// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization

@frozen
public enum Entity: Sendable {
  case entityWithArray(EntityWithArray)
  case entityWithArrayOfEnums(EntityWithArrayOfEnums)
  case entityWithArrayOfExpressions(EntityWithArrayOfExpressions)
  case entityWithArrayOfNestedItems(EntityWithArrayOfNestedItems)
  case entityWithArrayWithTransform(EntityWithArrayWithTransform)
  case entityWithComplexProperty(EntityWithComplexProperty)
  case entityWithComplexPropertyWithDefaultValue(EntityWithComplexPropertyWithDefaultValue)
  case entityWithEntityProperty(EntityWithEntityProperty)
  case entityWithOptionalComplexProperty(EntityWithOptionalComplexProperty)
  case entityWithOptionalProperty(EntityWithOptionalProperty)
  case entityWithOptionalStringEnumProperty(EntityWithOptionalStringEnumProperty)
  case entityWithPropertyWithDefaultValue(EntityWithPropertyWithDefaultValue)
  case entityWithRawArray(EntityWithRawArray)
  case entityWithRequiredProperty(EntityWithRequiredProperty)
  case entityWithSimpleProperties(EntityWithSimpleProperties)
  case entityWithStringArrayProperty(EntityWithStringArrayProperty)
  case entityWithStringEnumProperty(EntityWithStringEnumProperty)
  case entityWithStringEnumPropertyWithDefaultValue(EntityWithStringEnumPropertyWithDefaultValue)
  case entityWithoutProperties(EntityWithoutProperties)

  public var value: Serializable {
    switch self {
    case let .entityWithArray(value):
      return value
    case let .entityWithArrayOfEnums(value):
      return value
    case let .entityWithArrayOfExpressions(value):
      return value
    case let .entityWithArrayOfNestedItems(value):
      return value
    case let .entityWithArrayWithTransform(value):
      return value
    case let .entityWithComplexProperty(value):
      return value
    case let .entityWithComplexPropertyWithDefaultValue(value):
      return value
    case let .entityWithEntityProperty(value):
      return value
    case let .entityWithOptionalComplexProperty(value):
      return value
    case let .entityWithOptionalProperty(value):
      return value
    case let .entityWithOptionalStringEnumProperty(value):
      return value
    case let .entityWithPropertyWithDefaultValue(value):
      return value
    case let .entityWithRawArray(value):
      return value
    case let .entityWithRequiredProperty(value):
      return value
    case let .entityWithSimpleProperties(value):
      return value
    case let .entityWithStringArrayProperty(value):
      return value
    case let .entityWithStringEnumProperty(value):
      return value
    case let .entityWithStringEnumPropertyWithDefaultValue(value):
      return value
    case let .entityWithoutProperties(value):
      return value
    }
  }
}

extension Entity {
  public init(dictionary: [String: Any]) throws {
    let blockType = try dictionary.getField("type") as String
    switch blockType {
    case EntityWithArray.type:
      self = .entityWithArray(try EntityWithArray(dictionary: dictionary))
    case EntityWithArrayOfEnums.type:
      self = .entityWithArrayOfEnums(try EntityWithArrayOfEnums(dictionary: dictionary))
    case EntityWithArrayOfExpressions.type:
      self = .entityWithArrayOfExpressions(try EntityWithArrayOfExpressions(dictionary: dictionary))
    case EntityWithArrayOfNestedItems.type:
      self = .entityWithArrayOfNestedItems(try EntityWithArrayOfNestedItems(dictionary: dictionary))
    case EntityWithArrayWithTransform.type:
      self = .entityWithArrayWithTransform(try EntityWithArrayWithTransform(dictionary: dictionary))
    case EntityWithComplexProperty.type:
      self = .entityWithComplexProperty(try EntityWithComplexProperty(dictionary: dictionary))
    case EntityWithComplexPropertyWithDefaultValue.type:
      self = .entityWithComplexPropertyWithDefaultValue(try EntityWithComplexPropertyWithDefaultValue(dictionary: dictionary))
    case EntityWithEntityProperty.type:
      self = .entityWithEntityProperty(try EntityWithEntityProperty(dictionary: dictionary))
    case EntityWithOptionalComplexProperty.type:
      self = .entityWithOptionalComplexProperty(try EntityWithOptionalComplexProperty(dictionary: dictionary))
    case EntityWithOptionalProperty.type:
      self = .entityWithOptionalProperty(try EntityWithOptionalProperty(dictionary: dictionary))
    case EntityWithOptionalStringEnumProperty.type:
      self = .entityWithOptionalStringEnumProperty(try EntityWithOptionalStringEnumProperty(dictionary: dictionary))
    case EntityWithPropertyWithDefaultValue.type:
      self = .entityWithPropertyWithDefaultValue(try EntityWithPropertyWithDefaultValue(dictionary: dictionary))
    case EntityWithRawArray.type:
      self = .entityWithRawArray(try EntityWithRawArray(dictionary: dictionary))
    case EntityWithRequiredProperty.type:
      self = .entityWithRequiredProperty(try EntityWithRequiredProperty(dictionary: dictionary))
    case EntityWithSimpleProperties.type:
      self = .entityWithSimpleProperties(try EntityWithSimpleProperties(dictionary: dictionary))
    case EntityWithStringArrayProperty.type:
      self = .entityWithStringArrayProperty(try EntityWithStringArrayProperty(dictionary: dictionary))
    case EntityWithStringEnumProperty.type:
      self = .entityWithStringEnumProperty(try EntityWithStringEnumProperty(dictionary: dictionary))
    case EntityWithStringEnumPropertyWithDefaultValue.type:
      self = .entityWithStringEnumPropertyWithDefaultValue(try EntityWithStringEnumPropertyWithDefaultValue(dictionary: dictionary))
    case EntityWithoutProperties.type:
      self = .entityWithoutProperties(try EntityWithoutProperties(dictionary: dictionary))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "entity", representation: dictionary)
    }
  }
}

#if DEBUG
extension Entity: Equatable {
  public static func ==(lhs: Entity, rhs: Entity) -> Bool {
    switch (lhs, rhs) {
    case let (.entityWithArray(l), .entityWithArray(r)):
      return l == r
    case let (.entityWithArrayOfEnums(l), .entityWithArrayOfEnums(r)):
      return l == r
    case let (.entityWithArrayOfExpressions(l), .entityWithArrayOfExpressions(r)):
      return l == r
    case let (.entityWithArrayOfNestedItems(l), .entityWithArrayOfNestedItems(r)):
      return l == r
    case let (.entityWithArrayWithTransform(l), .entityWithArrayWithTransform(r)):
      return l == r
    case let (.entityWithComplexProperty(l), .entityWithComplexProperty(r)):
      return l == r
    case let (.entityWithComplexPropertyWithDefaultValue(l), .entityWithComplexPropertyWithDefaultValue(r)):
      return l == r
    case let (.entityWithEntityProperty(l), .entityWithEntityProperty(r)):
      return l == r
    case let (.entityWithOptionalComplexProperty(l), .entityWithOptionalComplexProperty(r)):
      return l == r
    case let (.entityWithOptionalProperty(l), .entityWithOptionalProperty(r)):
      return l == r
    case let (.entityWithOptionalStringEnumProperty(l), .entityWithOptionalStringEnumProperty(r)):
      return l == r
    case let (.entityWithPropertyWithDefaultValue(l), .entityWithPropertyWithDefaultValue(r)):
      return l == r
    case let (.entityWithRawArray(l), .entityWithRawArray(r)):
      return l == r
    case let (.entityWithRequiredProperty(l), .entityWithRequiredProperty(r)):
      return l == r
    case let (.entityWithSimpleProperties(l), .entityWithSimpleProperties(r)):
      return l == r
    case let (.entityWithStringArrayProperty(l), .entityWithStringArrayProperty(r)):
      return l == r
    case let (.entityWithStringEnumProperty(l), .entityWithStringEnumProperty(r)):
      return l == r
    case let (.entityWithStringEnumPropertyWithDefaultValue(l), .entityWithStringEnumPropertyWithDefaultValue(r)):
      return l == r
    case let (.entityWithoutProperties(l), .entityWithoutProperties(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension Entity: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
