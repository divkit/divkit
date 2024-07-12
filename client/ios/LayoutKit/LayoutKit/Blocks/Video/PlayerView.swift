import Foundation

#if os(iOS)

import UIKit
import VGSL

public protocol PlayerView: VisibleBoundsTrackingView {
  func attach(player: Player)
  func set(scale: VideoScale)
}

extension PlayerView {
  public func set(scale _: VideoScale) {}
}

#else

public protocol PlayerView {}

#endif
