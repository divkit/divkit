// Copyright 2021 Yandex LLC. All rights reserved.

import Foundation

extension FloatingPoint where Self.Stride: ExpressibleByFloatLiteral {
  public func isApproximatelyGreaterThan(
    _ number: Self,
    withAccuracy accuracy: Self.Stride
  ) -> Bool {
    number.distance(to: self) > accuracy
  }

  public func isApproximatelyGreaterThan(_ number: Self) -> Bool {
    isApproximatelyGreaterThan(number, withAccuracy: defaultAccuracy())
  }

  public func isApproximatelyGreaterOrEqualThan(
    _ number: Self,
    withAccuracy accuracy: Self.Stride
  ) -> Bool {
    !isApproximatelyLessThan(number, withAccuracy: accuracy)
  }

  public func isApproximatelyGreaterOrEqualThan(_ number: Self) -> Bool {
    !isApproximatelyLessThan(number, withAccuracy: defaultAccuracy())
  }

  public func isApproximatelyLessThan(_ number: Self, withAccuracy accuracy: Self.Stride) -> Bool {
    number.isApproximatelyGreaterThan(self, withAccuracy: accuracy)
  }

  public func isApproximatelyLessThan(_ number: Self) -> Bool {
    number.isApproximatelyGreaterThan(self, withAccuracy: defaultAccuracy())
  }

  public func isApproximatelyLessOrEqualThan(
    _ number: Self,
    withAccuracy accuracy: Self.Stride
  ) -> Bool {
    !isApproximatelyGreaterThan(number, withAccuracy: accuracy)
  }

  public func isApproximatelyLessOrEqualThan(_ number: Self) -> Bool {
    !isApproximatelyGreaterThan(number, withAccuracy: defaultAccuracy())
  }

  public func isApproximatelyEqualTo(_ number: Self, withAccuracy accuracy: Self.Stride) -> Bool {
    !isApproximatelyGreaterThan(number, withAccuracy: accuracy) &&
      !isApproximatelyLessThan(number, withAccuracy: accuracy)
  }

  public func isApproximatelyEqualTo(_ number: Self) -> Bool {
    !isApproximatelyGreaterThan(number, withAccuracy: defaultAccuracy()) &&
      !isApproximatelyLessThan(number, withAccuracy: defaultAccuracy())
  }

  public func isApproximatelyNotEqualTo(
    _ number: Self,
    withAccuracy accuracy: Self.Stride
  ) -> Bool {
    !isApproximatelyEqualTo(number, withAccuracy: accuracy)
  }

  public func isApproximatelyNotEqualTo(_ number: Self) -> Bool {
    !isApproximatelyEqualTo(number, withAccuracy: defaultAccuracy())
  }
}

private func defaultAccuracy<Type: ExpressibleByFloatLiteral>() -> Type { 1e-8 }
