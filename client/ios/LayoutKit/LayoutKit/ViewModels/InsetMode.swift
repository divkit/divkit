import CoreGraphics
import Foundation

import BaseUIPublic
import CommonCorePublic

public enum InsetMode: Equatable {
  public struct Resizable: Equatable {
    public let minValue: CGFloat
    public let maxViewportSize: CGFloat

    public init(
      minValue: CGFloat,
      maxViewportSize: CGFloat
    ) {
      self.minValue = minValue
      self.maxViewportSize = maxViewportSize
    }
  }

  case fixed(values: SideInsets)
  case resizable(params: Resizable)
}

extension InsetMode {
  public func insets(forSize size: CGFloat) -> SideInsets {
    switch self {
    case let .fixed(values):
      return values
    case let .resizable(params):
      let value = max(params.minValue, (size - params.maxViewportSize) / 2)
      return SideInsets(leading: value, trailing: value)
    }
  }
}
