// Copyright 2021 Yandex LLC. All rights reserved.

import CoreGraphics
import Foundation

extension CGPoint {
  public func movingX(by value: CGFloat) -> CGPoint {
    CGPoint(x: x + value, y: y)
  }

  public func movingY(by value: CGFloat) -> CGPoint {
    CGPoint(x: x, y: y + value)
  }
}

public func +(lhs: CGPoint, rhs: CGPoint) -> CGPoint {
  CGPoint(
    x: lhs.x + rhs.x,
    y: lhs.y + rhs.y
  )
}

public func -(lhs: CGPoint, rhs: CGPoint) -> CGPoint {
  CGPoint(
    x: lhs.x - rhs.x,
    y: lhs.y - rhs.y
  )
}

public prefix func -(rhs: CGPoint) -> CGPoint {
  CGPoint(x: -rhs.x, y: -rhs.y)
}

public func *(lhs: CGPoint, rhs: CGFloat) -> CGPoint {
  CGPoint(x: lhs.x * rhs, y: lhs.y * rhs)
}

public func *(lhs: CGFloat, rhs: CGPoint) -> CGPoint {
  rhs * lhs
}

public func /(lhs: CGPoint, rhs: CGFloat) -> CGPoint {
  CGPoint(x: lhs.x / rhs, y: lhs.y / rhs)
}

public func -(lhs: CGPoint, rhs: CGFloat) -> CGPoint {
  CGPoint(x: lhs.x - rhs, y: lhs.y - rhs)
}
