// Copyright 2022 Yandex LLC. All rights reserved.

import CoreGraphics
import Foundation

public final class BorderAttribute {
  public static let Key = NSAttributedString.Key("RangeBorder")

  public let color: CGColor?
  public let width: CGFloat?
  public let cornerRadius: CGFloat?
  public let range: CFRange

  public init(
    color: CGColor?,
    width: CGFloat?,
    cornerRadius: CGFloat?,
    range: CFRange
  ) {
    self.color = color
    self.width = width
    self.cornerRadius = cornerRadius
    self.range = range
  }

  public func apply(to str: CFMutableAttributedString, at range: CFRange) {
    CFAttributedStringSetAttribute(str, range, BorderAttribute.Key as CFString, self)
  }
}
