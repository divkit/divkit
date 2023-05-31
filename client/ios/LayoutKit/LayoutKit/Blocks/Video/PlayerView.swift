import Foundation

#if os(iOS)

import CommonCorePublic
import UIKit

public protocol PlayerView: VisibleBoundsTrackingView {
  func attach(player: Player)
}

#else

public protocol PlayerView {}

#endif
