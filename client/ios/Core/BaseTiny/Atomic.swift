// Copyright 2018 Yandex LLC. All rights reserved.

import Foundation

public final class Atomic<T> {
  private var unsafeValue: T
  private let queue: DispatchQueue

  public init(initialValue: T, accessQueue: DispatchQueue) {
    unsafeValue = initialValue
    queue = accessQueue
  }

  @inlinable
  public convenience init(initialValue: T, label: String) {
    let accessQueue = DispatchQueue(label: label, attributes: [.concurrent])
    self.init(initialValue: initialValue, accessQueue: accessQueue)
  }

  public func accessRead<U>(_ block: (T) throws -> U) rethrows -> U {
    try queue.sync {
      try block(unsafeValue)
    }
  }

  public func accessWrite<U>(_ block: (inout T) throws -> U) rethrows -> U {
    try queue.sync(flags: .barrier) {
      try block(&unsafeValue)
    }
  }
}
