// Copyright 2019 Yandex LLC. All rights reserved.

import CoreGraphics

import BaseUIPublic

/// Determines postioning of child item inside parent container
@frozen
public enum Alignment {
  /// Child items are laid out starting from top/left
  case leading
  /// Child items are centered in container
  case center
  /// Child items are laid out at the right/bottom of the container
  case trailing
}

extension Alignment {
  public func offset(
    forAvailableSpace availableSpace: CGFloat,
    contentSize: CGFloat = 0
  ) -> CGFloat {
    switch self {
    case .leading:
      return 0
    case .center:
      return ((availableSpace - contentSize) * 0.5).roundedToScreenScale
    case .trailing:
      return availableSpace - contentSize
    }
  }
}

extension Alignment: CustomDebugStringConvertible {
  public var debugDescription: String {
    switch self {
    case .leading:
      return "Leading"
    case .center:
      return "Center"
    case .trailing:
      return "Trailing"
    }
  }
}
