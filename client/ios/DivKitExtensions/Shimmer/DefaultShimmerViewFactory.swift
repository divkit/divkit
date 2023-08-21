import UIKit

public class DefaultShimmerViewFactory {
  private let effectBeginTime: CFTimeInterval

  public init(effectBeginTime: CFTimeInterval = CACurrentMediaTime()) {
    self.effectBeginTime = effectBeginTime
  }

  public func makeView(style: ShimmerStyle) -> UIView {
    return ShimmerView(style: style, effectBeginTime: effectBeginTime)
  }
}
