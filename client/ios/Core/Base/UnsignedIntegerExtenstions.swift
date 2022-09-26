// Copyright 2019 Yandex LLC. All rights reserved.

extension UnsignedInteger where Self: FixedWidthInteger {
  /// Creates a new instance from the given integer, if it can be represented
  /// exactly. Otherwise `InvalidArgumentError` will be thrown.
  @inlinable
  public init<T: BinaryInteger>(value: T) throws {
    guard let converted = Self(exactly: value) else {
      throw InvalidArgumentError(name: "value", value: value)
    }
    self = converted
  }
}
