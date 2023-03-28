// Copyright 2017 Yandex LLC. All rights reserved.

import CoreGraphics
import Foundation

import BaseUIPublic

extension EdgeInsets {
  public func insetHeight(_ height: CGFloat) -> CGFloat {
    height - (top + bottom)
  }

  public func isApproximatelyEqualTo(_ other: EdgeInsets) -> Bool {
    top.isApproximatelyEqualTo(other.top) &&
      left.isApproximatelyEqualTo(other.left) &&
      bottom.isApproximatelyEqualTo(other.bottom) &&
      right.isApproximatelyEqualTo(other.right)
  }
}

public func availableWidthForWidth(_ width: CGFloat, insets: EdgeInsets) -> CGFloat {
  width - (insets.left + insets.right)
}

public func availableWidthForWidth(_ width: CGFloat, insets: SideInsets) -> CGFloat {
  width - (insets.leading + insets.trailing)
}

public prefix func -(_ value: EdgeInsets) -> EdgeInsets {
  EdgeInsets(
    top: -value.top,
    left: -value.left,
    bottom: -value.bottom,
    right: -value.right
  )
}

public func *(lhs: EdgeInsets, rhs: CGFloat) -> EdgeInsets {
  EdgeInsets(
    top: lhs.top * rhs,
    left: lhs.left * rhs,
    bottom: lhs.bottom * rhs,
    right: lhs.right * rhs
  )
}
