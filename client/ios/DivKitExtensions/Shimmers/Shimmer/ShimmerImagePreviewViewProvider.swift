import Foundation
import UIKit

import LayoutKit
import VGSL

public final class ShimmerImagePreviewViewProvider: ViewProvider {
  private var view: UIView?
  private let effectBeginTime: CFTimeInterval
  private let style: ShimmerImagePreviewStyle
  private let path: UIElementPath

  public init(
    style: ShimmerImagePreviewStyle,
    effectBeginTime: CFTimeInterval,
    path: UIElementPath
  ) {
    self.style = style
    self.effectBeginTime = effectBeginTime
    self.path = path
  }

  public func loadView() -> ViewType {
    if let view {
      return view
    }
    let view = createImagePreviewView()
    self.view = view
    return view
  }

  public func equals(other: ViewProvider) -> Bool {
    guard let other = other as? ShimmerImagePreviewViewProvider else {
      return false
    }
    return style == other.style && effectBeginTime == other.effectBeginTime && path == other.path
  }

  private func createImagePreviewView() -> UIView {
    let view = ShimmerView()
    view.configureShimmer(
      colorsAndLocations: style.colorsAndLocations,
      angle: style.angle,
      gradientIdleState: .middle,
      animationParams: ShimmerView.AnimationParams(
        effectBeginTime: effectBeginTime,
        duration: style.duration
      )
    )
    return view
  }
}
