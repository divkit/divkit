import Foundation

#if os(iOS)

import UIKit

public protocol PlayerView: UIView {
  func attach(player: Player)
}

#else

public protocol PlayerView {}

#endif
