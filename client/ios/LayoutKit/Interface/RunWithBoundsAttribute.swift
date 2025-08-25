import CoreGraphics
import Foundation

public final class RunWithBoundsAttribute {
  public static let Key = NSAttributedString.Key("DKActions")

  public let actions: [UserInterfaceAction]
  public let mask: TextMask?

  public init(actions: [UserInterfaceAction], mask: TextMask?) {
    self.actions = actions
    self.mask = mask
  }

  public func apply(to str: CFMutableAttributedString, at range: CFRange) {
    if !actions.isEmpty || mask != nil {
      CFAttributedStringSetAttribute(str, range, RunWithBoundsAttribute.Key as CFString, self)
    }
  }
}
