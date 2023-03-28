// Copyright 2018 Yandex LLC. All rights reserved.

import Foundation

@dynamicMemberLookup
public final class Lazy<T> {
  public typealias Action = (T) -> Void
  public typealias Getter = () -> T

  private enum State {
    case willLoad(getter: Getter, deferredActions: [Action])
    case loading(deferredActions: [Action])
    case loaded(value: T)

    var value: T? {
      switch self {
      case .willLoad, .loading:
        return nil
      case let .loaded(value):
        return value
      }
    }
  }

  private let shouldAssertOnMainThread: Bool
  private var state: State

  @inlinable
  public subscript<U>(dynamicMember path: KeyPath<T, U>) -> Lazy<U> {
    map { $0[keyPath: path] }
  }

  public var currentValue: T? {
    state.value
  }

  public var value: T {
    ensureIsValidThread()
    switch state {
    case .willLoad:
      load()
    case .loading:
      assertionFailure()
    case .loaded:
      break
    }
    return state.value!
  }

  private init(_ getter: @escaping Getter, shouldAssertOnMainThread: Bool) {
    self.state = .willLoad(getter: getter, deferredActions: [])
    self.shouldAssertOnMainThread = shouldAssertOnMainThread
  }

  public convenience init(getter: @escaping Getter) {
    self.init(getter, shouldAssertOnMainThread: false)
  }

  public convenience init(onMainThreadGetter getter: @escaping Getter) {
    self.init(getter, shouldAssertOnMainThread: true)
  }

  public convenience init(value: T) {
    self.init({ value }, shouldAssertOnMainThread: false)
  }

  public init(loaded value: T) {
    self.state = .loaded(value: value)
    shouldAssertOnMainThread = false
  }

  public func whenLoaded(perform action: @escaping Action) {
    ensureIsValidThread()
    switch state {
    case let .willLoad(getter, deferredActions):
      state = .willLoad(getter: getter, deferredActions: deferredActions + [action])
    case let .loading(deferredActions):
      state = .loading(deferredActions: deferredActions + [action])
    case let .loaded(value):
      action(value)
    }
  }

  private func load() {
    ensureIsValidThread()
    switch state {
    case let .willLoad(getter, deferredActions):
      state = .loading(deferredActions: deferredActions)
      let value = getter()
      guard case let .loading(deferredActions) = state else {
        return assertionFailure()
      }
      state = .loaded(value: value)
      deferredActions.forEach { $0(value) }
    case .loading, .loaded:
      assertionFailure()
    }
  }

  public func ensureIsLoaded() {
    ensureIsValidThread()
    switch state {
    case .willLoad:
      load()
    case .loading, .loaded:
      break
    }
  }

  public func map<U>(_ transform: @escaping (T) -> U) -> Lazy<U> {
    let result = Lazy<U>(
      { transform(self.value) },
      shouldAssertOnMainThread: shouldAssertOnMainThread
    )
    whenLoaded { [weak result] _ in
      result?.ensureIsLoaded()
    }
    return result
  }

  private func ensureIsValidThread() {
    if shouldAssertOnMainThread {
      Thread.assertIsMain()
    }
  }
}
