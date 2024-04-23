import Foundation
import UIKit

import BasePublic

final class ShimmerViewProvider: ViewProvider {
  private var view: UIView?
  private let effectBeginTime: CFTimeInterval
  private let style: ShimmerStyle

  init(style: ShimmerStyle, effectBeginTime: CFTimeInterval) {
    self.style = style
    self.effectBeginTime = effectBeginTime
  }

  func loadView() -> BasePublic.ViewType {
    if let view {
      return view
    }
    let view = ShimmerView(style: style, effectBeginTime: effectBeginTime)
    self.view = view
    return view
  }

  func equals(other: ViewProvider) -> Bool {
    guard let other = other as? ShimmerViewProvider else {
      return false
    }
    return style == other.style && effectBeginTime == other.effectBeginTime
  }
}
