import CoreGraphics
import Foundation

import CommonCorePublic

struct IndicatorStateAnimator {
  let configuration: PageIndicatorConfiguration
  let boundsWidth: CGFloat
  let numberOfPages: Int

  func indicatorColor(for state: IndicatorState) -> Color {
    let normalColor = configuration.normalColor
    let highlightedColor = configuration.highlightedColor

    switch state.kind {
    case .highlighted where configuration.animation == .scale:
      return normalColor.interpolate(to: highlightedColor, progress: state.progress)
    case .highlighted, .normal:
      return normalColor
    }
  }

  func indicatorBorder(for state: IndicatorState) -> BlockBorder {
    let normalBorder = configuration.normalBorder
    let highlightedBorder = configuration.highlightedBorder

    switch state.kind {
    case .highlighted where configuration.animation == .scale:
      let border = BlockBorder(
        color: normalBorder.color.interpolate(
          to: highlightedBorder.color,
          progress: state.progress
        ),
        width: normalBorder.width.interpolated(
          to: highlightedBorder.width,
          progress: state.progress
        )
      )
      return border
    case .highlighted, .normal:
      return normalBorder
    }
  }

  func highlightedIndicatorScale(for state: IndicatorState, borderScale: Scale) -> Scale {
    switch configuration.animation {
    case .scale:
      return (
        configuration.highlightedWidthScale
          .interpolated(to: 1, progress: 1 - state.progress) * borderScale.x,
        configuration.highlightedHeightScale
          .interpolated(to: 1, progress: 1 - state.progress) * borderScale.y
      )
    case .worm, .slider:
      return (1, 1)
    }
  }

  func activeIndicatorOffsets(progress: CGFloat) -> ActiveIndicatorOffsets? {
    switch configuration.animation {
    case .scale:
      return nil
    case .slider:
      return ActiveIndicatorOffsets(
        xOffset: progress * itemWidth,
        widthOffset: 0
      )

    case .worm:
      let additionalWidthScale = 1 - abs(2 * progress - 1)
      let widthOffset = additionalWidthScale * itemWidth

      return ActiveIndicatorOffsets(
        xOffset: progress * itemWidth - widthOffset / 2,
        widthOffset: widthOffset
      )
    }
  }

  private var itemWidth: CGFloat {
    switch configuration.itemPlacement {
    case let .fixed(spaceBetweenCenters):
      return spaceBetweenCenters
    case let .stretch(_, maxVisibleItems):
      let visiblePageCount = min(maxVisibleItems, numberOfPages)
      return boundsWidth / CGFloat(visiblePageCount)
    }
  }
}
