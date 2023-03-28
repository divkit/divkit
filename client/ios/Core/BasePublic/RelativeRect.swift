// Copyright 2019 Yandex LLC. All rights reserved.

import CoreGraphics

import BaseUIPublic

public struct RelativeRect: Hashable {
  public var origin: RelativePoint
  public var size: RelativeSize

  public var rawValue: CGRect {
    get {
      CGRect(origin: origin.rawValue, size: size.rawValue)
    }
    set {
      origin = RelativePoint(rawValue: newValue.origin)
      size = RelativeSize(rawValue: newValue.size)
    }
  }

  public init(origin: RelativePoint, size: RelativeSize) {
    self.origin = origin
    self.size = size
  }

  public func hash(into hasher: inout Hasher) {
    origin.x.hash(into: &hasher)
    origin.y.hash(into: &hasher)
    size.width.hash(into: &hasher)
    size.height.hash(into: &hasher)
  }
}

extension RelativeRect {
  public var minX: CGFloat { rawValue.minX }
  public var midX: CGFloat { rawValue.midX }
  public var maxX: CGFloat { rawValue.maxX }
  public var minY: CGFloat { rawValue.minY }
  public var midY: CGFloat { rawValue.midY }
  public var maxY: CGFloat { rawValue.maxY }
  public var width: CGFloat { rawValue.width }
  public var height: CGFloat { rawValue.height }
}

extension RelativeRect {
  public static let zero = RelativeRect(origin: .zero, size: .zero)
  public static let full = RelativeRect(origin: .zero, size: .full)

  public var center: RelativePoint {
    get { RelativePoint(rawValue: rawValue.center) }
    set {
      rawValue = modified(rawValue) {
        $0.center = newValue.rawValue
      }
    }
  }

  public func absolute(in bounds: CGRect) -> CGRect {
    let transform = CGAffineTransform(scaleX: bounds.width, y: bounds.height)
    return CGRect(
      origin: bounds.origin + origin.rawValue.applying(transform),
      size: size.rawValue.applying(transform)
    )
  }

  public func coordinate(ofCorner corner: CGRect.Corner) -> RelativePoint {
    let result = rawValue.coordinate(ofCorner: corner)
    return RelativePoint(rawValue: result)
  }

  public mutating func set(coordinate: RelativePoint, ofCorner corner: CGRect.Corner) {
    rawValue = modified(rawValue) {
      $0.set(coordinate: coordinate.rawValue, ofCorner: corner)
    }
  }

  public func concat(_ other: RelativeRect) -> RelativeRect {
    RelativeRect(
      origin: other.origin + origin * other.size,
      size: size * other.size
    )
  }

  public func approximatelyEquals(
    to other: RelativeRect,
    withPrecision precision: CGFloat = 10e-7
  ) -> Bool {
    rawValue.approximatelyEquals(to: other.rawValue, withPrecision: precision)
  }

  public func intersection(_ other: RelativeRect) -> RelativeRect {
    modified(self) { $0.rawValue = $0.rawValue.intersection(other.rawValue) }
  }

  public func contains(_ point: RelativePoint) -> Bool {
    rawValue.contains(point.rawValue)
  }
}

extension CGRect {
  public func relative(in bounds: CGRect) -> RelativeRect {
    guard !bounds.isEmpty else { return .zero }
    let transform = CGAffineTransform(scaleX: 1 / bounds.width, y: 1 / bounds.height)
    return RelativeRect(
      origin: RelativePoint(rawValue: (origin - bounds.origin).applying(transform)),
      size: RelativeSize(rawValue: size.applying(transform))
    )
  }
}

private func *(lhs: RelativePoint, rhs: RelativeSize) -> RelativePoint {
  RelativePoint(x: lhs.x * rhs.width, y: lhs.y * rhs.height)
}

private func *(lhs: RelativeSize, rhs: RelativeSize) -> RelativeSize {
  RelativeSize(rawValue: CGSize(
    width: lhs.width * rhs.width,
    height: lhs.height * rhs.height
  ))
}
