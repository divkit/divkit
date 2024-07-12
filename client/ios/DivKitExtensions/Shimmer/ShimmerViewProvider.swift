import Foundation
import UIKit

import LayoutKit
import VGSL

public final class ShimmerViewProvider: ViewProvider {
  private var view: UIView?
  private let effectBeginTime: CFTimeInterval
  private let style: ShimmerStyle
  private let path: UIElementPath

  public init(style: ShimmerStyle, effectBeginTime: CFTimeInterval, path: UIElementPath) {
    self.style = style
    self.effectBeginTime = effectBeginTime
    self.path = path
  }

  public func loadView() -> ViewType {
    if let view {
      return view
    }
    let view = ShimmerView(style: style, effectBeginTime: effectBeginTime)
    self.view = view
    return view
  }

  public func equals(other: ViewProvider) -> Bool {
    guard let other = other as? ShimmerViewProvider else {
      return false
    }
    return style == other.style && effectBeginTime == other.effectBeginTime && path == other.path
  }
}
