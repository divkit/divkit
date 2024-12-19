import Foundation

import LayoutKit
import VGSL

public protocol AnimatableViewFactory: AnyObject {
  func createAnimatableView(withMode mode: AnimationRepeatMode, repeatCount count: Float)
    -> AnimatableView
}

public protocol AnimatableView: ViewType {
  func play()
  func setSource(_ source: AnimationSourceType)
}

@frozen
public enum AnimationRepeatMode {
  case restart
  case reverse
}
