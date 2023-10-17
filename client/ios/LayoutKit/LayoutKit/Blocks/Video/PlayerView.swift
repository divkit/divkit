import Foundation

#if os(iOS)

import CommonCorePublic
import UIKit

public protocol PlayerView: VisibleBoundsTrackingView {
  func attach(player: Player)
  func set(scale: VideoScale)
}

public extension PlayerView {
  func set(scale: VideoScale) { }
}

#else

public protocol PlayerView {}

#endif
