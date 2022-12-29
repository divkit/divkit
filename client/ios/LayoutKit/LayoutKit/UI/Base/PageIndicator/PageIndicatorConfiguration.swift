import CoreGraphics

import CommonCore

public struct PageIndicatorConfiguration: Equatable {
  public enum Animation {
    case scale, slider, worm
  }

  public enum ItemPlacement: Equatable {
    case fixed(spaceBetweenCenters: CGFloat)
    case stretch(spacing: CGFloat, maxVisibleItems: Int)
  }

  public let highlightedColor: Color
  public let normalColor: Color

  // "Normal" scale is considered as 1
  // for fully selected page
  public let highlightingScale: CGFloat
  // for edge pages if not all pages are visible
  public let disappearingScale: CGFloat
  public let pageSize: CGSize
  public let pageCornerRadius: CGFloat
  public let animation: Animation
  public let itemPlacement: ItemPlacement

  public init(
    highlightedColor: Color,
    normalColor: Color,
    highlightingScale: CGFloat,
    disappearingScale: CGFloat,
    pageSize: CGSize,
    pageCornerRadius: CGFloat,
    animation: Animation,
    itemPlacement: ItemPlacement
  ) {
    self.highlightedColor = highlightedColor
    self.normalColor = normalColor
    self.highlightingScale = highlightingScale
    self.disappearingScale = disappearingScale
    self.pageSize = pageSize
    self.pageCornerRadius = pageCornerRadius
    self.animation = animation
    self.itemPlacement = itemPlacement
  }
}
