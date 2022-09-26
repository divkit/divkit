// Copyright 2016 Yandex LLC. All rights reserved.

import Foundation

@dynamicMemberLookup
@propertyWrapper
public struct Variable<T> {
  private let getter: () -> T

  public var wrappedValue: T { getter() }

  public init(_ getter: @escaping () -> T) {
    self.getter = getter
  }
}

extension Variable {
  public var value: T {
    getter()
  }

  @inlinable
  public subscript<U>(dynamicMember path: KeyPath<T, U>) -> Variable<U> {
    Variable<U> { self.value[keyPath: path] }
  }

  @inlinable
  public subscript<ValueType, UnderlyingType>(
    dynamicMember path: KeyPath<UnderlyingType, ValueType>
  ) -> Variable<ValueType?> where T == UnderlyingType? {
    Variable<ValueType?> { self.value?[keyPath: path] }
  }

  @inlinable
  public subscript<ValueType, UnderlyingType>(
    dynamicMember path: KeyPath<UnderlyingType, ValueType?>
  ) -> Variable<ValueType?> where T == UnderlyingType? {
    Variable<ValueType?> { self.value?[keyPath: path] }
  }

  @inlinable
  public static func constant(_ value: T) -> Variable {
    Variable { value }
  }

  @inlinable
  public init<U>(_ object: U, keyPath: KeyPath<U, T>) {
    self.init { object[keyPath: keyPath] }
  }

  @inlinable
  public init<U>(rewrap other: Variable<U>?) where T == U? {
    self.init { other?.value }
  }

  public func map<U>(_ transform: @escaping (T) -> U) -> Variable<U> {
    Variable<U>(compose(transform, after: getter))
  }

  public func flatMap<U>(
    _ transform: @escaping (T) -> Variable<U>
  ) -> Variable<U> {
    Variable<U>(compose(\.value, after: compose(transform, after: getter)))
  }
}

extension Variable {
  public func assertingMainThread() -> Variable {
    Variable { [getter] in
      Thread.assertIsMain()
      return getter()
    }
  }
}

@inlinable
public func zip<T, U>(_ lhs: Variable<T>, _ rhs: Variable<U>) -> Variable<(T, U)> {
  .init { (lhs.value, rhs.value) }
}

@inlinable
public func zip3<T1, T2, T3>(
  _ v1: Variable<T1>, _ v2: Variable<T2>, _ v3: Variable<T3>
) -> Variable<(T1, T2, T3)> {
  Variable { (v1.value, v2.value, v3.value) }
}

@inlinable
public func zip4<T1, T2, T3, T4>(
  _ v1: Variable<T1>, _ v2: Variable<T2>, _ v3: Variable<T3>, _ v4: Variable<T4>
) -> Variable<(T1, T2, T3, T4)> {
  Variable { (v1.value, v2.value, v3.value, v4.value) }
}
