// Copyright 2017 Yandex LLC. All rights reserved.

import Foundation

extension Optional {
  public enum Error: Swift.Error {
    case valueIsNil
  }

  @inlinable
  public func asArray() -> [Wrapped] {
    switch self {
    case let .some(wrapped):
      return [wrapped]
    case .none:
      return []
    }
  }

  @inlinable
  public func asNonEmptyArray() -> NonEmptyArray<Wrapped>? {
    switch self {
    case let .some(wrapped):
      return NonEmptyArray(wrapped)
    case .none:
      return nil
    }
  }

  @inlinable
  public func flatten<T>() -> T? where Wrapped == T? {
    self ?? nil
  }

  @inlinable
  public func unwrap(elseThrow error: Swift.Error = Error.valueIsNil) throws -> Wrapped {
    guard let value = self else { throw error }
    return value
  }

  @inlinable
  public func apply(_ action: (Wrapped) -> Void) {
    switch self {
    case let .some(wrapped):
      action(wrapped)
    case .none:
      return
    }
  }
}

@inlinable
public func bothNilOrBothNot<T>(_ lhs: T?, _ rhs: T?) -> Bool {
  switch (lhs, rhs) {
  case (.none, .none):
    return true
  case (.some, .some):
    return true
  case (.none, .some),
       (.some, .none):
    return false
  }
}
