// Copyright 2021 Yandex LLC. All rights reserved.

import CoreGraphics
import Foundation

extension CGFloat {
  public func interpolated(to end: CGFloat, progress: CGFloat) -> CGFloat {
    let p = progress.clamp(0...1)
    return self * (1 - p) + end * p
  }

  public var half: CGFloat {
    (self * 0.5).rounded(.toNearestOrEven)
  }
}
