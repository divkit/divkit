import Foundation

import CommonCore
import Serialization

public enum Expression<T> {
  case value(T)
  case link(ExpressionLink<T>)
}

extension Expression {
  public var rawValue: T? {
    switch self {
    case let .value(value):
      return value
    case .link:
      return nil
    }
  }
}

extension Expression: Equatable where T: Equatable {
  public static func ==(lhs: Self, rhs: Self) -> Bool {
    switch (lhs, rhs) {
    case let (.value(lValue), .value(rValue)):
      return lValue == rValue
    case let (.link(lValue), .link(rValue)):
      return lValue == rValue
    case (.value, _), (.link, _):
      return false
    }
  }
}

extension Expression where T == Bool {
  func toValidSerializationValue() -> ValidSerializationValue {
    switch self {
    case let .value(value):
      return value ? 1 : 0
    case let .link(link):
      return link.rawValue
    }
  }
}

extension Expression where T == Double {
  func toValidSerializationValue() -> ValidSerializationValue {
    switch self {
    case let .value(value):
      return value
    case let .link(link):
      return link.rawValue
    }
  }
}

extension Expression where T == Int {
  func toValidSerializationValue() -> ValidSerializationValue {
    switch self {
    case let .value(value):
      return value
    case let .link(link):
      return link.rawValue
    }
  }
}

extension Expression where T == Color {
  func toValidSerializationValue() -> String {
    switch self {
    case let .value(value):
      return value.hexString
    case let .link(link):
      return link.rawValue
    }
  }
}

extension Expression where T == String {
  func toValidSerializationValue() -> String {
    switch self {
    case let .value(value):
      return value
    case let .link(link):
      return link.rawValue
    }
  }
}

extension Expression where T == CFString {
  func toValidSerializationValue() -> String {
    switch self {
    case let .value(value):
      return String(value)
    case let .link(link):
      return link.rawValue
    }
  }
}

extension Expression where T == URL {
  func toValidSerializationValue() -> String {
    switch self {
    case let .value(value):
      return value.absoluteString
    case let .link(link):
      return link.rawValue
    }
  }
}

extension Expression where T: RawRepresentable, T.RawValue == String {
  func toValidSerializationValue() -> String {
    switch self {
    case let .value(value):
      return value.rawValue
    case let .link(link):
      return link.rawValue
    }
  }
}
