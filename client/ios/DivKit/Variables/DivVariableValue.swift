import Foundation
import VGSL

@frozen
public enum DivVariableValue: Hashable {
  case string(String)
  case number(Double)
  case integer(Int)
  case bool(Bool)
  case color(Color)
  case url(URL)
  case dict(DivDictionary)
  case array(DivArray)

  init?(_ value: some Any) {
    switch value {
    case let value as String:
      self = .string(value)
    case let value as Double:
      self = .number(value)
    case let value as Int:
      self = .integer(value)
    case let value as Bool:
      self = .bool(value)
    case let value as Color:
      self = .color(value)
    case let value as URL:
      self = .url(value)
    case let value as DivDictionary:
      self = .dict(value)
    case let value as DivArray:
      self = .array(value)
    default:
      DivKitLogger.error("Unsupported variable value: \(value)")
      return nil
    }
  }

  @inlinable
  public func typedValue<T>() -> T? {
    switch self {
    case let .string(value):
      return value as? T
    case let .number(value):
      return value as? T
    case let .integer(value):
      return value as? T
    case let .bool(value):
      return value as? T
    case let .color(value):
      return value as? T
    case let .url(value):
      return value as? T
    case let .dict(value):
      return value as? T
    case let .array(value):
      return value as? T
    }
  }

}
