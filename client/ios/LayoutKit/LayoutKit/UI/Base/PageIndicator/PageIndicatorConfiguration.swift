import CoreGraphics

import CommonCore

public struct PageIndicatorConfiguration: Equatable {
  public enum Animation {
    case scale, slider, worm
  }

  public let highlightedColor: Color
  public let normalColor: Color

  // "Normal" scale is considered as 1
  // for fully selected page
  public let highlightingScale: CGFloat
  // for edge pages if not all pages are visible
  public let disappearingScale: CGFloat
  public let spaceBetweenCenters: CGFloat
  public let pageSize: CGSize
  public let pageCornerRadius: CGFloat
  public let animation: Animation

  public init(
    highlightedColor: Color,
    normalColor: Color,
    highlightingScale: CGFloat,
    disappearingScale: CGFloat,
    spaceBetweenCenters: CGFloat,
    pageSize: CGSize,
    pageCornerRadius: CGFloat,
    animation: Animation
  ) {
    self.highlightedColor = highlightedColor
    self.normalColor = normalColor
    self.highlightingScale = highlightingScale
    self.disappearingScale = disappearingScale
    self.spaceBetweenCenters = spaceBetweenCenters
    self.pageSize = pageSize
    self.pageCornerRadius = pageCornerRadius
    self.animation = animation
  }
}
