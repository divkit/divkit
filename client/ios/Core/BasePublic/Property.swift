// Copyright 2016 Yandex LLC. All rights reserved.

import Foundation

///
/// NB: if you use dynamic member lookup, consider following case:
///
/// ```
/// struct Struct {
///   var field: Int
/// }
///
/// let a = Property<Struct?>()
/// let b = a.field
/// a.value = Struct(value: 0)
/// b.value = nil
/// ```
///
/// What value should be in `a`?
/// Possible options:
/// 1. `Struct(value: 0)`
/// 2. `nil`
///
/// Due to ambiguity of this case, it is not supported (yet?)
///

@dynamicMemberLookup
@propertyWrapper
public struct Property<T> {
  private let getter: () -> T
  private let setter: (T) -> Void

  public var wrappedValue: T {
    get { getter() }
    nonmutating set { setter(newValue) }
  }

  @inlinable
  public var projectedValue: Variable<T> { asVariable() }

  public init(getter: @escaping () -> T, setter: @escaping (T) -> Void) {
    self.getter = getter
    self.setter = setter
  }

  @inlinable
  public init(wrappedValue: T) {
    self.init(initialValue: wrappedValue)
  }
}

extension Property {
  public var value: T {
    get { getter() }
    nonmutating set { setter(newValue) }
  }

  @inlinable
  public subscript<U>(dynamicMember path: WritableKeyPath<T, U>) -> Property<U> {
    Property<U>(
      getter: { self.value[keyPath: path] },
      setter: { self.value[keyPath: path] = $0 }
    )
  }

  @inlinable
  public subscript<ValueType, UnderlyingType>(
    dynamicMember path: WritableKeyPath<UnderlyingType, ValueType?>
  ) -> Property<ValueType?> where T == UnderlyingType? {
    Property<ValueType?>(
      getter: { self.value?[keyPath: path] },
      setter: { self.value?[keyPath: path] = $0 }
    )
  }

  public func asVariable() -> Variable<T> {
    Variable(getter)
  }

  @inlinable
  public init(initialValue: T) {
    var value = initialValue
    self.init(
      getter: { value },
      setter: { value = $0 }
    )
  }

  @inlinable
  public init<U>() where T == U? {
    self.init(initialValue: nil)
  }

  @inlinable
  public init<U>(_ object: U, refKeyPath: ReferenceWritableKeyPath<U, T>) {
    self.init(
      getter: { object[keyPath: refKeyPath] },
      setter: { object[keyPath: refKeyPath] = $0 }
    )
  }

  public func bimap<U>(get: @escaping (T) -> U, set: @escaping (U) -> T) -> Property<U> {
    Property<U>(
      getter: compose(get, after: getter),
      setter: compose(setter, after: set)
    )
  }

  public func bimap<U>(
    get: @escaping (T) -> U,
    modify: @escaping (inout T, U) -> Void
  ) -> Property<U> {
    Property<U>(
      getter: compose(get, after: getter),
      setter: { [getter, setter] value in setter(modified(getter()) { modify(&$0, value) }) }
    )
  }

  @inlinable
  public func withDefault<U>(_ defaultValue: U) -> Property<U> where T == U? {
    bimap(get: { $0 ?? defaultValue }, set: { $0 })
  }

  public func unsafeMakeObservable() -> ObservableProperty<T> {
    ObservableProperty(getter: getter, privateSetter: setter)
  }
}

extension Property {
  public func assertingMainThread() -> Property {
    Property(
      getter: { [getter] in
        Thread.assertIsMain()
        return getter()
      },
      setter: { [setter] in
        Thread.assertIsMain()
        setter($0)
      }
    )
  }

  @inlinable
  public static func weak<U>(
    _ initialValue: U? = nil
  ) -> Self where T == U?, U: AnyObject {
    weak var value: U? = initialValue
    return .init(getter: { value }, setter: { value = $0 })
  }
}

extension Property {
  @inlinable
  public func descend<Key, Value>(to key: Key) -> Property<Value?>
    where Key: Hashable, T == [Key: Value] {
    Property<Value?>(
      getter: { self.value[key] },
      setter: { self.value[key] = $0 }
    )
  }
}

extension Property where T: ExpressibleByArrayLiteral {
  public init() {
    self.init(initialValue: [])
  }
}

extension Property where T: ExpressibleByNilLiteral {
  public init() {
    self.init(initialValue: nil)
  }
}

extension Property where T: ExpressibleByDictionaryLiteral {
  public init() {
    self.init(initialValue: [:])
  }
}

extension Property where T: ExpressibleByStringLiteral {
  public init() {
    self.init(initialValue: "")
  }
}

extension Property where T: ExpressibleByIntegerLiteral {
  public init() {
    self.init(initialValue: 0)
  }
}

extension Property where T: ExpressibleByBooleanLiteral {
  public init() {
    self.init(initialValue: false)
  }
}

extension Property: ExpressibleByBooleanLiteral where T == Bool {
  public init(booleanLiteral value: Bool) {
    self.init(initialValue: value)
  }
}

extension Property: ExpressibleByIntegerLiteral where T == Int {
  public init(integerLiteral value: Int) {
    self.init(initialValue: value)
  }
}

extension Property: ExpressibleByUnicodeScalarLiteral where T == String {}
extension Property: ExpressibleByExtendedGraphemeClusterLiteral where T == String {}

extension Property: ExpressibleByStringLiteral where T == String {
  public init(stringLiteral value: String) {
    self.init(initialValue: value)
  }
}
