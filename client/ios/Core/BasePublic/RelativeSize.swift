// Copyright 2019 Yandex LLC. All rights reserved.

import CoreGraphics

public enum RelativeSizeTag {}
public typealias RelativeSize = Tagged<RelativeSizeTag, CGSize>

extension RelativeSize {
  public static let zero = RelativeSize(rawValue: .zero)
  public static let full = RelativeSize(rawValue: CGSize(squareDimension: 1))

  public var width: CGFloat {
    get { rawValue.width }
    set { rawValue.width = newValue }
  }

  public var height: CGFloat {
    get { rawValue.height }
    set { rawValue.height = newValue }
  }

  public func absolute(in size: CGSize) -> CGSize {
    CGSize(width: size.width * rawValue.width, height: size.height * rawValue.height)
  }

  public func centeredRect() -> RelativeRect {
    let origin = RelativePoint(x: (1 - width) / 2, y: (1 - height) / 2)
    return RelativeRect(origin: origin, size: self)
  }
}

extension CGSize {
  public func relative(in size: CGSize) -> RelativeSize {
    RelativeSize(rawValue: CGSize(
      width: width / size.width,
      height: height / size.height
    ))
  }
}
