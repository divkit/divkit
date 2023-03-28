// Copyright 2018 Yandex LLC. All rights reserved.

import CoreGraphics

import BaseUIPublic

extension EdgeInsets: YCEdgeInsets {
  public typealias Domain = CGFloat
  @inlinable
  public init(vertical: CGFloat, horizontal: CGFloat) {
    self.init(top: vertical, left: horizontal, bottom: vertical, right: horizontal)
  }

  @inlinable
  public static func maxComponents(_ lhs: Self, _ rhs: Self) -> Self {
    .init(
      top: max(lhs.top, rhs.top), left: max(lhs.left, rhs.left),
      bottom: max(lhs.bottom, rhs.bottom), right: max(lhs.right, rhs.right)
    )
  }

  @inlinable
  public func isAlmostZero(
    absoluteTolerance tolerance: CGFloat = CGFloat.ulpOfOne.squareRoot()
  ) -> Bool {
    top.isAlmostZero(absoluteTolerance: tolerance) &&
      left.isAlmostZero(absoluteTolerance: tolerance) &&
      bottom.isAlmostZero(absoluteTolerance: tolerance) &&
      right.isAlmostZero(absoluteTolerance: tolerance)
  }

  public static func +(lhs: Self, rhs: Self) -> Self {
    Self(
      top: lhs.top + rhs.top,
      left: lhs.left + rhs.left,
      bottom: lhs.bottom + rhs.bottom,
      right: lhs.right + rhs.right
    )
  }

  public static func -(lhs: Self, rhs: Self) -> Self {
    Self(
      top: lhs.top - rhs.top,
      left: lhs.left - rhs.left,
      bottom: lhs.bottom - rhs.bottom,
      right: lhs.right - rhs.right
    )
  }

  @inlinable
  public static func top(_ top: CGFloat) -> Self {
    Self(top: top, left: 0, bottom: 0, right: 0)
  }

  @inlinable
  public static func bottom(_ bottom: CGFloat) -> Self {
    Self(top: 0, left: 0, bottom: bottom, right: 0)
  }

  @inlinable
  public static func all(_ value: CGFloat) -> Self {
    Self(top: value, left: value, bottom: value, right: value)
  }
}
