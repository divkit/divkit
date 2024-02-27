import Foundation

import CommonCorePublic
import Serialization

extension Expression where T == Bool {
  func toValidSerializationValue() -> ValidSerializationValue {
    switch self {
    case let .value(value):
      value
    case let .link(link):
      link.rawValue
    }
  }
}

extension Expression where T == Double {
  func toValidSerializationValue() -> ValidSerializationValue {
    switch self {
    case let .value(value):
      value
    case let .link(link):
      link.rawValue
    }
  }
}

extension Expression where T == Int {
  func toValidSerializationValue() -> ValidSerializationValue {
    switch self {
    case let .value(value):
      value
    case let .link(link):
      link.rawValue
    }
  }
}

extension Expression where T == Color {
  func toValidSerializationValue() -> String {
    switch self {
    case let .value(value):
      value.hexString
    case let .link(link):
      link.rawValue
    }
  }
}

extension Expression where T == String {
  func toValidSerializationValue() -> String {
    switch self {
    case let .value(value):
      value
    case let .link(link):
      link.rawValue
    }
  }
}

extension Expression where T == URL {
  func toValidSerializationValue() -> String {
    switch self {
    case let .value(value):
      value.absoluteString
    case let .link(link):
      link.rawValue
    }
  }
}

extension Expression where T == [Any] {
  func toValidSerializationValue() -> ValidSerializationValue {
    switch self {
    case let .value(value):
      value
    case let .link(link):
      link.rawValue
    }
  }
}

extension Expression where T: RawRepresentable, T.RawValue == String {
  func toValidSerializationValue() -> String {
    switch self {
    case let .value(value):
      value.rawValue
    case let .link(link):
      link.rawValue
    }
  }
}
