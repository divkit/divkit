import Foundation
import LayoutKit
import VGSL

public protocol AsyncSourceAnimatableViewFactory: AnyObject {
  func createAsyncSourceAnimatableView(withMode mode: AnimationRepeatMode, repeatCount count: Float)
    -> AsyncSourceAnimatableView
}

/// This protocol is deprecated. Use AsyncSourceAnimatableViewFactory instead.
public protocol AnimatableViewFactory: AnyObject, AsyncSourceAnimatableViewFactory {
  func createAnimatableView(withMode mode: AnimationRepeatMode, repeatCount count: Float)
    -> AnimatableView
}

extension AnimatableViewFactory {
  public func createAsyncSourceAnimatableView(
    withMode mode: AnimationRepeatMode,
    repeatCount count: Float
  ) -> AsyncSourceAnimatableView {
    createAnimatableView(withMode: mode, repeatCount: count)
  }
}

public protocol AsyncSourceAnimatableView: ViewType {
  func play()
  @MainActor func setSourceAsync(_ source: AnimationSourceType) async
}

/// This protocol is deprecated. Use AsyncSourceAnimatableView instead.
public protocol AnimatableView: AsyncSourceAnimatableView {
  func setSource(_ source: AnimationSourceType)
}

extension AnimatableView {
  @MainActor public func setSourceAsync(_ source: AnimationSourceType) async {
    setSource(source)
  }
}

@frozen
public enum AnimationRepeatMode {
  case restart
  case reverse
}
