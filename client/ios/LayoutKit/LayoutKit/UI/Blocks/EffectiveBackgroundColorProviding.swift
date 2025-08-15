#if os(iOS)
import UIKit
import VGSL

public protocol EffectiveBackgroundColorProviding {
  var effectiveBackgroundColor: UIColor? { get }
}
#endif
