// Copyright 2021 Yandex LLC. All rights reserved.

import CoreGraphics
import Foundation

public final class BackgroundAttribute {
  public static let Key = NSAttributedString.Key("RangeBackground")

  public let color: CGColor
  public let range: CFRange

  public init(
    color: CGColor,
    range: CFRange
  ) {
    self.color = color
    self.range = range
  }

  public func apply(to str: CFMutableAttributedString, at range: CFRange) {
    CFAttributedStringSetAttribute(str, range, BackgroundAttribute.Key as CFString, self)
  }
}
