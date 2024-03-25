import UIKit

import BasePublic

public class DefaultShimmerViewFactory {
  private let effectBeginTime: CFTimeInterval

  public init(effectBeginTime: CFTimeInterval = CACurrentMediaTime()) {
    self.effectBeginTime = effectBeginTime
  }

  public func makeViewProvider(style: ShimmerStyle) -> ViewProvider {
    ShimmerViewProvider(style: style, effectBeginTime: effectBeginTime)
  }
}
