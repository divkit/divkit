// Copyright 2021 Yandex LLC. All rights reserved.

import CoreGraphics
import Foundation

import BaseTinyPublic

extension EdgeInsets {
  public var contentOrigin: CGPoint {
    CGPoint(x: left, y: top)
  }

  public init(horizontal: SideInsets = .zero, vertical: SideInsets = .zero) {
    self.init(
      top: vertical.leading,
      left: horizontal.leading,
      bottom: vertical.trailing,
      right: horizontal.trailing
    )
  }

  public init(uniform inset: CGFloat) {
    self.init(
      top: inset,
      left: inset,
      bottom: inset,
      right: inset
    )
  }

  public func addTop(_ value: CGFloat) -> EdgeInsets {
    EdgeInsets(top: top + value, left: left, bottom: bottom, right: right)
  }

  public var horizontalInsets: SideInsets {
    SideInsets(leading: left, trailing: right)
  }

  public var verticalInsets: SideInsets {
    SideInsets(leading: top, trailing: bottom)
  }
}
