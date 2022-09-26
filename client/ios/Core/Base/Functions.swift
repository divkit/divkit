// Copyright 2018 Yandex LLC. All rights reserved.

import Foundation

@inlinable
public func assertedCast<T, U>(_ value: T) -> U? {
  assert(value is U)
  return value as? U
}

@inlinable
public func assertedCast<T, U>(_ value: T?) -> U? {
  assert(value == nil || value is U)
  return value as? U
}

@inlinable
public func assertedCast<T, U>(_ value: T?, fallback: U) -> U {
  assertedCast(value) ?? fallback
}

@inlinable
public func modified<T>(_ value: T, _ modificator: (inout T) -> Void) -> T {
  var result = value
  modificator(&result)
  return result
}

@inlinable
public func identity<T>(_ value: T) -> T {
  value
}

@inlinable
public func traceId(_ x: AnyObject) -> UInt64 {
  UInt64(UInt(bitPattern: Unmanaged.passUnretained(x).toOpaque()))
}

@inlinable
public func autoReset<T, U>(
  _ value: inout T, _ newValue: T, _ code: () throws -> U
) rethrows -> U {
  let oldValue = value
  value = newValue
  defer {
    value = oldValue
  }
  return try code()
}

extension BinaryFloatingPoint {
  // https://github.com/apple/swift-evolution/blob/master/proposals/0259-approximately-equal.md
  @inlinable
  public func isAlmostEqual(
    _ other: Self,
    maxRelDev: Self = Self.ulpOfOne.squareRoot()
  ) -> Bool {
    precondition(maxRelDev >= Self.zero)
    precondition(isFinite && other.isFinite)
    guard self != other else { return true }
    guard !isZero else { return other.isAlmostZero() }
    guard !other.isZero else { return isAlmostZero() }
    let scale = max(abs(self), abs(other), .leastNormalMagnitude)
    return abs(self - other) < scale * maxRelDev
  }

  @inlinable
  public func isAlmostZero(
    absoluteTolerance tolerance: Self = Self.ulpOfOne.squareRoot()
  ) -> Bool {
    assert(tolerance > 0)
    return abs(self) < tolerance
  }
}

@inlinable
public func lerp<T: FloatingPoint>(at: T, beetween lhs: T, _ rhs: T) -> T {
  lhs + (rhs - lhs) * at
}

extension RandomAccessCollection {
  @inlinable
  public func lowerBound(where predicate: (Element) -> Bool) -> Index? {
    guard self.count > 0 else { return nil }
    var l = self.startIndex
    var r = self.index(self.endIndex, offsetBy: -1)
    while self.index(l, offsetBy: 1) < r {
      let distance = self.distance(from: l, to: r)
      let mid = self.index(l, offsetBy: distance / 2)
      if predicate(self[mid]) {
        r = mid
      } else {
        l = mid
      }
    }
    if predicate(self[l]) { return l }
    if predicate(self[r]) { return r }
    return nil
  }

  @inlinable
  public func upperBound(where predicate: (Element) -> Bool) -> Index? {
    guard self.count > 0 else { return nil }
    var l = self.startIndex
    var r = self.index(self.endIndex, offsetBy: -1)
    while self.index(l, offsetBy: 1) < r {
      let distance = self.distance(from: l, to: r)
      let mid = self.index(l, offsetBy: distance / 2)
      if predicate(self[mid]) {
        l = mid
      } else {
        r = mid
      }
    }
    if predicate(self[r]) { return r }
    if predicate(self[l]) { return l }
    return nil
  }
}

public func asyncActionAssertCompletionOnMT(
  _ action: @escaping AsyncAction
) -> AsyncAction {
  { completion in
    action {
      assert(Thread.isMainThread)
      completion()
    }
  }
}

@inlinable
public func partialApply<T, U>(_ f: @escaping (T) -> U, with arg: T) -> () -> U {
  { f(arg) }
}
