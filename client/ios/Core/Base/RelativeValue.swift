// Copyright 2021 Yandex LLC. All rights reserved.

import CoreGraphics

public enum RelativeTag {}
public typealias RelativeValue = Tagged<RelativeTag, CGFloat>

extension Tagged where Tag == RelativeTag, RawValue == CGFloat {
  public func absoluteValue(in value: CGFloat) -> CGFloat {
    rawValue * value
  }

  public static func *(lhs: Self, rhs: CGFloat) -> Self {
    Self(rawValue: lhs.rawValue * rhs)
  }
}

extension CGFloat {
  public var relative: RelativeValue {
    RelativeValue(rawValue: self)
  }
}
