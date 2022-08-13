// Copyright 2021 Yandex LLC. All rights reserved.

import CoreGraphics
import Foundation

extension CGSize {
  public var aspectRatio: CGFloat? {
    guard height != 0.0 else {
      return nil
    }

    return width / height
  }

  public func frameToFill(rect: CGRect) -> CGRect? {
    guard let size = sizeToFill(size: rect.size) else {
      return nil
    }

    return CGRect(center: rect.center, size: size)
  }

  public func sizeToFill(size: CGSize) -> CGSize? {
    guard size != .zero, hasValidDimensions,
          let sizeRatio = size.aspectRatio,
          let selfRatio = aspectRatio else {
      return nil
    }

    let multiplier: CGFloat

    // rect is "wider", so we cut top and bottom of self
    if sizeRatio > selfRatio {
      multiplier = size.width / width
    } else { // rect is "higher", so we cut left and right of self
      multiplier = size.height / height
    }

    return (self * multiplier).rounded()
  }

  public func frameToFit(rect: CGRect) -> CGRect? {
    guard let size = sizeToFit(size: rect.size) else {
      return nil
    }

    return CGRect(center: rect.center, size: size)
  }

  public func sizeToFit(size: CGSize) -> CGSize? {
    guard size != .zero, hasValidDimensions,
          let sizeRatio = size.aspectRatio,
          let selfRatio = aspectRatio else {
      return nil
    }

    let multiplier: CGFloat

    // rect is "wider", so we leave empty sides
    if sizeRatio > selfRatio {
      multiplier = size.height / height
    } else { // rect is "higher", so we leave empty top and bottom
      multiplier = size.width / width
    }

    return (self * multiplier).rounded()
  }

  public var hasValidDimensions: Bool {
    width.isNormal && height.isNormal
  }

  public func rounded() -> CGSize {
    CGSize(width: round(width), height: round(height))
  }

  @inlinable
  public static func -(lhs: CGSize, rhs: CGSize) -> CGSize {
    CGSize(width: lhs.width - rhs.width, height: lhs.height - rhs.height)
  }

  @inlinable
  public static func +(lhs: CGSize, rhs: CGSize) -> CGSize {
    CGSize(width: lhs.width + rhs.width, height: lhs.height + rhs.height)
  }

  @inlinable
  public static func *(lhs: CGSize, rhs: CGFloat) -> CGSize {
    CGSize(width: lhs.width * rhs, height: lhs.height * rhs)
  }

  @inlinable
  public static func *(lhs: CGFloat, rhs: CGSize) -> CGSize {
    rhs * lhs
  }

  @inlinable
  public static func *=(lhs: inout CGSize, rhs: CGFloat) {
    lhs = lhs * rhs
  }

  @inlinable
  public static func /(lhs: CGSize, rhs: CGFloat) -> CGSize {
    CGSize(width: lhs.width / rhs, height: lhs.height / rhs)
  }

  public func isApproximatelyEqualTo(_ other: CGSize) -> Bool {
    width.isApproximatelyEqualTo(other.width) && height.isApproximatelyEqualTo(other.height)
  }
}

public protocol YCEdgeInsets {
  associatedtype Domain
  var top: Domain { get set }
  var left: Domain { get set }
  var bottom: Domain { get set }
  var right: Domain { get set }
}

extension CGSize {
  public static let nan = CGSize(width: CGFloat.nan, height: CGFloat.nan)
  public static let infinity = CGSize(
    width: CGFloat.infinity,
    height: CGFloat.infinity
  )

  @inlinable
  public func yb_inset<T>(
    by insets: T
  ) -> CGSize where T: YCEdgeInsets, T.Domain == CGFloat {
    modified(self) {
      $0.width -= (insets.left + insets.right)
      $0.height -= (insets.top + insets.bottom)
    }
  }
}
