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
  case spaceBetween
  case spaceAround
  case spaceEvenly
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
    // TODO: @bugperson should be implemented here DIVKIT-2378
    default:
      assertionFailure("Others alignments not supported yet")
      return 0
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
    case .spaceBetween:
      return "space-between"
    case .spaceAround:
      return "space-around"
    case .spaceEvenly:
      return "space-evenly"
    }
  }
}
