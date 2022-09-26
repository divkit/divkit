// Copyright 2021 Yandex LLC. All rights reserved.

public protocol DeferredValue {
  associatedtype T

  var currentValue: T? { get }
  func whenLoaded(perform: @escaping (T) -> Void)
}

extension DeferredValue {
  public var observableCurrentValue: ObservableVariable<T?> {
    if let value = currentValue {
      return .constant(value)
    }

    return ObservableVariable(
      getter: { self.currentValue },
      newValues: makeReadySignal(notifyOnAdd: false, transform: { $0 })
    )
  }

  public var readySignal: Signal<T> {
    if let value = currentValue {
      return .value(value)
    }

    return makeReadySignal(notifyOnAdd: true, transform: { $0 })
  }

  @inlinable
  public func asObservableVariable<U>(fallbackUntilLoaded: U) -> T
    where T == ObservableVariable<U> {
    currentValue ?? observableCurrentValue.flatMap { $0 ?? .constant(fallbackUntilLoaded) }
  }

  @inlinable
  public func asObservableVariable<U>() -> T
    where T == ObservableVariable<U>, U: ExpressibleByNilLiteral {
    asObservableVariable(fallbackUntilLoaded: nil)
  }

  @inlinable
  public func asObservableVariable<U>() -> T
    where T == ObservableVariable<U>, U: ExpressibleByBooleanLiteral {
    asObservableVariable(fallbackUntilLoaded: false)
  }

  @inlinable
  public func asSignal<U>() -> T where T == Signal<U> {
    currentValue ?? readySignal.flatMap { $0 }
  }

  private func makeReadySignal<U>(notifyOnAdd: Bool, transform: @escaping (T) -> U) -> Signal<U> {
    Signal { [self] observer in
      if let value = currentValue {
        if notifyOnAdd {
          observer.action(transform(value))
        }
        return .empty
      }

      var subscribed = true
      whenLoaded { value in
        guard subscribed else { return }
        observer.action(transform(value))
      }

      return Disposable {
        withExtendedLifetime(self) {
          subscribed = false
        }
      }
    }
  }
}

extension Lazy: DeferredValue {}
