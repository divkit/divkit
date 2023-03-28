// Copyright 2018 Yandex LLC. All rights reserved.

import Foundation

/// Wraps instance function with self captured weak.
/// Example of use: `let perform = weakify(self, FooClass.perform)`
/// Semantically the same as `let perform = { [weak self] in self?.perform() }`
public func weakify<T: AnyObject>(_ self: T, in f: @escaping (T) -> () -> Void) -> () -> Void {
  WeakWrapper(`self`, f).invoke
}

@inlinable
public func weakify<T: AnyObject, U>(
  _ self_: T,
  in f: @escaping (T) -> (U) -> Void
) -> (U) -> Void {
  { [weak self_] u in
    guard let p = self_ else { return }
    f(p)(u)
  }
}

@inlinable
public func weakify<T: AnyObject>(_ value: T) -> Variable<T?> {
  Variable { [weak weakValue = value] in weakValue }
}

private struct WeakWrapper<T: AnyObject> {
  private weak var p: T?
  private let f: (T) -> () -> Void

  init(_ p: T, _ f: @escaping (T) -> () -> Void) {
    self.p = p
    self.f = f
  }

  func invoke() {
    guard let p = self.p else { return }
    f(p)()
  }
}
