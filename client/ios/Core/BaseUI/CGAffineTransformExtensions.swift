// Copyright 2019 Yandex LLC. All rights reserved.

import CoreGraphics

extension CGAffineTransform {
  public init(scale: CGFloat) {
    self = CGAffineTransform(scaleX: scale, y: scale)
  }

  public func scaled(by scale: CGFloat) -> CGAffineTransform {
    scaledBy(x: scale, y: scale)
  }
}
