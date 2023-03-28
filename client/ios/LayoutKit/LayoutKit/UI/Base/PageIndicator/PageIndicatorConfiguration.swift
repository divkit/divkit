import CoreGraphics

import CommonCorePublic

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
  public let highlightedBorder: BlockBorder
  public let normalBorder: BlockBorder

  // "Normal" scale is considered as 1
  // for fully selected page
  public let highlightedHeightScale: CGFloat
  public let highlightedWidthScale: CGFloat
  // for edge pages if not all pages are visible
  public let disappearingHeightScale: CGFloat
  public let disappearingWidthScale: CGFloat
  public let pageSize: CGSize
  public let highlightedPageCornerRadius: CGFloat?
  public let pageCornerRadius: CGFloat
  public let animation: Animation
  public let itemPlacement: ItemPlacement

  public init(
    highlightedColor: Color,
    normalColor: Color,
    highlightedBorder: BlockBorder,
    normalBorder: BlockBorder,
    highlightedHeightScale: CGFloat,
    highlightedWidthScale: CGFloat,
    disappearingHeightScale: CGFloat,
    disappearingWidthScale: CGFloat,
    pageSize: CGSize,
    highlightedPageCornerRadius: CGFloat?,
    pageCornerRadius: CGFloat,
    animation: Animation,
    itemPlacement: ItemPlacement
  ) {
    self.highlightedColor = highlightedColor
    self.normalColor = normalColor
    self.highlightedBorder = highlightedBorder
    self.normalBorder = normalBorder
    self.highlightedHeightScale = highlightedHeightScale
    self.highlightedWidthScale = highlightedWidthScale
    self.disappearingHeightScale = disappearingHeightScale
    self.disappearingWidthScale = disappearingWidthScale
    self.pageSize = pageSize
    self.highlightedPageCornerRadius = highlightedPageCornerRadius
    self.pageCornerRadius = pageCornerRadius
    self.animation = animation
    self.itemPlacement = itemPlacement
  }
}

extension PageIndicatorConfiguration {
  public struct RoundedRectangleIndicator {
    public let size: CGSize
    public let cornerRadius: CGFloat
    public let backgroundColor: Color
    public let borderWidth: CGFloat
    public let borderColor: Color

    public init(
      size: CGSize,
      cornerRadius: CGFloat,
      backgroundColor: Color,
      borderWidth: CGFloat,
      borderColor: Color
    ) {
      self.size = size
      self.cornerRadius = cornerRadius
      self.backgroundColor = backgroundColor
      self.borderWidth = borderWidth
      self.borderColor = borderColor
    }
  }
}
