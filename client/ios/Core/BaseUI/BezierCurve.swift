// Copyright 2022 Yandex LLC. All rights reserved.

import CoreGraphics

public struct BezierCurve {
  public var from: CGPoint
  public var control1: CGPoint
  public var control2: CGPoint
  public var to: CGPoint

  public init(
    from: CGPoint,
    control1: CGPoint,
    control2: CGPoint,
    to: CGPoint
  ) {
    self.from = from
    self.control1 = control1
    self.control2 = control2
    self.to = to
  }
}
