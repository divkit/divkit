// Copyright 2021 Yandex LLC. All rights reserved.

import CoreGraphics
import Foundation

import BaseTinyPublic

extension CGRect {
  public func inset(by value: CGFloat) -> CGRect {
    inset(by: EdgeInsets(top: value, left: value, bottom: value, right: value))
  }

  public func inset(horizontallyBy insets: SideInsets) -> CGRect {
    inset(by: EdgeInsets(horizontal: insets))
  }

  public func inset(verticallyBy insets: SideInsets) -> CGRect {
    inset(by: EdgeInsets(vertical: insets))
  }
}
