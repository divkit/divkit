// Copyright 2018 Yandex LLC. All rights reserved.

import Foundation

public final class Atomic<T> {
  private var unsafeValue: T
  private let lock = RWLock()

  public init(initialValue: T) {
    unsafeValue = initialValue
  }

  public func accessRead<U>(_ block: (T) throws -> U) rethrows -> U {
    try lock.read {
      try block(unsafeValue)
    }
  }

  public func accessWrite<U>(_ block: (inout T) throws -> U) rethrows -> U {
    try lock.write {
      try block(&unsafeValue)
    }
  }
}
