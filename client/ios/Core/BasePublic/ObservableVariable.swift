// Copyright 2018 Yandex LLC. All rights reserved.
import Foundation

@dynamicMemberLookup
@propertyWrapper
public struct ObservableVariable<T> {
  private let getter: () -> T
  public let newValues: Signal<T>

  // Obsolete. Do not use in new code.
  // For class variables consider:
  //    private let observableXXXXStorage = ObservableProperty<T>
  //    var observableXXXX { observableXXXXStorage.asObservableVariable() }
  // For adapters consider:
  //     init(initialValue: T, newValues: Signal<T>)
  public init(getter: @escaping () -> T, newValues: Signal<T>) {
    self.getter = getter
    self.newValues = newValues
  }

  public var wrappedValue: T { getter() }
  public var projectedValue: Self { self }

  @inlinable
  public subscript<U>(dynamicMember path: KeyPath<T, U>) -> ObservableVariable<U> {
    map { $0[keyPath: path] }
  }

  @inlinable
  public subscript<ValueType, UnderlyingType>(
    dynamicMember path: KeyPath<UnderlyingType, ValueType>
  ) -> ObservableVariable<ValueType?> where T == UnderlyingType? {
    map { $0?[keyPath: path] }
  }

  @inlinable
  public subscript<ValueType, UnderlyingType>(
    dynamicMember path: KeyPath<UnderlyingType, ValueType?>
  ) -> ObservableVariable<ValueType?> where T == UnderlyingType? {
    map { $0?[keyPath: path] }
  }
}

extension ObservableVariable {
  public var value: T {
    getter()
  }

  public func asVariable() -> Variable<T> {
    Variable(getter)
  }

  @inlinable
  public static func constant(_ value: T) -> ObservableVariable {
    ObservableVariable(getter: { value }, newValues: .empty)
  }

  public var currentAndNewValues: Signal<T> {
    newValues.startWith(getter)
  }

  public var oldAndNewValues: Signal<(old: T?, new: T)> {
    var oldValue = self.getter()
    return Signal<(old: T?, new: T)>(addObserver: { observer in
      self.newValues.addObserver { newValue in
        observer.action((old: oldValue, new: newValue))
        oldValue = newValue
      }
    }).startWith { (old: nil, new: self.getter()) }
  }

  public func map<U>(_ transform: @escaping (T) -> U) -> ObservableVariable<U> {
    ObservableVariable<U>(
      getter: compose(transform, after: getter),
      newValues: newValues.map(transform)
    )
  }

  @inlinable
  public func cachedMap<U>(_ transform: @escaping (T) -> U) -> ObservableVariable<U> {
    var current = transform(value)

    let pipe = SignalPipe<U>()
    let token = newValues.addObserver {
      current = transform($0)
      pipe.send(current)
    }
    return ObservableVariable<U>(
      getter: { current },
      newValues: Signal(addObserver: { observer in
        let outerToken = pipe.signal.addObserver(observer)
        return Disposable {
          withExtendedLifetime(token) { outerToken.dispose() }
        }
      })
    )
  }

  public func flatMap<U>(_ transform: @escaping (T) -> ObservableVariable<U>)
    -> ObservableVariable<U> {
    let newValues = self.newValues
      .map { transform($0).currentAndNewValues }
      .startWith { [getter] in transform(getter()).newValues }
      .flatMap { $0 }
    return ObservableVariable<U>(
      getter: { [getter] in transform(getter()).getter() },
      newValues: newValues
    )
  }

  @inlinable
  public func compactMap<U>(
    valueIfNil: @autoclosure () -> U,
    _ transform: @escaping (T) -> U?
  ) -> ObservableVariable<U> {
    ObservableVariable<U>(
      initialValue: transform(value) ?? valueIfNil(),
      newValues: newValues.compactMap(transform)
    )
  }

  public func skipRepeats(areEqual: @escaping (T, T) -> Bool) -> ObservableVariable {
    ObservableVariable(
      getter: getter,
      newValues: newValues.skipRepeats(areEqual: areEqual, initialValue: getter)
    )
  }

  @inlinable
  public var lazy: ObservableVariable<Lazy<T>> {
    ObservableVariable<Lazy<T>>(
      getter: { Lazy(getter: { self.value }) },
      newValues: newValues.map { Lazy(value: $0) }
    )
  }

