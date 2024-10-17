import Foundation

#if os(iOS)

import UIKit
import VGSL

public protocol PlayerView: VisibleBoundsTrackingView {
  var videoRatio: CGFloat? { get }

  func attach(player: Player)
  func set(scale: VideoScale)
}

extension PlayerView {
  public var videoRatio: CGFloat? { nil }
  public func set(scale _: VideoScale) {}
}

#else

public protocol PlayerView {}

#endif
