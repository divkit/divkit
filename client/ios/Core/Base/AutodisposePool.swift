// Copyright 2018 Yandex LLC. All rights reserved.
public final class AutodisposePool {
  private var disposables = [Disposable]()

  public init() {}

  deinit {
    drain()
  }

  public func add(_ disposable: Disposable) {
    disposables.append(disposable)
  }

  public func drain() {
    for disposable in disposables {
      disposable.dispose()
    }
    disposables.removeAll()
  }
}

extension Disposable {
  @discardableResult
  public func dispose(in pool: AutodisposePool) -> Disposable {
    pool.add(self)
    return self
  }
}

extension Sequence where Element == Disposable {
  public func dispose(in pool: AutodisposePool) {
    forEach(pool.add)
  }
}

extension AutodisposePool {
  @inlinable
  @discardableResult
  public func retain<T>(_ value: T) -> T {
    add(Disposable { withExtendedLifetime(value) {} })
    return value
  }
}
