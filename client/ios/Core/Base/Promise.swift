// Copyright 2019 Yandex LLC. All rights reserved.

import Foundation

public final class Promise<T> {
  public let future: Future<T>

  private let feed: (T) -> Void
  private let shouldAssertOnMainThread: Bool

  public init(shouldAssertOnMainThread: Bool = false) {
    (future, feed) = Future<T>.create()
    self.shouldAssertOnMainThread = shouldAssertOnMainThread
  }

  public func resolve(_ payload: T) {
    if shouldAssertOnMainThread {
      Thread.assertIsMain()
    }
    feed(payload)
  }
}

extension Promise where T == Void {
  public func resolve() {
    resolve(())
  }
}
