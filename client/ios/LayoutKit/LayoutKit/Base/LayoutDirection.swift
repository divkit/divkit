import UIKit

public enum LayoutDirection {
    case system
    case rightToLeft
    case leftToRight
}

extension LayoutDirection {
  public var uiLayoutDirection: UIUserInterfaceLayoutDirection {
    switch (self) {
    case .leftToRight:
      return .leftToRight
    case .rightToLeft:
      return .rightToLeft
    case .system:
      return UIApplication.shared.userInterfaceLayoutDirection
    }
  }
}