  @inlinable
  public func involving(_ signals: Signal<T>...) -> Self {
    involving(signals)
  }

  @inlinable
  public func involving(_ signals: [Signal<T>]) -> Self {
    Self(initialValue: value, newValues: .merge([newValues] + signals))
  }
}

extension ObservableVariable where T: Equatable {
  @inlinable
  public func skipRepeats() -> ObservableVariable {
    skipRepeats(areEqual: ==)
  }
}

extension ObservableVariable {
  public func assertingMainThread() -> ObservableVariable {
    ObservableVariable(
      getter: { [getter] in
        Thread.assertIsMain()
        return getter()
      },
      newValues: newValues.assertingMainThread()
    )
  }

  @inlinable
  public static func keyPath<Root: NSObject>(
    _ keyPath: KeyPath<Root, T>,
    onNSObject object: Root
  ) -> ObservableVariable {
    let newValues = Signal.changesForKeyPath(keyPath, onNSObject: object, options: .new)
      .compactMap { $0.change.newValue }
    return ObservableVariable(
      getter: { object[keyPath: keyPath] },
      newValues: newValues
    )
  }
}

extension ObservableVariable {
  @inlinable
  public static func `nil`<U>() -> ObservableVariable where T == U? {
    ObservableVariable(getter: { nil }, newValues: .empty)
  }
}

extension ObservableVariable: ExpressibleByBooleanLiteral where T == Bool {
  public init(booleanLiteral value: Bool) {
    self = .constant(value)
  }
}

extension ObservableVariable {
  /// This initializer stores the initial and intermediate values in the underlying storage
  /// Lifetime managment:
  /// `newValues: Signal<T>` is not retained
  /// `Disposable` of `newValues.addObserver()` is retained
  @inlinable
  public init(initialValue: T, newValues: Signal<T>) {
    var value = initialValue
    let pipe = SignalPipe<T>()
    let subscription = newValues.addObserver {
      value = $0
      pipe.send($0)
    }

    // keeps `subscription` alive as long as the `getter` is alive
    // to keep underlying `value` being updated regardless of losing
    // the `ObservableVariable.newValues`
    let getter: () -> T = {
      withExtendedLifetime(subscription) { value }
    }

    // keeps `subscription` alive as long as the `newValues` signal is alive
    // to keep receiving update events regardless of losing
    // the `ObservableVariable.getter`
    let newValues = pipe.signal.retaining(object: subscription)

    self.init(
      getter: getter,
      newValues: newValues
    )
  }
}

extension ObservableVariable {
  @inlinable
  public func retaining<U>(_ object: U) -> Self {
    Self(initialValue: value, newValues: newValues.retaining(object: object))
  }

  // if we draw analogy with array, this method makes something like this
  // [nil, nil, 1, 2, 3, nil, nil, 4, 5, nil] -> [nil, [1, 2, 3], nil, [4, 5], nil]
  @inlinable
  public func collapseNils<U>() -> ObservableVariable<ObservableVariable<U>?> where T == U? {
    @ObservableProperty
    var outer: ObservableVariable<U>? = nil

    var inner: ObservableProperty<U>?

    let subscription = currentAndNewValues.addObserver { state in
      guard let state = state else {
        if inner != nil {
          _outer.wrappedValue = nil
          inner = nil
        }
        return
      }
      if let inner = inner {
        inner.value = state
      } else {
        let localInner = ObservableProperty(initialValue: state)
        _outer.wrappedValue = localInner.asObservableVariable()
        inner = localInner
      }
    }

    return $outer.retaining(subscription)
  }
}

extension ObservableVariable {
  @inlinable
  public func multiplexed() -> (variable: Self, afterUpdateSignal: Signal<T>) {
    var value = value
    let afterUpdatePipe = SignalPipe<T>()
    let pipe = SignalPipe<T>()

    let subscription = newValues.addObserver {
      value = $0
      pipe.send($0)
      afterUpdatePipe.send($0)
    }

    let modelVariable = ObservableVariable(initialValue: value, newValues: pipe.signal)
    return (
      variable: modelVariable.retaining(subscription),
      afterUpdateSignal: afterUpdatePipe.signal.retaining(object: subscription)
    )
  }
}
