// Copyright 2019 Yandex LLC. All rights reserved.

import CoreGraphics

public protocol DimensionsFlipping {
  func flipDimensions() -> Self
}

extension CGPoint: DimensionsFlipping {
  public func flipDimensions() -> CGPoint {
    CGPoint(x: y, y: x)
  }
}

extension CGSize: DimensionsFlipping {
  public func flipDimensions() -> CGSize {
    CGSize(width: height, height: width)
  }
}

extension CGRect: DimensionsFlipping {
  public func flipDimensions() -> CGRect {
    CGRect(origin: origin.flipDimensions(), size: size.flipDimensions())
  }
}

extension RelativePoint: DimensionsFlipping {
  public func flipDimensions() -> RelativePoint {
    RelativePoint(rawValue: rawValue.flipDimensions())
  }
}

#if os(iOS) || os(tvOS)
extension Image.Orientation {
  public func fixDimensions(in value: CGSize) -> CGSize {
    sizeDimensionsAreFlippedInCGImage ? value.flipDimensions() : value
  }

  public func fixSizeDimensions(in value: CGRect) -> CGRect {
    CGRect(
      origin: value.origin,
      size: fixDimensions(in: value.size)
    )
  }
}
#endif
