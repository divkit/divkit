// Copyright 2021 Yandex LLC. All rights reserved.

import CoreGraphics
import Foundation

import BaseTinyPublic

extension CGSize: Hashable {
  public static var infinite: CGSize {
    CGSize(width: CGFloat.infinity, height: CGFloat.infinity)
  }

  public func hash(into hasher: inout Hasher) {
    width.hash(into: &hasher)
    height.hash(into: &hasher)
  }

  public func ceiled(toStep step: CGFloat = 1) -> Self {
    Self(width: width.ceiled(toStep: step), height: height.ceiled(toStep: step))
  }
}

extension CGSize {
  public init(squareDimension: CGFloat) {
    self.init(width: squareDimension, height: squareDimension)
  }

  public func inset(by insets: EdgeInsets) -> CGSize {
    CGSize(
      width: width - insets.horizontalInsets.sum,
      height: height - insets.verticalInsets.sum
    )
  }

  public var maxDimension: CGFloat {
    max(width, height)
  }

  public var minDimension: CGFloat {
    min(width, height)
  }

  public func expanded(by insets: EdgeInsets) -> CGSize {
    CGSize(width: width + insets.horizontalInsets.sum, height: height + insets.verticalInsets.sum)
  }

  public func swapDimensions() -> CGSize {
    CGSize(width: height, height: width)
  }
}

extension CGSize {
  public var isEmpty: Bool {
    width.isApproximatelyEqualTo(0) || height.isApproximatelyEqualTo(0)
  }
}
