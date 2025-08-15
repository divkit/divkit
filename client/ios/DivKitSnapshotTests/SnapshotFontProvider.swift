import DivKit
import UIKit

final class SnapshotFontProvider: DivFontProvider {
  func font(family: String, weight: DivFontWeight, size: CGFloat) -> UIFont {
    if family == "roboto_flex" {
      return UIFont(name: "RobotoFlex-Regular", size: size)!
    }
    return UIFont(name: weight.fontName, size: size)!
  }
}

extension DivFontWeight {
  fileprivate var fontName: String {
    switch self {
    case .light:
      "YSText-Light"
    case .regular:
      "YSText-Regular"
    case .medium:
      "YSText-Medium"
    case .bold:
      "YSText-Bold"
    }
  }
}
