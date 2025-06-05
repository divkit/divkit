#if DEBUG
import UIKit

extension UIView {
  var debugID: String? {
    get {
      objc_getAssociatedObject(self, &associatedKey) as? String
    }
    set {
      objc_setAssociatedObject(
        self,
        &associatedKey,
        newValue,
        .OBJC_ASSOCIATION_RETAIN_NONATOMIC
      )
    }
  }

  public override var debugDescription: String {
    if let debugID {
      return "Div ID: \(debugID). \(super.debugDescription)"
    }
    return super.debugDescription
  }
}

private var associatedKey: UInt8 = 0
#endif
