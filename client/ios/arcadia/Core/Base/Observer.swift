// Copyright 2018 Yandex LLC. All rights reserved.
public struct Observer<T> {
  public let action: (T) -> Void

  public init(action: @escaping (T) -> Void) {
    self.action = action
  }
}

extension Observer {
  public func contramap<U>(_ transform: @escaping (U) -> T) -> Observer<U> {
    Observer<U>(action: compose(action, after: transform))
  }

  public func filter(_ predicate: @escaping (T) -> Bool) -> Observer {
    Observer(action: { t in
      if predicate(t) {
        self.action(t)
      }
    })
  }

  public func compactContramap<U>(_ transform: @escaping (U) -> T?) -> Observer<U> {
    Observer<U>(action: { u in
      if let t = transform(u) {
        self.action(t)
      }
    })
  }

  public func performingBeforeEachValue(_ sideEffect: @escaping (T) -> Void) -> Observer {
    Observer(action: { t in
      sideEffect(t)
      self.action(t)
    })
  }

  public func skipRepeats(
    areEqual: @escaping (T, T) -> Bool,
    initialValue: T?
  ) -> Observer {
    var latest = initialValue
    return Observer(action: { t in
      if let latest = latest, areEqual(latest, t) {
        return
      }
      latest = t
      self.action(t)
    })
  }

  public func skipUntil(_ predicate: @escaping (T) -> Bool) -> Observer {
    var shouldSkip = true
    return Observer(action: { t in
      if !predicate(t) {
        shouldSkip = false
      }
      guard !shouldSkip else { return }
      self.action(t)
    })
  }

  public func takeUntil(_ predicate: @escaping (T) -> Bool) -> Observer {
    var shouldTake = true
    return Observer(action: { t in
      if !predicate(t) {
        shouldTake = false
      }
      guard shouldTake else { return }
      self.action(t)
    })
  }
}

extension Observer where T: Equatable {
  public func skipRepeats(initialValue: T?) -> Observer {
    skipRepeats(areEqual: ==, initialValue: initialValue)
  }
}

public func compose<A, B, C>(_ f: @escaping (B) -> C, after g: @escaping (A) -> B) -> (A) -> C {
  { a in f(g(a)) }
}

public func compose<B, C>(_ f: @escaping (B) -> C, after g: @escaping () -> B) -> () -> C {
  { f(g()) }
}
