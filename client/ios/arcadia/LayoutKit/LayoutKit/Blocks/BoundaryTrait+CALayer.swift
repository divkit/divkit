// Copyright 2022 Yandex LLC. All rights reserved.

import QuartzCore

extension CALayer {
  func apply(boundary: BoundaryTrait) {
    if #available(iOS 11, *) {
      let boundary = boundary.makeInfo(for: bounds.size)
      cornerRadius = boundary.radius
      maskedCorners = boundary.corners
      mask = boundary.layer
    } else {
      mask = boundary.makeMaskLayer(for: bounds.size)
    }
  }
}
