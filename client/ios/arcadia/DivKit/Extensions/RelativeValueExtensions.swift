// Copyright 2021 Yandex LLC. All rights reserved.

import CoreGraphics

import CommonCore

extension RelativeValue {
  init(double: Double) {
    self.init(rawValue: CGFloat(double))
  }
}
