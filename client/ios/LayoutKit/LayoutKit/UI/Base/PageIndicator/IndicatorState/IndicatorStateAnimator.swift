import CoreGraphics
import Foundation

import CommonCore

struct IndicatorStateAnimator {
  let configuration: PageIndicatorConfiguration

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

  func highlightedIndicatorScale(for state: IndicatorState) -> CGFloat {
    switch configuration.animation {
    case .scale:
      return configuration.highlightingScale.interpolated(to: 1, progress: 1 - state.progress)
    case .worm, .slider:
      return 1
    }
  }

  func activeIndicatorOffsets(progress: CGFloat) -> ActiveIndicatorOffsets? {
    switch configuration.animation {
    case .scale:
      return nil
    case .slider:
      return ActiveIndicatorOffsets(
        xOffset: progress * configuration.spaceBetweenCenters,
        widthOffset: 0
      )

    case .worm:
      let additionalWidthScale = 1 - abs(2 * progress - 1)
      let widthOffset = additionalWidthScale * configuration.spaceBetweenCenters

      return ActiveIndicatorOffsets(
        xOffset: progress * configuration.spaceBetweenCenters - widthOffset / 2,
        widthOffset: widthOffset
      )
    }
  }
}
