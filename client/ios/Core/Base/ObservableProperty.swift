// Copyright 2016 Yandex LLC. All rights reserved.

import Foundation

@dynamicMemberLookup
@propertyWrapper
public struct ObservableProperty<T> {
  private let getter: () -> T
  private let setter: (T) -> Void
  public let newValues: Signal<T>

  private init(
    getter: @escaping () -> T,
    setter: @escaping (T) -> Void,
    newValues: Signal<T>
  ) {
    self.getter = getter
    self.setter = setter
    self.newValues = newValues
  }

  @inlinable
  public var wrappedValue: T {
    get { value }
    nonmutating set { value = newValue }
  }

  @inlinable
  public var projectedValue: ObservableVariable<T> {
    asObservableVariable()
  }

  public subscript<U>(dynamicMember path: WritableKeyPath<T, U>) -> ObservableProperty<U> {
    ObservableProperty<U>(
      getter: { self.value[keyPath: path] },
      setter: { self.value[keyPath: path] = $0 },
      newValues: newValues.map { $0[keyPath: path] }
    )
  }

  public subscript<ValueType, UnderlyingType>(
    dynamicMember path: WritableKeyPath<UnderlyingType, ValueType?>
  ) -> ObservableProperty<ValueType?> where T == UnderlyingType? {
    ObservableProperty<ValueType?>(
      getter: { self.value?[keyPath: path] },
      setter: { self.value?[keyPath: path] = $0 },
      newValues: newValues.map { $0?[keyPath: path] }
    )
  }
}

extension ObservableProperty {
  public var value: T {
    get { getter() }
    nonmutating set { setter(newValue) }
  }

  public func asVariable() -> Variable<T> {
    Variable(getter)
  }

  public func asObservableVariable() -> ObservableVariable<T> {
    ObservableVariable(getter: getter, newValues: newValues)
  }

  public func asProperty() -> Property<T> {
    Property(getter: getter, setter: setter)
  }

  public init(initialValue: T) {
    var value = initialValue
    self.init(
      getter: { value },
      privateSetter: { value = $0 }
    )
  }

  @inlinable
  public init(wrappedValue: T) {
    self.init(initialValue: wrappedValue)
  }

  @inlinable
  public init<U>() where T == U? {
    self.init(initialValue: nil)
  }

  public var currentAndNewValues: Signal<T> {
    newValues.startWith(getter)
  }

  public func bimap<U>(get: @escaping (T) -> U, set: @escaping (U) -> T) -> ObservableProperty<U> {
    ObservableProperty<U>(
      getter: compose(get, after: getter),
      setter: compose(setter, after: set),
      newValues: newValues.map(get)
    )
  }

  @inlinable
  public func withDefault<U>(_ defaultValue: U) -> ObservableProperty<U> where T == U? {
    bimap(get: { $0 ?? defaultValue }, set: { $0 })
  }
}

extension ObservableProperty {
  init(
    getter: @escaping () -> T,
    privateSetter: @escaping (T) -> Void
  ) {
    let pipe = SignalPipe<T>()
    self.init(getter: getter, setter: {
      privateSetter($0)
      pipe.send($0)
    }, newValues: pipe.signal)
  }
}

extension ObservableProperty {
  public func assertingMainThread() -> ObservableProperty {
    ObservableProperty(
      getter: { [getter] in
        Thread.assertIsMain()
        return getter()
      },
      setter: { [setter] in
        Thread.assertIsMain()
        setter($0)
      },
      newValues: newValues.assertingMainThread()
    )
  }

  public static func keyPath<Root: NSObject>(
    _ keyPath: ReferenceWritableKeyPath<Root, T>,
    onNSObject object: Root
  ) -> ObservableProperty {
    let newValues = Signal.changesForKeyPath(keyPath, onNSObject: object, options: .new)
      .compactMap { $0.change.newValue }
    return ObservableProperty(
      getter: { object[keyPath: keyPath] },
      setter: { object[keyPath: keyPath] = $0 },
      newValues: newValues
    )
  }

  public func retaining<U>(_ object: U) -> Self {
    Self(
      getter: {
        withExtendedLifetime(object) {
          self.getter()
        }
      },
      setter: { newValue in
        withExtendedLifetime(object) {
          self.setter(newValue)
        }
      },
      newValues: newValues.retaining(object: object)
    )
  }
}

extension ObservableProperty {
  public func descend<Key, Value>(to key: Key) -> ObservableProperty<Value?>
    where Key: Hashable, T == [Key: Value] {
    ObservableProperty<Value?>(
      getter: { self.value[key] },
      setter: { self.value[key] = $0 },
      newValues: newValues.map { $0[key] }
    )
  }
}

extension ObservableProperty where T: ExpressibleByArrayLiteral {
  public init() {
    self.init(initialValue: [])
  }
}

extension ObservableProperty where T: ExpressibleByNilLiteral {
  public init() {
    self.init(initialValue: nil)
  }
}

extension ObservableProperty where T: ExpressibleByDictionaryLiteral {
  public init() {
    self.init(initialValue: [:])
  }
}

extension ObservableProperty where T: ExpressibleByStringLiteral {
  public init() {
    self.init(initialValue: "")
  }
}

extension ObservableProperty where T: ExpressibleByIntegerLiteral {
  public init() {
    self.init(initialValue: 0)
  }
}

extension ObservableProperty where T: ExpressibleByBooleanLiteral {
  public init() {
    self.init(initialValue: false)
  }
}
