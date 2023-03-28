// Copyright 2021 Yandex LLC. All rights reserved.

import CoreGraphics

import BaseTinyPublic

@_implementationOnly import CoreImage

extension CGColor {
  public var rgba: RGBAColor {
    let color = CIColor(cgColor: self)
    return RGBAColor(
      red: color.red, green: color.green, blue: color.blue, alpha: color.alpha
    )
  }
}
