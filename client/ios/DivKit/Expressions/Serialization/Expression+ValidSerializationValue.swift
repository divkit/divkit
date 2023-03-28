import Foundation

import CommonCorePublic
import Serialization

extension Expression where T == Bool {
  func toValidSerializationValue() -> ValidSerializationValue {
    switch self {
    case let .value(value):
      return value
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
